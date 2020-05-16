package tk.yallandev.saintmc.kitpvp.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class PvpKit extends Kit {

	public PvpKit() {
		super("PvP", "Kit padrão sem nenhuma habilidade!", GameMain.isFulliron() ? Material.DIAMOND_SWORD : Material.STONE_SWORD);
	}

	@Override
	public void applyKit(Player player) {
		
	}
	
}
