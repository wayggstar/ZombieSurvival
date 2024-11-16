package org.wayggstar.zombiesurvival;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.wayggstar.zombiesurvival.Team.SideManager;
import java.util.ArrayList;
import java.util.List;

public class HumanList {

    private final JavaPlugin plugin;
    private List<String> playerNames;

    public HumanList(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public int getSurvivorCount(SideManager sideManager) {
        return (int) playerNames.stream().filter(name -> !sideManager.isPlayerTeam(name, "zombie")).count();
    }

    public boolean isHuman(Player player) {
        return playerNames.contains(player.getName());
    }

    public void load() {
        FileConfiguration config = plugin.getConfig();
        playerNames = config.getStringList("humans");
        if (playerNames == null) playerNames = new ArrayList<>();
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();
        config.set("humans", playerNames);
        plugin.saveConfig();
    }
}