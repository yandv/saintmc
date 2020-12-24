package tk.yallandev.saintmc.lobby.menu.server;

import static tk.yallandev.saintmc.common.server.ServerType.FULLIRON;
import static tk.yallandev.saintmc.common.server.ServerType.GLADIATOR;
import static tk.yallandev.saintmc.common.server.ServerType.SIMULATOR;
import static tk.yallandev.saintmc.common.server.ServerType.HUNGERGAMES;
import static tk.yallandev.saintmc.common.server.ServerType.EVENTO;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.RequiredArgsConstructor;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuUpdateHandler;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.lobby.LobbyPlatform;

public class ServerInventory {

	public static boolean LOBBY_HG = true;

	public ServerInventory(Player player) {
		MenuInventory menuInventory = new MenuInventory("§7§nSelecionar modo", 3);

		createItens(player, menuInventory);

		menuInventory.setUpdateHandler(new MenuUpdateHandler() {

			@Override
			public void onUpdate(Player player, MenuInventory menu) {
				createItens(player, menuInventory);
			}
		});

		menuInventory.open(player);
	}

	public void createItens(Player player, MenuInventory menuInventory) {
		menuInventory.setItem(10,
				new ItemBuilder().name("§aKitPvP").type(Material.DIAMOND_SWORD)
						.lore("", " §7Um servidor focado em treino de pvp", " §7com sopa, warps customizadas para",
								" §7treinamento personalizado e kits do nosso HG!", "",
								"§8" + getTotalNumber(FULLIRON, SIMULATOR, ServerType.LOBBY_HG)
										+ " jogadores conectados",
								"§aClique para conectar.")
						.build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (type == ClickType.LEFT) {
							new KitpvpInventory(p);
							return;
						}

						sendServer(player, "PVP");
					}
				});

		menuInventory.setItem(11, new ItemBuilder().name("§aHG").type(Material.MUSHROOM_SOUP)
				.lore("", " §7Um servidor onde há uma batalha brutal", " §7com kits onde apenas um será o campeão", "",
						"§8" + getTotalNumber(ServerType.LOBBY_HG, EVENTO, HUNGERGAMES) + " jogadores conectados",
						"§aClique para conectar.")
				.build(), new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (LOBBY_HG) {
							ByteArrayDataOutput out = ByteStreams.newDataOutput();
							out.writeUTF("LobbyHG");
							player.sendPluginMessage(LobbyPlatform.getInstance().getPlugin(), "BungeeCord",
									out.toByteArray());
							player.closeInventory();
							return;
						}

						if (type == ClickType.LEFT) {
							new HungergamesInventory(p);
							return;
						}

						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("Hungergames");
						player.sendPluginMessage(LobbyPlatform.getInstance().getPlugin(), "BungeeCord",
								out.toByteArray());
						player.closeInventory();
					}
				});

		menuInventory.setItem(12, new ItemBuilder().name("§aGladiator").type(Material.IRON_FENCE)
				.lore("", " §7Um servidor onde há uma duelos individuais", " §7contra outros jogadores para saber quem",
						" §7é o melhor gladiador", "", "§8" + getTotalNumber(GLADIATOR) + " jogadores conectados",
						"§aClique para conectar.")
				.build(), new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (type == ClickType.LEFT) {
							new GladiatorInventory(p);
							return;
						}

						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("Gladiator");
						player.sendPluginMessage(LobbyPlatform.getInstance().getPlugin(), "BungeeCord",
								out.toByteArray());
						player.closeInventory();
					}
				});

//		menuInventory.setItem(12,
//				new ItemBuilder().name("§3§lGladiator").type(Material.IRON_FENCE).lore(StringLoreUtils.getLore(30,
//						"\n§7Neste modo de jogo você pode desafiar seus amigos ou inimigos para uma batalha mortal!\n§f\n§a"
//								+ (BukkitMain.getInstance().getServerManager().getBalancer(ServerType.GLADIATOR)
//										.getTotalNumber())
//								+ " jogadores online!"))
//						.build(),
//				new MenuClickHandler() {
//
//					@Override
//					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
//						if (type == ClickType.LEFT) {
//							new GladiatorInventory(p);
//							return;
//						}
//
//						sendServer(player, "Gladiator");
//					}
//				});

//		if (Member.hasGroupPermission(player.getUniqueId(), Group.TRIAL) || BukkitMain.getInstance().getServerManager()
//				.getBalancer(ServerType.EVENTO).getList().stream().filter(server -> server.isJoinEnabled()).count() > 0)
//			menuInventory.setItem(13,
//					new ItemBuilder().name("§3§lEvento").glow().type(Material.EMERALD).lore(StringLoreUtils.getLore(30,
//							"\n§7Salas destinadas a eventos!\n§f\n§a" + (BukkitMain.getInstance().getServerManager()
//									.getBalancer(ServerType.EVENTO).getTotalNumber()) + " jogadores online!"))
//							.build(),
//					new MenuClickHandler() {
//
//						@Override
//						public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
//							sendServer(player, "Event");
//						}
//					});
//
//		menuInventory.setItem(16,
//				new ItemBuilder().name("§e§lSkyWars").type(Material.GRASS)
//						.lore(StringLoreUtils.getLore(30, "\n§7Neste modo de jogo você batalhará nos céus!\n\n"
//								+ "§7Este modo está em fase §1§lBETA§7 e poderá mudar a qualquer momento!\n§f\n§a"
//								+ (getTotalNumber(SW_SOLO, SW_TEAM, SW_SQUAD)) + " jogadores online!"))
//						.build(),
//				new MenuClickHandler() {
//
//					@Override
//					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
//						if (type == ClickType.RIGHT && Member.hasGroupPermission(p.getUniqueId(), Group.TRIAL)) {
//							new SkywarsInventory(p);
//							return;
//						}
//
//						sendServer(player, "SWSolo");
//					}
//				});
	}

	public void sendServer(Player player, String serverType) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(serverType);
		player.sendPluginMessage(LobbyPlatform.getInstance().getPlugin(), "BungeeCord", out.toByteArray());
		player.closeInventory();
	}

	public int getTotalNumber(ServerType... serverTypes) {
		return BukkitMain.getInstance().getServerManager().getTotalNumber(serverTypes);
	}

	@RequiredArgsConstructor
	static class SendClick implements MenuClickHandler {

		private final String serverId;

		@Override
		public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
			BukkitMain.getInstance().sendPlayer(p, serverId);
		}

	}
}
