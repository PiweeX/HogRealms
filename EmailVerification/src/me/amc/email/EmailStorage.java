package me.amc.email;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class EmailStorage {

	public static HashMap<String, SerializablePlayer> addresses = new HashMap<>(); // uuid, object
	public static final String FILE_NAME = "addresses.yml";
	
	public static void save() {
		try {
			
			YamlConfiguration config = new YamlConfiguration();
			config.createSection("addresses", addresses);
			config.save(new File(MainCore.instance.getDataFolder(), FILE_NAME));
			
		} catch(Exception ex) {
			MainCore.instance.getLogger().log(Level.SEVERE, "Email save failed!", ex);
		}
	}
	
	public static void load() {
		try {
			
			File file = new File(MainCore.instance.getDataFolder(), FILE_NAME);
			if(file.exists()) {
				YamlConfiguration config = new YamlConfiguration();
				config.load(file);
				
				ConfigurationSection section = config.getConfigurationSection("addresses");
				for(String key : section.getKeys(false)) {
					SerializablePlayer player = (SerializablePlayer) section.get(key);
					if(!player.getName().isEmpty() && !player.getEmail().isEmpty())
						addresses.put(key, player);
				}
			}
			
		} catch(Exception ex) {
			MainCore.instance.getLogger().log(Level.SEVERE, "Email load failed!", ex);
		}
	}
	
}
