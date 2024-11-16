package org.wayggstar.zombiesurvival.Jobs;

import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public boolean isCooldown(Player player, String skill, long cooldownTimeSecond){
        UUID playerID = player.getUniqueId();
        Map<String, Long> playerCooldown = cooldowns.getOrDefault(playerID, new HashMap<>());

        long currentTime = Instant.now().getEpochSecond();
        long lastUseTime = playerCooldown.getOrDefault(skill, 0L);

        return (currentTime - lastUseTime) < cooldownTimeSecond;

    }

    public void activateCooldown(Player player, String skill) {
        UUID playerId = player.getUniqueId();
        cooldowns.computeIfAbsent(playerId, k -> new HashMap<>()).put(skill, Instant.now().getEpochSecond());
    }

    public long getRemainingCooldown(Player player, String skill, long cooldownTimeSecond) {
        UUID playerId = player.getUniqueId();
        Map<String, Long> playerCooldowns = cooldowns.getOrDefault(playerId, new HashMap<>());

        long currentTime = Instant.now().getEpochSecond();
        long lastUseTime = playerCooldowns.getOrDefault(skill, 0L);
        long remainingCooldown = cooldownTimeSecond - (currentTime - lastUseTime);

        return Math.max(remainingCooldown, 0);
    }

}
