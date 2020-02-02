package co.kanepu.kitpvpperkone;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class main extends JavaPlugin implements Listener {
    private File ConfigFile;
    private FileConfiguration Config;
    private final HashMap<UUID, Integer> cooldownp = new HashMap<>();

    public void onEnable() {
        ConfigFile = new File(getDataFolder(), "config.yml");
        if (!ConfigFile.exists()) {
            ConfigFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        Config= new YamlConfiguration();
        try {
            Config.load(ConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Plugin made by KanTheAstronaut/Kanepu as requested by iEddie for monman11.com (if u r readin dis thomas plox gib me dev)");
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if (!cooldownp.isEmpty()) {
                    for(HashMap.Entry<UUID, Integer> e : cooldownp.entrySet()) {
                        cooldownp.replace(e.getKey(), e.getValue() - 1);
                    }
                }
            }
        };
        r.runTaskTimerAsynchronously(this, 0, 20);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getPlayer().getItemInHand().getType().equals(Material.FIREWORK_CHARGE)) {
            if (!e.getPlayer().hasPermission("kitpvpperkone.use")) {
                e.setCancelled(true);
                return;
            }
            if (cooldownp.containsKey(e.getPlayer().getUniqueId()) && cooldownp.get(e.getPlayer().getUniqueId()) <= 0)
                cooldownp.remove(e.getPlayer().getUniqueId());
            else if (cooldownp.containsKey(e.getPlayer().getUniqueId()) && cooldownp.get(e.getPlayer().getUniqueId()) > 0) {
                e.getPlayer().sendMessage(ChatColor.RED + "You need to wait " + cooldownp.get(e.getPlayer().getUniqueId()) + " seconds before using that again!");
                e.setCancelled(true);
            }
            if (!cooldownp.containsKey(e.getPlayer().getUniqueId())) {
                if (Config.getBoolean("usermsg"))
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Config.getString("usermsgi")));
                if (!e.getPlayer().hasPermission("kitpvpperkone.nocooldown"))
                    cooldownp.put(e.getPlayer().getUniqueId(), Config.getInt("cooldown"));
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Config.getInt("invisibility") * 20, 1));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kitpvponereload")) {
            sender.sendMessage(ChatColor.GREEN + "The configuration file has been reloaded!");
            Config = YamlConfiguration.loadConfiguration(ConfigFile);
            return true;
        }
        return false;
    }
}
