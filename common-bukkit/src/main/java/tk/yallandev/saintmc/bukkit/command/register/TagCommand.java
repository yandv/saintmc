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
import tk.yallandev.saintmc.common.tag.Tag;

public class TagCommand implements CommandClass {

	@Command(name = "tag", runAsync = true)
	public void tag(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer()) {
			cmdArgs.getSender().sendMessage("§4§lERRO §fComando disponivel apenas §c§lin-game");
			return;
		}

		Player p = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (!BukkitMain.getInstance().isTagControl()) {
			p.sendMessage(" §a* §fO comando não está ativado nesse servidor!");
			return;
		}

		if (args.length == 0) {
			int max = player.getTags().size() * 2;
			TextComponent[] message = new TextComponent[max];
			message[0] = new TextComponent(" §a* §fTags disponíveis: ");
			int i = max - 1;

			for (Tag t : player.getTags()) {
				if (i < max - 1) {
					message[i] = new TextComponent("§f, ");
					i -= 1;
				}
				
				TextComponent component = new TextComponent((t == Tag.MEMBRO) ? "§7§lMEMBRO" : t.getPrefix());
				component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT,
						new TextComponent[] { new TextComponent("§fClique para selecionar a tag!") }));
				component.setClickEvent(
						new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tag " + t.getName()));
				message[i] = component;
				i -= 1;
			}

			p.spigot().sendMessage(message);
			return;
		}
		
		Tag tag = Tag.getByName(args[0]);
		
		if (tag != null) {
			if (player.getTags().contains(tag)) {
				if (player.getTag() != tag) {
					if (player.setTag(tag)) {
						p.sendMessage(" §a* §fVocê selecionou a tag "
								+ ((tag == Tag.MEMBRO) ? "§7§lMEMBRO" : tag.getPrefix()) + "§f!");
					}
				} else {
					p.sendMessage(" §c* §fVocê já está usando essa tag!");
				}
			} else {
				p.sendMessage(" §c* §fVocê não tem §cpermissão§f para essa tag!");
			}
		} else {
			p.sendMessage(" §c* §fA tag §a\"" + args[0] + "\"§f não existe!");
		}
	}

}
