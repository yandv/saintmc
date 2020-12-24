package tk.yallandev.saintmc;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.v1_8_R3.MinecraftServer;

public class BukkitConst {

	public static final int TPS;

	public static final List<String> SWEAR_WORDS = Arrays.asList("merda", "loser", "cu", "porra", "buceta", "lixo",
			"random", "bct", "caralho", "fdp", "vsf", "vsfd", "tnc", "vtnc", "crl", "klux", "arrombado", "krl",
			"hypemc", "hype", "mushmc", "prismamc", "mush", "prisma", "prismamc.com.br", "hypemc.com.br", "weaven",
			"weavenmc", "weavenhg", "mc-weaven.com.br", "weaven-network.com.br", "weaven-network", "logicmc.com.br",
			"empire-network.com.br", "empire", "empiremc", "empire-network", "wayzemc.com.br", "wayze", "wayzemc",
			"macaco", "macacos");

	static {
		int tps = 20;

		try {
			Field field = MinecraftServer.class.getField("TPS");
			field.setAccessible(true);
			tps = (int) field.get(null);
		} catch (Exception ex) {
		}

		TPS = tps;
	}

}
