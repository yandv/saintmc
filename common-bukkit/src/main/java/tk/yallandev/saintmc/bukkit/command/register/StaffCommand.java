package tk.yallandev.saintmc.bukkit.command.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.chat.ChatAPI;
import tk.yallandev.saintmc.bukkit.api.chat.ChatAPI.ChatState;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;

public class StaffCommand implements CommandClass {

	@Command(name = "setspawn", groupToUse = Group.BUILDER)
	public void setspawnCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = cmdArgs.getPlayer();

		if (!Member.hasGroupPermission(p.getUniqueId(), Group.GERENTE)
				&& !Member.isGroup(p.getUniqueId(), Group.BUILDER)) {
			p.sendMessage(" §c* §fVocê não tem §cpermissão§f para executar esse comando!");
			return;
		}

		String[] a = cmdArgs.getArgs();

		if (a.length == 0) {
			p.sendMessage(" §e* §fUse §a/setwarp <warpName>§f para setar uma warp.");
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int x = 0; x < a.length; x++) {
			stringBuilder.append(a[x]).append(" ");
		}

		String configName = a[0];
		BukkitMain.getInstance().registerLocationInConfig(p.getLocation(), configName);
		p.sendMessage(" §a* §fVocê setou a warp §a" + configName + "§f!");
	}

	@Command(name = "admin", groupToUse = Group.TRIAL)
	public void admin(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (args.length != 0) {
			if (args[0].equalsIgnoreCase("items")) {
				member.getAccountConfiguration().setAdminItems(!member.getAccountConfiguration().isAdminItems());
				member.sendMessage("§9§l> §fOs items do admin foram "
						+ (member.getAccountConfiguration().isAdminItems() ? "§aativado" : "§cdesativado§f!"));
				
				if (AdminMode.getInstance().isAdmin(p)) {
					AdminMode.getInstance().setPlayer(p, member);
					new BukkitRunnable() {
						
						@Override
						public void run() {
							AdminMode.getInstance().setAdmin(p, member);
						}
					}.runTaskLater(BukkitMain.getInstance(), 5l);
				}
				
				return;
			} else if (args[0].equalsIgnoreCase("join")) {
				member.getAccountConfiguration().setAdminOnJoin(!member.getAccountConfiguration().isAdminOnJoin());
				member.sendMessage("§9§l> §fO entrar no admin ao mudar de servidor foi "
						+ (member.getAccountConfiguration().isAdminOnJoin() ? "§aativado" : "§cdesativado§f!"));
				
				if (AdminMode.getInstance().isAdmin(p)) {
					AdminMode.getInstance().setPlayer(p, member);
					new BukkitRunnable() {
						
						@Override
						public void run() {
							AdminMode.getInstance().setAdmin(p, member);
						}
					}.runTaskLater(BukkitMain.getInstance(), 5l);
				}
				
				return;
			}
		}

		if (AdminMode.getInstance().isAdmin(p)) {
			AdminMode.getInstance().setPlayer(p, member);
		} else {
			AdminMode.getInstance().setAdmin(p, member);
		}
	}

	@Command(name = "updatevanish", groupToUse = Group.TRIAL)
	public void updatevanish(BukkitCommandArgs args) {
		if (args.isPlayer()) {
			Player p = args.getPlayer();
			VanishAPI.getInstance().updateVanishToPlayer(p);
		}
	}

	@Command(name = "visible", aliases = { "vis", "visivel" }, groupToUse = Group.TRIAL)
	public void visible(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;

		Player p = args.getPlayer();
		VanishAPI.getInstance().showPlayer(p);
		p.sendMessage("\n §a* §fVocê está visível para todos os jogadores!\n§f");
	}

	@Command(name = "invisible", aliases = { "invis", "invisivel" }, groupToUse = Group.TRIAL)
	public void invisible(BukkitCommandArgs args) {
		if (!args.isPlayer()) {
			return;
		}

		Player p = args.getPlayer();
		Member bP = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());
		Group group = Group.MEMBRO;

		if (args.getArgs().length > 0) {
			try {
				group = Group.valueOf(args.getArgs()[0].toUpperCase());
			} catch (Exception e) {
				p.sendMessage(" §c* §fO grupo §a" + group.name() + "§f não existe!");
				return;
			}
			if (group.ordinal() >= bP.getServerGroup().ordinal()) {
				p.sendMessage(" §c* §fO grupo §a" + group.name() + "§f não está disponível para você!");
				return;
			}
		} else
			group = VanishAPI.getInstance().hidePlayer(p);

		VanishAPI.getInstance().setPlayerVanishToGroup(p, group);
		p.sendMessage("\n §a* §fVocê está invisivel para §a" + group.toString() + " e inferiores§f!\n§f");
	}

	@Command(name = "inventorysee", aliases = { "invsee", "inv" }, groupToUse = Group.TRIAL)
	public void inventorysee(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;

		Player p = args.getPlayer();

		if (args.getArgs().length == 0) {
			p.sendMessage(" §e* §fUse §a/" + args.getLabel() + " <player>§f para abrir o inventário de alguém!");
			return;
		}

		Player t = Bukkit.getPlayer(args.getArgs()[0]);

		if (t == null) {
			p.sendMessage(" §c* §fO jogador §a\"" + args.getArgs()[0] + "\"§f não existe!");
			return;
		}

		p.sendMessage(" §a* §fVocê abriu o inventário de §a" + t.getName() + "§f.");
		p.openInventory(t.getInventory());
	}

	@Command(name = "chat", groupToUse = Group.MOD)
	public void chat(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;

		if (args.getArgs().length == 0) {
			args.getPlayer().sendMessage(" §e* §fUse §a/chat <on:off>§f para ativar ou desativar o chat!");
			return;
		}

		if (args.getArgs()[0].equalsIgnoreCase("on")) {
			if (ChatAPI.getInstance().getChatState() == ChatState.ENABLED) {
				args.getPlayer().sendMessage(" §c* §fO chat já está §aativado§f!");
				return;
			}
			ChatAPI.getInstance().setChatState(ChatState.ENABLED);
			args.getPlayer().sendMessage(" §a* §fVocê §aativou§f o chat!");
		} else if (args.getArgs()[0].equalsIgnoreCase("off")) {
			if (ChatAPI.getInstance().getChatState() == ChatState.YOUTUBER) {
				args.getPlayer().sendMessage(" §c* §fO chat já está §cdesativado§f!");
				return;
			}
			ChatAPI.getInstance().setChatState(ChatState.YOUTUBER);
			args.getPlayer().sendMessage(" §a* §fVocê §cdesativou§f o chat!");
		} else {
			args.getPlayer().sendMessage(" §e* §fUse §a/chat <on:off>§f para ativar ou desativar o chat!");
		}
	}

	@Command(name = "clearchat", aliases = { "limparchat", "cc" }, groupToUse = Group.TRIAL)
	public void clearchat(BukkitCommandArgs args) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (int i = 0; i < 100; i++)
				p.sendMessage("");

			p.sendMessage(" §8* §fO chat foi limpo!\n§f");
		}
	}

	@Command(name = "tell", usage = "/<command> <message>", aliases = { "msg" })
	public void tell(BukkitCommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();
		if (!cmdArgs.isPlayer()) {
			sender.sendMessage(" §c §fVocê precisa ser um §ajogador §fpara executar este comando!");
			return;
		}
		if (args.length == 0) {
			sender.sendMessage(" §e* §fVocê deve utilizar §a/" + cmdArgs.getLabel()
					+ " <mensagem>§f, para enviar uma mensagem privada!");
		} else {

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("on")) {

					Member member = CommonGeneral.getInstance().getMemberManager()
							.getMember(cmdArgs.getPlayer().getUniqueId());

					if (member.getAccountConfiguration().isTellEnabled()) {
						sender.sendMessage(" §c* §fSeu tell já está §aativado§f!");
						return;
					}

					member.getAccountConfiguration().setTellEnabled(!member.getAccountConfiguration().isTellEnabled());
					sender.sendMessage(" §a* §fVocê §aativou fo seu tell!");
				} else if (args[0].equalsIgnoreCase("off")) {

					Member member = CommonGeneral.getInstance().getMemberManager()
							.getMember(cmdArgs.getPlayer().getUniqueId());

					if (!member.getAccountConfiguration().isTellEnabled()) {
						sender.sendMessage(" §c* §fSeu tell já está §cdesativado§f!");
						return;
					}

					member.getAccountConfiguration().setTellEnabled(!member.getAccountConfiguration().isTellEnabled());
					sender.sendMessage(" §a* §fVocê §cdesativou fo seu tell!");
				} else {
					sender.sendMessage(" §e* §fVocê deve utilizar §a/" + cmdArgs.getLabel()
							+ " <mensagem>§f, para enviar uma mensagem privada!");
				}
				return;

			}

			Player target = Bukkit.getPlayer(args[0]);

			if (target == null) {
				sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f está offline!");
				return;
			}

			String msg = "";

			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < args.length; i++)
				sb.append(args[i]).append(" ");
			msg = sb.toString();

			sender.sendMessage("§7[§e" + cmdArgs.getPlayer().getName() + " §7» §e" + target.getName() + "§7] §f" + msg);
			target.sendMessage("§7[§e" + cmdArgs.getPlayer().getName() + " §7» §e" + target.getName() + "§7] §f" + msg);

		}
	}

}
