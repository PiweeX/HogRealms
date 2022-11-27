package me.amc.psihotest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHelper {

private FileConfiguration config;
	
	public ConfigHelper(FileConfiguration config) {
		this.config = config;
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	public boolean isFakeMaxPlayersEnabled() {
		return config.getBoolean("FakeMaxPlayers.Enable");
	}
	
	public int getFakeMaxPlayers() {
		return config.getInt("FakeMaxPlayers.MaxPlayers");
	}
	
	public boolean isFakeServerListEnabled() {
		return config.getBoolean("FakeServerList.Enable");
	}
	
	public boolean isFakePlayersInListEnabled() {
		return config.getBoolean("FakeServerList.FakePlayersInList");
	}
	
	public List<String> getFakePlayers() {
		List<String> temp = config.getStringList("FakePlayers");
		List<String> edited = new ArrayList<>();
		for(String s : temp) {
			if(s.contains(",")) s = s.split(",")[1];
			s = s.replace('&', '§');
			s = ChatColor.stripColor(s);
			edited.add(s);
		}
		return edited;
	}
	
	public List<String> getFakePlayersPrefix() {
		List<String> temp = config.getStringList("FakePlayers");
		List<String> edited = new ArrayList<>();
		for(String s : temp) {
			if(s.contains(",")) s = s.split(",")[0];
			else s = "";
			s = s.replace('&', '§');
			edited.add(s);
		}
		return edited;
	}
	
	public List<String> getKickCommands() {
		return config.getStringList("KickCommands");
	}
	
	public String getJoinMessage(String name) {
		return config.getString("JoinMessage.Text").replace('&', '§').replace("%player%", name);
	}
	
	public boolean isJoinOnServerEnabled() {
		return config.getBoolean("JoinMessage.OnServer");
	}
	
	public boolean isJoinOnDiscordEnabled() {
		return config.getBoolean("JoinMessage.OnDiscord");
	}
	
	public String getLeaveMessage(String name) {
		return config.getString("LeaveMessage.Text").replace('&', '§').replace("%player%", name);
	}
	
	public boolean isLeaveOnServerEnabled() {
		return config.getBoolean("LeaveMessage.OnServer");
	}
	
	public boolean isLeaveOnDiscordEnabled() {
		return config.getBoolean("LeaveMessage.OnDiscord");
	}
	
	public int getRandomEventMin() {
		return config.getInt("RandomEvent.Min");
	}
	
	public int getRandomEventMax() {
		return config.getInt("RandomEvent.Max");
	}
	
	public int getRandomEventJoinChance() {
		return config.getInt("RandomEvent.JoinChance");
	}
	
}
