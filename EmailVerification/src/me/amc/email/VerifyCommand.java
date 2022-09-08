package me.amc.email;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VerifyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) return false;

		Player player = (Player) sender;
		
		if(args.length == 1) {
			String name = ChatColor.stripColor(player.getName());
			String email = args[0];
			
			SerializablePlayer sp = new SerializablePlayer(name, email);
			
			String uuid = player.getUniqueId().toString();
			
			if(!playerExists(uuid)) {
				
				if(!isEmailValid(email)) {
					player.sendMessage(MainCore.instance.configHelper.getInvalidEmail());
					return true;
				}
				
				EmailStorage.addresses.put(uuid, sp);
				EmailStorage.save();
				
				writeRawAddress(email);
				
				player.sendMessage(MainCore.instance.configHelper.getVerificationSuccessPersonal());
				Bukkit.broadcastMessage(MainCore.instance.configHelper.getVerificationSuccessGlobal(name));
				player.playSound(player.getLocation(), 
						Sound.valueOf(MainCore.instance.configHelper.getVerificationSuccessSound()), 5.0f, 1.0f);
			
				for(String key : MainCore.instance.configHelper.getVerificationSuccessCommands(name))
					Bukkit.dispatchCommand(MainCore.instance.getServer().getConsoleSender(), key);
			
			} else {
				player.sendMessage(MainCore.instance.configHelper.getAlreadyVerified());
			}
			
		} else {
			player.sendMessage(MainCore.instance.configHelper.getWrongVerifySyntax());
		}
		
		return true;
	}
	
	private boolean playerExists(String uuid) {
		return EmailStorage.addresses.containsKey(uuid);
	}
	
	private boolean isEmailValid(String email) {
		for(String ending : MainCore.instance.configHelper.getValidEndings())
			if(email.endsWith(ending))
				return true;
		return false;
	}
	
	private void writeRawAddress(String email) {
		try {
			FileWriter fw = new FileWriter(new File(MainCore.instance.getDataFolder(), "addresses_raw.txt"), true);
			fw.append(email+'\n');
			fw.close();
		} catch (IOException e) {
			MainCore.instance.getLogger().log(Level.WARNING, "Could not write raw address to file!", e);
		}
	}
	
}
