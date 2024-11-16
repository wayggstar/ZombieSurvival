package org.wayggstar.zombiesurvival.Listener;

import armorequip.armorequip.ArmorEquip;
import armorequip.armorequip.ArmorEquipEvent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJob;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJobManager;
import org.wayggstar.zombiesurvival.Team.SideManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieListener implements Listener {

    private final SideManager sideManager;
    private final JavaPlugin plugin;
    private final ZombieJobManager zombieJobManager;
    private final Random random = new Random();
    private List<ZombieJob> zombieJobs = new ArrayList<>();
    private List<ItemStack> itemStacks;
    private List<Player> randomplayer;

    public ZombieListener(SideManager sideManager, JavaPlugin plugin) {
        this.sideManager = sideManager;
        this.plugin = plugin;
        this.zombieJobManager = new ZombieJobManager(zombieJobs);
        Random random = new Random();
        DiaItemList();
    }

    @EventHandler
    public void BlockingArmorforZombie(ArmorEquipEvent e){
        if (sideManager.isPlayerTeam(e.getPlayer().getName(), "zombie")) {
            if (e.getNewArmorPiece().getType().toString().contains("DIAMOND")) {
                e.getPlayer().sendMessage(ChatColor.RED + "좀비는 다이아몬드 장비를 착용할 수 없습니다.");
                e.setCancelled(true);
            }
            if (e.getNewArmorPiece().getType().toString().contains("LEGGINGS")) {
                e.getPlayer().sendMessage(ChatColor.RED + "좀비는 바지를 착용할 수 없습니다.");
                e.setCancelled(true);
            }
            if (e.getNewArmorPiece().getType().toString().contains("HELMET")) {
                e.getPlayer().sendMessage(ChatColor.RED + "좀비는 모자를 착용할 수 없습니다.");
                e.setCancelled(true);
            }
        }
    }
    public void DiaItemList() {
        this.itemStacks = new ArrayList<>();
        addItem(Material.DIAMOND_SWORD);
        addItem(Material.DIAMOND_AXE);
        addItem(Material.DIAMOND_SHOVEL);
        addItem(Material.DIAMOND_PICKAXE);
        addItem(Material.DIAMOND_HOE);
        addItem(Material.DIAMOND_CHESTPLATE);
        addItem(Material.DIAMOND_HELMET);
        addItem(Material.DIAMOND_LEGGINGS);
        addItem(Material.DIAMOND_BOOTS);
    }

    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }

    public void addItem(Material material) {
        ItemStack item = new ItemStack(material);
        itemStacks.add(item);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        if (sideManager.isPlayerTeam(player.getName(), "zombie")) {
            ItemStack newItem = player.getInventory().getItem(e.getNewSlot());
            if (newItem != null) {
                for (ItemStack itemStack : getItemStacks()) {
                    if (newItem.getType() == itemStack.getType()) {
                        e.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "좀비는 다이아몬드 아이템을 사용할 수 없습니다.");
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void BlockingBow(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        if (sideManager.isPlayerTeam(player.getName(), "zombie")) {
            ItemStack newItem = player.getInventory().getItem(e.getNewSlot());
            if (newItem != null) {
                if (newItem.getType() == Material.CROSSBOW || newItem.getType() == Material.BOW){
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "좀비는 활/석궁을 사용할 수 없습니다.");
                }
            }
        }
    }

    @EventHandler
    public void InteractChestZombie(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if (sideManager.isPlayerTeam(player.getName(), "zombie")) {
            if (e.getClickedBlock() == null) {
                return;
            }
            if (e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.CHEST_MINECART || e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "좀비는 상자를 사용할 수 없습니다.");
            }
        }
    }

    @EventHandler
    public void HopperBlock(BlockPlaceEvent e){
        Player player = e.getPlayer();
        if (sideManager.isPlayerTeam(player.getName(), "zombie")) {
            if (e.getBlock() == null) {
                return;
            }
            if (e.getBlock().getType() == Material.HOPPER || e.getBlock().getType() == Material.HOPPER_MINECART) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "좀비는 호퍼를 설치할 수 없습니다.");
            }
        }
    }
    @EventHandler
    public void onSwapHandsInInventory(InventoryClickEvent event){
        if(!event.getClick().equals(ClickType.SWAP_OFFHAND)){
            return;
        }
        if(!event.getSlotType().equals(InventoryType.SlotType.ARMOR)){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void LavaZombie(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if (sideManager.isPlayerTeam(player.getName(), "zombie")) {
            if (e.getItem() != null && e.getItem().getType() == Material.LAVA_BUCKET) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "좀비는 용암을 사용할 수 없습니다.");
            }
        }
    }

    @EventHandler
    public void InteractTradeZombie(PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        if (sideManager.isPlayerTeam(player.getName(), "zombie")) {
            if (e.getRightClicked() instanceof Villager) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "좀비는 주민과 거래할 수 없습니다.");
            }
        }
    }

    @EventHandler
    public void FoodMax(FoodLevelChangeEvent e){
        if (e.getEntity().getType() == EntityType.PLAYER){
            Entity entity = e.getEntity();
            Player player = (Player) entity;
            if (sideManager.isPlayerTeam(player.getName(), "zombie")){
                e.setCancelled(true);
                player.setFoodLevel(19);
            }
        }
    }

    @EventHandler
    public void onZombieDeath(PlayerDeathEvent e){
        Player player = (Player) e.getEntity();
        if (sideManager.isPlayerTeam(player.getName(), "zombie")){
            e.setDeathMessage("");
        }
    }

    @EventHandler
    public void SpiderFallenDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (sideManager.isPlayerTeam(player.getName(), "zombie")){
                ZombieJob job = zombieJobManager.getPlayerJob(player);
                if (job != null && job.getJob().equals("거미좀비")){
                    EntityDamageEvent.DamageCause cause = e.getCause();
                    if (cause == EntityDamageEvent.DamageCause.FALL){
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void DiaHumanTracker(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            if (sideManager.isPlayerTeam(player.getName(), "zombie")) {
                if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND) {
                    List<Player> randomplayer = new ArrayList<>();
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (!sideManager.isPlayerTeam(target.getName(), "zombie") &&
                                !sideManager.isPlayerTeam(target.getName(), "DIE")) {
                            randomplayer.add(target);
                        }
                    }
                    if (randomplayer.isEmpty()) {
                        player.sendMessage("추적할 수 있는 대상이 없습니다.");
                        return;
                    }
                    Player target = randomplayer.get(random.nextInt(randomplayer.size()));
                    Location targetLocation = target.getLocation();
                    setupCompass(player, target, targetLocation);
                }
            }
        }
    }

    private void setupCompass(Player player, Player target, Location targetLocation) {
        if (player.getInventory().contains(Material.COMPASS)) {
            player.getInventory().remove(Material.COMPASS);
        }
        player.setCompassTarget(targetLocation);
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName(target.getName() + "님의 §4위치");
        compass.setItemMeta(meta);
        player.getInventory().addItem(compass);
        player.sendMessage("나침반이 " + target.getName() + "의 위치를 가리키도록 설정되었습니다.");
    }
}