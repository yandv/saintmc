package tk.yallandev.saintmc.bukkit.listener.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.player.FakePlayerAPI;
import tk.yallandev.saintmc.bukkit.listener.Listener;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

public class FakeListener extends Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (member.isUsingFake()) {
			FakePlayerAPI.changePlayerName(player, member.getFakeName());
			member.sendMessage("§aVocê está usando o fake " + member.getFakeName() + "!");
		}
		
		getMain().getSkinManager().getSkin(member.getPlayerName(), new FutureCallback<JsonElement>() {
			
			@Override
			public void result(JsonElement result, Throwable error) {
				if (error != null) {
					member.sendMessage("§cNão foi possível carregar sua skin!");
					return;
				}
				
				JsonObject jsonObject = result.getAsJsonObject();
				
				WrappedSignedProperty proper = null;
				
				if (jsonObject != null) {
					if (jsonObject.has("properties")) {
						JsonArray jsonArray = jsonObject.get("properties").getAsJsonArray();

						for (int x = 0; x < jsonArray.size(); x++) {
							JsonObject json = (JsonObject) jsonArray.get(x);

							if (json.get("name").getAsString().equalsIgnoreCase("textures")) {
								proper = new WrappedSignedProperty(json.get("name").getAsString(),
										json.get("value").getAsString(), json.get("signature").getAsString());
							}
						}
					}
				}
				
				if (proper == null)
					return;
				
				if (member.getSessionTime() <= 5000) {
					member.sendMessage("§aSua skin foi alterada!");
					
					final WrappedSignedProperty property = proper;
					
					new BukkitRunnable() {
						
						@Override
						public void run() {
							
							FakePlayerAPI.changePlayerSkin(player, property, true);							
						}
					}.runTask(BukkitMain.getInstance());
				}
			}
		});
	}

}
