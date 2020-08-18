package tk.yallandev.saintmc.bukkit.command.register;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.player.PlayerAPI;
import tk.yallandev.saintmc.bukkit.api.player.TextureFetcher;
import tk.yallandev.saintmc.bukkit.api.scoreboard.ScoreboardAPI;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandArgs;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class YoutubeCommand implements CommandClass {

	private static final String[] FAKERANDOM = { "broowkk_", "yNegocioNegocio", "YTBERMASTER__", "KillepHG", "Foccus",
			"_Usayy_", "_Xereyy_", "_UseiDrogas_", "_ProerdHG_", "_Dollows_", "_Finalee_", "BrabaoPvP", "brouqui",
			"YanDavii", "abreuzinpvp", "Vooei", "Surfaaay", "uDeathadder", "MouseG0D", "ThePrinceHG", "LGostosoNooT",
			"GANGMEMBERXITO", "XITOCONTRAISPOPI", "MEBANIRAMNOCOGU" };

	@Command(name = "fake", groupToUse = Group.YOUTUBER, runAsync = true)
	public void fakeCommand(CommandArgs args) {
		if (!args.isPlayer())
			return;

		Player player = ((BukkitMember) args.getSender()).getPlayer();
		Member member = (Member) args.getSender();

		if (args.getArgs().length != 1) {
			player.sendMessage(
					" §e* §fUse §a/fake <player>§f para trocar de nick! §7(Cooldown de 1 minuto para trocar de fake)");
			return;
		}

		String fakeName = args.getArgs()[0].equals("#") ? member.getPlayerName()
				: args.getArgs()[0].equalsIgnoreCase("random")
						? FAKERANDOM[CommonConst.RANDOM.nextInt(FAKERANDOM.length)]
						: args.getArgs()[0];

		if (Bukkit.getPlayer(fakeName) != null) {
			if (args.getArgs()[0].equalsIgnoreCase("random"))
				while (Bukkit.getPlayer(fakeName) != null)
					fakeName = args.getArgs()[0].equals("#") ? member.getPlayerName()
							: args.getArgs()[0].equalsIgnoreCase("random")
									? FAKERANDOM[CommonConst.RANDOM.nextInt(FAKERANDOM.length)]
									: args.getArgs()[0];
			else
				player.sendMessage(" §c* §fVocê não pode usar este fake!");

			return;
		}

		if (!PlayerAPI.validateName(fakeName)) {
			player.sendMessage(" §c* §fO nickname que você colocou está inválido!");
			return;
		}

		if (!member.hasGroupPermission(Group.YOUTUBER))
			if (member.isOnCooldown("fakeCommand") && !fakeName.equals("#")
					&& !fakeName.equals(member.getPlayerName())) {
				member.sendMessage(" §c* §fVocê precisa esperar §e"
						+ DateUtils.getTime(member.getCooldown("fakeCommand")) + "§f para trocar de fake novamente!");
				return;
			}

		UUID uuid = CommonGeneral.getInstance().getMojangFetcher().requestUuid(fakeName);

		if (!fakeName.equals(member.getPlayerName()) && !member.hasGroupPermission(Group.DIRETOR)) {
			if (uuid != null) {
				player.sendMessage(" §c* §fO jogador existe na mojang!");
				return;
			}
		}

		String playerName = fakeName;
		WrappedSignedProperty property = BukkitMain.getInstance().getSkinManager().getSkin(playerName);

		new BukkitRunnable() {

			@Override
			public void run() {
				if (member.getPlayerName().equals(playerName)) {
					ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(player);

					if (property == null)
						PlayerAPI.changePlayerSkin(player, playerName, member.getUniqueId(), false);
					else
						PlayerAPI.changePlayerSkin(player, property, false);

					PlayerAPI.changePlayerName(player, playerName);
					member.setTag(member.getTag());
					member.setFakeName(member.getPlayerName());
					player.sendMessage(" §a* §fO seu fake foi removido!");
				} else {
					ScoreboardAPI.leaveCurrentTeamForOnlinePlayers(player);
					PlayerAPI.changePlayerName(player, playerName);
					PlayerAPI.removePlayerSkin(player);
					member.setTag(Tag.MEMBRO);
					member.setFakeName(playerName);
					player.sendMessage(" §a* §fSeu fake foi alterado para §a" + playerName + "§f!");
					player.sendMessage(" §a* §fUse §a/fake #§f para remover seu fake!");
					member.setCooldown("fakeCommand",
							member.hasGroupPermission(Group.GERENTE) ? System.currentTimeMillis() + (1000)
									: System.currentTimeMillis() + (1000 * 60));
				}
			}

		}.runTask(BukkitMain.getInstance());
	}

	@Command(name = "changeskin", aliases = { "skin" }, groupToUse = Group.MEMBRO, runAsync = true)
	public void changeskinCommand(BukkitCommandArgs args) {
		if (!args.isPlayer())
			return;

		Player player = args.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (args.getArgs().length != 1) {
			player.sendMessage(" §e* §fUse §a/changeskin <player>§f para trocar de skin!");
			if (member.isOnCooldown("changeskinCommand")) {
				player.sendMessage(
						" §e* §fVocê precisa esperar §c" + DateUtils.getTime(member.getCooldown("changeskinCommand"))
								+ "§f para trocar de skin novamente!");
			}
			return;
		}

		if (!member.hasGroupPermission(Group.MODPLUS))
			if (member.isOnCooldown("changeskinCommand")) {
				member.sendMessage(
						" §c* §fVocê precisa esperar §e" + DateUtils.getTime(member.getCooldown("changeskinCommand"))
								+ "§f para trocar de skin novamente!");
				return;
			}

		String playerName = args.getArgs()[0];

		if (!PlayerAPI.validateName(playerName) && !playerName.equals("#")) {
			player.sendMessage(" §c* §fO nickname que você colocou está inválido!");
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getMojangFetcher().requestUuid(playerName);

		if (uuid == null) {
			player.sendMessage(" §c* §fO jogador não existe!");
			return;
		}

		WrappedSignedProperty property = TextureFetcher.loadTexture(new WrappedGameProfile(uuid, playerName));

		new BukkitRunnable() {

			@Override
			public void run() {
				PlayerAPI.changePlayerSkin(player, property, true);
				player.sendMessage(" §a* §fSua skin foi alterada para a do §a" + playerName + "§f!");

				if (playerName.equals("#") || member.getPlayerName().equals(playerName))
					BukkitMain.getInstance().getSkinManager().deleteSkin(member);
				else
					BukkitMain.getInstance().getSkinManager().saveSkin(member, property);

				member.setCooldown("changeskinCommand",
						member.hasGroupPermission(Group.TRIAL) ? System.currentTimeMillis() + (1000 * 30)
								: member.hasGroupPermission(Group.SAINT) ? System.currentTimeMillis() + (1000 * 60 * 1)
										: member.hasGroupPermission(Group.BLIZZARD)
												? System.currentTimeMillis() + (1000 * 60 * 3)
												: System.currentTimeMillis() + (1000 * 60 * 7));
			}

		}.runTask(BukkitMain.getInstance());
	}
}
