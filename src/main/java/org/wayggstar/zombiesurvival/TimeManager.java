package org.wayggstar.zombiesurvival;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeManager {
    private boolean isDay = true;
    private boolean morningMessageSent = false;
    private boolean nightMessageSent = false;
    private final GameManager gameManager;
    private int gameDay = 0;
    private final JavaPlugin plugin;

    public TimeManager(JavaPlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public void resetDayCycle() {
        gameDay = 1;
    }

    public void startDayNightCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("world");
                if (world == null) return;

                long time = world.getTime();

                if (time >= 0 && time < 1200) {
                    if (!morningMessageSent) {
                        isDay = true;
                        morningMessageSent = true;
                        nightMessageSent = false;
                        broadcastMessage(ChatColor.GREEN + "새로운 날이 시작되었습니다! 오늘은 " + gameDay + "일째입니다.");
                        gameDay++;
                        if (gameDay == 6){
                            gameManager.HumanWinEndGame();
                        }
                    }
                }
                else if (time >= 13000 && time < 14000) {
                    if (!nightMessageSent) {
                        isDay = false;
                        nightMessageSent = true;
                        morningMessageSent = false;
                        broadcastMessage(ChatColor.RED + "밤이 되었습니다.");
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 100L);
    }

    private void broadcastMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(message, "", 5, 40, 5);
        }
    }

    public boolean IsDay(){
        return isDay;
    }
}