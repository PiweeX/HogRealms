package me.amc.psihotest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;

import me.amc.psihotest.player.FakePlayer;
import me.amc.psihotest.player.PlayerCache;

import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

public class PLib {

	private static boolean enabled = false;
    private static ProtocolManager protocolManager;

    public static boolean isEnabled() {
        return enabled;
    }

    static MainCore plg() {
        return MainCore.instance;
    }

    public static void init() {
        try {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
                protocolManager = ProtocolLibrary.getProtocolManager();
                enabled = true;
            }
        } catch (Throwable e) {
        }
    }

    public static void sendFakePlayerPackets(Player player, Collection<FakePlayer> playerList, boolean show) {
        if (!enabled) return;
        PacketContainer fakePlayerPacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        List<PlayerInfoData> pInfos = new ArrayList<PlayerInfoData>();
        for (FakePlayer pStr : playerList) {
            UUID u = pStr.uuid;
            WrappedGameProfile wgp = new WrappedGameProfile(u, pStr.playerName);
            WrappedSignedProperty property = PlayerCache.getPlayerUnit(pStr.playerName).getProperty();

            if (property != null) 
                wgp.getProperties().put("textures", property);
            
            PlayerInfoData pi = new PlayerInfoData(wgp, 16, NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(pStr.displayName));
            pInfos.add(pi);

        }
        
        fakePlayerPacket.getPlayerInfoAction().write(0, show ? PlayerInfoAction.ADD_PLAYER : PlayerInfoAction.REMOVE_PLAYER);
        fakePlayerPacket.getPlayerInfoDataLists().write(0, pInfos);
        
        try {
            protocolManager.sendServerPacket(player, fakePlayerPacket);
        } catch (InvocationTargetException e) {
        }


    }

    public static void sendFakePlayerPackets(Collection<? extends Player> collection, Collection<FakePlayer> playerList, boolean show) {
        if (!enabled) return;
        for (Player player : collection) {
            sendFakePlayerPackets(player, playerList, show);
        }
    }

    public static void initPacketListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plg(), PacketType.Status.Server.SERVER_INFO) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        ShowList.fillShowList(null);
                        WrappedServerPing ping = (WrappedServerPing) event.getPacket().getServerPings().read(0);
                        ping.setPlayersOnline(plg().getPlayersOnline());
                        ping.setPlayersMaximum(plg().getMaxPlayers());
                        if (plg().configHelper.isFakePlayersInListEnabled()) {
                            List<WrappedGameProfile> players = new ArrayList<WrappedGameProfile>();
                            for (FakePlayer fp : ShowList.getPlayerList()) {
                                players.add(new WrappedGameProfile(fp.uuid, ChatColor.translateAlternateColorCodes('&', fp.displayName)));
                            }
                            if (!players.isEmpty()) {
                                ping.setPlayersVisible(true);
                                ping.setPlayers(players);
                            }
                        }
                        event.getPacket().getServerPings().write(0, ping);
                    }
                });
    }
	
}
