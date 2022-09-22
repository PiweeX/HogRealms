package me.amc.psihotest;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIManager extends PlaceholderExpansion {

	private MainCore plugin;
    
    public PAPIManager(MainCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getAuthor() {
        return "Arionas_MC";
    }
    
    @Override
    public String getIdentifier() {
        return "psihotest";
    }

    @Override
    public String getVersion() {
        return ""+MainCore.instance.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }
    
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("active")){
            return ""+plugin.getPlayersOnline();
        }
        
        if(params.equalsIgnoreCase("max")) {
            return ""+plugin.getMaxPlayers();
        }
        
        return null; // Placeholder is unknown by the Expansion
    }
	
}
