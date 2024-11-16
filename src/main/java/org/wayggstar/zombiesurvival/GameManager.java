package org.wayggstar.zombiesurvival;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import org.wayggstar.zombiesurvival.Jobs.Human.HumanJob;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJobManager;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJob;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJobManager;
import org.wayggstar.zombiesurvival.Team.SideManager;
import org.wayggstar.zombiesurvival.util.GameUtils;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class GameManager implements Listener {

    private HumanJobManager humanJobManager;
    private ZombieJobManager zombieJobManager;
    private final JavaPlugin plugin;
    private final SideManager sideManager;
    private final HumanList humanList;
    private TimeManager timeManager;
    private boolean gamePlaying = false;
    private int gameDay = 0;
    private final Map<Player, Integer> burningStack = new HashMap<>();
    private List<HumanJob> humanJobs = new ArrayList<>();
    private List<ZombieJob> zombieJobs = new ArrayList<>();

    public GameManager(JavaPlugin plugin, SideManager sideManager, HumanList humanList) {
        this.plugin = plugin;
        this.sideManager = sideManager;
        this.humanList = humanList;
        this.timeManager = new TimeManager(plugin);
        HumanJobSetUp();
        ZombieJobSetUp();
        this.humanJobManager = new HumanJobManager(humanJobs);
        this.zombieJobManager = new ZombieJobManager(zombieJobs);
        initActionBarDisplay();
        initBurningEffect();
        SpiderPenalty();
        ZombieNightBuff();
    }

    private void HumanJobSetUp() {
        HumanJob Miner = new HumanJob("광부", "처음부터 효율 1 내구성 1 철 곡괭이를 받고 시작합니다");
        ItemStack minerpickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta1 = minerpickaxe.getItemMeta();
        meta1.setDisplayName("§6§l광부§r의 §7§l곡괭이");
        meta1.addEnchant(Enchantment.DURABILITY, 1, true);
        meta1.addEnchant(Enchantment.DIG_SPEED, 1, true);
        minerpickaxe.setItemMeta(meta1);
        Miner.addStartingItem(minerpickaxe);

        HumanJob Doctor = new HumanJob("의사", "주변에 사람들을 회복시켜 줍니다 (3칸) X Y Z (3칸 3칸 3칸)");
        ItemStack healkit = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta2 = healkit.getItemMeta();
        meta2.setDisplayName("§a§l의사§r의 §4§l치료키트");
        healkit.setItemMeta(meta2);
        Doctor.addStartingItem(healkit);

        HumanJob Fire = new HumanJob("소방대원", "체력 5칸을 추가로 얻습니다");

        HumanJob Glider = new HumanJob("글라이더", "겉날개 내구도 30짜리 지급");
        ItemStack wing = new ItemStack(Material.ELYTRA);
        ItemMeta meta3 = wing.getItemMeta();
        meta3.setDisplayName("§7§l글라이더§r의 §0망가진 §7§l날개");
        Damageable damageable = (Damageable) meta3;
        damageable.setDamage(402);
        wing.setItemMeta(meta3);
        Glider.addStartingItem(wing);

        HumanJob Afraid = new HumanJob("겁쟁이", "체력이 5칸 이하일 경우 이동속도가 1.5배 증가합니다");

        humanJobs.add(Miner);
        humanJobs.add(Doctor);
        humanJobs.add(Fire);
        humanJobs.add(Glider);
        humanJobs.add(Afraid);
    }

    private void ZombieJobSetUp() {
        ZombieJob Tank = new ZombieJob("탱커좀비", "체력이 2줄이다.");
        ZombieJob Husk = new ZombieJob("사막좀비", "아침에 받는 디버프를 받지 않습니다");
        ZombieJob Spider = new ZombieJob("거미좀비", "낙댐을 받지 않고 이속 버프를 받습니다 단 아침에 나약함 5를 받습니다");
        ZombieJob Grab = new ZombieJob("그랩좀비", "투사체를 날리고 맞출경우 끌어당깁니다");
        ItemStack graping = new ItemStack(Material.EMERALD);
        ItemMeta meta = graping.getItemMeta();
        meta.setDisplayName("§6§l그랩");
        graping.setItemMeta(meta);
        Grab.addStartingItem(graping);
        zombieJobs.add(Tank);
        zombieJobs.add(Husk);
        zombieJobs.add(Spider);
        zombieJobs.add(Grab);
    }

    public void startGame() {
        if (gamePlaying) return;
        gamePlaying = true;
        gameDay = 1;
        World world = Bukkit.getWorld("world");
        if (world != null) world.setTime(0);
        updateTeams();
        Bukkit.broadcastMessage(ChatColor.GREEN + "게임이 시작되었습니다!");
        assignJobsToHumans();
        assignJobsToZombies();
        timeManager.resetDayCycle();
        timeManager.startDayNightCycle();
    }

    public void endGame() {
        zombieJobManager.resetJobs();
        humanJobManager.resetJobs();
        if (!gamePlaying) return;
        gamePlaying = false;
        gameDay = 0;
        World world = Bukkit.getWorld("world");
        if (world != null) world.setTime(0);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (sideManager.isPlayerTeam(player.getName(), "zombie")) {
                sideManager.removePlayerFromTeam(player, "zombie");
                player.sendMessage(ChatColor.RED + "게임 종료로 인해 좀비 팀에서 제거되었습니다.");
            }
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    public Map<Player, Integer> getBurningStack() {
        return burningStack;
    }

    private void updateTeams() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!humanList.isHuman(player)) {
                sideManager.addPlayerToTeam(player, "zombie");
                player.sendMessage(ChatColor.RED + "당신은 좀비 팀에 추가되었습니다.");
            }
        }
    }

    private void assignJobsToZombies() {
        for (Player player : getServer().getOnlinePlayers()) {
            if (sideManager.isPlayerTeam(player.getName(), "zombie")) {
                ZombieJob job = zombieJobManager.assignRandomJob(player);
                if (job != null) {
                    player.sendMessage("당신의 직업: " + job.getJob());
                    if (zombieJobManager.getPlayerJob(player) != null) {
                        ZombieJob job1 = zombieJobManager.getPlayerJob(player);
                        if (job1.getJob().equals("탱커좀비")) {
                            player.setMaxHealth(40.0);
                            player.setHealth(40.0);
                        }else {
                            player.setMaxHealth(20.0);
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.YELLOW + "10% 확률에 미달하여 직업이 할당되지 않았습니다.");
                }
            }
        }
    }


    private void assignJobsToHumans() {
        for (Player player : getServer().getOnlinePlayers()) {
            if (humanList.isHuman(player)) {
                humanJobManager.assignJob(player);
            }
            if (humanJobManager.getPlayerJob(player) != null){
                HumanJob job = humanJobManager.getPlayerJob(player);
                if (job.getJob().equals("소방대원")) {
                    player.setMaxHealth(30.0);
                    player.setHealth(30.0);
                }else {
                    player.setMaxHealth(20.0);

                }
            }
        }
    }


    public void initActionBarDisplay() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gamePlaying) return;
                World world = Bukkit.getWorld("world");
                if (world == null) return;

                int survives = humanList.getSurvivorCount(sideManager);
                String formattedTime = GameUtils.convertTicksToTime(world.getTime());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                            gameDay + "일차 / 현재 시간: " + ChatColor.YELLOW + formattedTime + " / 생존자 수: " + ChatColor.GREEN + survives));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    public void ZombieNightBuff() {
        new BukkitRunnable() {
            @Override
            public void run(){
                for (Player player : Bukkit.getOnlinePlayers()){
                    if (!sideManager.isPlayerTeam(player.getName(), "zombie")){
                        if (!timeManager.IsDay()){
                            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 1));

                        } else{
                            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            player.removePotionEffect(PotionEffectType.REGENERATION);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void initBurningEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ZombieJob job = zombieJobManager.getPlayerJob(player);
                    if (sideManager.isPlayerTeam(player.getName(), "zombie") && (job == null || !job.getJob().equals("사막좀비"))) {
                        if (player.getWorld().getTime() < 12300 || player.getWorld().getTime() > 23850) {
                            if (player.getLocation().getBlock().getLightFromSky() >= 13) {
                                if (timeManager.IsDay()){
                                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                                        ZombieJob job1 = zombieJobManager.getPlayerJob(player);
                                        if ((job != null || job.getJob().equals("거미좀비"))) {
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 4));
                                        }
                                    }
                                }
                                int stack = burningStack.getOrDefault(player, 0) + 1;
                                burningStack.put(player, stack);
                                if (stack >= 10) {
                                    burningStack.put(player, 0);
                                    player.damage(2.0);
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 4));
                                }
                            }
                        } else {
                            burningStack.put(player, 0);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void SpiderPenalty(){

    }

    @EventHandler
    public void onAfraidgetDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            HumanJob job = humanJobManager.getPlayerJob(player);
            if (job != null && job.getJob() != null) {
                if (job.getJob().equals("겁쟁이")) {
                    if (player.getHealth() <= 10) {
                        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.15);
                        player.sendMessage("§c무서워!!!!!!!!!! 도망가 !!!!!!!!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e){
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            HumanJob job = humanJobManager.getPlayerJob(player);
            if ((job != null && job.getJob().equals("겁쟁이"))){
                if (player.getHealth() >= 10){
                    player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                }
            }
        }
    }

    @EventHandler
    public void onDamageZombie(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            Player player = (Player) e.getEntity();
            if (sideManager.isPlayerTeam(player.getName(), "zombie")){
                e.setDamage(e.getDamage() * 1.25);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (gamePlaying){
            e.setCancelled(true);
        }
    }
}