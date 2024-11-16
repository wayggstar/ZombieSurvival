package org.wayggstar.zombiesurvival.Team;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Side {
    private String Side;
    private Set<Player> members;

    public Side(String side){
        this.Side = side;
        this.members = new HashSet<>();
    }

    public String getTeam(){
        return Side;
    }

    public void addPlayerZombie(Player player){
        members.add(player);
    }

    public void clearZombie(){
        members.clear();
    }
    public Set<Player> getZombies(){
        return members;
    }
}
