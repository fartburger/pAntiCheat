package com.fartburger;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.VoxelShape;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.getServer;


public class AntiNoFall implements Listener {
    static double heightdif = 0;
    static boolean isInAir = false;
    static int fallenblocks = 0;
    static boolean waitforcheck=false;
    static int checktimer=0;
    static double expectedplyhealth=0;
    static int falldamage=0;

    static List<Material> fallcancellers = Arrays.asList(Material.WATER,Material.LAVA,Material.COBWEB,Material.SWEET_BERRY_BUSH);

    @EventHandler
    public void noNoFall(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        double xVel = (e.getTo().getX()-e.getFrom().getX())/.05;
        double yVel = (e.getTo().getY()-e.getFrom().getY())/.05;
        double zVel = (e.getTo().getZ()-e.getFrom().getZ())/.05;
        Location loc = player.getLocation();

        if(player.getGameMode()== GameMode.SURVIVAL&&!player.isOp()) {

            if (player.isGliding() || player.isClimbing()) {
                return;
            }
            player.getActivePotionEffects();
            if (player.getActivePotionEffects().size() != 0 && player.getActivePotionEffects().contains(PotionEffectType.SLOW_FALLING)) {
                return;
            }


            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ()) {
                if (!inAir(player.getWorld(), player.getLocation(), yVel)) {
                    falldamage = (fallenblocks > 3 ? fallenblocks - 3 : 0);
                    if (fallcancellers.contains(player.getWorld().getBlockAt(player.getLocation()).getType())) {
                        return;
                    }
                    if(player.getWorld().getBlockAt(player.getLocation()).isLiquid()) {
                        return;
                    }
                    switch (player.getWorld().getBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ()).getType()) {
                        case SLIME_BLOCK -> {
                            if (!player.isSneaking()) return;
                        }
                        case HONEY_BLOCK, HAY_BLOCK -> falldamage -= (int) (falldamage * (20 / 100.0f));
                        case BLACK_BED, BLUE_BED, BROWN_BED, CYAN_BED, LIGHT_BLUE_BED, LIGHT_GRAY_BED,
                                GRAY_BED, LIME_BED, GREEN_BED, MAGENTA_BED, ORANGE_BED, PINK_BED, PURPLE_BED,
                                RED_BED, WHITE_BED, YELLOW_BED -> falldamage -= (int) (falldamage * (50 / 100.0f));
                    }
                    if(player.getInventory().getBoots()!=null) {
                        if (player.getInventory().getBoots().containsEnchantment(Enchantment.PROTECTION_FALL)) {
                            falldamage -= (int) (falldamage * ((12 * player.getInventory().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_FALL)) / 100.0f));
                        }
                    }

                    //player.sendMessage("Fall damage you should receive: "+ChatColor.GREEN+"["+falldamage+"]");
                    expectedplyhealth = player.getHealth() - falldamage;
                    if (player.getHealth() > expectedplyhealth) {
                        //player.damage(falldamage);
                    }
                    //waitforcheck=true;
                    fallenblocks = 0;
                    return;
                } else {
                    if (e.getFrom().getBlockY() > e.getTo().getBlockY()) {
                        fallenblocks++;
                    }
                }
            }

        }
    }

    public boolean inAir(World w, Location l,double yvel) {
        for (int y=0;y>=-1;y--) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (w.getBlockAt(l.getBlockX() + x, l.getBlockY() +y, l.getBlockZ() + z).getType() != Material.AIR) {
                        return false;
                    }
                }
            }
        }
        if(w.getBlockAt(l.getBlockX(),l.getBlockY()-1,l.getBlockZ()).getType()==Material.AIR&&
        w.getBlockAt(l).getType()==Material.AIR) {
            return true;
        } else {
            return false;
        }
    }
}
