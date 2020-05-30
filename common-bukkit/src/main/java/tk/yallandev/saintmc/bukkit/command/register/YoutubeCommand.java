package tk.yallandev.saintmc.bukkit.command.register;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.player.FakePlayerAPI;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.bukkit.scoreboard.ScoreboardAPI;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class YoutubeCommand implements CommandClass {

	@Command(name = "fake", groupToUse = Group.YOUTUBER, runAsync = true)
	public void fakeCommand(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;

		Player player = args.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (args.getArgs().length != 1) {
			player.sendMessage(
					" §e* §fUse §a/fake <player>§f para trocar de nick! §7(Cooldown de 1 minuto para trocar de fake)");
			return;
		}

		String playerName = args.getArgs()[0].equals("#") ? member.getPlayerName() : args.getArgs()[0];

		if (!FakePlayerAPI.validateName(playerName)) {
			player.sendMessage(" §c* §fO nickname que você colocou está inválido!");
			return;
		}

		if (member.isOnCooldown("fakeCommand") && !playerName.equals("#")
				&& !playerName.equals(member.getPlayerName())) {
			member.sendMessage(" §c* §fVocê precisa esperar §e" + DateUtils.getTime(member.getCooldown("fakeCommand"))
					+ "§f para trocar de fake novamente!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getMojangFetcher().requestUuid(playerName);

		if (!playerName.equals(member.getPlayerName()) && !member.hasGroupPermission(Group.DIRETOR)) {
			if (uuid != null) {
				player.sendMessage(" §c* §fO jogador existe na mojang!");
				return;
			}
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				if (member.getPlayerName().equals(playerName)) {
					ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(player);

					WrappedSignedProperty property = BukkitMain.getInstance().getSkinManager().getSkin(playerName);

					if (property == null)
						FakePlayerAPI.changePlayerSkin(player, playerName, member.getUniqueId(), false);
					else
						FakePlayerAPI.changePlayerSkin(player, property, false);

					FakePlayerAPI.changePlayerName(player, playerName);
					member.setTag(member.getTag());
					member.setFakeName(member.getPlayerName());
					player.sendMessage(" §a* §fO seu fake foi removido");
				} else {
					ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(player);
					FakePlayerAPI.changePlayerName(player, playerName);
					FakePlayerAPI.removePlayerSkin(player);
					member.setTag(member.getTag());
					member.setFakeName(playerName);
					player.sendMessage(" §a* §fSeu fake foi alterado para §a" + playerName + "§f!");
					player.sendMessage(" §a* §fUse §a/fake #§f para remover seu fake!");
				}
				member.setCooldown("fakeCommand",
						member.hasGroupPermission(Group.GERENTE) ? System.currentTimeMillis() + (1000 * 60 * 1)
								: System.currentTimeMillis() + (1000 * 60 * 5));
			}

		}.runTask(BukkitMain.getInstance());
	}

	@Command(name = "changeskin", aliases = { "skin" }, groupToUse = Group.LIGHT, runAsync = true)
	public void changeskinCommand(BukkitCommandArgs args) {
		if (!args.isPlayer()) {
			return;
		}

		Player p = args.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

		if (member.isOnCooldown("changeskinCommand")) {
			member.sendMessage(" §c* §fVocê precisa esperar §e"
					+ DateUtils.getTime(member.getCooldown("changeskinCommand")) + "§f para trocar de skin novamente!");
			return;
		}

		if (args.getArgs().length != 1) {
			p.sendMessage(" §e* §fUse §a/changeskin <player>§f para trocar de skin!");
			return;
		}

		String playerName = args.getArgs()[0];

		if (!FakePlayerAPI.validateName(playerName)) {
			p.sendMessage(" §c* §fO nickname que você colocou está inválido!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getMojangFetcher().requestUuid(playerName);

		if (uuid == null) {
			p.sendMessage(" §c* §fO jogador não existe!");
			return;
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				WrappedSignedProperty property = FakePlayerAPI.changePlayerSkin(p, playerName, uuid, true);
				p.sendMessage(" §a* §fSua skin foi alterada para a do §a" + playerName + "§f!");

				if (playerName.equals("#") || member.getPlayerName().equals(playerName))
					BukkitMain.getInstance().getSkinManager().deleteSkin(member);
				else
					BukkitMain.getInstance().getSkinManager().saveSkin(member, property);

				member.setCooldown("changeskinCommand",
						member.hasGroupPermission(Group.TRIAL) ? System.currentTimeMillis() + (1000 * 60 * 2)
								: member.hasGroupPermission(Group.SAINT) ? System.currentTimeMillis() + (1000 * 60 * 10)
										: System.currentTimeMillis() + (1000 * 60 * 30));
			}

		}.runTask(BukkitMain.getInstance());
	}
}
