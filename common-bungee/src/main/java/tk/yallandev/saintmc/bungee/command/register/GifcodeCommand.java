package tk.yallandev.saintmc.bungee.command.register;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.BungeeMain;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandArgs;
import tk.yallandev.saintmc.common.command.CommandClass;
import tk.yallandev.saintmc.common.command.CommandFramework.Command;
import tk.yallandev.saintmc.common.command.CommandSender;
import tk.yallandev.saintmc.common.controller.GiftcodeController.ExecutionResponse;
import tk.yallandev.saintmc.common.giftcode.types.KitGiftcode;
import tk.yallandev.saintmc.common.giftcode.types.RankGiftcode;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

public class GifcodeCommand implements CommandClass {

	private static final String CHARS_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz12345689";

	@Command(name = "giftcode", aliases = { "resgatar", "codigo" })
	public void giftcodeCommand(CommandArgs cmdArgs) {
		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel() + " <código>§f para resgatar um código!");

			if (Member.hasGroupPermission(sender.getUniqueId(), Group.DIRETOR)) {
				sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel()
						+ " criar rank <rank> <tempo>§f para criar um código de rank!");
				sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel()
						+ " criar kit <kit> <tempo>§f para criar um código de kit!");
				return;
			}
			return;
		}

		if (args[0].equalsIgnoreCase("criar")) {
			if (!cmdArgs.isPlayer() || Member.hasGroupPermission(sender.getUniqueId(), Group.DIRETOR)) {
				String code = "";

				do {
					StringBuilder stringBuilder = new StringBuilder();

					for (int x = 1; x <= 15; x++) {
						stringBuilder.append(CHARS_STRING.charAt(CommonConst.RANDOM.nextInt(CHARS_STRING.length())));
						if (x % 5 == 0 && x != 15)
							stringBuilder.append('-');
					}

					code = stringBuilder.toString().trim();
				} while (BungeeMain.getInstance().getGiftcodeController().containsKey(code));

				if (args.length >= 2) {
					if (args[1].equalsIgnoreCase("rank")) {
						if (args.length >= 4) {
							RankType rankType = null;

							try {
								rankType = RankType.valueOf(args[2].toUpperCase());
							} catch (Exception ex) {
								sender.sendMessage("§cO rank " + args[2] + " não existe!");
								return;
							}

							long time = DateUtils.getTime(args[3]);

							if (BungeeMain.getInstance().getGiftcodeController().registerGiftcode(code,
									new RankGiftcode(code, rankType, time))) {
								sender.sendMessage(new MessageBuilder("§aO código " + code + " foi gerado!")
										.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new ComponentBuilder("§aClique para copiar!").create()))
										.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, code))
										.create());
								CommonGeneral.getInstance().getMemberManager()
										.broadcast("§7O " + sender.getName() + " criou um código de Rank "
												+ rankType.name() + " com a duração de " + DateUtils.getTime(time)
												+ "!", Group.MODPLUS);
							}
						} else
							sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel()
									+ " criar rank <rank> <tempo>§f para criar um código de rank!");
					} else if (args[1].equalsIgnoreCase("kit")) {
						if (args.length >= 3) {
							long time = args.length >= 4 ? DateUtils.getTime(args[3]) : -1l;

							if (BungeeMain.getInstance().getGiftcodeController().registerGiftcode(code,
									new KitGiftcode(code, args[2], time))) {
								sender.sendMessage(new MessageBuilder("§aO código " + code + " foi gerado!")
										.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new ComponentBuilder("§aClique para copiar!").create()))
										.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, code))
										.create());
								CommonGeneral.getInstance().getMemberManager()
										.broadcast(
												"§7O " + sender.getName() + " criou um código de Kit " + args[2]
														+ " com a duração de " + DateUtils.getTime(time) + "!",
												Group.MODPLUS);
							}
						} else
							sender.sendMessage(" §e* §fUse §a/" + cmdArgs.getLabel()
									+ " criar kit <kit> <tempo>§f para criar um código de kit!");
					}
				}
			}
			return;
		}

		String code = args[0];

		if (args.length >= 2) {
			if (args[1].equalsIgnoreCase("deletar")) {
				if (!cmdArgs.isPlayer() || Member.hasGroupPermission(sender.getUniqueId(), Group.DIRETOR)) {
					if (BungeeMain.getInstance().getGiftcodeController().deleteGiftcode(code)) {
						sender.sendMessage("§aO código " + code + " foi deletado com sucesso!");
						CommonGeneral.getInstance().getMemberManager().broadcast(
								"§7O " + sender.getName() + " deletou o código " + code + "!", Group.MODPLUS);
					} else
						sender.sendMessage("§cO código " + code + " não existe!");
				}
				return;
			}
		}

		if (!cmdArgs.isPlayer())
			return;

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());
		ExecutionResponse response = BungeeMain.getInstance().getGiftcodeController().execute(member, code);

		switch (response) {
		case ALREADY_USED: {
			member.sendMessage("§cO código " + code + " já foi utilizado!");
			break;
		}
		case SUCCESS: {
			CommonGeneral.getInstance().getMemberManager()
					.broadcast("§7O " + sender.getName() + " resgatou o código " + code + "!", Group.TRIAL);
			break;
		}
		case NOT_FOUND: {
			member.sendMessage("§cO código " + code + " não foi encontrado!");
			break;
		}
		}
		return;
	}

}
