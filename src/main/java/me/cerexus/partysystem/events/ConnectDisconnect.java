package me.cerexus.partysystem.events;

import me.cerexus.partysystem.*;
import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.event.*;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.plugin.*;

public class ConnectDisconnect implements Listener {

	private PartySystemAPI api;
	private final PartySystem plugin;

	public ConnectDisconnect(final PartySystem plugin) {
		this.api = new PartySystemAPI();
		this.plugin = plugin;
	}

	@EventHandler
	public void onQuit(final ServerConnectEvent e) {
		final ProxiedPlayer p = e.getPlayer();
		if (p.getServer() == null) {
			final boolean toggle = this.plugin.partyToggle.isPartyInviteDisabled(p.getUniqueId());
			this.plugin.partyToggle.partytoggle.add(p.getUniqueId());
		}
	}

	@EventHandler
	public void onQuit(final PlayerDisconnectEvent e) {
		final ProxiedPlayer p = e.getPlayer();
		ProxyServer.getInstance().getScheduler().runAsync((Plugin) this.plugin, () -> {
			if (this.plugin.partyToggle.partytoggle.contains(p.getUniqueId())) {
				this.plugin.partyToggle.partytoggle.remove(p.getUniqueId());
			}
			if (this.plugin.partys.containsKey(p.getName())) {
				this.api.disbandParty(p.getName());
			} else {
				this.api.leaveParty(p.getUniqueId(), p.getName());
			}
		});
	}
}
