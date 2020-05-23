package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class MonkKit extends DefaultKit {

	public MonkKit() {
		super("monk", "Desarme seu inimigo usando seu Blaze Rod", new ItemBuilder().type(Material.BLAZE_ROD).build(), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("monk"));
	}

}
