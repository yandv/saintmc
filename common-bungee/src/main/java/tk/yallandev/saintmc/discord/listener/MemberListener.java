package tk.yallandev.saintmc.discord.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tk.yallandev.saintmc.discord.DiscordMain;
import tk.yallandev.saintmc.discord.guild.GuildConfiguration;

public class MemberListener extends ListenerAdapter {
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Member member = event.getMember();
		Guild guild = event.getGuild();
		GuildConfiguration guildConfiguration = DiscordMain.getInstance().getGuildManager().getGuild(guild.getIdLong());
		
		if (!guildConfiguration.getRoleMap().containsKey("membro"))
			return;
		
		long roleId = guildConfiguration.getRoleMap().get("membro");
		Role role = guild.getRoleById(roleId);
		
		guild.addRoleToMember(member, role).complete();
	}
}
