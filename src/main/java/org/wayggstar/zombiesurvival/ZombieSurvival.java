package org.wayggstar.zombiesurvival;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJob;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJobManager;
import org.wayggstar.zombiesurvival.Jobs.JobAbility;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJob;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJobManager;
import org.wayggstar.zombiesurvival.Listener.ZombieListener;
import org.wayggstar.zombiesurvival.Team.SideManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public final class ZombieSurvival extends JavaPlugin implements CommandExecutor {

    private GameManager gameManager;
    private HumanList humanList;
    private SideManager sideManager;
    private ZombieListener zombieListener;
    private JobAbility jobAbility;
    private ZombieJob zombieJob;
    private ZombieJobManager zombieJobManager;
    private HumanJob humanJob;
    private HumanJobManager humanJobManager;
    private List<ZombieJob> zombieJobs = new ArrayList<>();
    private List<HumanJob> humanJobs = new ArrayList<>();
    private final Random random = new Random();
    private int maxX;
    private int maxY;
    private int maxZ;

    @Override
    public void onEnable() {
        loadConfigValues();

        sideManager = new SideManager();
        sideManager.createTeam("zombie");
        sideManager.createTeam("DIE");

        getLogger().info(ChatColor.GREEN + "좀비 서바이벌 플러그인 활성화");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        saveDefaultConfig();

        humanList = new HumanList(this);
        gameManager = new GameManager(this, sideManager, humanList, this);
        zombieListener = new ZombieListener(sideManager, this);
        jobAbility = new JobAbility(sideManager, this, humanJobManager);
        zombieJobManager = new ZombieJobManager(gameManager.zombieJobs);
        humanJobManager = new HumanJobManager(gameManager.humanJobs);
        getServer().getPluginManager().registerEvents(gameManager, this);
        getServer().getPluginManager().registerEvents(zombieListener, this);
        getServer().getPluginManager().registerEvents(jobAbility, this);

        System.out.println(humanList.getPlayerNames().toString());

        getCommand("게임시작").setExecutor(this);
        getCommand("리로드").setExecutor(this);
        getCommand("게임종료").setExecutor(this);
        getCommand("특수좀비").setTabCompleter(new TabCompleterZombie(zombieJobManager, humanList));
        getCommand("직업확정").setTabCompleter(new TabCompleterHuman(humanJobManager, humanList));
        getCommand("스폰지정");

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) return false;
        Player player = (Player) sender;
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
        if (cmd.getName().equalsIgnoreCase("스폰지점")){
            Location humanspawn = ((Player) sender).getLocation();
            gameManager.humansetspawn = humanspawn;
            createWorldBorder(humanspawn);
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

            if (sideManager.isPlayerTeam(sender.getName(), "zombie")) {

                if (zombieJobManager.assignSpecificJob(target, job) != null) {
                    sender.sendMessage(ChatColor.GREEN + "플레이어 " + target.getName() + "에게 '" + job.getJob() + "' 직업을 성공적으로 할당했습니다.");
                    return true;
                }
            }else {
                sender.sendMessage("§c좀비팀이 아닙니다.");
            }
        }
        if (cmd.getName().equalsIgnoreCase("직업확정")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "사용법: /직업확정 <직업> <플레이어>");
                return true;
            }
            if (humanList.isHuman(player)) {
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
            }else {
                sender.sendMessage("§c인간팀이 아닙니다.");
            }
        }
        return false;
    }

    private void createWorldBorder(Location spawnLocation) {
        World world = spawnLocation.getWorld();

        if (world == null) {
            getLogger().warning("월드가 존재하지 않습니다.");
            return;
        }
        world.getWorldBorder().setCenter(spawnLocation);
        double size = 3000;
        world.getWorldBorder().setSize(size);
        world.getWorldBorder().setWarningDistance(10);
        world.getWorldBorder().setWarningTime(15);
        getLogger().info("월드 보더가 설정되었습니다: " + spawnLocation.toString());
    }

    @Override
    public void onDisable() {
        if (humanList != null) {
            humanList.save();
        }
        getLogger().info("좀비서바이벌 비활성화");
    }

    public HumanList getHumanList() {
        return humanList;
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        maxX = config.getInt("mapsize.X", 3000);
        maxY = config.getInt("mapsize.Y", 256);
        maxZ = config.getInt("mapsize.Z", 3000);
    }

    public Location getRandomSafeLocation(Player player) {
        for (int attempts = 0; attempts < 10; attempts++) {
            int x = random.nextInt(maxX * 2) - maxX;
            int z = random.nextInt(maxZ * 2) - maxZ;
            int y = player.getWorld().getHighestBlockYAt(x, z);

            if (y <= 0 || y >= maxY) continue;

            Location location = new Location(player.getWorld(), x + 0.5, y, z + 0.5);
            Material blockType = player.getWorld().getBlockAt(location).getType();

            if (blockType != Material.WATER && blockType != Material.LAVA) {
                return location;
            }
        }
        return null;
    }
}