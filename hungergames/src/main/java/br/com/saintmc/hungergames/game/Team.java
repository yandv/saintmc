package br.com.saintmc.hungergames.game;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.team.TeamPlayerJoinEvent;
import br.com.saintmc.hungergames.event.team.TeamPlayerLeaveEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class Team {

    private final int id;
    private final Color color;

    private List<UUID> playerList = new ArrayList<>();

    @Setter
    private int kills;

    public Team addPlayer(Gamer gamer) {
        if (isFull()) {
            gamer.getPlayer().sendMessage("§cEste time está cheio!");
            return this;
        }

        if (playerList.contains(gamer.getUniqueId())) {
            gamer.getPlayer().sendMessage("§cVocê já está neste time!");
            return this;
        }

        if (gamer.getTeam() != null) {
            gamer.getTeam().removePlayer(gamer);
        }

        gamer.setTeam(this);

        TeamPlayerJoinEvent event = new TeamPlayerJoinEvent(this, gamer);
        Bukkit.getPluginManager().callEvent(event);

        playerList.add(gamer.getUniqueId());
        sendMessage("" + gamer.getPlayer().getName() + " entrou no time!");
        return this;
    }

    public Team removePlayer(Gamer gamer) {
        if (!getPlayerList().contains(gamer.getUniqueId())) {
            gamer.getPlayer().sendMessage("§cVocê não está neste time!");
            return this;
        }

        gamer.setTeam(null);

        TeamPlayerLeaveEvent event = new TeamPlayerLeaveEvent(this, gamer);
        Bukkit.getPluginManager().callEvent(event);

        playerList.remove(gamer.getUniqueId());
        sendMessage("" + gamer.getPlayer().getName() + " saiu do time!");
        return this;
    }

    public void addKill() {
        setKills(getKills() + 1);
    }

    public void forceAddGamer(Gamer gamer) {
        gamer.setTeam(this);

        TeamPlayerLeaveEvent event = new TeamPlayerLeaveEvent(this, gamer);
        Bukkit.getPluginManager().callEvent(event);

        playerList.add(gamer.getUniqueId());
    }

    public void forceRemoveGamer(Gamer gamer) {
        gamer.setTeam(null);
        playerList.remove(gamer.getUniqueId());
    }

    public List<Gamer> getParticipantsAsGamer() {
        return playerList.stream().map(uuid -> GameGeneral.getInstance().getGamerController().getGamer(uuid))
                         .collect(Collectors.toList());
    }

    public List<Player> getParticipantsAsPlayer() {
        return playerList.stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public void sendMessage(String message) {
        getParticipantsAsPlayer().forEach(player -> player.sendMessage(
                getColor().getChatColor() + "Time " + getColor().getName() + "> §f" + message));
    }

    public boolean isFull() {
        return playerList.size() >= GameMain.getInstance().getMaxPlayersPerTeam();
    }

    public static Team createTeam(int id, Color color) {
        return new Team(id, color);
    }

    public static Team createTeamForGamer(int id, Color color, Gamer gamer) {
        return new Team(id, color).addPlayer(gamer);
    }

    public boolean isAlive() {
        if (playerList.size() == 0) {
            return false;
        }

        return getParticipantsAsGamer().stream().anyMatch(Gamer::isPlaying);
    }
}
