package com.fartburger;

import org.bukkit.plugin.java.JavaPlugin;

public final class main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Anti cheat online.");
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(),this);
        getServer().getPluginManager().registerEvents(new PlayerMineListener(),this);
        getServer().getPluginManager().registerEvents(new AntiNoFall(),this);
        this.getCommand("hello").setExecutor(new HelloCommand());


    }

    @Override
    public void onDisable() {
        getLogger().info("Anti cheat offline.");
    }


}
