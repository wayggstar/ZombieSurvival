package org.wayggstar.zombiesurvival.Jobs.Zombie;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ZombieJob {
    private final String name;
    private final List<ItemStack> startingItems;


    public ZombieJob(String name){
            this.name = name;
        this.startingItems = new ArrayList<>();
        }

        public String getJob(){
            return name;
        }

        public List<ItemStack> getStartingItems() {
            return startingItems;
        }

        public void addStartingItem(ItemStack itemStack) {
            startingItems.add(new ItemStack(itemStack));
        }
}
