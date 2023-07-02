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
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.character.Character;
import tk.yallandev.saintmc.bukkit.api.character.Character.Interact;
import tk.yallandev.saintmc.bukkit.api.hologram.Hologram;
import tk.yallandev.saintmc.bukkit.api.hologram.impl.CraftHologram;
import tk.yallandev.saintmc.bukkit.event.server.ServerPlayerJoinEvent;
import tk.yallandev.saintmc.bukkit.event.server.ServerPlayerLeaveEvent;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.menu.server.HungergamesInventory;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory;

public class CharacterListener implements Listener {

    private List<HologramInfo> hologramList;

    public CharacterListener() {
        hologramList = new ArrayList<>();
        ServerInventory.LOBBY_HG = false;

        Hologram hologram = createCharacter("§bCompetitivo", "CabecinhaDeKiwi", "npc-hg", new Interact() {

            @Override
            public boolean onInteract(Player player, boolean right) {
                if (right) {
                    new HungergamesInventory(player);
                } else {
                    sendPlayer(player, "Hungergames");
                }

                return false;
            }
        }, ServerType.HUNGERGAMES);

        hologram.addLineAbove("");
        hologram.addLineAbove("§e14:00 16:30h 19:00 21:30h");

        createCharacter("§bEvento", "yandv", "npc-evento", new Interact() {

            @Override
            public boolean onInteract(Player player, boolean right) {
                return false;
            }
        }, ServerType.EVENTO);
    }

    @EventHandler
    public void onServerPlayerJoin(ServerPlayerJoinEvent event) {
        updateHologram(event.getServerType());
    }

    @EventHandler
    public void onServerPlayerJoin(ServerPlayerLeaveEvent event) {
        updateHologram(event.getServerType());
    }

    public Hologram createCharacter(String displayName, String skinName, String configName, Interact interact, ServerType... serverType) {
        new Character(skinName, BukkitMain.getInstance().getLocationFromConfig(configName), interact);

        Hologram hologram = new CraftHologram(displayName, BukkitMain.getInstance().getLocationFromConfig(configName)
                                                                     .add(0, 0.25, 0));

        int playerCount = 0;

        for (int integer : Arrays.stream(serverType)
                                 .map(sT -> BukkitMain.getInstance().getServerManager().getBalancer(sT)
                                                      .getTotalNumber()).collect(Collectors.toList())) {
            playerCount += integer;
        }

        Hologram hologramLine = hologram.addLineBelow(!Arrays.stream(serverType)
                                                             .map(sT -> BukkitMain.getInstance().getServerManager()
                                                                                  .getBalancer(sT).getTotalNumber())
                                                             .findAny().isPresent() ? "§cNenhum servidor disponível!" :
                                                      "§e" + playerCount + " jogadores!");

        hologramList.add(new HologramInfo(Arrays.asList(serverType), hologramLine));
        BukkitMain.getInstance().getHologramController().loadHologram(hologram);
        return hologram;
    }

    public void updateHologram(ServerType type) {
        HologramInfo entry = hologramList.stream().filter(info -> info.typeList.contains(type)).findFirst()
                                         .orElse(null);

        if (entry != null) {
            if (BukkitMain.getInstance().getServerManager().getBalancer(type).getList().isEmpty()) {
                entry.hologram.setDisplayName("§cNenhum servidor disponível!");
            } else {
                int playerCount = 0;

                for (int integer : entry.typeList.stream().map(serverType -> BukkitMain.getInstance().getServerManager()
                                                                                       .getBalancer(serverType)
                                                                                       .getTotalNumber())
                                                 .collect(Collectors.toList()))
                    playerCount += integer;

                entry.hologram.setDisplayName("§e" + playerCount + " jogadores!");
            }
        }
    }

    private void sendPlayer(Player player, String string) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(string);
        player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
        player.closeInventory();
    }

    @AllArgsConstructor
    public class HologramInfo {

        private List<ServerType> typeList;
        private Hologram hologram;
    }
}
