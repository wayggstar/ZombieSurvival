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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJob;
import org.wayggstar.zombiesurvival.Jobs.Human.HumanJobManager;
import org.wayggstar.zombiesurvival.Jobs.Zombie.ZombieJobManager;
import org.wayggstar.zombiesurvival.Team.SideManager;

public class JobAbility implements Listener {
    private SideManager sideManager;
    private ZombieJobManager zombieJobManager;
    private HumanJobManager humanJobManager;
    private JavaPlugin plugin;
    private final Cooldown cooldown;

    public JobAbility(SideManager sideManager, JavaPlugin plugin){
        this.sideManager = sideManager;
        this.cooldown = new Cooldown();
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        String skillName = "§4응급처치§a";
        int PARTICLE_COUNT = 10;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            if (!sideManager.isPlayerTeam(player.getName(), "zombie")){
                if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§a§l의사§r의 §4§l치료키트")) {
                    if (cooldown.isCooldown(player, skillName, 40)) {
                        long remainingTime = cooldown.getRemainingCooldown(player, skillName, 40);
                        player.sendMessage("§a스킬 '" + skillName + "'의 쿨타임이 " + ChatColor.RED + remainingTime + "§r초 남았습니다.");
                        return;
                    }
                    double myhealth = Math.min(player.getHealth() + 6.0, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    player.setHealth(myhealth);
                    player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), PARTICLE_COUNT);
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
                            target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(0, 2, 0), PARTICLE_COUNT);
                            target.sendMessage(ChatColor.GREEN + "의사에게 치유받았습니다. (체력 3칸 회복)");
                        }
                    }
                    cooldown.activateCooldown(player, skillName);
                }
            }
        }
    }

    @EventHandler
    public void ZombieGrab(PlayerInteractEvent event) {
        String skillName = "§6그랩§a";
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            if (sideManager.isPlayerTeam(player.getName(), "zombie")) {
                if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§6§l그랩")) {
                    if (cooldown.isCooldown(player, skillName, 50)) {
                        long remainingTime = cooldown.getRemainingCooldown(player, skillName, 50);
                        player.sendMessage("§a스킬 '" + skillName + "'의 쿨타임이 " + ChatColor.RED + remainingTime + "§r초 남았습니다.");
                        return;
                    }
                    grab(player);
                }
            }
        }
    }
    public void grab(Player player) {
        Location eyelocation = player.getEyeLocation();
        Vector direction = eyelocation.getDirection().clone();
        new BukkitRunnable() {
            @Override
            public void run() {
                Location now = eyelocation.clone();
                double max = 20.0;
                double distanceTraveled = 0.0;
                now.getWorld().spawnParticle(Particle.END_ROD, now, 1);
                now.add(direction.multiply(0.5));
                distanceTraveled += 0.5;
                for (Entity entity : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity target = (LivingEntity) entity;
                        now.getWorld().playSound(target.getLocation(), Sound.ENTITY_EVOKER_FANGS_ATTACK, 1.0f, 1.0f);
                        pullEntityToPlayer(player, target);
                        cancel();
                        return;
                    }
                }
                if (distanceTraveled >= max) {
                    cancel();}
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    public void pullEntityToPlayer(Player player, Entity target) {
        Location playerLocation = player.getLocation();
        Location targetLocation = target.getLocation();
        Vector pullDirection = playerLocation.toVector().subtract(targetLocation.toVector()).normalize();
        target.setVelocity(pullDirection.multiply(1.5));
    }
}
