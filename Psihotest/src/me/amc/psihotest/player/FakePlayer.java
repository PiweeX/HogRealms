package me.amc.psihotest.player;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.amc.psihotest.NMSHandler;

public class FakePlayer {

	public UUID uuid;
	public String playerName;
	public String displayName;
	public int ping;

    public FakePlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.playerName = player.getName();
        this.displayName = player.getDisplayName();
        this.ping = NMSHandler.getPlayerPing(player);
    }

    public FakePlayer(String fakeName, String fakeListName, UUID uuid, int ping) {
        this.uuid = uuid;
        this.playerName = fakeName;
        this.displayName = fakeListName;
        this.ping = ping;
    }
	
}
