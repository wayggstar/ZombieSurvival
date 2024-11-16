package org.wayggstar.zombiesurvival;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeManager {
    private boolean isDay = true;
    private int gameDay = 0;
    private final JavaPlugin plugin;

    public TimeManager(JavaPlugin plugin) {
        this.plugin = plugin;
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

                if (time >= 0 && time < 12300 && !isDay) {
                    isDay = true;
                    broadcastMessage(ChatColor.GREEN + "낮이 되었습니다.");
                    gameDay++;
                } else if (time >= 12300 && time < 24000 && isDay) {
                    isDay = false;
                    broadcastMessage(ChatColor.RED + "밤이 되었습니다.");
                }

                if (time == 0) {
                    gameDay++;
                    Bukkit.broadcastMessage("새로운 날이 시작되었습니다! 오늘은 " + gameDay + "일째입니다.");
                }
            }
        }.runTaskTimer(plugin, 0L, 100L);
    }

    private void broadcastMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(message, "", 5, 40, 5);
        }
    }
}