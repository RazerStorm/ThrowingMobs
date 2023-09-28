package be.razerstorm.throwingmobs;

import org.bukkit.Bukkit;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class ThrowingMobs extends JavaPlugin implements Listener {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (cooldowns.containsKey(player.getUniqueId())) {
                    if (cooldowns.get(player.getUniqueId()) + 2*1000 > System.currentTimeMillis()) {
                        return;
                    }
                }
                int radius = getConfig().getInt("radius-around-players");
                player.getNearbyEntities(radius, radius,radius).forEach(entity -> {
                    if (entity instanceof org.bukkit.entity.LivingEntity livingEntity) {
                        TNTPrimed tnt = livingEntity.getWorld().spawn(livingEntity.getLocation(), TNTPrimed.class);
                        tnt.setVelocity(livingEntity.getLocation().getDirection().multiply(getConfig().getInt("throw-strength")).setY(1));
                    }
                });
            });
        }, 5*20, getConfig().getInt("throw-interval") * 20L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        cooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }
}