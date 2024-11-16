package org.wayggstar.zombiesurvival.Jobs;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ZombieJob {
    private String name;
    private String description;

    public ZombieJob(String name, String description){
        this.name = name;
        this.description = description;
    }

    public String getJob(){
        return name;
    }

}
