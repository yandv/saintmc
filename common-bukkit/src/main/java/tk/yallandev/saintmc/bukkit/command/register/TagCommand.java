package tk.yallandev.saintmc.bukkit.command.register;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

public class TagCommand implements CommandClass {

	@Command(name = "tag", runAsync = true)
	public void tag(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player player = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(player.getUniqueId());

		if (!BukkitMain.getInstance().isTagControl()) {
			player.sendMessage(" §a* §fO comando não está ativado nesse servidor!");
			return;
		}

		if (args.length == 0) {
			TextComponent message = new TextComponent(" §a* §fTags disponíveis: ");

			int max = member.getTags().size() * 2;
			int i = max - 1;

			for (Tag t : member.getTags()) {
				if (i < max - 1) {
					message.addExtra(new TextComponent("§f, "));
					i -= 1;
				}

				message.addExtra(new MessageBuilder((t == Tag.MEMBRO) ? "§7§lMEMBRO" : t.getPrefix())
						.setHoverEvent(new HoverEvent(Action.SHOW_TEXT,
								new TextComponent[] { new TextComponent("§fClique para selecionar a tag!") }))
						.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
								"/tag " + t.getName()))
						.create());
				i -= 1;
			}

			player.spigot().sendMessage(message);
			return;
		}
		
		if (args[0].equalsIgnoreCase("chroma")) {
			if (member.hasGroupPermission(Group.ADMIN)) {
				member.setChroma(!member.isChroma());
				member.setTag(member.getTag());
				player.sendMessage(
						" §a* §fO modo chroma foi " + (member.isChroma() ? "§aativado" : "§cdesativado") + "§f!");
				return;
			}
		}
		
		if (args[0].equalsIgnoreCase("default")) {
			if (member.setTag(member.getDefaultTag())) {
				player.sendMessage(" §a* §fVocê voltou para sua tag padrão!");
			}
			return;
		}

		Tag tag = Tag.getByName(args[0]);

		if (tag == null) {
			player.sendMessage(" §c* §fA tag §a\"" + args[0] + "\"§f não existe!");
			return;
		}

		if (member.hasTag(tag)) {
			if (!member.getTag().equals(tag)) {
				if (member.setTag(tag)) {
					player.sendMessage(" §a* §fVocê selecionou a tag "
							+ ((tag == Tag.MEMBRO) ? "§7§lMEMBRO" : tag.getPrefix()) + "§f!");
				}
			} else {
				player.sendMessage(" §c* §fVocê já está usando essa tag!");
			}
		} else {
			player.sendMessage(" §c* §fVocê não tem §cpermissão§f para essa tag!");
		}
	}

}
