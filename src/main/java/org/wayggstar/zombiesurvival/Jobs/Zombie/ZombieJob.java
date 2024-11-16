package org.wayggstar.zombiesurvival.Jobs.Zombie;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ZombieJob {
    private String name;
    private String description;
    private List<ItemStack> startingItems;
    private List<ZombieJob> availableJobs;


    public ZombieJob(String name, String description){
            this.name = name;
            this.description = description;
            this.startingItems = new ArrayList<>();
        }

        // 직업 이름 반환
        public String getJob(){
            return name;
        }

        // 직업 설명 반환
        public String getDescription() {
            return description;
        }

        // 시작 아이템 목록 반환
        public List<ItemStack> getStartingItems() {
            return startingItems;
        }

        // 시작 아이템 추가
        public void addStartingItem(ItemStack itemStack) {
            startingItems.add(new ItemStack(itemStack));
        }
}
