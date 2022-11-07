package com.fartburger;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;


public class PlayerMineListener implements Listener {
    static List<Material> alertable = Arrays.asList(Material.DIAMOND_ORE, Material.ANCIENT_DEBRIS,
            Material.DEEPSLATE_DIAMOND_ORE);
    static Map<UUID, Integer> alertableCount = new HashMap<>();

    @EventHandler
    public void onMine(BlockBreakEvent e) {
        alertableCount.keySet().forEach(key -> {
            if (getServer().getPlayer(key) == null) {
                alertableCount.remove(key);
            }
        });
        Player player = e.getPlayer();
        if (alertable.contains(e.getBlock().getType())) {
            getLogger().info(e.getPlayer().getName() + " has mined " + e.getBlock().getType().name() + " at " +
                    Math.round(e.getBlock().getLocation().getX()) + "," + Math.round(e.getBlock().getLocation().getY()) + "," + Math.round(e.getBlock().getLocation().getZ()));
            if (alertableCount.containsKey(player.getUniqueId())) {
                alertableCount.put(player.getUniqueId(), alertableCount.get(player.getUniqueId()) + 1);
                if (alertableCount.get(player.getUniqueId()) == 32) {
                    getServer().getOnlinePlayers().forEach(ply -> {
                        if (ply.isOp()) {
                            ply.sendMessage(Component.text(ChatColor.RED + player.getName() + " has mined 32 supervaluable ores (diamond/ancient debris) in the time" +
                                    " since they joined the server. The odds of them using xray are about 99.9999999999999%."));
                            getLogger().info(player.getName()+ "has mined 32 supervaluable ores (diamond/ancient debris) in the time" +
                                    " since they joined the server. The odds of them using xray are about 99.9999999999999%.");
                        }
                    });
                    player.sendMessage(Component.text(ChatColor.DARK_RED+" Stop xraying please"));
                }
            } else {
                alertableCount.put(player.getUniqueId(), 1);
            }
        }
    }
}
