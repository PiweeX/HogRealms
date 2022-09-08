package me.amc.email;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("PlayerEmail")
public class SerializablePlayer implements ConfigurationSerializable {

	private String name;
	private String email;
	
	public SerializablePlayer(String name, String email) {
		this.name = name;
		this.email = email;
	}
	
	public SerializablePlayer(Map<String, Object> map) {
		this.name = (String) map.get("name");
		this.email = (String) map.get("email");
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("name", name);
		map.put("email", email);
		return map;
	}
	
}
