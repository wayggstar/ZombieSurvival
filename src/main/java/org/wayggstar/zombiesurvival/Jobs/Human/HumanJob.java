package org.wayggstar.zombiesurvival.Jobs.Human;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HumanJob {
    private String name;
    private String description;
    private List<ItemStack> startingItems;

    public HumanJob(String name, String description){
        this.name = name;
        this.description = description;
        this.startingItems = new ArrayList<>();
    }

    public String getJob(){
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ItemStack> getStartingItems() {
        return startingItems;
    }

    public void addStartingItem(ItemStack itemStack) {
        startingItems.add(new ItemStack(itemStack));
    }
}
