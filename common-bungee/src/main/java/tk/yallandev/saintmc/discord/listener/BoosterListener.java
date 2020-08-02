package tk.yallandev.saintmc.discord.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.account.medal.Medal;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.tag.Tag;

public class BoosterListener extends ListenerAdapter {

	@Override
	public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent event) {
		User user = event.getUser();

		if (event.getNewTimeBoosted() != null) {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(user.getIdLong());

			if (member == null) {
				MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(user.getIdLong());

				if (memberModel == null)
					return;

				member = new MemberVoid(memberModel);
			}

			member.getRanks().put(RankType.DONATOR, System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
			member.saveRanks();
			member.addMedal(Medal.BOOSTER);
			member.sendMessage("§a§l> §fObrigado por ajudar o discord doando §d§lBOOST§f!");
			member.sendMessage("§a§l> §fVocê recebeu a tag " + Tag.DONATOR.getPrefix() + "§f!");
		}
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		User user = event.getUser();

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(user.getIdLong());

		if (member == null) {
			MemberModel memberModel = CommonGeneral.getInstance().getPlayerData().loadMember(user.getIdLong());

			if (memberModel == null) {
				return;
			}

			member = new MemberVoid(memberModel);
		}

		if (member.getRanks().containsKey(RankType.DONATOR)) {
			member.getRanks().remove(RankType.DONATOR);
			member.saveRanks();
			member.sendMessage("§a§l> §fVocê perdeu seu grupo " + Tag.DONATOR.getPrefix() + "§f por sair do discord!");
		}

		member.setDiscordId(0l, "");
	}

}
