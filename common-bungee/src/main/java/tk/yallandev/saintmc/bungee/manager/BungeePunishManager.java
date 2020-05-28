package tk.yallandev.saintmc.bungee.manager;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.ban.constructor.Ban.UnbanReason;
import tk.yallandev.saintmc.common.ban.constructor.Mute;
import tk.yallandev.saintmc.common.ban.constructor.Warn;
import tk.yallandev.saintmc.common.controller.PunishManager;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class BungeePunishManager implements PunishManager {

	private Cache<String, Entry<UUID, Ban>> banCache;

	public BungeePunishManager() {
		banCache = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.HOURS)
				.build(new CacheLoader<String, Entry<UUID, Ban>>() {
					@Override
					public Entry<UUID, Ban> load(String name) throws Exception {
						return null;
					}
				});
	}

	@Override
	public boolean ban(Member member, Ban ban) {
		Ban activeBan = member.getPunishmentHistory().getActiveBan();

		if (activeBan != null && !activeBan.isPermanent() && !ban.isPermanent())
			return false;

		member.getPunishmentHistory().ban(ban);

		CommonGeneral.getInstance().getMemberManager().getMembers().forEach(m -> {
			if (m.hasGroupPermission(Group.TRIAL)) {
				if (ban.isPermanent()) {
					m.sendMessage(" §4* §cO jogador " + member.getPlayerName() + " foi banido pelo "
							+ ban.getBannedBy() + " por " + ban.getReason() + "!");
				} else {
					TextComponent textComponent = new TextComponent(
							" §4* §cO jogador " + member.getPlayerName() + " foi banido temporariamente pelo "
									+ ban.getBannedBy() + " por " + ban.getReason() + "!");

					textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent
							.fromLegacyText("§fTempo da punição: §c" + DateUtils.getTime(ban.getBanExpire()))));

					m.sendMessage(textComponent);
				}
			} else {
				m.sendMessage("");
				m.sendMessage(" §4* §cO jogador " + member.getPlayerName() + " foi banido do servidor!");
				m.sendMessage("");
			}
		});

//		if (ban.isPermanent())
//			if (member.getLastIpAddress() != null)
//				banCache.put(member.getLastIpAddress(),
//						new AbstractMap.SimpleEntry<UUID, Ban>(member.getUniqueId(), ban));

		ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(member.getUniqueId());

		if (proxiedPlayer != null)
			proxiedPlayer.disconnect(TextComponent.fromLegacyText(getBanMessage(ban)));

		Report report = CommonGeneral.getInstance().getReportManager().getReport(member.getUniqueId());

		if (report != null)
			report.banPlayer();

		CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");
//		MessageUtils.sendMessage("", new EmbedBuilder().build(), true);
		return true;
	}

	@Override
	public boolean mute(Member member, Mute mute) {
		Mute activeMute = member.getPunishmentHistory().getActiveMute();

		if (activeMute != null && !activeMute.isPermanent() && !mute.isPermanent())
			return false;

		member.getPunishmentHistory().mute(mute);

		CommonGeneral.getInstance().getMemberManager().getMembers().stream().forEach(m -> {
			if (m.hasGroupPermission(Group.TRIAL)) {

				if (mute.isPermanent()) {
					m.sendMessage(" §4* §cO jogador " + member.getPlayerName() + " foi mutado pelo " + mute.getMutedBy()
							+ " por " + mute.getReason() + "!");
				} else {
					TextComponent textComponent = new TextComponent(
							" §4* §cO jogador " + member.getPlayerName() + " foi mutado temporariamente pelo "
									+ mute.getMutedBy() + " por " + mute.getReason() + "!");

					textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent
							.fromLegacyText("§fTempo da punição: §c" + DateUtils.getTime(mute.getMuteExpire()))));

					m.sendMessage(textComponent);
				}
			} else {
				m.sendMessage("");
				m.sendMessage(" §4* §cO jogador " + member.getPlayerName() + " foi mutado do servidor!");
				m.sendMessage("");
			}
		});

		Report report = CommonGeneral.getInstance().getReportManager().getReport(member.getUniqueId());

		if (report != null)
			report.mutePlayer();
		
		CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");
		return true;
	}

	@Override
	public boolean warn(Member member, Warn warn) {
		List<Warn> list = member.getPunishmentHistory().getWarnList().stream().filter(w -> !w.hasExpired())
				.collect(Collectors.toList());

		if (list.size() >= 3) {
			Ban ban = new Ban(member.getUniqueId(), warn.getWarnedBy(), warn.getWarnedByUuid(), "AUTOBAN: Excesso de avisos (3/3)",
					System.currentTimeMillis() + (1000 * 60 * 60 * 6));

			member.getPunishmentHistory().warn(warn);
			return ban(member, ban);
		}

		member.getPunishmentHistory().warn(warn);
		CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");

		CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(m -> m.hasGroupPermission(Group.TRIAL)).forEach(m -> {

					TextComponent textComponent = new TextComponent(
							" §4* §cO jogador §c" + member.getPlayerName() + " foi avisado pelo "
									+ warn.getWarnedBy() + " por " + warn.getReason() + "!");

					textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent
							.fromLegacyText("§fTempo da punição: §c" + DateUtils.getTime(warn.getWarnExpire()))));

					m.sendMessage(textComponent);
				});

		member.setReputation(member.getReputation() - 3);
		member.sendMessage("§c* Você recebeu um aviso por: " + warn.getReason()
				+ ". Por isso você perdeu 3 pontos de sua reputação.");

		CommonGeneral.getInstance().getPunishData().addWarn(warn);
		return true;
	}

	@Override
	public boolean unban(Member member, UUID uniqueId, String userName, UnbanReason unbanReason) {
		Ban activeBan = member.getPunishmentHistory().getActiveBan();

		if (activeBan == null)
			return false;
		
//		if (banCache.asMap().containsKey(member.getLastIpAddress()))
//			banCache.asMap().remove(member.getLastIpAddress());
		
		activeBan.unban(uniqueId, userName, unbanReason);
		CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(m -> m.hasGroupPermission(Group.MODPLUS)).forEach(m -> {
					m.sendMessage("");
					m.sendMessage(" §4* §cO jogador " + member.getPlayerName() + " foi desbanido pelo " + userName
							+ " do servidor!");
					m.sendMessage("");
				});
		CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");

		return true;
	}

	@Override
	public boolean unmute(Member member, UUID uniqueId, String userName) {
		Mute activeMute = member.getPunishmentHistory().getActiveMute();

		if (activeMute == null)
			return false;

		activeMute.unmute(uniqueId, userName);
		CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(m -> m.hasGroupPermission(Group.MODPLUS)).forEach(m -> {
					m.sendMessage("");
					m.sendMessage(" §4* §cO jogador " + member.getPlayerName() + " foi desmutado pelo " + userName
							+ " do servidor!");
					m.sendMessage("");
				});
		CommonGeneral.getInstance().getPlayerData().updateMember(member, "punishmentHistory");

		return true;
	}

	@Override
	public boolean isIpBanned(String ipAddress) {
		return banCache.asMap().containsKey(ipAddress);
	}

	@Override
	public String getBanMessage(Ban ban) {
		return "§4§l" + CommonConst.KICK_PREFIX + "\n§f\n§cSua conta foi suspensa "
				+ (ban.isPermanent() ? "permanentemente" : "temporariamente") + " do servidor!\n§f\n§fBanido por: §e"
				+ ban.getBannedBy() + "\n§fMotivo: §e" + ban.getReason()
				+ (ban.isPermanent() ? "" : "\n§fTempo: §e" + DateUtils.getTime(ban.getBanExpire()))
				+ "\n§f\n§6Acesse nosso discord para pedir appeal:\n§b" + CommonConst.DISCORD;
	}

	@Override
	public String getMuteMessage(Mute mute) {
		return " §4§l> §fVocê está mutado " + (mute.isPermanent() ? "permanentemente" : "temporariamente")
				+ " do servidor!"
				+ (mute.isPermanent() ? "" : "\n §4§l> §fExpira em §e" + DateUtils.getTime(mute.getMuteExpire()));
	}

}
