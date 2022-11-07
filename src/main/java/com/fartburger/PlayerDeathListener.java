package com.fartburger;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class PlayerDeathListener implements Listener {
    static Map<UUID,Integer> deathCount = new HashMap<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        deathCount.keySet().forEach(key -> {
            if(Bukkit.getServer().getPlayer(key)==null) {
                deathCount.remove(key);
            }
        });
        Player player = e.getPlayer();
        if(deathCount.containsKey(player.getUniqueId())) {
            deathCount.put(player.getUniqueId(),deathCount.get(player.getUniqueId())+1);
            if(deathCount.get(player.getUniqueId())==10) {
                player.sendMessage(Component.text(ChatColor.GREEN+" skill issue"));
            }
        } else {
            deathCount.put(player.getUniqueId(),1);
        }
    }
}
