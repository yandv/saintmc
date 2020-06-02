package tk.yallandev.saintmc.bukkit.command.register;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandFramework.Completer;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;

public class GroupCommand implements CommandClass {

	@Command(name = "groupset", usage = "/<command> <player> <group>", groupToUse = Group.GERENTE, aliases = {
			"setargrupo" }, runAsync = true)
	public void groupset(BukkitCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length != 2) {
			sender.sendMessage(" §e* §fUse §a/groupset <player> <group>§f para setar um grupo.");
			return;
		}

		Group grupo = null;

		try {
			grupo = Group.valueOf(args[1].toUpperCase());
		} catch (Exception e) {
			sender.sendMessage(" §c* §fO grupo §a" + args[1].toUpperCase() + "§f não existe!");
			return;
		}

		final Group group = grupo;
		Group playerGroup = Group.MEMBRO;

		if (cmdArgs.isPlayer()) {
			Member battleSender = CommonGeneral.getInstance().getMemberManager()
					.getMember(cmdArgs.getPlayer().getUniqueId());
			playerGroup = battleSender.getServerGroup();
		} else {
			playerGroup = Group.DONO;
		}

		if (group.ordinal() <= Group.YOUTUBER.ordinal() && group.ordinal() >= Group.LIGHT.ordinal()) {
			sender.sendMessage(" §e* §fO grupo §a" + group.name() + "§f pode ser setado, somente, temporariamente.");
			return;
		}

		if (cmdArgs.isPlayer()) {
			switch (playerGroup) {
			case GERENTE: {
				if (group.ordinal() >= Group.MOD.ordinal()) {
					sender.sendMessage(" §c* §fVocê só pode manejar o grupo §5§lMOD§f ou inferior!");
					return;
				}
				break;
			}
			case ADMIN: {
				if (group.ordinal() >= Group.MODPLUS.ordinal()) {
					sender.sendMessage(" §c* §fVocê só pode manejar o grupo §5§lMOD+§f ou inferior!");
					return;
				}
				break;
			}
			case DIRETOR: {
				if (group.ordinal() >= Group.ADMIN.ordinal()) {
					sender.sendMessage(" §c* §fVocê só pode manejar o grupo §c§lADMIN§f ou inferior!");
					return;
				}
				break;
			}
			case DONO: {
				if (group.ordinal() > Group.DONO.ordinal()) {
					sender.sendMessage(" §c* §fVocê só pode manejar o grupo §4§lDIRETOR§f ou inferior!");
					sender.sendMessage(" §c* §fSó o console consegue manejar §4§lDONO§f e §3§lDEVELOPER§f!");
					return;
				}
				break;
			}
			default:
				break;
			}
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(" §c* §fNão foi possível pegar as informações do jogador §a" + args[0] + "§f!");
				return;
			}
		}
		
		if (cmdArgs.isPlayer())
			if (player.getGroup().ordinal() < playerGroup.ordinal()) {
				sender.sendMessage(" §c* §fVocê não pode majenar o grupo desse jogador!");
				return;
			}

		Group actualGroup = player.getGroup();

		if (actualGroup == group) {
			sender.sendMessage(" §c* §fO jogador §a" + player.getPlayerName() + "§f já está nesse grupo!");
			return;
		}

		player.setGroup(group);
		sender.sendMessage(" §a* §fVocê alterou o grupo do §a" + player.getPlayerName() + "("
				+ player.getUniqueId().toString().replace("-", "") + ")" + "§f para §a" + group.name() + "§f!");
	}

	@Completer(name = "groupset", aliases = { "setargrupo" })
	public List<String> groupsetCompleter(CommandArgs args) {
		if (args.getArgs().length == 1) {
			ArrayList<String> players = new ArrayList<>();
			
			for (Player p : BukkitMain.getInstance().getServer().getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(args.getArgs()[0].toLowerCase())) {
					players.add(p.getName());
				}
			}
			
			return players;
		} else if (args.getArgs().length == 2) {
			ArrayList<String> grupos = new ArrayList<>();
			for (Group group : Group.values()) {
				if (group.toString().toLowerCase().startsWith(args.getArgs()[1].toLowerCase())) {
					grupos.add(group.toString());
				}
			}
			return grupos;
		}
		
		return new ArrayList<>();
	}
}