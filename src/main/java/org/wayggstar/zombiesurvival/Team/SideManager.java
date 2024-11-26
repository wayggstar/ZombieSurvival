package org.wayggstar.zombiesurvival.Team;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class SideManager {

    private final Map<String, Side> sides;

    public SideManager() {
        sides = new HashMap<>();
    }

    public Side createTeam(String name) {
        if (!sides.containsKey(name)) {
            Side team = new Side(name);
            sides.put(name, team);
            return team;
        }
        return null;
    }

    public Side getSide(String name) {
        return sides.get(name);
    }

    public void addPlayerToTeam(Player player, String teamName) {
        Side side = sides.get(teamName);
        if (side != null) {
            side.addPlayerSide(player);
        }
    }

    public void removePlayerFromTeam(Player player, String teamName) {
        Side side = sides.get(teamName);
        if (side != null) {
            side.removePlayerSide(player);
        }
    }

    public boolean isPlayerTeam(String playerName, String sideName) {
        Side team = sides.get(sideName);
        return team != null && team.getMembers().stream().anyMatch(member -> member.getName().equals(playerName));
    }
}
