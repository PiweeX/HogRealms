package me.amc.psihotest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.amc.psihotest.player.FakePlayer;
import me.amc.psihotest.player.PlayerCache;
import me.amc.psihotest.player.PlayerUnit;

public class ShowList {

	private static Set<FakePlayer> showList = new HashSet<FakePlayer>();
    private static Random random = new Random();

    public static void clear() {
        showList.clear();
    }

    public static void refreshOnlineList() {
        hideShowList();
        showAndFillShowList();
    }

    public static void showAndFillShowList() {
        for (Player player : Bukkit.getServer().getOnlinePlayers())
            ShowList.showAndFillShowList(player);
    }

    static void fillShowList(Player player) {
        showList.clear();
        int pingMin = Bukkit.getServer().getOnlinePlayers().isEmpty() ? 0 : 1000;
        int pingMax = Bukkit.getServer().getOnlinePlayers().isEmpty() ? 1000 : 0;
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!p.isOnline()) continue;
            
            String plName = p.getPlayerListName();
            int ping = NMSHandler.getPlayerPing(p);
            pingMin = Math.min(pingMin, ping);
            pingMax = Math.max(pingMax, ping);

            plName = ChatColor.translateAlternateColorCodes('&', p.getName());
            if (plName.length() > 16) plName = plName.substring(0, 15);
            
            p.setPlayerListName(plName);

            showList.add(new FakePlayer(p));
        }
        if (!MainCore.instance.onlineFakePlayers.isEmpty()) {
            for (int i = 0; i < MainCore.instance.onlineFakePlayers.size(); i++) {
                int ping = random.nextInt(pingMax - pingMin + 1) + pingMin;
                String fakeName = MainCore.instance.onlineFakePlayers.get(i);
                String fakeListName = ChatColor.translateAlternateColorCodes('&', fakeName);
                if (fakeListName.length() > 16) fakeListName = fakeListName.substring(0, 15);
                PlayerUnit unit = PlayerCache.getPlayerUnit(fakeName);
                String prefix = getPrefixOfFakePlayer(fakeName);
                if(addSpace(prefix)) prefix+=" ";
                showList.add(new FakePlayer(fakeName, prefix+fakeListName, unit.getUuid(), ping));
            }
        }

    }
    
    private static boolean addSpace(String prefix) {
    	//System.out.println("prefix="+prefix);
    	if(prefix.isBlank()) {
    		//System.out.println("blank prefix"+prefix);
    		return false;
    	}
    	
    	if(prefix.length() == 2 && prefix.charAt(0) == '§') {
    		//System.out.println("color code only"+prefix);
    		return false;
    	}
    	return true;
    }

    public static void showAndFillShowList(Player player) {
        fillShowList(player);
        PLib.sendFakePlayerPackets(player, showList, true);
    }

    public static void hideShowList() {
        List<FakePlayer> realPlayers = new ArrayList<FakePlayer>();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) realPlayers.add(new FakePlayer(p));
        PLib.sendFakePlayerPackets(Bukkit.getServer().getOnlinePlayers(), realPlayers, false);
        PLib.sendFakePlayerPackets(Bukkit.getServer().getOnlinePlayers(), showList, false);
        showList.clear();
    }
    
    public static String getPrefixOfFakePlayer(String name) {
    	ConfigHelper config = MainCore.instance.configHelper;
    	String prefix = "";
    	for(int i = 0; i < config.getFakePlayers().size(); i++)
    		if(config.getFakePlayers().get(i).equals(name))
    			return config.getFakePlayersPrefix().get(i);
    	return prefix;
    }

    public static Set<FakePlayer> getPlayerList() {
        return showList;
    }

    public static int size() {
        return showList.size();
    }

    public static String getPlayers() {
        if (showList.isEmpty()) return "";
        StringBuilder sb = null;
        for (FakePlayer fp : showList) {
            if (sb == null) sb = new StringBuilder(fp.playerName);
            else sb.append(", ").append(fp.playerName);
        }
        return sb.toString();
    }
	
}
