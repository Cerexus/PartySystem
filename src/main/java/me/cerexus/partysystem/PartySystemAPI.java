package me.cerexus.partysystem;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PartySystemAPI {

	public String getPartyNameFromUUID(UUID uuid) {
		for (ArrayList<UUID> al : PartySystem.partys.values()) {
			if (al.contains(uuid)) {
				for (String s : PartySystem.partys.keySet()) {
					if (PartySystem.partys.get(s).equals(al)) {
						return s;
					}
				}
			}
		}
		return null;
	}

	public ArrayList<UUID> getPartyMembersFromUUID(UUID uuid) {
		for (ArrayList<UUID> al : PartySystem.partys.values()) {
			if (al.contains(uuid)) {
				return al;
			}
		}
		return null;
	}

	public ArrayList<UUID> getPartyMembersFromParty(String partyname) {
		if (PartySystem.partys.containsKey(partyname)) {
			return PartySystem.partys.get(partyname);
		}
		return null;
	}

	public void sendPartyToServerWithPartyName(String servername, String partyname) {
		ArrayList<UUID> list = PartySystem.partys.get(partyname);
		if (list != null) {
			ServerInfo targetserver = ProxyServer.getInstance().getServerInfo(servername);
			if (targetserver != null) {
				for (UUID uuid : list) {
					ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
					p.connect(targetserver);
				}
			}
		}
	}

	public void sendPartyToServerWithMemberUUID(String servername, UUID memberuuid) {
		for (ArrayList<UUID> al : PartySystem.partys.values()) {
			if (al.contains(memberuuid)) {
				ServerInfo targetserver = ProxyServer.getInstance().getServerInfo(servername);
				if (targetserver != null) {
					for (UUID uuid : al) {
						ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
						p.connect(targetserver);

					}
				}
			}
		}
	}

	public boolean existParty(String partyname) {
		return PartySystem.partys.containsKey(partyname);
	}

	public void disbandParty(String partyname) {
		ArrayList<UUID> members = getPartyMembersFromParty(partyname);
		if (members != null) {
			for (UUID uuid : members) {
				ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
				if (p != null) {
					p.sendMessage(TextComponent.fromLegacyText(PartySystem.prefix + "§bThe Party was disband!"));
				}
			}
			PartySystem.partys.remove(partyname);
		}
	}

	public void leaveParty(UUID uuid) {
		for (ArrayList<UUID> al : PartySystem.partys.values()) {
			if (al.contains(uuid)) {
				if (al.size() >= 1) {
					disbandParty(getPartyNameFromUUID(uuid));
				}
				al.remove(uuid);
				break;
			}
		}
	}

	public boolean isInSameParty(UUID player1, UUID player2) {
		ArrayList<UUID> party = this.getPartyMembersFromUUID(player1);
		if (party != null) {
			if (party.contains(player2)) {
				return true;
			}
		}
		return false;

	}

	public void changePartyOwner(String partyname, UUID newownerid) {
		if (PartySystem.partys.containsKey(partyname)) {
			ArrayList<UUID> oldparty = PartySystem.partys.get(partyname);
			PartySystem.partys.remove(partyname);
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(newownerid);
			PartySystem.partys.put(p.getName(), oldparty);
		}
	}

	public void invitePlayer(UUID requester, UUID replier) {
		if (!PartySystem.requesterandreplier.containsKey(requester) && !PartySystem.requesterandreplier.containsValue(replier)) {
			if (!PartySystem.requesterandreplier.containsKey(replier)) {
				ProxiedPlayer requesterp = ProxyServer.getInstance().getPlayer(requester);
				ProxiedPlayer replierp = ProxyServer.getInstance().getPlayer(replier);
				requesterp.sendMessage(TextComponent.fromLegacyText(PartySystem.prefix + "§bYou have invited " + replierp.getName() + "!"));
				replierp.sendMessage(TextComponent.fromLegacyText(PartySystem.prefix + "§bYou got an invitation from " + requesterp.getName() + "!"));
				PartySystem.requesterandreplier.put(requester, replier);
				PartySystem.pendingrequests.put(requester, ProxyServer.getInstance().getScheduler().schedule(PartySystem.plugin, () -> {
					requesterp.sendMessage(TextComponent.fromLegacyText(PartySystem.prefix + "§bYour invitation to " + replierp.getName() + " has expired!"));
					replierp.sendMessage(TextComponent.fromLegacyText(PartySystem.prefix + "§bYour invitation from " + requesterp.getName() + " has expired!"));
					PartySystem.requesterandreplier.remove(requester);
					PartySystem.pendingrequests.remove(requester);
				}, 10, TimeUnit.SECONDS));
			}
		}
	}

	public void declineParty(UUID replier) {
		if (PartySystem.requesterandreplier.containsValue(replier)) {
			for (UUID requester : PartySystem.requesterandreplier.keySet()) {
				if (PartySystem.requesterandreplier.get(requester).equals(replier)) {
					PartySystem.requesterandreplier.remove(requester);
					ProxyServer.getInstance().getScheduler().cancel(PartySystem.pendingrequests.get(requester));
					PartySystem.pendingrequests.remove(requester);
					ProxiedPlayer requesterp = ProxyServer.getInstance().getPlayer(requester);
					ProxiedPlayer replierp = ProxyServer.getInstance().getPlayer(replier);
					requesterp.sendMessage(TextComponent.fromLegacyText(PartySystem.prefix + "§bYour invitation to " + replierp.getName() + " was declined!"));
					replierp.sendMessage(TextComponent.fromLegacyText(PartySystem.prefix + "§bYou declined the invitation from " + requesterp.getName() + "!"));
				}
			}
		}
	}

	public void acceptParty(UUID replier) {
		if (PartySystem.requesterandreplier.containsValue(replier)) {
			for (UUID requester : PartySystem.requesterandreplier.keySet()) {
				if (PartySystem.requesterandreplier.get(requester).equals(replier)) {
					PartySystem.requesterandreplier.remove(requester);
					ProxyServer.getInstance().getScheduler().cancel(PartySystem.pendingrequests.get(requester));
					PartySystem.pendingrequests.remove(requester);
					ArrayList<UUID> newparty = new ArrayList<>();
					if (PartySystem.partys.containsKey(requester)) {
						for (UUID uuid : PartySystem.partys.get(requester)) {
							newparty.add(uuid);
						}
						newparty.add(replier);
					} else {
						newparty.add(requester);
						newparty.add(replier);
					}

					ProxiedPlayer requesterp = ProxyServer.getInstance().getPlayer(requester);
					ProxiedPlayer replierp = ProxyServer.getInstance().getPlayer(replier);
					requesterp.sendMessage(TextComponent.fromLegacyText(PartySystem.prefix + "§b" + replierp.getName() + " joined the Party!"));
					replierp.sendMessage(TextComponent.fromLegacyText(PartySystem.prefix + "§b" + replierp.getName() + " joined the Party!"));

					PartySystem.partys.put(requesterp.getName(), newparty);
				}
			}
		}
	}

}
