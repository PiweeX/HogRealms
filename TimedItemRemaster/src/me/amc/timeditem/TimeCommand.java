package me.amc.timeditem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class TimeCommand implements TabExecutor {

	public static final NamespacedKey DATE_KEY = new NamespacedKey(MainCore.instance, "date");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) return false;
		
		Player player = (Player) sender;
		
		if(args.length == 2) { // /timeditem day hour
			if(player.getInventory().getItemInMainHand() == null 
					|| player.getInventory().getItemInMainHand().getType() == Material.AIR) {
				player.sendMessage(MainCore.instance.configHelper.getHoldItemMessage());
				return false;
			}
			
			ItemStack item = player.getInventory().getItemInMainHand();
			ItemMeta meta = item.getItemMeta();
			String date = args[0]+" "+args[1];
			meta.getPersistentDataContainer().set(DATE_KEY, PersistentDataType.STRING, date);
			
			String loreDate = MainCore.instance.configHelper.getDateLorePrefix()+date;
			
			if(meta.hasLore()) {
				List<String> newLore = new ArrayList<String>();
				for(String line : meta.getLore()) {
					if(line.startsWith(MainCore.instance.configHelper.getDateLorePrefix()))
						newLore.add(loreDate);
					else
						newLore.add(line);
				}
				meta.setLore(newLore);
			} else {
				meta.setLore(Arrays.asList(loreDate));
			}
			
			item.setItemMeta(meta);
			player.getInventory().setItemInMainHand(item);
			
		} else {
			player.sendMessage(MainCore.instance.configHelper.getWrongSyntaxMessage());
			/*
			if(player.getInventory().getItemInMainHand()!=null) { //testing
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item.getItemMeta().getPersistentDataContainer().has(DATE_KEY, PersistentDataType.STRING))
					player.sendMessage(item.getItemMeta().getPersistentDataContainer().get(DATE_KEY, PersistentDataType.STRING));
			}
			*/
			return false;
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		String parts[] = MainCore.instance.configHelper.getTimeFormat().split(" ");
		if(args.length <= 2 && args.length > 0) return Arrays.asList(parts[args.length-1]);

		return null;
	}
	
}
