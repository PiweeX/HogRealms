package me.amc.psihotest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSHandler {

	private static String version = "";

    private static String cboPrefix = "org.bukkit.craftbukkit.";
    private static String nmsPrefix = "net.minecraft.server.";
    private static boolean block_executing = false;
    private static Class<?> CraftEntity;
    private static Field CraftEntity_entity;
    private static Class<?> EntityPlayer;
    private static Field entityPlayer_ping;

    public static void init() {
        try {
            Object s = Bukkit.getServer();
            Method m = s.getClass().getMethod("getHandle");
            Object cs = m.invoke(s);
            String className = cs.getClass().getName();
            String[] v = className.split("\\.");
            if (v.length == 5) {
                version = v[3];
                cboPrefix = "org.bukkit.craftbukkit." + version + ".";
                nmsPrefix = "net.minecraft.server." + version + ".";
                
            }
            EntityPlayer = nmsClass("EntityPlayer");

            CraftEntity = cboClass("entity.CraftEntity");
            CraftEntity_entity = CraftEntity.getDeclaredField("entity");
            CraftEntity_entity.setAccessible(true);

            entityPlayer_ping = EntityPlayer.getField("ping");
            

        } catch (Exception e) {
            block_executing = true;
        }
    }

    public static String getMinecraftVersion() {
        return version;
    }

    public static boolean isBlocked() {
        return block_executing;
    }

    private static Class<?> nmsClass(String classname) throws Exception {
        return Class.forName(nmsPrefix + classname);
    }

    private static Class<?> cboClass(String classname) throws Exception {
        return Class.forName(cboPrefix + classname);
    }

    public static int getPlayerPing(Player p) {
        if (block_executing) return 0;
        try {
            Object craftEntity = p;
            Object nmsPlayer = CraftEntity_entity.get(craftEntity);
            return entityPlayer_ping.getInt(nmsPlayer);
        } catch (Exception e) {
            //e.printStackTrace();
            return 0;
        }
    }
	
}
