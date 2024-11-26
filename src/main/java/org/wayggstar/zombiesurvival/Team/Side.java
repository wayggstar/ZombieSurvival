package org.wayggstar.zombiesurvival.Team;

import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;

public class Side {

    private final String name;
    private final Set<Player> members;

    public Side(String name) {
        this.name = name;
        this.members = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Set<Player> getMembers() {
        return members;
    }

    public void addPlayerSide(Player player) {
        members.add(player);
    }

    public void removePlayerSide(Player player) {
        members.remove(player);
    }
}
