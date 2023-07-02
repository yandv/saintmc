package br.com.saintmc.hungergames.controller;

import br.com.saintmc.hungergames.GameConst;
import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.VarChangeEvent;
import br.com.saintmc.hungergames.game.Color;
import br.com.saintmc.hungergames.game.Team;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tk.yallandev.saintmc.CommonGeneral;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
public class TeamManager {

    private Map<Integer, Team> teamMap;

    public TeamManager() {
        teamMap = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onVarChange(VarChangeEvent event) {
                if (GameGeneral.getInstance().getGameState().isPregame()) {
                    if (event.getVarName().equals(GameConst.MAX_PLAYERS_PER_TEAM_VAR)) {
                        unregisterTeams();
                        createTeams(Integer.parseInt(event.getNewValue()));

                        for (Gamer gamer : GameGeneral.getInstance().getGamerController().getGamers())
                            joinRandomTeam(gamer);
                    } else if (event.getVarName().equals(GameConst.TEAM_HG_VAR)) {
                        if (Boolean.parseBoolean(event.getNewValue())) {
                            createTeams(GameMain.getInstance().getMaxPlayersPerTeam());

                            for (Gamer gamer : GameGeneral.getInstance().getGamerController().getGamers())
                                joinRandomTeam(gamer);
                        } else {
                            unregisterTeams();
                        }
                    } else if (event.getVarName().equals(GameConst.MAX_SERVER_TEAMS)) {
                        unregisterTeams();
                        createTeams(GameMain.getInstance().getMaxPlayersPerTeam(),
                                    Integer.parseInt(event.getNewValue()));

                        for (Gamer gamer : GameGeneral.getInstance().getGamerController().getGamers())
                            joinRandomTeam(gamer);
                    }
                }
            }
        }, GameMain.getInstance());
    }

    public boolean joinRandomTeam(Gamer gamer) {
        Team team = getEmptyTeam();

        if (team == null) {
            return false;
        }

        team.addPlayer(gamer);
        return true;
    }

    public boolean joinEmptyTeam(Gamer gamer) {
        Team team = getTeams().stream().filter(t -> t.getPlayerList().isEmpty()).findFirst().orElse(null);

        if (team == null) {
            return false;
        }

        team.addPlayer(gamer);
        return true;
    }

    public Team registerNewTeam(int id, Color color) {
        Team team = Team.createTeam(id, color);
        teamMap.put(id, team);
        return team;
    }

    public void createTeams(int playersPerTeam) {
        createTeams(playersPerTeam, GameMain.getInstance().getVarManager().getVar(GameConst.MAX_SERVER_TEAMS, -1));
    }

    public void createTeams(int playersPerTeam, int maxTeams) {
        int maxPlayers = Bukkit.getMaxPlayers();

        if (maxTeams == -1) {
            maxTeams = maxPlayers / playersPerTeam;
        }

        for (int i = 0, colorId = 0; i < maxTeams; i++, colorId++) {
            if (colorId == Color.values().length) {
                colorId = 0;
            }

            registerNewTeam(i, Color.getColorById(colorId));
        }
    }

    public void unregisterTeams() {
        teamMap.clear();

        for (Gamer gamer : GameGeneral.getInstance().getGamerController().getGamers()) {
            gamer.setTeam(null);

            if (gamer.getPlayer() != null) {
                gamer.getPlayer().sendMessage("§cVocê foi removido do seu time por motivos de força maior.");
            }
        }
    }

    public Collection<Team> getTeams() {
        return teamMap.values();
    }

    public Team getEmptyTeam() {
        return getTeams().stream().filter(t -> t.getPlayerList().isEmpty()).findFirst().orElse(null);
    }
}
