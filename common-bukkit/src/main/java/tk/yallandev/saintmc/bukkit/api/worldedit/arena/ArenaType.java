package tk.yallandev.saintmc.bukkit.api.worldedit.arena;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * 
 * Estudei esse tipo de enum e achei interessante aplicar na ideia de criar
 * arenas
 * 
 * Para criar arena é simples, use o ArenaType.<Type>.place(methods...)
 * 
 * @author yandv
 *
 */

@SuppressWarnings("deprecation")
public enum ArenaType implements ArenaCreator {

	CIRCLE {

		@Override
		public ArenaResponse place(Location location, Material material, int id, int radius, int height, boolean wall,
				boolean async) {

			ArenaResponse response = new ArenaResponse();

			if (async) {
				try {
					throw new Exception("Asynchronously isnt enabled!");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {

				int cx = location.getBlockX();
				int cz = location.getBlockZ();

				int rSquared = radius * radius;

				for (int x = cx - radius; x <= cx + radius; x++) {
					for (int z = cz - radius; z <= cz + radius; z++) {
						Location actualLocation = new Location(location.getWorld(), x, location.getY(), z);

						double distance = (cx - x) * (cx - x) + (cz - z) * (cz - z);

						if (distance <= rSquared) {
							response.addMap(actualLocation, actualLocation.getBlock().getState());
							actualLocation.getBlock().setType(material);
							actualLocation.getBlock().setData((byte) id);
						}
					}
				}

				for (double t = 0; t < 50; t += 0.5) {
					double x = (radius * Math.sin(t));
					double z = (radius * Math.cos(t));

					for (int y = 0; y <= height; y++) {
						Location actualLocation = location.clone().add(x, y, z);
						
						response.addMap(actualLocation, actualLocation.getBlock().getState());
						actualLocation.getBlock().setType(material);
						actualLocation.getBlock().setData((byte) id);
					}
				}
			}

			return response;
		}

	},
	SQUARE {

		@Override
		public ArenaResponse place(Location location, Material material, int id, int radius, int height, boolean wall,
				boolean async) {

			ArenaResponse response = new ArenaResponse();

			if (async) {
				try {
					throw new Exception("Asynchronously isnt enabled!");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				for (int x = -radius; x <= radius; x++) {
					for (int z = -radius; z <= radius; z++) {
						Location currentLocation = location.clone().add(x, 0, z);

						if (wall)
							if (z == radius || z == -radius || x == radius || x == -radius) {
								for (int y = 1; y <= height; y++) {
									Location actualLocation = currentLocation.clone().add(0, y, 0);

									response.addMap(actualLocation, actualLocation.getBlock().getState());
									actualLocation.getBlock().setType(material);
									actualLocation.getBlock().setData((byte) id);
								}
							}

						response.addMap(currentLocation, currentLocation.getBlock().getState());
						currentLocation.getBlock().setType(material);
						currentLocation.getBlock().setData((byte) id);
					}
				}
			}

			return response;
		}

	},
	PRESET_MLG {

		@Override
		public ArenaResponse place(Location location, Material material, int id, int radius, int height, boolean wall,
				boolean async) {
			return new ArenaResponse();
		}

	};

//	private ArenaCreator arenaCreator;
//	
//	private ArenaType(ArenaCreator arenaCreator) {
//	}
}
