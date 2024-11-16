package org.wayggstar.zombiesurvival.Jobs.Human;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class HumanJobManager {
    private final List<HumanJob> availableJobs;
    private final Map<HumanJob, Integer> jobLimits;
    private final Map<HumanJob, List<Player>> jobAssignments;
    private final Map<Player, HumanJob> playerJobs;

    public HumanJobManager(List<HumanJob> availableJobs){
        this.availableJobs = availableJobs;
        this.jobLimits = new HashMap<>();
        this.jobAssignments = new HashMap<>();
        this.playerJobs = new HashMap<>();

        for (HumanJob job : availableJobs){
            int MAX_PLAYERS_PER_JOB = 2;
            jobLimits.put(job, MAX_PLAYERS_PER_JOB);
            jobAssignments.put(job, new ArrayList<>());
        }
    }

    public void assignJob(Player player){
        if (playerJobs.containsKey(player)){
            return;
        }

        HumanJob job = getRandomAvailableJob();
        if (job != null){
            playerJobs.put(player, job);
            jobAssignments.get(job).add(player);
            player.getInventory().addItem(job.getStartingItems().toArray(new ItemStack[0]));
            player.sendMessage(ChatColor.GREEN + "축하합니다! 당신은 인간 직업 '" + job.getJob() + ChatColor.GREEN + "'이/가 되었습니다.");
            return;
        }
        player.sendMessage(ChatColor.RED + "아쉽게도 일반인이군요..");
    }
    public HumanJob getPlayerJob(Player player){
        return playerJobs.get(player);
    }
    private HumanJob getRandomAvailableJob(){
        Random random = new Random();
        List<HumanJob> availableJobs = this.availableJobs.stream()
                .filter(job -> jobAssignments.get(job).size() < jobLimits.get(job))
                .collect(Collectors.toList());

        if (availableJobs.isEmpty()) {
            return null;
        }

        return availableJobs.get(random.nextInt(availableJobs.size()));
    }

    public void resetJobs() {
        playerJobs.clear();
        for (HumanJob job : availableJobs) {
            jobAssignments.put(job, new ArrayList<>());
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            HumanJob job = playerJobs.get(player);
            if (job != null) {
                player.getInventory().removeItem(job.getStartingItems().toArray(new ItemStack[0]));
            }
            player.sendMessage(ChatColor.RED + "게임 종료로 인해 직업이 초기화되었습니다.");
        }
    }

    public boolean forceAssignJob(Player player, HumanJob job) {
        if (!availableJobs.contains(job)) {
            player.sendMessage(ChatColor.RED + "직업 '" + job.getJob() + "'은(는) 유효하지 않습니다.");
            return false;
        }

        HumanJob currentJob = playerJobs.get(player);
        if (currentJob != null) {
            jobAssignments.get(currentJob).remove(player);
            player.getInventory().removeItem(currentJob.getStartingItems().toArray(new ItemStack[0]));
        }

        playerJobs.put(player, job);
        jobAssignments.get(job).add(player);
        player.getInventory().addItem(job.getStartingItems().toArray(new ItemStack[0]));
        player.sendMessage(ChatColor.GREEN + "강제로 '" + job.getJob() + "' 직업이 할당되었습니다.");
        return true;
    }

    public List<HumanJob> getAvailableJobs() {
        return new ArrayList<>(availableJobs);
    }

    public List<String> getJobNames() {
        return availableJobs.stream()
                .map(HumanJob::getJob)
                .collect(Collectors.toList());
    }
}
