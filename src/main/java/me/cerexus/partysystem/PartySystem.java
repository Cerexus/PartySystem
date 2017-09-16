package me.cerexus.partysystem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import me.cerexus.partysystem.commands.PartyCommand;
import me.cerexus.partysystem.events.JoinQuit;
import me.cerexus.partysystem.events.PartyActivity;
import me.cerexus.partysystem.objects.Party;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class PartySystem extends Plugin {

	public File toggle;
	public String prefix = "§7[§5PartySystem§7] §r";
	public HashMap<String, Party> partys = new HashMap<>();
	public HashMap<UUID, ScheduledTask> pendingRequests = new HashMap<>();
	public HashMap<UUID, UUID> requesterAndReplier = new HashMap<>();
	public PartyToggle partyToggle;

	private static PartySystem plugin;

	@Override
	public void onEnable() {
		this.plugin = this;
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		this.toggle = new File(getDataFolder(), "Toggle.yml");
		if (!this.toggle.exists()) {
			try {
				this.toggle.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("Unable to create configuration file!", e);
			}
		}

		this.partyToggle = new PartyToggle(this);

		registerListener();
		registerCommands();
		System.out.println("[PartySystem] wurde aktiviert!");
	}

	@Override
	public void onDisable() {
		System.out.println("[PartySystem] wurde deaktiviert!");
	}

	private void registerListener() {
		ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinQuit(this));
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PartyActivity());

	}

	private void registerCommands() {
		getProxy().getPluginManager().registerCommand(this, new PartyCommand(this));
	}

	public static PartySystem getInstance() {
		return PartySystem.plugin;
	}

}
