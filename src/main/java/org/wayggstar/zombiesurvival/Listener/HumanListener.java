package org.wayggstar.zombiesurvival.Listener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.wayggstar.zombiesurvival.Team.SideManager;

public class HumanListener implements Listener {

    private final SideManager sideManager;

    public HumanListener() {
        this.sideManager = new SideManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!isZombie(player)) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("당신은 탈락했습니다.");
            event.setDeathMessage(ChatColor.RED + "인간 " + ChatColor.RED + player.getName() + ChatColor.RED + "님이 탈락했습니다.......");
            sideManager.addPlayerToTeam(player, "DIE");
        }
    }

    private boolean isZombie(Player player) {
        return sideManager.isPlayerTeam(player.getName(), "zombie");
    }
}
