package tk.yallandev.saintmc.kitpvp.kit.register;

import java.util.ArrayList;

import org.bukkit.Material;

import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class PvpKit extends Kit {

	public PvpKit() {
		super("PvP", "Kit padrão sem nenhuma habilidade!",
				GameMain.isFulliron() ? Material.DIAMOND_SWORD : Material.STONE_SWORD, 0, new ArrayList<>());
	}

}
