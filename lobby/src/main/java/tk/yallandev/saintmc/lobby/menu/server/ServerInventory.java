package tk.yallandev.saintmc.lobby.menu.server;

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
import tk.yallandev.saintmc.bukkit.utils.string.StringLoreUtils;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.lobby.LobbyMain;

public class ServerInventory {

	public ServerInventory(Player player) {
		MenuInventory menuInventory = new MenuInventory("§7Servidores", 3);

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
				new ItemBuilder().name("§1§lKitPvP").type(Material.DIAMOND_SWORD).lore(StringLoreUtils.getLore(30,
						"\n§7Novo servidor de kitpvp com sopa feito para todos usarem estratégias e lutarem sem armudura em um estilo mais Hardcore simulando um HG\n§f\n§a"
								+ (BukkitMain.getInstance().getServerManager().getBalancer(ServerType.SIMULATOR)
										.getTotalNumber()
										+ BukkitMain.getInstance().getServerManager().getBalancer(ServerType.FULLIRON)
												.getTotalNumber())
								+ " jogadores online!"))
						.build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (type == ClickType.LEFT) {
							new KitpvpInventory(p);
							return;
						}

						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("PVP");
						player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
						player.closeInventory();
					}
				});

		menuInventory.setItem(11,
				new ItemBuilder().name("§a§lHungerGames").type(Material.MUSHROOM_SOUP).lore(StringLoreUtils.getLore(30,
						"\n§7Seja o ultimo sobrevivente em uma batalha brutal com kits onde apenas um será o campeão!\n§f\n§a"
								+ (BukkitMain.getInstance().getServerManager().getBalancer(ServerType.HUNGERGAMES)
										.getTotalNumber())
								+ " jogadores online!"))
						.build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (type == ClickType.LEFT) {
							new HungergamesInventory(p);
							return;
						}

						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("Hungergames");
						player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
						player.closeInventory();
					}
				});

		menuInventory.setItem(12,
				new ItemBuilder().name("§3§lGladiator").type(Material.IRON_FENCE).lore(StringLoreUtils.getLore(30,
						"\n§7Neste modo de jogo você pode desafiar seus amigos ou inimigos para uma batalha mortal!\n§f\n§a"
								+ (BukkitMain.getInstance().getServerManager().getBalancer(ServerType.GLADIATOR)
										.getTotalNumber())
								+ " jogadores online!"))
						.build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (type == ClickType.LEFT) {
							new GladiatorInventory(p);
							return;
						}

						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("Gladiator");
						player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
						player.closeInventory();
					}
				});

		if (BukkitMain.getInstance().getServerManager().getBalancer(ServerType.EVENTO).getList().stream()
				.filter(server -> !server.isJoinEnabled()).count() > 0)
			menuInventory.setItem(13,
					new ItemBuilder().name("§3§lEvento").type(Material.EMERALD).lore(StringLoreUtils.getLore(30,
							"\n§7Salas destinadas a vento!\n§f\n§a" + (BukkitMain.getInstance().getServerManager()
									.getBalancer(ServerType.EVENTO).getTotalNumber()) + " jogadores online!"))
							.build(),
					new MenuClickHandler() {

						@Override
						public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
							ByteArrayDataOutput out = ByteStreams.newDataOutput();
							out.writeUTF("Event");
							player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
							player.closeInventory();
						}
					});

		menuInventory.setItem(16, new ItemBuilder().name("§e§lSkyWars").type(Material.GRASS)
				.lore(StringLoreUtils.getLore(30, "\n§7Neste modo de jogo você batalhará nos céus!\n\n"
						+ "§7Este modo está em fase §1§lBETA§7 e poderá mudar a qualquer momento!\n§f\n§a"
						+ (BukkitMain.getInstance().getServerManager().getBalancer(ServerType.SW_SOLO).getTotalNumber()
								+ BukkitMain.getInstance().getServerManager().getBalancer(ServerType.SW_TEAM)
										.getTotalNumber()
								+ BukkitMain.getInstance().getServerManager().getBalancer(ServerType.SW_SQUAD)
										.getTotalNumber())
						+ " jogadores online!"))
				.build(), new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (type == ClickType.RIGHT && Member.hasGroupPermission(p.getUniqueId(), Group.TRIAL)) {
							new SkywarsInventory(p);
							return;
						}

						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("SWSolo");
						player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
						player.closeInventory();
					}
				});
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
