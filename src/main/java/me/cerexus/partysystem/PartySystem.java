package me.cerexus.partysystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import me.cerexus.partysystem.commands.party;
import me.cerexus.partysystem.events.joinquit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class PartySystem extends Plugin {

	public PSLogger log = new PSLogger();
	public File toggle;

	public static String prefix = "§7[§5PartySystem§7] §r";
	public static HashMap<String, ArrayList<UUID>> partys = new HashMap<>();
	public static HashMap<UUID, ScheduledTask> pendingrequests = new HashMap<>();
	public static HashMap<UUID, UUID> requesterandreplier = new HashMap<>();
	public static PartyToggle partytoggle;
	public static PartySystem plugin;

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

		this.partytoggle = new PartyToggle(this);

		registerListener();
		registerCommands();
		this.log.enable();
	}

	@Override
	public void onDisable() {
		this.log.disable();
	}

	private void registerListener() {
		ProxyServer.getInstance().getPluginManager().registerListener(this, new joinquit(this));

	}

	private void registerCommands() {
		getProxy().getPluginManager().registerCommand(this, new party(this));
	}

}
