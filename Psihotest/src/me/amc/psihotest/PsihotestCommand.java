package me.amc.psihotest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class PsihotestCommand implements CommandExecutor {

	public static Permission reloadPerm = new Permission("psihotest.reload");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 0) { // Default credits command
			sender.sendMessage(ChatColor.YELLOW+"Thanks for purchasing Psihotest and trusting Hogo Studios since 2017.");
			sender.sendMessage(ChatColor.YELLOW+"Our motive is simplicity and efficiency. We hope the plugin will help your community!");
			sender.sendMessage(ChatColor.YELLOW+"Plugin author: "+ChatColor.RED+"Arionas_MC");
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission(reloadPerm)) {
				try {
					MainCore.instance.reloadConfig();
					sender.sendMessage(ChatColor.GREEN+"Psihotest config reloaded!");		
				} catch(Exception ex) {
					sender.sendMessage(ChatColor.RED+"Something went wrong!");
				}
			}
		}
		
		return true;
	}

}
