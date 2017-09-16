package me.cerexus.partysystem.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.cerexus.partysystem.PartySystem;
import me.cerexus.partysystem.events.other.PartyActivityEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Party {

	private UUID partyOwner;
	private List<UUID> partyMembers = new ArrayList<>();
	private String partyName;

	public Party(UUID partyOwner, List<UUID> partyMembers) {
		this.partyOwner = partyOwner;
		for (UUID uuid : partyMembers) {
			this.addMember(uuid);
		}
		this.partyName = ProxyServer.getInstance().getPlayer(this.partyOwner).getName();

	}

	public List<UUID> getPartyMembers() {
		return this.partyMembers;
	}

	public UUID getPartyOwner() {
		return this.partyOwner;
	}

	public String getPartyName() {
		return this.partyName;
	}

	public boolean isOwner(UUID owner) {
		return this.partyOwner.equals(owner);
	}

	public void changeOwner(UUID newOwner) {
		if (ProxyServer.getInstance().getPlayer(newOwner) != null) {
			PartySystem.getInstance().partys.remove(this.getPartyName());
			this.partyOwner = newOwner;
			this.partyName = ProxyServer.getInstance().getPlayer(newOwner).getName();
			PartySystem.getInstance().partys.put(this.partyName, this);
			ProxyServer.getInstance().getPluginManager().callEvent(new PartyActivityEvent(PartyActivityEvent.PartyActivityType.CHANGE_OWNER, this, null, null, null, newOwner, null, null));
		} else {
			this.disbandParty();
		}
	}

	public void addMember(UUID newMember) {
		if (!this.partyMembers.contains(newMember)) {
			this.partyMembers.add(newMember);
			ProxyServer.getInstance().getPluginManager().callEvent(new PartyActivityEvent(PartyActivityEvent.PartyActivityType.ADD_MEMBER, this, null, newMember, null, null, null, null));
		}
	}

	public boolean hasMember(UUID member) {
		return this.partyMembers.contains(member);
	}

	public void removeMember(UUID member) {
		if (this.partyMembers.contains(member)) {
			this.partyMembers.remove(member);
			ProxyServer.getInstance().getPluginManager().callEvent(new PartyActivityEvent(PartyActivityEvent.PartyActivityType.REMOVE_MEMBER, this, null, null, member, null, null, null));
			if (this.partyOwner.equals(member)) {
				this.disbandParty();
			}
		}
	}

	public void leaveMemberQuiet(UUID member) {
		if (this.partyMembers.contains(member)) {
			this.partyMembers.remove(member);
			if (this.partyOwner.equals(member) || this.partyMembers.size() <= 1) {
				this.disbandParty();
			}
		}
	}

	public void leaveMember(UUID member, String name) {
		if (this.partyMembers.contains(member)) {
			this.partyMembers.remove(member);
			ProxyServer.getInstance().getPluginManager().callEvent(new PartyActivityEvent(PartyActivityEvent.PartyActivityType.LEFT_MEMBER, this, member, null, null, null, null, name));
			if (this.partyOwner.equals(member) || this.partyMembers.size() <= 1) {
				this.disbandParty();
			}
		}
	}

	public void disbandParty() {
		if (PartySystem.getInstance().partys.containsKey(this.getPartyName())) {
			PartySystem.getInstance().partys.remove(this.partyName);
			ProxyServer.getInstance().getPluginManager().callEvent(new PartyActivityEvent(PartyActivityEvent.PartyActivityType.DISBAND, this, null, null, null, null, null, null));
		}
	}

	public void sendPartyToServer(String severname) {
		ServerInfo targetserver = ProxyServer.getInstance().getServerInfo(severname);
		if (targetserver != null) {
			for (UUID uuid : this.getPartyMembers()) {
				ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
				p.connect(targetserver);
			}
		}
	}

}
