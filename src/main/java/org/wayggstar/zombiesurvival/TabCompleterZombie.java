package org.wayggstar.zombiesurvival;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJob;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJobManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleterZombie implements org.bukkit.command.TabCompleter {
    private final ZombieJobManager zombieJobManager;
    private final HumanList humanList;

    public TabCompleterZombie(ZombieJobManager zombieJobManager, HumanList humanList){
        this.zombieJobManager = zombieJobManager;
        this.humanList = humanList;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("특수좀비")) {
            if (args.length == 1) {
                completions = zombieJobManager.getAvailableJobs().stream()
                        .map(ZombieJob::getJob)
                        .filter(job -> job.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 2) {
                completions = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> !humanList.getPlayerNames().contains(name))
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }

}
