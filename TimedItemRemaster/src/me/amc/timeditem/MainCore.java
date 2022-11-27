package me.amc.timeditem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MainCore extends JavaPlugin {

	public static MainCore instance; // Singleton
	
	public ConfigHelper configHelper;
	public DateTimeFormatter dateFormatter;
	
	@Override
	public void onEnable() {
		instance = this;

		saveDefaultConfig();
		reloadConfig();
		
		dateFormatter = DateTimeFormatter.ofPattern(configHelper.getTimeFormat());
		
		getCommand("timeditem").setExecutor(new TimeCommand());
		
		new TimeEvent();
		
		BukkitRunnable runnable = new BukkitRunnable() {

			@Override
			public void run() {
				instance.getLogger().log(Level.INFO, "Checking for expiring items...");
				int items = 0;
				for(Player player : Bukkit.getOnlinePlayers()) {
					for(ItemStack item : player.getInventory()) {
						if(item != null && hasItemExpired(item)) {
							items++;
							player.getInventory().remove(item);
							player.sendMessage(configHelper.getExpirationMessage());
						}
					}
					if(player.getOpenInventory() != null) {
						for(ItemStack item : player.getOpenInventory().getTopInventory()) {
							if(item != null && hasItemExpired(item)) {
								items++;
								player.getOpenInventory().getTopInventory().remove(item);
							}
						}
					}
					player.updateInventory();
				}
				instance.getLogger().log(Level.INFO, "Check completed! Item(s) expired: "+items);
			}
			
		};
		
		long period = 20 * 60 * configHelper.getCheckTime();
		
		runnable.runTaskTimer(this, 0, period);
		
	}
	
	// Check if item has expired
	public boolean hasItemExpired(ItemStack item) {
		if(item.hasItemMeta())
			if(item.getItemMeta().getPersistentDataContainer().has(TimeCommand.DATE_KEY, PersistentDataType.STRING))
			{
				String s = item.getItemMeta().getPersistentDataContainer().get(TimeCommand.DATE_KEY, PersistentDataType.STRING);
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime itemTime = LocalDateTime.parse(s, dateFormatter);
			
				long diff = ChronoUnit.SECONDS.between(now, itemTime);
				//System.out.println(item.getType()+" "+diff);
				if(diff < 0) 
					return true;
			}
		
		return false;
	}
	
	@Override
	public void reloadConfig() {
		super.reloadConfig();
		configHelper = new ConfigHelper(this.getConfig());
	}
	
}
