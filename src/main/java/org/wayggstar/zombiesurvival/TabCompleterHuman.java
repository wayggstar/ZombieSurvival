package org.wayggstar.zombiesurvival;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJobManager;
import org.wayggstar.zombiesurvival.HumanList;

import java.util.ArrayList;
import java.util.List;

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
                for (int i = 0; i < humanJobManager.getAvailableJobs().size(); i++) {
                    suggestions.add(humanJobManager.getAvailableJobs().get(i).toString());
                }
            } else if (args.length == 2) {
                for (Player player : sender.getServer().getOnlinePlayers()) {
                    suggestions.addAll(humanList.getPlayerNames());
                }
            }
        }
        return suggestions;
    }
}
