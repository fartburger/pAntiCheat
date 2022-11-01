package com.fartburger;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Anti cheat online.");
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
