package me.cerexus.partysystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class PartyToggle {

	public ArrayList<UUID> partytoggle = new ArrayList<>();

	private final PartySystem plugin;

	PartyToggle(PartySystem plugin) {
		this.plugin = plugin;
	}

	public void allowPartyInvites(UUID uuid) {
		ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
			try {
				Configuration toggle = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.plugin.toggle);
				if (!toggle.contains(uuid.toString())) {
					toggle.set(uuid.toString(), " ");
				}
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(toggle, this.plugin.toggle);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}

	public void denyPartyInvites(UUID uuid) {
		ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
			try {
				Configuration toggle = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.plugin.toggle);
				if (toggle.contains(uuid.toString())) {
					toggle.set(uuid.toString(), null);
				}
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(toggle, this.plugin.toggle);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}

	public void togglePartyInvites(UUID uuid) {
		ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
			try {
				Configuration toggle = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.plugin.toggle);
				if (toggle.contains(uuid.toString())) {
					toggle.set(uuid.toString(), null);
				}else{
					toggle.set(uuid.toString(), " ");
				}
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(toggle, this.plugin.toggle);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}

	public boolean isPartyInviteDisabled(UUID uuid) {
		try {
			Configuration toggle = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.plugin.toggle);
			return toggle.contains(uuid.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
