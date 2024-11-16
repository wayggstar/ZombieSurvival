package org.wayggstar.zombiesurvival.Jobs.Zombie;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ZombieJobManager {

    private final List<ZombieJob> availableJobs;
    private final Map<Player, ZombieJob> playerJobs = new HashMap<>(); // 플레이어별 직업 저장
    private final Random random = new Random();

    public ZombieJobManager(List<ZombieJob> availableJobs) {
        this.availableJobs = availableJobs;
    }

    public List<ZombieJob> getAvailableJobs(){
        return availableJobs;
    }

    public ZombieJob assignRandomJob(Player player){
        if (!playerJobs.containsKey(player) && random.nextInt(100) < 10){
            ZombieJob job = getRandomJob();
            playerJobs.put(player, job);
            player.getInventory().addItem(job.getStartingItems().toArray(new ItemStack[0]));
            player.sendMessage(ChatColor.DARK_GREEN + "축하합니다! 당신은 특수좀비 '" + job.getJob() + ChatColor.DARK_GREEN + "'이 되었습니다");
            return job;
        }
        return null;
    }

    public ZombieJob assignSpecificJob(Player player, ZombieJob job) {
        if (!playerJobs.containsKey(player)) {
            playerJobs.put(player, job);
            player.sendMessage(ChatColor.DARK_GREEN + "축하합니다! 당신은 특수좀비 '" + job.getJob() + ChatColor.DARK_GREEN + "'이/가 되었습니다");
            return job;
        } else {
            player.sendMessage(ChatColor.RED + "이미 좀비 직업이 할당되었습니다!");
            return null;
        }
    }

    private ZombieJob getRandomJob(){
        int index = random.nextInt(availableJobs.size());
        return availableJobs.get(index);
    }

    public ZombieJob getPlayerJob(Player player){
        return playerJobs.get(player);
    }

    public void resetJobs() {
        playerJobs.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ZombieJob job = playerJobs.get(player);
            if (job != null) {
                player.sendMessage(ChatColor.RED + "게임 종료로 인해 좀비 직업이 초기화되었습니다.");
            }
        }
    }
}
