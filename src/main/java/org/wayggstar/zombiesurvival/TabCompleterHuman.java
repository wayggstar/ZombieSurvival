package org.wayggstar.zombiesurvival;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJob;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJobManager;
import org.wayggstar.zombiesurvival.HumanList;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJob;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleterHuman implements TabCompleter {
    private final HumanJobManager humanJobManager;
    private final HumanList humanList;


    public TabCompleterHuman(HumanJobManager humanJobManager, HumanList humanList) {
        this.humanJobManager = humanJobManager;
        this.humanList = humanList;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("직업확정")) {
            if (args.length == 1) {
                if (humanJobManager.getAvailableJobs() != null) {
                    suggestions = humanJobManager.getAvailableJobs().stream()
                            .map(HumanJob::getJob)
                            .filter(job -> job.toLowerCase().startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }         else if (args.length == 2) {
                if (humanList.getPlayerNames() != null && !humanList.getPlayerNames().isEmpty()) {
                    suggestions = Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> !humanList.getPlayerNames().contains(name))
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }
        }
        return suggestions;
    }
}
