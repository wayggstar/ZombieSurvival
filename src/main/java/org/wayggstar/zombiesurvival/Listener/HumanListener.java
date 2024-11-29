package org.wayggstar.zombiesurvival.Listener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.wayggstar.zombiesurvival.GameManager;
import org.wayggstar.zombiesurvival.HumanList;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJobManager;
import org.wayggstar.zombiesurvival.Team.SideManager;

import java.util.Random;

public class HumanListener implements Listener {

    private final GameManager gameManager;
    private final SideManager sideManager;
    private Random random;
    private ZombieJobManager zombieJobManager;
    private JavaPlugin plugin;
    private HumanList humanList;

    public HumanListener(GameManager gameManager, HumanList humanList, ZombieJobManager zombieJobManager) {
        if (gameManager == null || humanList == null) {
            throw new IllegalArgumentException("Plugin and HumanList must not be null!");
        }
        this.gameManager = gameManager;
        this.sideManager = new SideManager();
        this.humanList = humanList;
        this.zombieJobManager = zombieJobManager;
        this.random = new Random();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (humanList.isHuman(player)) {
            player.sendMessage("당신은 탈락했습니다.");
            event.setDeathMessage(ChatColor.RED + "인간 " + ChatColor.RED + player.getName() + ChatColor.RED + "님이 탈락했습니다.......");
            zombieJobManager.assignSpecificJob(player, zombieJobManager.getAvailableJobs().get(random.nextInt(zombieJobManager.getAvailableJobs().size())));
            sideManager.addPlayerToTeam(player, "zombie");
        }
    }

}
