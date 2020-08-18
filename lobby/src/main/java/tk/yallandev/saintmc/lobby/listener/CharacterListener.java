package tk.yallandev.saintmc.lobby.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.AllArgsConstructor;
import tk.yallandev.hologramapi.hologram.Hologram;
import tk.yallandev.hologramapi.hologram.impl.SimpleHologram;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.character.Character;
import tk.yallandev.saintmc.bukkit.api.character.Character.Interact;
import tk.yallandev.saintmc.bukkit.event.server.ServerPlayerJoinEvent;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.menu.server.GladiatorInventory;
import tk.yallandev.saintmc.lobby.menu.server.HungergamesInventory;
import tk.yallandev.saintmc.lobby.menu.server.KitpvpInventory;
import tk.yallandev.saintmc.lobby.menu.server.SkywarsInventory;
import tk.yallandev.saintmc.lobby.menu.tournament.TournamentInventory;

public class CharacterListener implements Listener {

	private List<HologramInfo> hologramList;

	public CharacterListener() {
		hologramList = new ArrayList<>();

		new Character("§1§lTORNEIO", "Steve", BukkitMain.getInstance().getLocationFromConfig("npc-tournament"),
				new Interact() {

					@Override
					public boolean onInteract(Player player, boolean right) {
						new TournamentInventory(player, null, false, false);
						return false;
					}
				});

		createCharacter("§bHungerGames", "yukiritoBDF", "npc-hg", new Interact() {

			@Override
			public boolean onInteract(Player player, boolean right) {

				if (right) {
					new HungergamesInventory(player);
				} else {
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Hungergames");
					player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
					player.closeInventory();
				}

				return false;
			}
		}, ServerType.HUNGERGAMES);

		createCharacter("§bSkywars", "DoutorBiscoito", "npc-skywars", new Interact() {

			@Override
			public boolean onInteract(Player player, boolean right) {

				if (right) {
					new SkywarsInventory(player);
				} else {
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("SWSolo");
					player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
					player.closeInventory();
				}

				return false;
			}
		}, ServerType.SW_SOLO, ServerType.SW_SQUAD, ServerType.SW_TEAM);

		createCharacter("§bKitPvP", "broowk", "npc-pvp", new Interact() {

			@Override
			public boolean onInteract(Player player, boolean right) {

				if (right) {
					new KitpvpInventory(player);
				} else {
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("PVP");
					player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
					player.closeInventory();
				}

				return false;
			}
		}, ServerType.FULLIRON, ServerType.SIMULATOR);

		createCharacter("§bGladiator", "SpectroPlayer", "npc-gladiator", new Interact() {

			@Override
			public boolean onInteract(Player player, boolean right) {
				if (right) {
					new GladiatorInventory(player);
				} else {
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Gladiator");
					player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
					player.closeInventory();
				}
				return false;
			}
		}, ServerType.GLADIATOR);

	}

	@EventHandler
	public void onServerPlayerJoin(ServerPlayerJoinEvent event) {
		HologramInfo entry = hologramList.stream().filter(info -> info.typeList.contains(event.getServerType()))
				.findFirst().orElse(null);

		if (entry != null) {
			int playerCount = 0;

			for (int integer : entry.typeList.stream().map(
					serverType -> BukkitMain.getInstance().getServerManager().getBalancer(serverType).getTotalNumber())
					.collect(Collectors.toList()))
				playerCount += integer;

			entry.hologram.setDisplayName("§e" + playerCount + " jogadores!");
		}
	}

	public void createCharacter(String displayName, String skinName, String configName, Interact interact,
			ServerType... serverType) {
		new Character(displayName, skinName, BukkitMain.getInstance().getLocationFromConfig(configName), interact);

		Hologram hologram = new SimpleHologram(displayName,
				BukkitMain.getInstance().getLocationFromConfig(configName).add(0, 0.25, 0));

		int playerCount = 0;

		for (int integer : Arrays.asList(serverType).stream()
				.map(sT -> BukkitMain.getInstance().getServerManager().getBalancer(sT).getTotalNumber())
				.collect(Collectors.toList())) {
			playerCount += integer;
		}

		Hologram hologramLine = hologram.addLine(Arrays.asList(serverType).stream()
				.map(sT -> BukkitMain.getInstance().getServerManager().getBalancer(sT).getTotalNumber())
				.collect(Collectors.toList()).isEmpty() ? "§cNenhum servidor disponível!"
						: "§e" + playerCount + " jogadores!");

		hologramList.add(new HologramInfo(Arrays.asList(serverType), hologramLine));
		hologram.spawn();
		BukkitMain.getInstance().getHologramController().registerHologram(hologram);
	}

	@AllArgsConstructor
	public class HologramInfo {

		private List<ServerType> typeList;
		private Hologram hologram;

	}
}
