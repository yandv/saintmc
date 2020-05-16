package tk.yallandev.saintmc.game.games.hungergames.map;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class PregameMap extends MapRenderer {

	@Override
	public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
		clearCanvas(mapCanvas);
		
		try {
			Method getServer = NMS.MinecraftS.getMethod("getServer");
			Object s = getServer.invoke(null);
			Field worlds_1 = s.getClass().getField("worlds");
			worlds_1.setAccessible(true);
			List worlds_final = (List) worlds_1.get(s);
			Object worlds = NMS.WorldS.cast(worlds_final.get(0));

			Field world_maps = worlds.getClass().getField("worldMaps");
			Object world_maps_final = NMS.PersistantC.cast(world_maps.get(worlds));
			Method w = world_maps_final.getClass().getMethod("get", NMS.PersistantC.getClass(), String.class);

			Constructor craftMapConstructor = NMS.CraftMapRender.getDeclaredConstructor(NMS.CraftMapV, NMS.WorldM);
			craftMapConstructor.setAccessible(true);
			Object world_map = NMS.WorldM
					.cast(w.invoke(world_maps_final, NMS.WorldM.getClass(), "map_" + mapView.getId()));
			Object craftMapRenderer = craftMapConstructor.newInstance(NMS.CraftMapV.cast(mapView), world_map);

			craftMapRenderer.getClass().getMethod("initialize", MapView.class).invoke(craftMapRenderer, mapView);
			craftMapRenderer.getClass().getMethod("render", MapView.class, MapCanvas.class, Player.class).invoke(craftMapRenderer, mapView, mapCanvas, player);

		} catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException | InstantiationException | InvocationTargetException exception) {
			exception.printStackTrace();
		}
		
		mapView.setCenterX(0);
		mapView.setCenterZ(0);
	}

	public static void clearCanvas(MapCanvas mapCanvas) {
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 128; j++) {
				mapCanvas.setPixel(i, j, (byte) 0);
			}
		}
	}

	@Override
	public void initialize(MapView map) {
		super.initialize(map);
	}

}
