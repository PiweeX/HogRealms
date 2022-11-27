package me.amc.timeditem;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TimeEvent implements Listener {

	public TimeEvent() {
		MainCore.instance.getServer().getPluginManager().registerEvents(this, MainCore.instance);
	}
	
	@EventHandler
	public void openInv(InventoryOpenEvent event) {
		Inventory inv = event.getInventory();
		
		for(ItemStack item : inv.getStorageContents()) {
			if(item == null || item.getType() == Material.AIR) continue;
				
			if(MainCore.instance.hasItemExpired(item)) {
				inv.remove(item);
				//System.out.println("removed!");
			}
			
		}
		
	}
	
}
