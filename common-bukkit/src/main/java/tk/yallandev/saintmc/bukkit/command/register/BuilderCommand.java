package tk.yallandev.saintmc.bukkit.command.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.worldedit.WorldeditController;
import tk.yallandev.saintmc.bukkit.api.worldedit.arena.ArenaResponse;
import tk.yallandev.saintmc.bukkit.api.worldedit.arena.ArenaType;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

@SuppressWarnings("deprecation")
public class BuilderCommand implements CommandClass {

	private WorldeditController controller;

	public BuilderCommand() {
		controller = BukkitMain.getInstance().getWorldeditController();
	}

	@Command(name = "build")
	public void buildCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		BukkitMember member = (BukkitMember) cmdArgs.getSender();

		if (checkPermission(member))
			return;

		member.setBuildEnabled(!member.isBuildEnabled());
		member.sendMessage(" §a* §fVocê " + (member.isBuildEnabled() ? "§aativou§f" : "§cdesativou§f")
				+ "§f o modo de construção!");
	}

	@Command(name = "wand", groupToUse = Group.TRIAL)
	public void wandCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (checkPermission(member))
			return;

		controller.giveWand(player);
		player.sendMessage(" §a* §fVocê recebeu a varinha do §aWorldedit§f!");
	}

	@Command(name = "createarena", groupToUse = Group.TRIAL)
	public void createarenaCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (checkPermission(member))
			return;

		String[] args = cmdArgs.getArgs();

		if (args.length < 4) {
			player.sendMessage(" §e* §fUse §a/createarena <"
					+ (Joiner.on(":")
							.join(Arrays.asList(ArenaType.values()).stream()
									.map(arenaType -> arenaType.name().toLowerCase()).collect(Collectors.toList())))
					+ "> <material:id> <radius> <height>§f para setar um grupo.");
			return;
		}

		ArenaType arenaType = null;

		try {
			arenaType = ArenaType.valueOf(args[0].toUpperCase());
		} catch (Exception ex) {

		}

		Material blockMaterial = null;
		byte blockId = 0;

		if (args[1].contains(":")) {
			blockMaterial = Material.getMaterial(args[1].split(":")[0].toUpperCase());

			if (blockMaterial == null) {
				try {
					blockMaterial = Material.getMaterial(Integer.valueOf(args[1].split(":")[0]));
				} catch (NumberFormatException e) {
				}
			}

			try {
				blockId = Byte.valueOf(args[1].split(":")[1]);
			} catch (Exception e) {
				player.sendMessage("§cO bloco " + args[1] + " não existe!");
				return;
			}
		} else {
			blockMaterial = Material.getMaterial(args[1]);

			if (blockMaterial == null) {
				try {
					blockMaterial = Material.getMaterial(Integer.valueOf(args[1]));
				} catch (NumberFormatException e) {
				}
			}
		}

		Integer radius = null;

		try {
			radius = Integer.valueOf(args[2]);
		} catch (NumberFormatException ex) {
			player.sendMessage(" §c* §fFormato de numero para o radius inválidof!");
			return;
		}

		Integer height = null;

		try {
			height = Integer.valueOf(args[3]);
		} catch (NumberFormatException ex) {
			player.sendMessage(" §c* §fFormato de numero para o radius inválidof!");
			return;
		}

		ArenaResponse arenaResponse = arenaType.place(player.getLocation(), blockMaterial, blockId, radius, height,
				true, false);

		controller.addUndo(player, arenaResponse.getMap());
		player.sendMessage("§dVocê criou uma arena " + NameUtils.formatString(arenaType.name()) + ", colocando "
				+ arenaResponse.getBlocks() + " blocos!");
	}

	@Command(name = "set", groupToUse = Group.TRIAL)
	public void setCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (checkPermission(member))
			return;

		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(" §e* §fUse §a/set <material:id>§f para setar um grupo.");
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
					player.sendMessage("§cNão foi possível encontrar esse bloco!");
					return;
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
					player.sendMessage("§cNão foi possível encontrar esse bloco!");
					return;
				}
			}
		}

		if (blockMaterial == null) {
			player.sendMessage("§cNão foi possível encontrar o bloco " + args[0] + "!");
			return;
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
					Location location = new Location(first.getWorld(), x, y, z);
					map.put(location.clone(), location.getBlock().getState());

					if (location.getBlock().getType() != blockMaterial || location.getBlock().getData() != blockId) {
						location.getBlock().setType(blockMaterial);
						location.getBlock().setData(blockId);
						amount++;
					}
				}
			}
		}

		controller.addUndo(player, map);
		player.sendMessage("§dVocê colocou " + amount + " blocos!");
	}

	@Command(name = "undo", groupToUse = Group.TRIAL)
	public void undoCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();
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
			entry.getKey().getBlock().setType(entry.getValue().getType());
			entry.getKey().getBlock().setData(entry.getValue().getData().getData());
			amount++;
		}

		controller.removeUndo(player, map);
		player.sendMessage("§dVocê colocou " + amount + " blocos!");
	}

	public boolean checkPermission(Member member) {

		if (member.isGroup(Group.TRIAL) || member.hasGroupPermission(Group.ADMIN)
				|| member.hasPermission("permission.build"))
			return false;

		if ((CommonGeneral.getInstance().getServerType() == ServerType.HUNGERGAMES
				|| CommonGeneral.getInstance().getServerType() == ServerType.EVENTO
				|| CommonGeneral.getInstance().getServerType() == ServerType.CLANXCLAN)
				&& member.hasGroupPermission(Group.MODPLUS))
			return false;

		member.sendMessage(" §c* §fVocê não tem §cpermissão§f para executar esse comando!");
		return true;
	}

}
