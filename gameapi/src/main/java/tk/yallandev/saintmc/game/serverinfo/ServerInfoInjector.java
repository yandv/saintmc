package tk.yallandev.saintmc.game.serverinfo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.GameType;
import tk.yallandev.saintmc.game.stage.GameStage;

public class ServerInfoInjector {

	public static void inject(BukkitMain plugin) {
		plugin.getProcotolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO) {
	            
			@Override	
	            public void onPacketSending(PacketEvent event) {
	                PacketContainer container = event.getPacket();
	                WrappedServerPing ping = container.getServerPings().read(0);
	                
	                if (GameMain.getPlugin().getGameType() == GameType.HUNGERGAMES) {
	                	if (GameMain.getPlugin().getGameStage() == GameStage.WAITING) {
	                		ping.setMotD("§aAguardando jogadores para o inicio da partida!\n§7Tempo: §a" + StringUtils.format(GameMain.getPlugin().getTimer()));
	                	} else if (GameMain.getPlugin().getGameStage() == GameStage.PREGAME || GameMain.getPlugin().getGameStage() == GameStage.STARTING) {
	                		ping.setMotD("§aA partida sendo iniciada!\n§7Tempo: §a" + StringUtils.format(GameMain.getPlugin().getTimer()));
	                	} else if (GameMain.getPlugin().getGameStage() == GameStage.INVINCIBILITY) {
	                		ping.setMotD("§eA partida está na invencibilidade!\n§7Tempo: §a" + StringUtils.format(GameMain.getPlugin().getTimer()));
	                	} else if (GameMain.getPlugin().getGameStage() == GameStage.GAMETIME) {
	                		ping.setMotD("§cTorneio em andamento!\n§7Tempo: §a" + StringUtils.format(GameMain.getPlugin().getTimer()));
	                	} else {
	                		ping.setMotD("§cTorneio em andamento!\n§7Tempo: §a" + StringUtils.format(GameMain.getPlugin().getTimer()));
	                	}
	                }
	            }
	        });
	}
	
}
