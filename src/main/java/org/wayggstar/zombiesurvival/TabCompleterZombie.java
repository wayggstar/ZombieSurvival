package org.wayggstar.zombiesurvival;


import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJob;

import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJobManager;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    private final ZombieJobManager zombieJobManager;

    public TabCompleter(ZombieJobManager zombieJobManager){
        this.zombieJobManager = zombieJobManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("특수좀비")) {
            if (args.length == 1) {
                // 첫 번째 인수: 직업 이름 자동 완성
                completions = zombieJobManager.getAvailableJobs().stream()
                        .map(ZombieJob::getJob)
                        .filter(job -> job.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 2) {
                // 두 번째 인수: 플레이어 이름 자동 완성
                completions = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }

}
