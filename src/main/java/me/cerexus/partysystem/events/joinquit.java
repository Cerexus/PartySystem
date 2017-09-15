package me.cerexus.partysystem.events;

import me.cerexus.partysystem.PartySystem;
import me.cerexus.partysystem.PartySystemAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class joinquit implements Listener {

	private final PartySystem plugin;

	private PartySystemAPI api = new PartySystemAPI();
	
	public joinquit(PartySystem plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onQuit(ServerConnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		if (p.getServer() == null) {
			boolean toggle = this.plugin.partytoggle.isPartyInviteDisabled(p.getUniqueId());
			this.plugin.partytoggle.partytoggle.add(p.getUniqueId());

		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onQuit(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
			if (this.plugin.partytoggle.partytoggle.contains(p.getUniqueId())) {
				this.plugin.partytoggle.partytoggle.remove(p.getUniqueId());
			}

			if (this.plugin.partys.containsKey(p.getName())) {
				this.api.disbandParty(p.getName());
			}else{
				this.api.leaveParty(p.getUniqueId());
			}

		});

	}

}
