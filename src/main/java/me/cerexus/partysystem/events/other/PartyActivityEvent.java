package me.cerexus.partysystem.events.other;

import java.util.UUID;
import me.cerexus.partysystem.objects.Party;
import net.md_5.bungee.api.plugin.Event;

public class PartyActivityEvent extends Event {

	private final PartyActivityType partyActivityType;
	private final Party party;
	private final UUID leaveMember;
	private UUID addMember;
	private UUID removeMember;
	private UUID changeOwner;
	private String sendServer;
	private final String leaveMemberName;

	public enum PartyActivityType {
		DISBAND,
		LEFT_MEMBER,
		ADD_MEMBER,
		REMOVE_MEMBER,
		CHANGE_OWNER,
		SEND_SERVER
	}

	public PartyActivityEvent(PartyActivityType partyActivityType, Party party, UUID leaveMember, UUID addMember, UUID removeMember, UUID changeOwner, String sendServer, String leaveMemberName) {
		this.partyActivityType = partyActivityType;
		this.party = party;
		this.leaveMember = leaveMember;
		this.addMember = addMember;
		this.removeMember = removeMember;
		this.changeOwner = changeOwner;
		this.sendServer = sendServer;
		this.leaveMemberName = leaveMemberName;
	}

	public PartyActivityType getType() {
		return this.partyActivityType;
	}

	public Party getParty() {
		return this.party;
	}

	public UUID getNewMember() {
		return this.addMember;
	}

	public UUID getRemovedMember() {
		return this.removeMember;
	}

	public UUID getLeftMember() {
		return this.leaveMember;
	}

	public UUID getNewOwner() {
		return this.changeOwner;
	}

	public String getSendedServerName() {
		return this.sendServer;
	}

	public String getLeaveMemberName() {
		return this.leaveMemberName;
	}

}
