package me.amc.psihotest;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mojang.authlib.GameProfile;

public class FakeEvents implements Listener {

	private Class<?> ENTITY, CRAFT_WORLD, CRAFT_SERVER, ENTITY_PLAYER, MINECRAFT_SERVER, 
		WORLD_SERVER, PLAYER_INTERACT_MANAGER, CRAFT_PLAYER;
	
	public FakeEvents() {
		Bukkit.getPluginManager().registerEvents(this, MainCore.instance);
		
		try {
			CRAFT_PLAYER = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".entity.CraftPlayer");
			CRAFT_SERVER = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".CraftServer");
			CRAFT_WORLD = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".CraftWorld");
			
			MINECRAFT_SERVER = Class.forName((getVersionNumber() >= 17
                    ? "net.minecraft.server"
                    : "net.minecraft.server." + getVersion())
                    + ".MinecraftServer");
			
			WORLD_SERVER = Class.forName((getVersionNumber() >= 17
                    ? "net.minecraft.server.level"
                    : "net.minecraft.server." + getVersion())
                    + ".WorldServer");
			
			PLAYER_INTERACT_MANAGER = Class.forName((getVersionNumber() >= 17
                    ? "net.minecraft.server.level"
                    : "net.minecraft.server." + getVersion())
                    + ".PlayerInteractManager");
			
			ENTITY = Class.forName((getVersionNumber() >= 17
	                ? "net.minecraft.world.entity"
	                : "net.minecraft.server." + getVersion())
	                + ".Entity");
			
			ENTITY_PLAYER = Class.forName((getVersionNumber() >= 17
                    ? "net.minecraft.server.level" : "net.minecraft.server." + getVersion())
                    + ".EntityPlayer");
			
		} catch(Exception ex) {
			System.out.println("FakeEvents main exception!");
		}
		
	}
	
	private int getVersionNumber() {
        return Integer.parseInt(getVersion().split("_")[1]);
    }
	
	public String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);
    }
	
	private Object getMinecraftServer() throws Exception {
        Object craftServer = CRAFT_SERVER.cast(Bukkit.getServer());
        return CRAFT_SERVER.getDeclaredMethod("getServer").invoke(craftServer);
    }

    private Object getWorldServer() throws Exception {
        Object craftWorld = CRAFT_WORLD.cast(getRandomWorld());
        return CRAFT_WORLD.getDeclaredMethod("getHandle").invoke(craftWorld);
    }
    
    private World getRandomWorld() {
        return Bukkit.getWorlds().get(ThreadLocalRandom.current().nextInt(Bukkit.getWorlds().size()));
    }
	
	public Player toBukkitPlayer(Object entityPlayer) throws Exception {
        return (Player) ENTITY.getDeclaredMethod("getBukkitEntity").invoke(entityPlayer);
    }
	
	public Object createPlayer(String name) throws Exception{
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        Object entityPlayer = null;
        if(getVersionNumber() >= 17) {
            entityPlayer = ENTITY_PLAYER.getDeclaredConstructor(MINECRAFT_SERVER, WORLD_SERVER, GameProfile.class)
                    .newInstance(getMinecraftServer(), getWorldServer(), profile);
        }else{
            Object playerInteractManager = PLAYER_INTERACT_MANAGER.getDeclaredConstructor(WORLD_SERVER)
                    .newInstance(getWorldServer());
            entityPlayer = ENTITY_PLAYER.getDeclaredConstructor(MINECRAFT_SERVER, WORLD_SERVER, GameProfile.class, PLAYER_INTERACT_MANAGER)
                    .newInstance(getMinecraftServer(), getWorldServer(), profile, playerInteractManager);
        }
        return entityPlayer;
    }
	
	public void makePlayedBefore(Object entityPlayer) throws Exception {
        Object craftPlayer = CRAFT_PLAYER.cast(toBukkitPlayer(entityPlayer));
        Field field = CRAFT_PLAYER.getDeclaredField("hasPlayedBefore");
        field.setAccessible(true);
        field.setBoolean(craftPlayer, true);
        field.setAccessible(false);
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        ShowList.refreshOnlineList();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskLater(MainCore.instance, new Runnable() {
            @Override
            public void run() {
                ShowList.refreshOnlineList();
            }
        }, 1);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onListCommand(PlayerCommandPreprocessEvent event) {
        String[] ln = MainCore.instance.listCommands.replace(" ", "").split(",");
        for (String cmd : ln)
            if (event.getMessage().startsWith("/" + cmd)) {
                String str = ShowList.getPlayers();
                event.getPlayer().sendMessage(str);
                event.setCancelled(true);
                break;
            }
        
        for(String cmd : MainCore.instance.configHelper.getKickCommands()) {
        	String[] text = event.getMessage().split(" ");
        	if(text[0].startsWith("/"+cmd)) {
        		String name = text[1];
        		
        		// if player is fake
        		if(MainCore.instance.configHelper.getFakePlayers().contains(name)) {
        			// if fake player is online
        			if(MainCore.instance.onlineFakePlayers.contains(name)) {
        				// kick fake player
        				MainCore.instance.removeFakePlayer(name);
        			}
        		}
        		
        		event.setCancelled(true);
        		break;
        	}
        }
    }
	
}
