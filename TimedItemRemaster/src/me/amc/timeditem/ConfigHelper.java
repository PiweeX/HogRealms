package me.amc.timeditem;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHelper {

	private FileConfiguration config;
	
	public ConfigHelper(FileConfiguration config) {
		this.config = config;
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	private String getColoredMessage(String message) {
		return config.getString(message).replace('&', '§');
	}
	
	public String getTimeFormat() {
		return config.getString("TimeFormat");
	}
	
	public String getHoldItemMessage() {
		return getColoredMessage("HoldItemMessage");
	}
	
	public String getWrongSyntaxMessage() {
		String m = getColoredMessage("WrongSyntaxMessage");
		m = m.replace("%format%", getTimeFormat());
		return m;
	}
	
	public String getDateLorePrefix() {
		return getColoredMessage("DateLorePrefix");
	}
	
	public String getExpirationMessage() {
		return getColoredMessage("ExpirationMessage");
	}
	
	public int getCheckTime() {
		return config.getInt("CheckTime");
	}
}
