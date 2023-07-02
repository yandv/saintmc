package tk.yallandev.saintmc.bukkit.api.hologram;

import org.bukkit.entity.Player;


/**
 *
 * Class to watch a Hologram View
 *
 * Por enquanto não da para editar o holograma quando está sendo visto
 *
 * @author yandv
 *
 */

public interface ViewHandler {

	public static final ViewHandler EMPTY = new ViewHandler() {

		@Override
		public String onView(Hologram hologram, Player player, String text) {
			return text;
		}
	};

	String onView(Hologram hologram, Player player, String text);

}
