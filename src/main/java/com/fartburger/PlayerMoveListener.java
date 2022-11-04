package com.fartburger;

import com.google.common.util.concurrent.Service;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class PlayerMoveListener implements Listener {

    static List<Material> iceblocks = Arrays.asList(Material.ICE,Material.PACKED_ICE,Material.BLUE_ICE,Material.FROSTED_ICE);
    static List<Material> cancelfallblocks = Arrays.asList(Material.WATER,Material.LAVA,Material.COBWEB);
    static int a = 0;
    static int officetime = 0;
    static int timenotgliding = 0;
    static int timenotjumping = 0;
    static int botmotionticks = 0;
    static int entervehicledelay = 0;

    static double lxVel=0;
    static double lyVel=0;
    static double lzVel=0;

    static int fallenBlocks=0;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        double xVel = (e.getTo().getX()-e.getFrom().getX())/.05;
        double yVel = (e.getTo().getY()-e.getFrom().getY())/.05;
        double zVel = (e.getTo().getZ()-e.getFrom().getZ())/.05;

        // work in progress
        /*
        if(e.getTo().getBlockY()<e.getFrom().getBlockY()) {
            fallenBlocks++;
        } else {
            int damageTaken = fallenBlocks>3 ? fallenBlocks-3 : 0;

            fallenBlocks=0;
        }
         */

        // for testing purposes only
        //player.sendMessage(Component.text("xvel ["+Math.round(xVel)+"] yvel ["+Math.round(yVel)+"] zvel ["+Math.round(zVel)+"]"));
        if(e.getFrom().getX()!=e.getTo().getX()||e.getFrom().getY()!=e.getTo().getY()||e.getFrom().getZ()!=e.getTo().getZ()) {
            //player.sendMessage(Component.text("getTo:["+ ChatColor.BLUE + Math.abs(e.getTo().getX()-e.getFrom().getX()) + ChatColor.WHITE +  "," + ChatColor.GREEN + e.getTo().getZ() +
            //        "] getFrom:[" + Math.abs(e.getTo().getZ()-e.getFrom().getZ()) + "," + e.getFrom().getZ() + "]"));

            botmotionticks = (((Math.abs(e.getTo().getX()-e.getFrom().getX())<=0.003)||Math.abs(e.getTo().getZ()-e.getFrom().getZ())<=0.003)&&
                    Math.abs(e.getTo().getY()-e.getFrom().getY())<0.01&&
                    Math.abs(e.getTo().getX()-e.getFrom().getX())!=0&&Math.abs(e.getTo().getZ()-e.getFrom().getZ())!=0) ? clamp(0,40,botmotionticks+1) : 0;

            if(botmotionticks>=30) {
                Bukkit.getServer().getOnlinePlayers().forEach(ply -> {
                    if(ply.isOp()) {
                        ply.sendMessage(Component.text(ChatColor.GREEN+player.getName()+" triggered baritone detection at ["+player.getLocation().getBlockX()+
                                ","+player.getLocation().getBlockY()+","+player.getLocation().getBlockZ()));
                        getLogger().info(player.getName()+" triggered baritone detection.");
                    }
                });
                botmotionticks=0;
            }
        }

        // I LOVE IF ELSE I LOVE IF ELSE I LOVE IF ELSE I LOVE IF ELSE I LOVE IF ELSE

        timenotgliding = player.isGliding() ? 0 : clamp(0,70,timenotgliding+1);
        timenotjumping = (yVel!=0) ? 0 : clamp(0,7,timenotjumping+1);
        officetime = onIce(player.getWorld(),player.getLocation()) ? 0 : clamp(0,70,officetime+1);
        entervehicledelay = player.isInsideVehicle() ? 0 : clamp(0,10,entervehicledelay+1);
        if(player.getGameMode()==GameMode.SURVIVAL&&!player.isOp()) {
            if((player.getActivePotionEffects().size()==0||player.getActivePotionEffects()==null)&&
                    !onIce(player.getWorld(),player.getLocation())&&!player.isInsideVehicle()&&!inAir(player.getWorld(),player.getLocation())
                    &&timenotgliding>=70&&timenotjumping>=8) {
                if(player.getInventory().getBoots()!=null) {
                    if(player.getInventory().getBoots().containsEnchantment(Enchantment.SOUL_SPEED) &&
                            (player.getWorld().getBlockAt(player.getLocation().getBlockX(),player.getLocation().getBlockY()-1,
                            player.getLocation().getBlockZ()).getType()==Material.SOUL_SOIL||player.getWorld().getBlockAt(player.getLocation().getBlockX(),player.getLocation().getBlockY()-1,
                                    player.getLocation().getBlockZ()).getType()==Material.SOUL_SAND)) {
                        double maxspeed = 7*(player.getInventory().getBoots().getEnchantmentLevel(Enchantment.SOUL_SPEED)*
                                1.05)+1.3;
                        if(xVel>maxspeed||zVel>maxspeed) {
                            player.kick(Component.text(ChatColor.GREEN + "Detected Speed. blah blah blah too fast for soul speed boots"));
                            Bukkit.broadcast(Component.text(ChatColor.GREEN+player.getName()+" was kicked for speeding."));
                        }
                    }
                }
                if (xVel > 7 || zVel > 7 && yVel == 0) {
                    player.kick(Component.text(ChatColor.GREEN + "Detected Speed. Speed limit on foot is 7 blocks per second."));
                    Bukkit.broadcast(Component.text(ChatColor.GREEN+player.getName()+" was kicked for speeding."));
                }
            }

            if(player.isInsideVehicle()) {
                if(entervehicledelay>=10) {
                    if (!onIce(player.getWorld(), player.getLocation()) && officetime >= 70) {
                        if(player.getVehicle().equals(EntityType.HORSE)) {
                            if(xVel>15||zVel>15) {
                                player.kick(Component.text(ChatColor.GREEN + "Detected Entity Speed. Speed limit on horses is 15 blocks per second."));
                                Bukkit.broadcast(Component.text(ChatColor.GREEN + player.getName() + " was kicked for speeding on a horse."));
                            }
                        }
                        if(player.getVehicle().equals(EntityType.BOAT)||player.getVehicle().equals(EntityType.CHEST_BOAT)) {
                            if (xVel > 9 || zVel > 9) {
                                player.kick(Component.text(ChatColor.GREEN + "Detected Vehicle Speed. Speed limit in boats off ice is 9 blocks per second."));
                                Bukkit.broadcast(Component.text(ChatColor.GREEN + player.getName() + " was kicked for speeding in a boat."));
                                officetime = 0;
                            } else {
                                if (xVel > 40 || zVel > 40) {
                                    player.kick(Component.text(ChatColor.GREEN + "Detected Vehicle Speed. Speed limit in boats on ice is 40 blocks per second."));
                                    Bukkit.broadcast(Component.text(ChatColor.GREEN + player.getName() + " was kicked for speeding in a boat."));
                                }
                            }
                        }
                        if(player.getVehicle().equals(EntityType.PIG)) {
                            if(xVel>6||zVel>6) {
                                player.kick(Component.text(ChatColor.GREEN + "Detected Entity Speed. Speed limit on pigs is 40 blocks per second."));
                                Bukkit.broadcast(Component.text(ChatColor.GREEN + player.getName() + " was kicked for speeding on a pig"));
                            }
                        }

                    }
                }
            } else {
                entervehicledelay=0;
            }

            if(inAir(player.getWorld(),player.getLocation())&&player.getPlayerTime()>6500&&!player.isGliding()) {
                if(yVel>=-2) {
                    a++;
                } else {
                    a=0;
                }
                if(a>30) {
                    player.kick(Component.text(ChatColor.GREEN+"Flight detected."));
                    Bukkit.broadcast(Component.text(ChatColor.GREEN+player.getName()+" was kicked for flying."));
                    a=0;
                }
            }
        }

        lxVel = (e.getTo().getX()-e.getFrom().getX())/.05;
        lyVel = (e.getTo().getY()-e.getFrom().getY())/.05;
        lzVel = (e.getTo().getZ()-e.getFrom().getZ())/.05;
    }

    public boolean onIce(World w, Location l) {
        for(int y=-2;y<=2;y++) {
            for(int x=-1;x<=1;x++) {
                for(int z=-1;z<=1;z++) {
                    if(iceblocks.contains(w.getBlockAt(l.getBlockX()+x,l.getBlockY()+y,l.getBlockZ()+z).getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean inAir(World w,Location l) {

            for(int x=-1;x<=1;x++) {
                for(int z=-1;z<=1;z++) {
                    if(w.getBlockAt(l.getBlockX()+x,l.getBlockY()-1,l.getBlockZ()+z).getType()!=Material.AIR||
                    w.getBlockAt(l.getBlockX()+x,l.getBlockY(),l.getBlockZ()+z).getType()!=Material.AIR) {
                        return false;
                    }
                }
            }
            if(w.getBlockAt(l.getBlockX(),l.getBlockY()-3,l.getBlockZ()).getType()==Material.AIR) {
                return true;
            } else {
                return false;
            }
    }

    public int clamp(int min,int max,int val) {
        return (val<min) ? min : (Math.min(val, max));
    }






}
