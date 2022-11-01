package com.fartburger;

import com.google.common.util.concurrent.Service;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PlayerMoveListener implements Listener {

    Thread t = new Thread(() -> {

    });

    static int a = 0;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        double xVel = (e.getTo().getX()-e.getFrom().getX())/.05;
        double yVel = (e.getTo().getY()-e.getFrom().getY())/.05;
        double zVel = (e.getTo().getZ()-e.getFrom().getZ())/.05;

        player.sendMessage(Component.text("xvel ["+Math.round(xVel)+"] yvel ["+Math.round(yVel)+"] zvel ["+Math.round(zVel)+"]"));

        if(player.getGameMode()==GameMode.SURVIVAL&&!player.isOp()) {
            if(player.getActivePotionEffects().size()==0&&!onIce(player.getWorld(),player.getLocation())&&player.isOnGround()&&!player.isInsideVehicle()) {
                if (xVel>15||zVel>15) {
                    player.kick(Component.text(ChatColor.GREEN+"Detected Speed. Speed limit on foot is 15 blocks per second."));
                }
            }
            if(player.isInsideVehicle()) {
                if(!onIce(player.getWorld(),player.getLocation())) {
                    if(xVel>9||zVel>9) {
                        player.kick(Component.text(ChatColor.GREEN+"Detected Vehicle Speed. Speed limit in boats off ice in 9 blocks per second."));
                    }
                } else {
                    if(xVel>40||zVel>40) {
                        player.kick(Component.text(ChatColor.GREEN+"Detected Vehicle Speed. Speed limit in boats on ice is 40 blocks per second."));
                    }
                }
            }
            if(inAir(player.getWorld(),player.getLocation())&&player.getPlayerTime()>6500) {
                if(yVel>=-2) {
                    a++;
                } else {
                    a=0;
                }
                if(a>80) {
                    player.kick(Component.text(ChatColor.GREEN+"Flight detected."));
                    a=0;
                }
            }
        }
    }

    public boolean onIce(World w, Location l) {
        if(w.getBlockAt(l.getBlockX(),l.getBlockY()-1,l.getBlockZ()).getType()==Material.ICE||
                w.getBlockAt(l.getBlockX(),l.getBlockY()-1,l.getBlockZ()).getType()==Material.BLUE_ICE||
                w.getBlockAt(l.getBlockX(),l.getBlockY()-1,l.getBlockZ()).getType()==Material.PACKED_ICE||
                w.getBlockAt(l.getBlockX(),l.getBlockY()-1,l.getBlockZ()).getType()==Material.FROSTED_ICE||
                w.getBlockAt(l.getBlockX(),l.getBlockY()-2,l.getBlockZ()).getType()==Material.ICE||
                w.getBlockAt(l.getBlockX(),l.getBlockY()-2,l.getBlockZ()).getType()==Material.BLUE_ICE||
                w.getBlockAt(l.getBlockX(),l.getBlockY()-2,l.getBlockZ()).getType()==Material.PACKED_ICE||
                w.getBlockAt(l.getBlockX(),l.getBlockY()-2,l.getBlockZ()).getType()==Material.FROSTED_ICE) {
            return true;
        } else {
            return false;
        }
    }
    public boolean inAir(World w,Location l) {
        if(w.getBlockAt(l.getBlockX(),l.getBlockY()-3,l.getBlockZ()).getType()==Material.AIR&&
        w.getBlockAt(l.getBlockX(),l.getBlockY()-1,l.getBlockZ()).getType()!=Material.AIR) {
            return true;
        } else {
            return false;
        }
    }






}
