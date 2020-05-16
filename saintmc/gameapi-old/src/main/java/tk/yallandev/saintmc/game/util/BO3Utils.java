package tk.yallandev.saintmc.game.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.Material;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.BO3Blocks;

public class BO3Utils {

	public static ArrayList<BO3Blocks> loadBO3(String path) {
		File file = new File(GameMain.getPlugin().getDataFolder(), "/BO3/" + path + ".bo3");
		if (!file.exists()) {
			GameMain.getPlugin().getLogger().log(Level.SEVERE, "Nao foi possivel encontrar o arquivo " + path + ".bo3");
			return new ArrayList<>();
		}
		ArrayList<BO3Blocks> blocks = new ArrayList<>();
		
		try {
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				
				if (!line.startsWith("Block"))
					continue;
				
				String[] bo3 = line.replace("Block(", "").replace(")", "").split(",");
				int x = Integer.valueOf(bo3[0]);
				int y = Integer.valueOf(bo3[1]);
				int z = Integer.valueOf(bo3[2]);
				String mat = bo3[3];
				byte data = (byte) 0;
				
				if (bo3[3].contains(":")) {
					String[] material = bo3[3].split(":");
					mat = material[0];
					data = Byte.valueOf(material[1]);
				}
				
				blocks.add(new BO3Blocks(x, y, z, Material.valueOf(mat), data));
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			GameMain.getPlugin().getLogger().log(Level.INFO, "Carregado arquivo " + path + ".bo3");
		}
		return blocks;
	}
}
