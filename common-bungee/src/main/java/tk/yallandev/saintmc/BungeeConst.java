package tk.yallandev.saintmc;

import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

public class BungeeConst {

	/*
	 * DISCORD CHANNEL
	 */

	public static final long DISCORD_CHANNEL_PLAYER_LOG = 777987122615025675l;
	public static final long DISCORD_CHANNEL_LOG = 777987165052207105l;
	public static final long DISCORD_CHANNEL_REPORT_LOG = 777987403670880317l;

	public static final CommandSender CONSOLE_SENDER = new CommandSender() {

		@Override
		public void sendMessage(BaseComponent[] fromLegacyText) {
			ProxyServer.getInstance().getConsole().sendMessage(fromLegacyText);
		}

		@Override
		public void sendMessage(BaseComponent str) {
			ProxyServer.getInstance().getConsole().sendMessage(str);
		}

		@Override
		public void sendMessage(String str) {
			ProxyServer.getInstance().getConsole().sendMessage(str);
		}

		@Override
		public boolean isPlayer() {
			return false;
		}

		@Override
		public UUID getUniqueId() {
			return UUID.randomUUID();
		}

		@Override
		public String getName() {
			return "CONSOLE";
		}
	};

	/*
	 * BACKEND
	 */

	public static final String MONGO_URL = "mongodb://admin:erANIaNutYpNeUBl@127.0.0.1/admin?retryWrites=true&w=majority";

	public static final String REDIS_HOSTNAME = "127.0.0.1";
	public static final String REDIS_PASSWORD = "yandv123";

	public static final TextComponent[] BROADCAST_MESSAGES = new TextComponent[] {
			new MessageBuilder("§e§lDICA§f: §7Viu um player suspeito? Use o §a/report §7para reportá-lo").create(),
			new MessageBuilder(
					"§e§lDICA§f: §7Se você é um §dNitro Booster§7 em nosso discord use o §a/discord§7 para ganhar beneficios no servidor")
							.create(),
			new MessageBuilder(
					"§e§lDICA§f: §7Você sabia que além de ajudar o servidor, você ainda tem vantagens com o vip? Acesse a ")
							.addExtre(new MessageBuilder("§aloja ")
									.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											TextComponent.fromLegacyText("§aClique aqui para acessar a loja!")))
									.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.STORE))
									.create())
							.addExtre(new MessageBuilder("§7para saber mais!").create()).create(),
			new MessageBuilder("§e§lDICA§f: §7Acesse o nosso ")
					.addExtre(new MessageBuilder("§bDiscord")
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									TextComponent.fromLegacyText("§aClique aqui para acessar a discord!")))
							.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.DISCORD)).create())
					.addExtre(new MessageBuilder(" §7e fique por dentro de todas as novidades em tempo real!").create())
					.create(),
			new MessageBuilder(
					"§e§lDICA§f: §7Sincronize seu discord com o servidor e desbloqueie algumas funções novas! §a/discord sync§7!")
							.create(),
			new MessageBuilder(
					"§e§lDICA§f: §7Fique ligado no nosso twitter! ")
							.addExtre(
									new MessageBuilder("§b@RedePentaMc_")
											.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													TextComponent
															.fromLegacyText("§bClique aqui para acessar a twitter!")))
											.setClickEvent(
													new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.TWITTER))
											.create())
							.create(),
			new MessageBuilder(
					"§e§lDICA§f: §7Você sabia que no nosso site você pode ver o seu perfil e o rank com todos os jogadores? Isso e muito mais! ")
							.addExtre(new MessageBuilder("§eClique aqui para acessar!")
									.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											TextComponent.fromLegacyText("§bClique aqui para acessar a loja!")))
									.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.WEBSITE))
									.create())
							.create(),

			new MessageBuilder(
					"§e§lDICA§f: §7Fique por dentro das regras do servidor! Acesse o ")
							.addExtre(
									new MessageBuilder("§asite ")
											.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													TextComponent
															.fromLegacyText("§aClique aqui para acessar a website!")))
											.setClickEvent(
													new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.WEBSITE))
											.create())
							.addExtre(
									new MessageBuilder("§7ou o nosso ").create())
							.addExtre(
									new MessageBuilder("§bDiscord§7.")
											.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													TextComponent
															.fromLegacyText("§aClique aqui para acessar a discord!")))
											.setClickEvent(
													new ClickEvent(ClickEvent.Action.OPEN_URL, CommonConst.DISCORD))
											.create())
							.create(),

			new MessageBuilder("§e§lDICA§f: §7Saiba mais sobre os §eranks §7do servidor, digite §e/ranks§7!")
					.create() };

}
