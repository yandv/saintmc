package tk.yallandev.saintmc.bukkit.command.register;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.menu.account.AccountInventory;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;

public class AccountCommand implements CommandClass {

	@Command(name = "account", aliases = { "acc", "info" })
	public void accountCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player sender = cmdArgs.getPlayer();
		String[] args = cmdArgs.getArgs();

		Member player;

		if (args.length == 0) {
			player = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());
		} else {
			UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

			if (uuid == null) {
				sender.sendMessage(" §c* §fO jogador §a" + args[0] + "§f não existe!");
				return;
			}

			player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

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
		}

		new AccountInventory(sender, player);
	}

	@Command(name = "scoreboard", aliases = { "score" })
	public void scoreCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

//		Player sender = cmdArgs.getPlayer();
//		String[] args = cmdArgs.getArgs();
	}

	@Command(name = "rank", aliases = { "ranks", "liga", "ligas" })
	public void rankCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player p = cmdArgs.getPlayer();
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		List<League> leagues = Arrays.asList(League.values());
		Collections.reverse(leagues);

		for (League league : leagues) {
			if (player.getLeague() == league) {
				TextComponent text = new TextComponent(league.getColor() + league.getSymbol() + " " + league.name());

				text.setHoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aSeu rank!")));

				player.sendMessage(text);
			} else {
				p.sendMessage(league.getColor() + league.getSymbol() + " " + league.name());
			}
		}

		p.sendMessage("");
		p.sendMessage("§a§l> §fSeu rank atual é " + player.getLeague().getColor() + player.getLeague().getSymbol() + " "
				+ player.getLeague().getName());
		p.sendMessage("§a§l> §fSeu xp §e" + player.getXp());

		if (player.getLeague() == League.CHALLENGER) {
			p.sendMessage("");
			p.sendMessage("§a§l> §fVocê está no maior rank do servidor");
			p.sendMessage("§a§l> §fContinue ganhando XP para ficar no topo do ranking");
		} else {
			p.sendMessage("");
			p.sendMessage("§a§l> §fPróximo rank §e" + player.getLeague().getNextLeague().getColor()
					+ player.getLeague().getNextLeague().getSymbol() + " "
					+ player.getLeague().getNextLeague().getName());
			p.sendMessage(
					"§a§l> §fXP necessário para o próximo rank §e" + (player.getLeague().getMaxXp() - player.getXp()));
		}
	}

}
