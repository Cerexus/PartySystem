package me.cerexus.partysystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.cerexus.partysystem.objects.Party;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PartySystemAPI {

	private final PartySystem plugin;

	public PartySystemAPI() {
		this.plugin = PartySystem.getInstance();
	}

	public Party getPartyFromUUID(UUID uuid) {
		for (Party party : this.plugin.partys.values()) {
			if (party.hasMember(uuid)) {
				return party;
			}
		}
		return null;
	}

	public Party getPartyFromName(String name) {
		if (this.plugin.partys.containsKey(name)) {
			return this.plugin.partys.get(name);
		}
		return null;
	}

	public String getPartyNameFromUUID(UUID uuid) {
		Party party = getPartyFromUUID(uuid);
		if (party != null) {
			return party.getPartyName();
		}
		return null;
	}

	public List<UUID> getPartyMembersFromUUID(UUID uuid) {
		Party party = getPartyFromUUID(uuid);
		if (party != null) {
			return party.getPartyMembers();
		}
		return null;
	}

	public List<UUID> getPartyMembersFromPartyname(String partyname) {
		Party party = getPartyFromName(partyname);
		if (party != null) {
			return party.getPartyMembers();
		}
		return null;
	}

	public void sendPartyToServerWithPartyName(String partyname, String servername) {
		Party party = getPartyFromName(partyname);
		if (party != null) {
			party.sendPartyToServer(servername);
		}
	}

	public void sendPartyToServerWithMemberUUID(String servername, UUID memberUUID) {
		Party party = getPartyFromUUID(memberUUID);
		if (party != null) {
			party.sendPartyToServer(servername);
		}
	}

	public boolean existParty(String partyname) {
		return this.plugin.partys.containsKey(partyname);
	}

	public void disbandParty(String partyname) {
		Party party = getPartyFromName(partyname);
		if (party != null) {
			party.disbandParty();
		}
	}

	public void leavePartyQuiet(UUID uuid) {
		Party party = getPartyFromUUID(uuid);
		if (party != null) {
			party.leaveMemberQuiet(uuid);
		}
	}

	public void leaveParty(UUID uuid, String name) {
		Party party = getPartyFromUUID(uuid);
		if (party != null) {
			party.leaveMember(uuid, name);
		}
	}

	public boolean isInSameParty(UUID player1, UUID player2) {
		Party party = getPartyFromUUID(player1);
		if (party != null) {
			return party.hasMember(player2);
		}
		return false;

	}

	public void changePartyOwner(String partyname, UUID newOwnerId) {
		if (this.plugin.partys.containsKey(partyname)) {
			Party party = this.plugin.partys.get(partyname);
			party.changeOwner(newOwnerId);
		}
	}

	public void invitePlayer(UUID requester, UUID replier) {
		if (!this.plugin.requesterAndReplier.containsKey(requester) && !this.plugin.requesterAndReplier.containsValue(replier)) {
			if (!this.plugin.requesterAndReplier.containsKey(replier)) {
				ProxiedPlayer requesterp = ProxyServer.getInstance().getPlayer(requester);
				ProxiedPlayer replierp = ProxyServer.getInstance().getPlayer(replier);
				requesterp.sendMessage(TextComponent.fromLegacyText(this.plugin.prefix + "§bYou have invited " + replierp.getName() + "!"));
				replierp.sendMessage(TextComponent.fromLegacyText(this.plugin.prefix + "§bYou got an invitation from " + requesterp.getName() + "!"));
				this.plugin.requesterAndReplier.put(requester, replier);
				this.plugin.pendingRequests.put(requester, ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> {
					requesterp.sendMessage(TextComponent.fromLegacyText(this.plugin.prefix + "§bYour invitation to " + replierp.getName() + " has expired!"));
					replierp.sendMessage(TextComponent.fromLegacyText(this.plugin.prefix + "§bYour invitation from " + requesterp.getName() + " has expired!"));
					this.plugin.requesterAndReplier.remove(requester);
					this.plugin.pendingRequests.remove(requester);
				}, 10, TimeUnit.SECONDS));
			}
		}
	}

	public void declineParty(UUID replier) {
		if (this.plugin.requesterAndReplier.containsValue(replier)) {
			for (UUID requester : this.plugin.requesterAndReplier.keySet()) {
				if (this.plugin.requesterAndReplier.get(requester).equals(replier)) {
					this.plugin.requesterAndReplier.remove(requester);
					ProxyServer.getInstance().getScheduler().cancel(this.plugin.pendingRequests.get(requester));
					this.plugin.pendingRequests.remove(requester);
					ProxiedPlayer requesterp = ProxyServer.getInstance().getPlayer(requester);
					ProxiedPlayer replierp = ProxyServer.getInstance().getPlayer(replier);
					requesterp.sendMessage(TextComponent.fromLegacyText(this.plugin.prefix + "§bYour invitation to " + replierp.getName() + " was declined!"));
					replierp.sendMessage(TextComponent.fromLegacyText(this.plugin.prefix + "§bYou declined the invitation from " + requesterp.getName() + "!"));
				}
			}
		}
	}

	public void acceptParty(UUID replier) {
		if (this.plugin.requesterAndReplier.containsValue(replier)) {
			for (UUID requester : this.plugin.requesterAndReplier.keySet()) {
				if (this.plugin.requesterAndReplier.get(requester).equals(replier)) {
					this.plugin.requesterAndReplier.remove(requester);
					ProxyServer.getInstance().getScheduler().cancel(this.plugin.pendingRequests.get(requester));
					this.plugin.pendingRequests.remove(requester);
					this.plugin.partys.put(ProxyServer.getInstance().getPlayer(requester).getName(), new Party(requester, Arrays.asList(requester, replier)));
					return;
				}
			}
		}
	}

}
