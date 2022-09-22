package me.amc.email;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

	public JoinEvent() {
		MainCore.instance.getServer().getPluginManager().registerEvents(this, MainCore.instance);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if(!EmailStorage.addresses.containsKey(player.getUniqueId().toString())) 
			player.sendMessage(MainCore.instance.configHelper.getVerificationReminder());
		
	}
	
}
