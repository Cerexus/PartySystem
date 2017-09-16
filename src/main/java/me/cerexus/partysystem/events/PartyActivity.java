package me.cerexus.partysystem.events;

import java.util.List;
import java.util.UUID;
import me.cerexus.partysystem.PartySystem;
import me.cerexus.partysystem.events.other.PartyActivityEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyActivity implements Listener {

	@EventHandler
	public void onPartyActivity(PartyActivityEvent e) {
		switch (e.getType()) {
			case REMOVE_MEMBER:
				sendMessage(e.getParty().getPartyMembers(), "§b" + ProxyServer.getInstance().getPlayer(e.getRemovedMember()).getName() + " was removed from the Party!");
				return;
			case LEFT_MEMBER:
				sendMessage(e.getParty().getPartyMembers(), "§b" + e.getLeaveMemberName() + " has left the Party");
				return;
			case ADD_MEMBER:
				sendMessage(e.getParty().getPartyMembers(), "§b" + ProxyServer.getInstance().getPlayer(e.getNewMember()).getName() + " was added to the Party!");
				return;
			case CHANGE_OWNER:
				sendMessage(e.getParty().getPartyMembers(), "§bThe new Party Owner is now " + ProxyServer.getInstance().getPlayer(e.getNewOwner()).getName() + "!");
				return;
			case DISBAND:
				sendMessage(e.getParty().getPartyMembers(), "§bThe Party was disband!");
				return;
			case SEND_SERVER:
				sendMessage(e.getParty().getPartyMembers(), "§bThe Party trys to connect to " + e.getSendedServerName() + "!");
				return;
			default:
				return;
		}
	}

	private void sendMessage(List<UUID> members, String message) {
		for (UUID uuid : members) {
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
			if (p != null) {
				p.sendMessage(TextComponent.fromLegacyText(PartySystem.getInstance().prefix + message));
			}
		}
	}

}
