package tk.yallandev.saintmc.bukkit.command.register;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.worldedit.WorldeditController;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;

@SuppressWarnings("deprecation")
public class WorldeditCommand implements CommandClass {

	private WorldeditController controller;

	public WorldeditCommand() {
		controller = BukkitMain.getInstance().getWorldeditController();
	}

	@Command(name = "wand", groupToUse = Group.BUILDER)
	public void wandCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (checkPermission(member))
			return;

		controller.giveWand(player);
		player.sendMessage(" §a* §fVocê recebeu a varinha do §aWorldedit§f!");
	}

	@Command(name = "set", groupToUse = Group.BUILDER)
	public void setCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (checkPermission(member))
			return;

		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(" §e* §fUse §a/groupset <player> <group>§f para setar um grupo.");
			return;
		}

		Material blockMaterial = null;
		byte blockId = 0;

		if (args[0].contains(":")) {
			blockMaterial = Material.getMaterial(args[0].split(":")[0].toUpperCase());

			if (blockMaterial == null) {
				try {
					blockMaterial = Material.getMaterial(Integer.valueOf(args[0].split(":")[0]));
				} catch (NumberFormatException e) {
				}
			}

			try {
				blockId = Byte.valueOf(args[0].split(":")[1]);
			} catch (Exception e) {
				player.sendMessage("§cO bloco " + args[0] + " não existe!");
				return;
			}
		} else {
			blockMaterial = Material.getMaterial(args[0]);

			if (blockMaterial == null) {
				try {
					blockMaterial = Material.getMaterial(Integer.valueOf(args[0]));
				} catch (NumberFormatException e) {
				}
			}
		}
		
		if (!controller.hasFirstPosition(player)) {
			player.sendMessage("§cA primeira posição não foi setada!");
			return;
		}
		
		if (!controller.hasSecondPosition(player)) {
			player.sendMessage("§cA segunda posição não foi setada!");
			return;
		}
		
		Location first = controller.getFirstPosition(player);
		Location second = controller.getSecondPosition(player);
		
		Map<Location, BlockState> map = new HashMap<>();
		int amount = 0;
		
		for (int x = (first.getBlockX() > second.getBlockX() ? second.getBlockX()
				: first.getBlockX()); x <= (first.getBlockX() < second.getBlockX() ? second.getBlockX()
						: first.getBlockX()); x++) {
			for (int z = (first.getBlockZ() > second.getBlockZ() ? second.getBlockZ()
					: first.getBlockZ()); z <= (first.getBlockZ() < second.getBlockZ() ? second.getBlockZ()
							: first.getBlockZ()); z++) {
				for (int y = (first.getBlockY() > second.getBlockY() ? second.getBlockY()
						: first.getBlockY()); y <= (first.getBlockY() < second.getBlockY() ? second.getBlockY()
								: first.getBlockY()); y++) {
					Location l = new Location(first.getWorld(), x, y, z);
					map.put(l.clone(), l.getBlock().getState());

					if (l.getBlock().getType() != blockMaterial || l.getBlock().getData() != blockId) {
						l.getBlock().setType(blockMaterial);
						l.getBlock().setData(blockId);
						amount++;
					}
				}
			}
		}
		
		controller.addUndo(player, map);
		player.sendMessage("§dVocê colocou " + amount + " blocos!");
	}

	@Command(name = "undo", groupToUse = Group.BUILDER)
	public void undoCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = cmdArgs.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (checkPermission(member))
			return;

		if (!controller.hasUndoList(player)) {
			player.sendMessage("§cVocê não tem nada para desfazer");
			return;
		}

		Map<Location, BlockState> map = controller.getUndoList(player).get(controller.getUndoList(player).size() - 1);

		int amount = 0;

		for (Entry<Location, BlockState> entry : map.entrySet()) {
			if (entry.getValue().getType() != entry.getKey().getBlock().getType()
					|| entry.getValue().getData().getData() != entry.getKey().getBlock().getData()) {
				entry.getKey().getBlock().setType(entry.getValue().getType());
				entry.getKey().getBlock().setData(entry.getValue().getData().getData());
				amount++;
			}
		}
		
		controller.removeUndo(player, map);
		player.sendMessage("§dVocê colocou " + amount + " blocos!");
	}

	public boolean checkPermission(Member member) {
		if (!(member.isGroup(Group.BUILDER) || member.hasPermission("permission.build"))
				&& !member.hasGroupPermission(Group.ADMIN)) {
			member.sendMessage(" §c* §fVocê não tem §cpermissão§f para executar esse comando!");
			return true;
		}

		return false;
	}

}
