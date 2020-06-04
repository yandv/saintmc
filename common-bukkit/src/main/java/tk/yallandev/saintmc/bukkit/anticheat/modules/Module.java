package tk.yallandev.saintmc.bukkit.anticheat.modules;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;

@AllArgsConstructor
@Getter
public abstract class Module implements Listener {
	
	private String name;
	
	@Setter
	private boolean autoBan;
	@Setter
	private int maxAlerts = 30;
	
	public Module() {
		name = getClass().getSimpleName().replace("Module", "");
	}
	
	public void alert(Player player) {
		alert(player, 0);
	}
	
	public void alert(Player player, int cps) {
		CommonGeneral.getInstance().getMemberManager().getMembers().stream()
				.filter(member -> member.getAccountConfiguration().isAnticheatEnabled())
				.forEach(member -> member.sendMessage("§9Anticheat> §fO jogador §d" + player.getName()
						+ "§f está usando §c" + getName() + (cps > 0 ? " §4(" + cps + " cps)" : "") + "§f!"));
	}

}
