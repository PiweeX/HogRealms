package me.amc.psihotest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import github.scarsz.discordsrv.DiscordSRV;

public class MainCore extends JavaPlugin {
	
	public static MainCore instance;
	
	public ConfigHelper configHelper;
	
	boolean protocolLibEnabled = false;
	boolean discordEnabled = false;
    
    String listCommands = "list,players,online,playerlist,who";
    
    public List<String> onlineFakePlayers = new ArrayList<String>();
    
    public int seconds;
    int ticks = 0;
    
    public FakeEvents fakeEvents;
    
	@Override
	public void onEnable() {
		instance = this;
		
		saveDefaultConfig();
		reloadConfig();
		
		/* for testing purposes
		
		for(String name : configHelper.getFakePlayers())
			addFakePlayer(name);
		*/
		
		fakeEvents = new FakeEvents();
		
		getLogger().info("Thanks for purchasing "+getDescription().getName()+" and trusting Hogo Studios since 2017!");
		getLogger().info("Our motive is simplicity and efficiency. We hope the plugin will help your community!");
		getLogger().info("OFFICIAL LINKS:");
		getLogger().info("- https://hogostudios.com/");
		getLogger().info("- https://discord.gg/yq9gC87b58%60");
		
		try {
            PLib.init();
            if (configHelper.isFakeServerListEnabled()) PLib.initPacketListener();
            getLogger().log(Level.INFO, "Connected to ProtocolLib!");
            protocolLibEnabled = true;
        } catch (Throwable ignore) {
        	getLogger().log(Level.SEVERE, "ProtocolLib is not found! Disabling plugin...");
        	this.getServer().getPluginManager().disablePlugin(this);
        }

        NMSHandler.init();
        ShowList.refreshOnlineList();
        
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPIManager(this).register();
            getLogger().log(Level.INFO, "Connected to PlaceholderAPI!");
        }
        
        if(Bukkit.getPluginManager().getPlugin("DiscordSRV") != null) {
        	discordEnabled = true;
        	getLogger().log(Level.INFO, "Connected to DiscordSRV!");
        }
        
        int min = configHelper.getRandomEventMin();
        int max = configHelper.getRandomEventMax();
        seconds = getRandomNumber(min, max);
        
        BukkitRunnable runnable = new BukkitRunnable() {
        	@Override
        	public void run() {
        		ticks++;
        		if(ticks >= seconds) {
        			Random r = new Random();
            		if(r.nextInt(101) <= configHelper.getRandomEventJoinChance() 
            				&& onlineFakePlayers.size() != configHelper.getFakePlayers().size()) {
            			String p = getRandomOfflineFakePlayer();
            			if(p != null) 
            				addFakePlayer(p);

            		} else {
            			if(!onlineFakePlayers.isEmpty())
            				removeFakePlayer(getRandomOnlineFakePlayer());

            		}
            		seconds = getRandomNumber(min, max);

        			ticks = 0;
        		}
        		
        	}
        };
        
        runnable.runTaskTimer(this, 0, 20);
        
		//sendTestPingPacket();
        
        Bukkit.getPluginManager().addPermission(PsihotestCommand.reloadPerm);
        getCommand("psihotest").setExecutor(new PsihotestCommand());
		
	}
	
	@Override
	public void onDisable() { 
		onlineFakePlayers.clear();
		ShowList.refreshOnlineList();
	}
	
	@Override
	public void reloadConfig() {
		super.reloadConfig();
		configHelper = new ConfigHelper(this.getConfig());
	}
	
	public int getMaxPlayers() {
        if (configHelper.isFakeMaxPlayersEnabled()) return configHelper.getFakeMaxPlayers();
        return getServer().getMaxPlayers();
    }

    public int getPlayersOnline() {
        return Math.max(ShowList.size(), getServer().getOnlinePlayers().size() + onlineFakePlayers.size());
    }
    
    public void addFakePlayer(String name) {
    	name = name.replace('&', '§');
    	name = ChatColor.stripColor(name);
    	onlineFakePlayers.add(name);
    	
    	if(configHelper.isJoinOnServerEnabled())
    		Bukkit.broadcastMessage(configHelper.getJoinMessage(name));
    	
    	ShowList.refreshOnlineList();
    	
    	if(configHelper.isJoinOnDiscordEnabled())
    		sendDiscordJoinMessage(name, configHelper.getJoinMessage(name));
    }
    
    public void removeFakePlayer(String name) {
    	name = name.replace('&', '§');
    	name = ChatColor.stripColor(name);
    	onlineFakePlayers.remove(name);

    	if(configHelper.isLeaveOnServerEnabled())
    		Bukkit.broadcastMessage(configHelper.getLeaveMessage(name));
		
    	ShowList.refreshOnlineList();

    	if(configHelper.isLeaveOnDiscordEnabled())
    		sendDiscordLeaveMessage(name, configHelper.getLeaveMessage(name));
    }
    
    private HashMap<String, Object> discordPlayers = new HashMap<String, Object>();
    
    private void sendDiscordJoinMessage(String name, String message) {
    	if(!discordEnabled) return;
    	try {
    		Object entityPlayer = fakeEvents.createPlayer(name);
    		
            fakeEvents.makePlayedBefore(entityPlayer);
            discordPlayers.put(name, entityPlayer);

            Player player = fakeEvents.toBukkitPlayer(entityPlayer);
            
    		DiscordSRV.getPlugin().sendJoinMessage(player, message);
    	} catch(Exception ex) {
    		getLogger().log(Level.WARNING, "Did not find player with name: "+name);
    	}
    }
    
    private void sendDiscordLeaveMessage(String name, String message) {
    	if(!discordEnabled) return;
    	if(!discordPlayers.containsKey(name)) return;

    	try {
    		Object entityPlayer = discordPlayers.get(name);
    		DiscordSRV.getPlugin().sendLeaveMessage(fakeEvents.toBukkitPlayer(entityPlayer), message);
    		discordPlayers.remove(name);
    	} catch(Exception ex) {
    		getLogger().log(Level.WARNING, "Did not find player with name: "+name);
    	}
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    
    private String getRandomOfflineFakePlayer() {
    	if(onlineFakePlayers.size() == configHelper.getFakePlayers().size()) return null;
    	Random r = new Random();
    	String p;
    	do {
    		p = configHelper.getFakePlayers().get(r.nextInt(configHelper.getFakePlayers().size()));
    		p = p.replace('&', '§');
        	p = ChatColor.stripColor(p);
    	} while(onlineFakePlayers.contains(p));
    	
    	return p;
    }
    
    private String getRandomOnlineFakePlayer() {
    	if(onlineFakePlayers.isEmpty()) return null;
    	Random r = new Random();
    	return onlineFakePlayers.get(r.nextInt(onlineFakePlayers.size()));
    }

	// for testing purposes
	@SuppressWarnings("unused")
	private void sendTestPingPacket() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this,
				ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO) {
			@Override
			public void onPacketSending(PacketEvent event) {
				event.getPacket().getServerPings().read(0).setPlayersMaximum(1000);
				event.getPacket().getServerPings().read(0).setPlayersOnline(100);
			}
		});
	}
}
