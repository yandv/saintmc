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

	public static final String SERVER_NAME = "SaintMC";
	public static final String KICK_PREFIX = "SaintMC";

	public static final String IP_END = "saintmc.net";

	/*
	 * HOWEVER
	 */

	public static final String WEBSITE = "https://saintmc.net/";
	public static final String SITE = "www.saintmc.net";
	public static final String STORE = "loja.saintmc.net";
	public static final String TWITTER = "twitter.saintmc.net";
	public static final String DISCORD = "https://discord.saintmc.net";
	public static final String TORNEIO_DISCORD = "https://torneio.saintmc.net/";

	/*
	 * FORM
	 */

	public static final String TRIAL_FORM = "https://trial.saintmc.net/";
	public static final String HELPER_FORM = "https://helper.saintmc.net/";
	public static final String YOUTUBER_FORM = "https://youtuber.saintmc.net/";
	public static final String APPEAL_FORM = "https://appeal.saintmc.net/";

	/*
	 * PAINEL
	 */

	public static final String PAINEL = "https://painel.saintmc.net/";
	public static final String PAINEL_PROFILE = "https://painel.saintmc.net/profile/";
	public static final String DASHBOARD = "https://painel.saintmc.net/dashboard/";
	public static final String DASHBOARD_PROFILE = "https://painel.saintmc.net/dashboard/profile/";

	/*
	 * API
	 */

	public static final String API = "http://minhapicaaoquadrado.yandv.com.br:3333";
	public static final String DOWNLOAD_KEY = "kangaroo123";

	public static final String SKIN_URL = API + "/skin/";
	public static final String MOJANG_FETCHER = API + "/mojang/";
	public static final String CRACKED_FETCHER = API + "/mojang/cracked/";
	public static final String DISCORD_URL = API + "/discord/configuration/";

	public static final String STORE_API = "https://" + STORE + "/wp-json/wmc/v1/server/"
			+ "H0x062i7rnjyx6zj9qur2i4nrf0jcs";

	/*
	 * BACKEND
	 */

	public static final String MONGO_URL = "mongodb://admin:erANIaNutYpNeUBl@localhost/admin?retryWrites=true&w=majority";

	public static final String REDIS_HOSTNAME = "127.0.0.1";
	public static final String REDIS_PASSWORD = "yandv123";

}
