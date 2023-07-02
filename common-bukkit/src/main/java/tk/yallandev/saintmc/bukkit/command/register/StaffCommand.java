package tk.yallandev.saintmc.bukkit.command.register;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.server.chat.ChatState;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;

public class StaffCommand implements CommandClass {

	@Command(name = "say", groupToUse = Group.TRIAL, usage = "/<command> <mesage>")
	public void broadcastCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§eUso /" + cmdArgs.getLabel() + " <mensagem> para mandar uma mensagem para todos.");
			return;
		}

		String msg = "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++)
			sb.append(args[i]).append(" ");
		msg = sb.toString();

		Bukkit.broadcastMessage("§6§lPENTAMC §e> §f" + msg.replace("&", "§"));
	}

	@Command(name = "setspawn", groupToUse = Group.TRIAL)
	public void setspawnCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = ((BukkitMember) cmdArgs.getSender()).getPlayer();

		if (CommonGeneral.getInstance().getServerType() == ServerType.HUNGERGAMES)
			if (!Member.hasGroupPermission(p.getUniqueId(), Group.MODPLUS)
					&& !Member.isGroup(p.getUniqueId(), Group.TRIAL)) {
				p.sendMessage(" §c* §fVocê não tem §cpermissão§f para executar esse comando!");
				return;
			} else if (!Member.hasGroupPermission(p.getUniqueId(), Group.ADMIN)
					&& !Member.isGroup(p.getUniqueId(), Group.TRIAL)) {
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

	@Command(name = "admin", aliases = { "adm" }, groupToUse = Group.TRIAL)
	public void admin(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = ((BukkitMember) cmdArgs.getSender()).getPlayer();
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
	public void updatevanish(CommandArgs args) {
		if (args.isPlayer()) {
			VanishAPI.getInstance().updateVanishToPlayer(((BukkitMember) args.getSender()).getPlayer());
		}
	}

	@Command(name = "visible", aliases = { "vis", "visivel" }, groupToUse = Group.TRIAL)
	public void visibleCommand(CommandArgs args) {
		if (!args.isPlayer())
			return;

		Player p = ((BukkitMember) args.getSender()).getPlayer();
		VanishAPI.getInstance().showPlayer(p);
		p.sendMessage("\n §a* §fVocê está visível para todos os jogadores!\n§f");
	}

	@Command(name = "invisible", aliases = { "invis", "invisivel" }, groupToUse = Group.TRIAL)
	public void invisibleCommand(CommandArgs args) {
		if (!args.isPlayer()) {
			return;
		}

		Player p = ((BukkitMember) args.getSender()).getPlayer();
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
	public void inventorysee(CommandArgs args) {
		if (!args.isPlayer())
			return;

		Player p = ((BukkitMember) args.getSender()).getPlayer();

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
		staffLog("O §a" + p.getName() + " §fabriu o inventário de §e" + t.getName() + "§f!", Group.MODPLUS);
	}

	@Command(name = "chat", groupToUse = Group.MOD)
	public void chatCommand(CommandArgs args) {
		CommandSender sender = args.getSender();

		if (args.getArgs().length == 0) {
			sender.sendMessage(" §e* §fUse §a/chat <on:off>§f para ativar ou desativar o chat!");
			return;
		}

		if (args.getArgs()[0].equalsIgnoreCase("on")) {
			if (BukkitMain.getInstance().getServerConfig().getChatState().isEnabled()) {
				sender.sendMessage(" §c* §fO chat já está §aativado§f!");
				return;
			}

			BukkitMain.getInstance().getServerConfig().setChatState(ChatState.ENABLED);
			sender.sendMessage(" §a* §fO chat está disponível para todos!");
			CommonGeneral.getInstance().getMemberManager().broadcast("§7" + sender.getName() + " ativou o chat!",
					Group.TRIAL);
		} else if (args.getArgs()[0].equalsIgnoreCase("off")) {
			if (!BukkitMain.getInstance().getServerConfig().getChatState().isEnabled()) {
				sender.sendMessage(" §c* §fO chat já está §cdesativado§f!");
				return;
			}

			BukkitMain.getInstance().getServerConfig()
					.setChatState(CommonGeneral.getInstance().getServerType() == ServerType.LOBBY ? ChatState.YOUTUBER
							: ChatState.STAFF);
			sender.sendMessage(" §a* §fO chat agora está disponível somente para §a"
					+ BukkitMain.getInstance().getServerConfig().getChatState().name() + "§f!");
			CommonGeneral.getInstance().getMemberManager().broadcast("§7" + sender.getName() + " desativou o chat!",
					Group.TRIAL);
		} else {
			ChatState chatState = null;

			try {
				chatState = ChatState.valueOf(args.getArgs()[0].toUpperCase());
			} catch (Exception ex) {
			}

			if (chatState == null) {
				sender.sendMessage(" §e* §fUse §a/chat <on:off>§f para ativar ou desativar o chat!");
				return;
			}

			BukkitMain.getInstance().getServerConfig().setChatState(chatState);
			sender.sendMessage(chatState == ChatState.ENABLED ? " §a* §fO chat está disponível para todos!"
					: " §a* §fO chat agora está disponível somente para §a"
							+ BukkitMain.getInstance().getServerConfig().getChatState().getAvailableTo() + "§f!");
			return;
		}
	}

	@Command(name = "skull", groupToUse = Group.MODPLUS)
	public void eventoCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		BukkitMember sender = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(cmdArgs.getSender().getUniqueId());

		if (cmdArgs.getArgs().length == 0) {
			sender.sendMessage(" §e* §fUse §a/skull <playerName>§f para receber a cabeça!");
			return;
		}

		sender.getPlayer().getInventory().addItem(new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
				.name("§a" + cmdArgs.getArgs()[0]).skin(cmdArgs.getArgs()[0]).build());
		sender.sendMessage("§aHead of " + sender.getName());
	}

	@Command(name = "clearchat", aliases = { "limparchat", "cc" }, groupToUse = Group.TRIAL)
	public void clearchat(CommandArgs args) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (int i = 0; i < 100; i++)
				p.sendMessage("");

			p.sendMessage(" §8* §fO chat foi limpo!\n§f");
		}

		staffLog("O §a" + args.getSender().getName() + " §flimpou o chat§f!", Group.MODPLUS);
	}

}
