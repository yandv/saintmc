package tk.yallandev.saintmc.lobby.menu.tournament;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Joiner;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuUpdateHandler;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.menu.types.ConfirmInventory;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.TournamentGroup;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class TournamentInventory {

	private static final int MAX_TICKET = 100;
	private static final int PRICE = 750;

	public static int GROUP_A;
	public static int GROUP_B;
	public static int GROUP_C;
	public static int GROUP_D;

	private static final ItemStack INFO = new ItemBuilder().type(Material.BOOK).name("§aComo funciona!")
			.lore("", "§7Todas informações estão no", "§7discord do torneio: §f" + CommonConst.TORNEIO_DISCORD).build();
	private static final ItemStack REWARD = new ItemBuilder().type(Material.GOLD_INGOT).name("§aPremiação")
			.lore("", "§a1° - §aMouse Razer Deathadder 2013", "§e2° - §aMinecraft FA", "§c3° - §aCapa optifine",
					"§c4° - §aMinecraft SFA", "§c5° - §aSaint Eterno")
			.build();

	public TournamentInventory(Player player, TournamentGroup tournamentGroup, boolean finish, boolean premium) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		MenuInventory inventory = new MenuInventory("§7Torneio", 3);

		if (tournamentGroup == null) {
			if (member.getTournamentGroup() == null || member.getTournamentGroup() == TournamentGroup.NONE) {
				inventory.setTitle("§7Torneio - Escolha o seu grupo!");

				addItem(player, 10, GROUP_A, TournamentGroup.GROUP_A, inventory, member, premium);
				addItem(player, 11, GROUP_B, TournamentGroup.GROUP_B, inventory, member, premium);
				addItem(player, 15, GROUP_C, TournamentGroup.GROUP_C, inventory, member, premium);
				addItem(player, 16, GROUP_D, TournamentGroup.GROUP_D, inventory, member, premium);

				inventory.setUpdateHandler(new MenuUpdateHandler() {

					@Override
					public void onUpdate(Player player, MenuInventory menu) {
						addItem(player, 10, GROUP_A, TournamentGroup.GROUP_A, menu, member, premium);
						addItem(player, 11, GROUP_B, TournamentGroup.GROUP_B, menu, member, premium);
						addItem(player, 15, GROUP_C, TournamentGroup.GROUP_C, menu, member, premium);
						addItem(player, 16, GROUP_D, TournamentGroup.GROUP_D, menu, member, premium);
					}
				});

				inventory.setItem(13, INFO);

			} else {
				inventory.setTitle("§7Torneio - Informacoes!");
				ItemBuilder item = new ItemBuilder().type(Material.PAPER).name("§aFaça o upgrade no seu ingresso");

				if (member.hasPermission("tag.torneioplus")) {
					item.glow();
					item.lore("", "§7Você está com a tag " + Tag.TORNEIOPLUS.getPrefix());
				}

				inventory.setItem(10,
						new ItemBuilder()
								.name(member.hasPermission("tag.torneioplus")
										? Tag.TORNEIOPLUS.getPrefix() + " " + member.getPlayerName()
										: Tag.TORNEIO.getPrefix() + " " + member.getPlayerName())
								.type(Material.SKULL_ITEM).durability(3).skin(member.getPlayerName())
								.lore("", "§7Grupo: §f" + (member.getTournamentGroup().name().split("_")[1])).build());

				inventory.setItem(11, item.build(), member.hasPermission("tag.torneioplus")
						? (Player p, Inventory inv, ClickType type, ItemStack stack, int slot) -> {
							player.sendMessage(
									"§aVocê já possui o pacote da tag " + Tag.TORNEIOPLUS.getPrefix() + "§a!");

							player.closeInventory();
						}
						: (Player p, Inventory inv, ClickType type, ItemStack stack, int slot) -> {
							if (!member.hasPermission("tag.torneioplus"))
								player.sendMessage("§a§l> §fCompre o ingresso do " + Tag.TORNEIOPLUS.getPrefix()
										+ "§f em §bhttps://" + CommonConst.STORE + "/torneio/");

							player.closeInventory();
						});

				inventory.setItem(12, new ItemBuilder().name("§cDesinscrever-se do Torneio").lore(
						"\n§7Caso você queira sair do torneio, sua tag será mantida por ter participado, e seu grupo será removido, dando o lugar para outra pessoa jogar")
						.type(Material.TNT).build(),
						(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) -> {

							new ConfirmInventory(player, "§cSair da copa!", new ConfirmInventory.ConfirmHandler() {

								@Override
								public void onConfirm(boolean confirmed) {
									if (confirmed) {
										player.sendMessage("§cVocê saiu do seu grupo!");
										member.setTournamentGroup(TournamentGroup.NONE);

										switch (member.getTournamentGroup()) {
										case GROUP_A:
											GROUP_A--;
											break;
										case GROUP_B:
											GROUP_B--;
											break;
										case GROUP_C:
											GROUP_C--;
											break;
										case GROUP_D:
											GROUP_D--;
											break;
										default:
											break;
										}

										player.closeInventory();
									}
								}
							}, inventory);
						});

				inventory.setItem(14, REWARD);
				inventory.setItem(15, INFO);
			}

			inventory.open(player);
		} else {
			if (finish) {
				member.addPermission("tag.torneio");
				member.setTournamentGroup(tournamentGroup);

				if (!member.hasPermission("tag.torneio")) {
					member.addPermission("tag.torneio");
					player.sendMessage("§aVocê recebeu a tag " + Tag.TORNEIO.getPrefix() + "§f!");
				}

				if (premium)
					if (!member.hasPermission("tag.torneioplus")) {
						player.sendMessage("§a§l> §fCompre o ingresso do " + Tag.TORNEIOPLUS.getPrefix()
								+ "§f em §bhttps://" + CommonConst.STORE + "/torneio/");
					}

				((BukkitMember) member).loadTags();

				player.sendMessage(
						"§aVocê está no grupo: " + Joiner.on(" ").join(Arrays.asList(tournamentGroup.name().split("_"))
								.stream().map(s -> NameUtils.formatString(s)).collect(Collectors.toList())));
				player.sendMessage("§aEntre no discord do torneio: " + CommonConst.TORNEIO_DISCORD);

				switch (tournamentGroup) {
				case GROUP_A:
					GROUP_A++;
					break;
				case GROUP_B:
					GROUP_B++;
					break;
				case GROUP_C:
					GROUP_C++;
					break;
				case GROUP_D:
					GROUP_D++;
					break;
				default:
					break;
				}

				player.closeInventory();
			} else {
				inventory.setTitle("§7Torneio - Planos!");

				inventory.setItem(11,
						new ItemBuilder().name("§aIngresso Premium §7(Muito recomendado)").type(Material.PAPER).glow()
								.lore("\n§7Preço: §e5 reais\n§7Com esse ingresso, você ganha: \n§7A tag "
										+ Tag.TORNEIO.getPrefix()
										+ "§7 até a copa!\n§7Acesso a todos os kits até fim do torneio!")
								.build(),
						new MenuClickHandler() {

							@Override
							public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
								if (member.hasPermission("tag.torneioplus")) {
									new TournamentInventory(player, tournamentGroup, true, false);
								} else {
									player.sendMessage("§a§l> §fCompre o ingresso do " + Tag.TORNEIOPLUS.getPrefix()
											+ "§f em §bhttps://" + CommonConst.STORE
											+ "/torneio/ §fe §aescolha§f o Grupo quando for §6ativado§f!");
									player.closeInventory();
								}
							}
						});

				inventory.setItem(12, new ItemBuilder().name("§aIngresso Normal").type(Material.PAPER)
						.lore("\n§7Necessário §e" + PRICE + " coins§7!\n§7Com esse ingresso, você ganha: \n§7A tag "
								+ Tag.TORNEIO.getPrefix()
								+ "§7 até a copa!\n§7Acesso aos kits: §fMiner, Lumberjack, Forger e Cultivator §e(no dia do torneio)")
						.build(), new MenuClickHandler() {

							@Override
							public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
								if (member.hasPermission("tag.torneioplus") || System.currentTimeMillis()
										- member.getFirstLogin() >= (1000 * 60 * 60 * 24 * 7)) {

									if (member.getMoney() >= PRICE) {
										new TournamentInventory(player, tournamentGroup, true, false);
										member.removeMoney(PRICE);
									} else {
										player.sendMessage("§cVocê precisa de mais " + (PRICE - member.getMoney())
												+ " coins para comprar o ingresso!");
										player.closeInventory();
									}

								} else {
									player.sendMessage(
											"§cSua conta precisa ter no mínimo 7 dias desde o primeiro login.");
									player.closeInventory();
								}
							}
						});

				inventory.setItem(14, REWARD);
				inventory.setItem(15, INFO);

				inventory.open(player);
			}

		}
	}

	public TournamentInventory(Player player) {
		MenuInventory inventory = new MenuInventory("§7Torneio", 3);

		inventory.setItem(10, new ItemBuilder().type(Material.INK_SACK).name("§aGrupo A")
				.lore("", "§7Ingressos restantes: §f" + (MAX_TICKET - 0)).durability(1).build());

		inventory.setItem(11, new ItemBuilder().type(Material.INK_SACK).name("§aGrupo B")
				.lore("", "§7Ingressos restantes: §f" + (MAX_TICKET - 0)).durability(11).build());

		inventory.setItem(13, INFO);

		inventory.setItem(15, new ItemBuilder().type(Material.INK_SACK).durability(4).name("§aGrupo C")
				.lore("", "§7Ingressos restantes: §f" + (MAX_TICKET - 0)).build());

		inventory.setItem(16, new ItemBuilder().type(Material.INK_SACK).durability(14).name("§aGrupo D")
				.lore("", "§7Ingressos restantes: §f" + (MAX_TICKET - 0)).build());

		inventory.open(player);
	}

	public void addItem(Player player, int slot, int remaining, TournamentGroup tournamentGroup,
			MenuInventory inventory, Member member, boolean premium) {

		ItemBuilder builder = new ItemBuilder().type(Material.INK_SACK)
				.durability(tournamentGroup == TournamentGroup.GROUP_A ? 1
						: tournamentGroup == TournamentGroup.GROUP_B ? 11
								: tournamentGroup == TournamentGroup.GROUP_C ? 4 : 14);

		boolean a = true;

		if (tournamentGroup == TournamentGroup.GROUP_C) {
//			a = GROUP_A > 90 && GROUP_B > 90;
			builder.lore("\n" + (!a ? "§cOs ingressos para esse grupo ainda não estão disponíveis!"
					: ((MAX_TICKET - remaining) > 0 ? "§7Ingressos restantes: §f" + (MAX_TICKET - remaining)
							: "§cIngressos esgotados\n\n§fAdquiria " + Tag.TORNEIOPLUS.getPrefix()
									+ "§f para entrar nesse grupo!")));
		} else if (tournamentGroup == TournamentGroup.GROUP_D) {
//			a = GROUP_A > 90 && GROUP_B > 90 && GROUP_C > 90;
			builder.lore("\n" + (!a ? "§cOs ingressos para esse grupo ainda não estão disponíveis!"
					: ((MAX_TICKET - remaining) > 0 ? "§7Ingressos restantes: §f" + (MAX_TICKET - remaining)
							: "§cIngressos esgotados\n\n§fAdquiria " + Tag.TORNEIOPLUS.getPrefix()
									+ "§f para entrar nesse grupo!")));
		} else
			builder.lore("\n" + ((MAX_TICKET - remaining) > 0 ? "§7Ingressos restantes: §f" + (MAX_TICKET - remaining)
					: "§cIngressos esgotados!\n\n§fAdquiria " + Tag.TORNEIOPLUS.getPrefix()
							+ "§f para entrar nesse grupo!"));

		builder.name((a ? "§a" : "§c")
				+ Joiner.on(" ").join(Arrays.asList(tournamentGroup.name().replace("GROUP", "Grupo").split("_"))
						.stream().map(s -> NameUtils.formatString(s)).collect(Collectors.toList())));

		boolean available = a;

		inventory.setItem(slot, builder.build(), new MenuClickHandler() {

			@Override
			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				if (available) {
					if (MAX_TICKET - remaining > 0 || member.hasPermission("tag.torneioplus"))
						new TournamentInventory(player, tournamentGroup, false,
								type == ClickType.LEFT ? true : premium);
					else {
						player.sendMessage(
								"§cOs ingressos desse grupo esgotaram, para entrar nesse grupo adquira a tag "
										+ Tag.TORNEIOPLUS.getPrefix() + "§c em https://" + CommonConst.STORE
										+ "/torneio/");
						player.closeInventory();
					}
				} else {
					player.sendMessage("§cOs ingressos desse grupo ainda não estão disponíveis!");
					player.sendMessage("§cCompre os grupos anteriores antes de comprar esse grupo!");
					player.closeInventory();
				}
			}
		});
	}

}
