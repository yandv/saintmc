package tk.yallandev.saintmc;

import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.web.WebHelper;
import tk.yallandev.saintmc.common.utils.web.http.ApacheWebImpl;
import tk.yallandev.saintmc.serializer.TagSerializer;

public class CommonConst {

	public static final Gson GSON = new GsonBuilder()
			.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC, Modifier.PROTECTED)
			.registerTypeAdapter(Tag.class, new TagSerializer())
			.setPrettyPrinting().create();
	
	public static final Random RANDOM = new Random();

	public static final WebHelper DEFAULT_WEB = new ApacheWebImpl();

	public static final Pattern NICKNAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{1,16}");

	public static final String SERVER_NAME = "SaintMC";
	public static final String KICK_PREFIX = "SaintMC";

	public static final String WEBSITE = "http://saintmc.com.br/";
	public static final String SITE = "saintmc.com.br";
	public static final String STORE = "loja.saintmc.com.br";
	public static final String DISCORD = "http://discord.saintmc.com.br/";

	public static final String SKIN_URL = "http://localhost:3333/skin/";
	public static final String DISCORD_URL = "http://localhost:3333/discord/configuration/";
	public static final String STORE_URL = "https://" + STORE + "/wp-json/wmc/v1/server/"
			+ "H0x062i7rnjyx6zj9qur2i4nrf0jcs";
	public static final String MONGO_URL = "mongodb://localhost/commons?retryWrites=true&w=majority";

	public static final String MOJANG_FETCHER = "http://localhost:3333/mojang/";
	public static final String CRACKED_FETCHER = "http://localhost:3333/mojang/cracked/";

}
