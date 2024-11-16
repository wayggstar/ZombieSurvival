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

    public String getSide(){
        return Side;
    }

    public void addPlayerSide(Player player){
        members.add(player);
    }

    public void removePlayerSide(Player player){
        members.remove(player);
    }
    public Set<Player> getMembers(){
        return members;
    }
}
