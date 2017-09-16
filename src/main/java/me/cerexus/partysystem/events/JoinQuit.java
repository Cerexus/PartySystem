package me.cerexus.partysystem.events;

import me.cerexus.partysystem.PartySystem;
import me.cerexus.partysystem.PartySystemAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class JoinQuit implements Listener {

	private PartySystemAPI api = new PartySystemAPI();
	private final PartySystem plugin;

	public JoinQuit(PartySystem plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onQuit(ServerConnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		if (p.getServer() == null) {
			boolean toggle = this.plugin.partyToggle.isPartyInviteDisabled(p.getUniqueId());
			this.plugin.partyToggle.partytoggle.add(p.getUniqueId());

		}
	}

	@EventHandler
	public void onQuit(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
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
