package org.wayggstar.zombiesurvival.Jobs;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJob;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJobManager;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJobManager;
import org.wayggstar.zombiesurvival.Team.SideManager;

import java.util.Objects;

public class JobAbility implements Listener {
    private SideManager sideManager;
    private ZombieJobManager zombieJobManager;
    private final HumanJobManager humanJobManager;
    private JavaPlugin plugin;
    private final Cooldown cooldown;

    public JobAbility(SideManager sideManager, JavaPlugin plugin, HumanJobManager humanJobManager){
        this.sideManager = sideManager;
        this.cooldown = new Cooldown();
        this.plugin = plugin;
        this.humanJobManager = humanJobManager;
    }
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        String skillName = "§4응급처치§a";
        int PARTICLE_COUNT = 10;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getHand() != EquipmentSlot.OFF_HAND) {
                Player player = event.getPlayer();
                if (!sideManager.isPlayerTeam(player.getName(), "zombie")) {
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand.getItemMeta().getDisplayName().equals("§a§l의사§r의 §4§l치료키트")) {

                        if (cooldown.isCooldown(player, skillName, 40)) {
                            long remainingTime = cooldown.getRemainingCooldown(player, skillName, 40);
                            player.sendMessage("§a스킬 '" + skillName + "'의 쿨타임이 " + ChatColor.RED + remainingTime + "§a초 남았습니다.");
                            return;
                        }
                        double myhealth = Math.min(player.getHealth() + 6.0, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                        player.setHealth(myhealth);
                        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 3, 0), PARTICLE_COUNT);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        player.sendMessage(ChatColor.GREEN + "자신을 치유했습니다. (체력 3칸 회복)");

                        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
                            if (entity instanceof Player) {
                                Player target = (Player) entity;
                                if (sideManager.isPlayerTeam(target.getName(), "zombie")) {
                                    return;
                                }
                                double heal = 6.0;
                                double newHealth = Math.min(target.getHealth() + heal, target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                                target.setHealth(newHealth);
                                target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(0, 3, 0), PARTICLE_COUNT);
                                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                target.sendMessage(ChatColor.GREEN + "의사에게 치유받았습니다. (체력 3칸 회복)");
                            }
                        }
                        cooldown.activateCooldown(player, skillName);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerInventory inventory = player.getInventory();
        event.getDrops().removeIf(item -> item.getItemMeta().getDisplayName().equalsIgnoreCase("§a§l의사§r의 §4§l치료키트"));
        event.getDrops().removeIf(item -> item.getItemMeta().getDisplayName().equalsIgnoreCase("§6§l광부§r의 §7§l곡괭이"));
        event.getDrops().removeIf(item -> item.getItemMeta().getDisplayName().equalsIgnoreCase("§7§l글라이더§r의 §0망가진 §7§l날개"));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§a§l의사§r의 §4§l치료키트") ||
        event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§6§l광부§r의 §7§l곡괭이") ||
        event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§7§l글라이더§r의 §0망가진 §7§l날개")){
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "이 아이템은 버릴 수 없습니다!");

        }
    }
}
