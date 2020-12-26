package tk.yallandev.saintmc.bukkit.listener.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.player.PlayerAPI;
import tk.yallandev.saintmc.bukkit.api.player.TextureFetcher;
import tk.yallandev.saintmc.bukkit.listener.Listener;
import tk.yallandev.saintmc.common.account.Member;

public class FakeListener extends Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (member.hasSkin())
			new BukkitRunnable() {

				@Override
				public void run() {
					if (player.isOnline()) {
						if (member.isUsingFake())
							new BukkitRunnable() {

								@Override
								public void run() {
									PlayerAPI.changePlayerName(player, member.getFakeName());
								}
							}.runTask(BukkitMain.getInstance());

						if (member.hasSkin()) {
							WrappedSignedProperty property = TextureFetcher.loadTexture(new WrappedGameProfile(
									member.getSkinProfile().getUniqueId(), member.getSkinProfile().getPlayerName()));

							new BukkitRunnable() {

								@Override
								public void run() {
									PlayerAPI.changePlayerSkin(player, property, true);
								}
							}.runTask(BukkitMain.getInstance());
						}
					}
				}
			}.runTaskAsynchronously(BukkitMain.getInstance());
	}

}
