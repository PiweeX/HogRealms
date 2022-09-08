package me.amc.email;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class MainCore extends JavaPlugin {

	public static MainCore instance; // Singleton
	
	public ConfigHelper configHelper;
	
	@Override
	public void onEnable() {
		instance = this;
		
		ConfigurationSerialization.registerClass(SerializablePlayer.class, "PlayerEmail");
		
		saveDefaultConfig();
		reloadConfig();
		
		EmailStorage.load();
		
		getCommand("verify").setExecutor(new VerifyCommand());
		
		new JoinEvent();
		
	}
	
	@Override
	public void reloadConfig() {
		super.reloadConfig();
		configHelper = new ConfigHelper(this.getConfig());
	}
	
	@Override
	public void onDisable() {
		EmailStorage.save();
	}
	
}
