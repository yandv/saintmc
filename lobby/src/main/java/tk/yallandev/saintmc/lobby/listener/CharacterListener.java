package tk.yallandev.saintmc.lobby.listener;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.github.juliarn.npc.NPC;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.character.Character;
import tk.yallandev.saintmc.bukkit.api.character.Character.Interact;
import tk.yallandev.saintmc.lobby.menu.tournament.TournamentInventory;

public class CharacterListener implements Listener {

	private Character tournamentCharacter;
	private Character hungergamesCharacter;
	private Character skywarsCharacter;
	private Character pvpCharacter;
	private Character gladiatorCharacter;

	public CharacterListener() {
		createTournament();
//		createHungergames();
//		createSkywars();
//		createKitpvp();
//		createGladiator();
	}

	private void createTournament() {
		tournamentCharacter = new Character("§1§lTORNEIO", UUID.randomUUID(),
				UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"),
				BukkitMain.getInstance().getLocationFromConfig("npc-tournament"), new Interact() {

					@Override
					public boolean onInteract(Player player, NPC npc, EntityUseAction action) {
						new TournamentInventory(player, null, false, false);
						return false;
					}
				});

		tournamentCharacter.getNpc().setLookAtPlayer(true);
	}

	private void createHungergames() {
		hungergamesCharacter = new Character("§bHungerGames", UUID.randomUUID(),
				UUID.fromString("4aca31f6-7bf8-4704-b35b-ef37a730f506"),
				BukkitMain.getInstance().getLocationFromConfig("npc-hg"), new Interact() {

					@Override
					public boolean onInteract(Player player, NPC npc, EntityUseAction action) {
						new TournamentInventory(player, null, false, false);
						return false;
					}
				});

		hungergamesCharacter.getNpc().setLookAtPlayer(true);
	}

	private void createSkywars() {
		skywarsCharacter = new Character("§bSkywars", UUID.randomUUID(),
				UUID.fromString("1ad70885-4704-4eaa-86f2-b5f1484f1843"),
				BukkitMain.getInstance().getLocationFromConfig("npc-skywars"), new Interact() {

					@Override
					public boolean onInteract(Player player, NPC npc, EntityUseAction action) {
						new TournamentInventory(player, null, false, false);
						return false;
					}
				});

		skywarsCharacter.getNpc().setLookAtPlayer(true);
	}

	private void createKitpvp() {
		pvpCharacter = new Character("§bKitPvP", UUID.randomUUID(),
				UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"),
				BukkitMain.getInstance().getLocationFromConfig("npc-pvp"), new Interact() {

					@Override
					public boolean onInteract(Player player, NPC npc, EntityUseAction action) {
						new TournamentInventory(player, null, false, false);
						return false;
					}
				});

		pvpCharacter.getNpc().setLookAtPlayer(true);
	}

	private void createGladiator() {
		gladiatorCharacter = new Character("§bKitPvP", UUID.randomUUID(),
				UUID.fromString("90d331e4-066d-4f45-bb5e-bd895f2bc257"),
				BukkitMain.getInstance().getLocationFromConfig("npc-gladiator"), new Interact() {

					@Override
					public boolean onInteract(Player player, NPC npc, EntityUseAction action) {
						new TournamentInventory(player, null, false, false);
						return false;
					}
				});

		gladiatorCharacter.getNpc().setLookAtPlayer(true);
	}

}
