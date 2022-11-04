package com.fartburger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;


public class PlayerMineListener implements Listener {
    static List<Material> alertable = Arrays.asList(Material.DIAMOND_ORE,Material.ANCIENT_DEBRIS,Material.GOLD_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,Material.DEEPSLATE_GOLD_ORE);
    static Map<UUID,Integer> alertableCount = new HashMap<>();
    @EventHandler
    public void onMine(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if(alertable.contains(e.getBlock().getType())) {
            getLogger().info(e.getPlayer().getName()+" has mined "+e.getBlock().getType().name()+" at "+
                    Math.round(e.getBlock().getLocation().getX())+","+Math.round(e.getBlock().getLocation().getY())+","+Math.round(e.getBlock().getLocation().getZ()));

        }
    }
}
