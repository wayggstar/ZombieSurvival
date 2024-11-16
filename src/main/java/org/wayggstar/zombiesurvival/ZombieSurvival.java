package org.wayggstar.zombiesurvival;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJob;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJobManager;
import org.wayggstar.zombiesurvival.Jobs.JobAbility;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJob;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJobManager;
import org.wayggstar.zombiesurvival.Listener.HumanListener;
import org.wayggstar.zombiesurvival.Listener.ZombieListener;
import org.wayggstar.zombiesurvival.Team.SideManager;

import java.util.Objects;


public final class ZombieSurvival extends JavaPlugin implements CommandExecutor {

    private GameManager gameManager;
    private HumanList humanList;
    private final ZombieJobManager zombieJobManager;
    private final HumanJobManager humanJobManager;

    public ZombieSurvival(ZombieJobManager zombieJobManager, HumanJobManager humanJobManager) {
        this.zombieJobManager = zombieJobManager;
        this.humanJobManager = humanJobManager;
    }

    @Override
    public void onEnable() {
        SideManager sideManager = new SideManager();
        sideManager.createTeam("zombie");
        sideManager.createTeam("DIE");

        getLogger().info(ChatColor.GREEN + "좀비 서바이벌 플러그인 활성화");

        if (!getDataFolder().exists()) {
            boolean mkdirs = getDataFolder().mkdirs();
        }
        saveDefaultConfig();

        humanList = new HumanList(this);
        gameManager = new GameManager(this, sideManager, humanList);
        ZombieListener zombieListener = new ZombieListener(sideManager);
        HumanListener humanListener = new HumanListener();
        JobAbility jobAbility = new JobAbility(sideManager, this);
        getServer().getPluginManager().registerEvents(gameManager, this);
        getServer().getPluginManager().registerEvents(zombieListener, this);
        getServer().getPluginManager().registerEvents(jobAbility, this);
        getServer().getPluginManager().registerEvents(humanListener, this);

        System.out.println(humanList.getPlayerNames().toString());

        Objects.requireNonNull(getCommand("게임시작")).setExecutor(this);
        Objects.requireNonNull(getCommand("리로드")).setExecutor(this);
        Objects.requireNonNull(getCommand("게임종료")).setExecutor(this);
        Objects.requireNonNull(getCommand("특수좀비")).setTabCompleter(new TabCompleterZombie(zombieJobManager, humanList));
        Objects.requireNonNull(getCommand("직업확정")).setTabCompleter(new TabCompleterHuman(humanJobManager, humanList));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) return false;

        if (cmd.getName().equalsIgnoreCase("게임시작")) {
            gameManager.startGame();
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("리로드")) {
            reloadConfig();
            humanList.load();
            sender.sendMessage(ChatColor.GREEN + "플러그인 설정이 리로드되었습니다.");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("게임종료")) {
            gameManager.endGame();
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("특수좀비")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "사용법: /특수좀비 <직업> <플레이어>");
                return false;
            }
            String playerName = args[1];
            String jobName = args[0];

            Player target = Bukkit.getPlayer(playerName);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "플레이어 " + playerName + "을(를) 찾을 수 없습니다.");
                return false;
            }

            if (zombieJobManager == null) {
                sender.sendMessage(ChatColor.RED + "좀비 직업 관리자 초기화 오류");
                return false;
            }

            ZombieJob job = zombieJobManager.getAvailableJobs().stream()
                    .filter(j -> j.getJob().equalsIgnoreCase(jobName))
                    .findFirst()
                    .orElse(null);

            if (job == null) {
                sender.sendMessage(ChatColor.RED + "직업 '" + jobName + "'을(를) 찾을 수 없습니다.");
                return false;
            }

            if (zombieJobManager.assignSpecificJob(target, job) != null) {
                sender.sendMessage(ChatColor.GREEN + "플레이어 " + target.getName() + "에게 '" + job.getJob() + "' 직업을 성공적으로 할당했습니다.");
                return true;
            }
        }
        if (cmd.getName().equalsIgnoreCase("직업확정")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "사용법: /직업확정 <직업> <플레이어>");
                return true;
            }

            String jobName = args[0];
            String playerName = args[1];
            Player target = Bukkit.getPlayer(playerName);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "플레이어 '" + playerName + "'을(를) 찾을 수 없습니다.");
                return true;
            }
            HumanJob job = humanJobManager.getAvailableJobs().stream()
                    .filter(j -> j.getJob().equalsIgnoreCase(jobName))
                    .findFirst()
                    .orElse(null);

            if (job == null) {
                sender.sendMessage(ChatColor.RED + "직업 '" + jobName + "'은(는) 존재하지 않습니다.");
                return true;
            }
            if (humanJobManager.forceAssignJob(target, job)) {
                sender.sendMessage(ChatColor.GREEN + "플레이어 '" + target.getName() + "'에게 직업 '" + job.getJob() + "'을(를) 성공적으로 확정했습니다.");
            } else {
                sender.sendMessage(ChatColor.RED + "플레이어 '" + target.getName() + "'에게 직업을 확정하는 데 실패했습니다.");
            }
            return true;
        }
        return false;
    }
}