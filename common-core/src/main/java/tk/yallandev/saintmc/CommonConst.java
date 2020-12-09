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

	public static final String SERVER_NAME = "ClouthNetwork";
	public static final String KICK_PREFIX = "ClouthNetwork";

	public static final String IP_END = "clouth-network.com.br";

	/*
	 * HOWEVER
	 */

	public static final String WEBSITE = "clouth-network.com.br/";
	public static final String SITE = "clouth-network.com.br";
	public static final String STORE = "loja.clouth-network.com.br";
	public static final String TWITTER = "twitter.clouth-network.com.br";
	public static final String DISCORD = "discord.clouth-network.com.br";
	public static final String TORNEIO_DISCORD = "torneio.clouth-network.com.br/";

	/*
	 * FORM
	 */

	public static final String TRIAL_FORM = "https://trial.clouth-network.com.br/";
	public static final String HELPER_FORM = "https://helper.clouth-network.com.br/";
	public static final String YOUTUBER_FORM = "https://youtuber.clouth-network.com.br/";
	public static final String APPEAL_FORM = "https://appeal.clouth-network.com.br/";

	/*
	 * PAINEL
	 */

	public static final String PAINEL = "https://painel.clouth-network.com.br/";
	public static final String PAINEL_PROFILE = "https://painel.clouth-network.com.br/profile/";
	public static final String DASHBOARD = "https://painel.clouth-network.com.br/dashboard/";
	public static final String DASHBOARD_PROFILE = "https://painel.clouth-network.com.br/dashboard/profile/";

	/*
	 * API
	 */

	public static final String API = "http://api.clouthnetwork.spectrum-mc.net:3333";
	public static final String DOWNLOAD_KEY = "kangaroo123";

	public static final String SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
	public static final String MOJANG_FETCHER = "https://api.mojang.com/users/profiles/minecraft/";
	public static final String DISCORD_URL = API + "/discord/configuration/";

	public static final String STORE_API = "https://" + STORE + "/wp-json/wmc/v1/server/"
			+ "H0x062i7rnjyx6zj9qur2i4nrf0jcs";

}
