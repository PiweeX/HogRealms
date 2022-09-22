package me.amc.email;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHelper {

	private FileConfiguration config;
	
	public ConfigHelper(FileConfiguration config) {
		this.config = config;
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	public String getColoredMessage(String message) {
		return config.getString(message).replace('&', '§');
	}
	
	public String getWrongVerifySyntax() {
		return getColoredMessage("WrongVerifySyntax");
	}
	
	public String getAlreadyVerified() {
		return getColoredMessage("AlreadyVerified");
	}
	
	public String getVerificationSuccessPersonal() {
		return getColoredMessage("VerificationSuccess.Personal");
	}
	
	public String getVerificationSuccessGlobal(String name) {
		return getColoredMessage("VerificationSuccess.Global").replace("%player%", name);
	}
	
	public String getVerificationSuccessSound() {
		return getColoredMessage("VerificationSuccess.Sound");
	}
	
	public List<String> getVerificationSuccessCommands(String name) {
		List<String> temp = config.getStringList("VerificationSuccess.Commands");
		List<String> edited = new ArrayList<>();
		for(String cmd : temp) 
			edited.add(cmd.replace("%player%", name));
		return edited;
	}
	
	public List<String> getValidEndings() {
		return config.getStringList("ValidEndings");
	}
	
	public String getInvalidEmail() {
		return getColoredMessage("InvalidEmail");
	}
	
	public String getVerificationReminder() {
		return getColoredMessage("VerificationReminder");
	}
}
