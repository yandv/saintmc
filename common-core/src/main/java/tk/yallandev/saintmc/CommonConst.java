package tk.yallandev.saintmc;

import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tk.yallandev.saintmc.common.serializer.TagSerializer;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.web.WebHelper;
import tk.yallandev.saintmc.common.utils.web.http.ApacheWebImpl;

public class CommonConst {

	public static final Gson GSON = new GsonBuilder()
			.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC, Modifier.PROTECTED)
			.registerTypeAdapter(Tag.class, new TagSerializer()).setPrettyPrinting().create();

	public static final Random RANDOM = new Random();

	public static final WebHelper DEFAULT_WEB = new ApacheWebImpl();

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

	public static final Pattern NICKNAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{1,16}");

	public static final String SERVER_NAME = "NordMC";
	public static final String KICK_PREFIX = "NordMC";

	public static final String IP_END = "nordmc.com.br";

	/*
	 * HOWEVER
	 */

	public static final String WEBSITE = "nordmc.com.br/";
	public static final String SITE = "nordmc.com.br";
	public static final String STORE = "loja.nordmc.com.br";
	public static final String TWITTER = "twitter.nordmc.com.br";
	public static final String DISCORD = "discord.nordmc.com.br";
	public static final String TORNEIO_DISCORD = "torneio.nordmc.com.br/";

	/*
	 * FORM
	 */

	public static final String TRIAL_FORM = "https://trial.nordmc.com.br/";
	public static final String HELPER_FORM = "https://helper.nordmc.com.br/";
	public static final String YOUTUBER_FORM = "https://youtuber.nordmc.com.br/";
	public static final String APPEAL_FORM = "https://appeal.nordmc.com.br/";

	/*
	 * API
	 */

	public static final String API = "http://api.nordmc.spectrum-mc.net:3333";
	public static final String DOWNLOAD_KEY = "kangaroo123";

	public static final String SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
	public static final String MOJANG_FETCHER = "https://api.mojang.com/users/profiles/minecraft/";
	public static final String DISCORD_URL = API + "/discord/configuration/";

	public static final String STORE_API = "https://" + STORE + "/wp-json/wmc/v1/server/"
			+ "H0x062i7rnjyx6zj9qur2i4nrf0jcs";

}
