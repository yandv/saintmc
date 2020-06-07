package tk.yallandev.saintmc.bukkit.listener.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.player.FakePlayerAPI;
import tk.yallandev.saintmc.bukkit.listener.Listener;
import tk.yallandev.saintmc.common.account.Member;

public class FakeListener extends Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (member.isUsingFake()) {
			FakePlayerAPI.changePlayerName(player, member.getFakeName());
			member.sendMessage("§a§l> §fVocê está usando o fake §a" + member.getFakeName() + "§f!");
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				WrappedSignedProperty property = BukkitMain.getInstance().getSkinManager()
						.getSkin(member.getPlayerName());

				if (property != null) {
					new BukkitRunnable() {

						@Override
						public void run() {
							FakePlayerAPI.changePlayerSkin(player, property, true);

							if (member.getSessionTime() <= 5000)
								member.sendMessage("§a§l> §fSkin alterada com sucesso!");
						}

					}.runTask(getMain());
				}
			}
		}.runTaskAsynchronously(getMain());
	}

}
