package me.cerexus.partysystem.commands;

import me.cerexus.partysystem.PartySystem;
import me.cerexus.partysystem.PartySystemAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class party extends Command {

	private PartySystem plugin;
	private PartySystemAPI api = new PartySystemAPI();

	public party(PartySystem plugin) {
		super("party", "", new String[]{"p"});
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			switch (args.length) {
				case 1:
					if (args[0].equalsIgnoreCase("toggle")) {
						this.plugin.partytoggle.togglePartyInvites(p.getUniqueId());
						if (this.plugin.partytoggle.isPartyInviteDisabled(p.getUniqueId())) {
							p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bParty Invites are now: §aon"));
						} else {
							p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bParty Invites are now: §coff"));
						}
					} else if (args[0].equalsIgnoreCase("disband")) {
						String partyname = this.api.getPartyNameFromUUID(p.getUniqueId());
						if (partyname != null) {
							if (partyname.equalsIgnoreCase(p.getName())) {
								this.api.disbandParty(partyname);
								p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bThe Party is now disband!"));
							} else {
								p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bYou are not Owner of the Party!"));
							}
						} else {
							p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bYou are not in a Party!"));
						}

					} else if (args[0].equalsIgnoreCase("accept")) {
						if (PartySystem.requesterandreplier.containsValue(p.getUniqueId())) {
							this.api.acceptParty(p.getUniqueId());
						} else {
							p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bYou have no invitation!"));
						}
					} else if (args[0].equalsIgnoreCase("decline")) {
						if (PartySystem.requesterandreplier.containsValue(p.getUniqueId())) {
							this.api.declineParty(p.getUniqueId());
						} else {
							p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bYou have no invitation!"));
						}
					} else if (args[0].equalsIgnoreCase("leave")) {
						String partyname = this.api.getPartyNameFromUUID(p.getUniqueId());
						if (partyname != null) {
							this.api.leaveParty(p.getUniqueId());
							p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bYou left the Party!"));
						} else {
							p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bYou are not in a Party!"));
						}
					}
					return;
				case 2:
					if (args[0].equalsIgnoreCase("owner")) {
						String partyname = this.api.getPartyNameFromUUID(p.getUniqueId());
						if (partyname != null) {
							if (partyname.equalsIgnoreCase(p.getName())) {
								ProxiedPlayer newowner = ProxyServer.getInstance().getPlayer(args[1]);
								if (newowner != null) {
									if (this.api.isInSameParty(p.getUniqueId(), newowner.getUniqueId())) {
										this.api.changePartyOwner(partyname, newowner.getUniqueId());
										p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bNew Owner is now set!"));
									} else {
										p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bThis Player is not in your Party!"));
									}
								} else {
									p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bThis Player does not exist!"));
								}
							} else {
								p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bYou are not Owner of the Party!"));
							}
						} else {
							p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bYou are not in a Party!"));
						}
					} else if (args[0].equalsIgnoreCase("invite")) {
						ProxiedPlayer replier = ProxyServer.getInstance().getPlayer(args[1]);
						if (replier != null) {
							if (!PartySystem.partytoggle.isPartyInviteDisabled(replier.getUniqueId())) {
								if (!replier.getName().equals(p.getName())) {
									String partyname = this.api.getPartyNameFromUUID(p.getUniqueId());
									if (partyname == null) {
										if (!PartySystem.requesterandreplier.containsKey(p.getUniqueId())) {
											if (!PartySystem.requesterandreplier.containsValue(replier.getUniqueId())) {
												if (!PartySystem.requesterandreplier.containsKey(replier.getUniqueId())) {
													this.api.invitePlayer(p.getUniqueId(), replier.getUniqueId());
												} else {
													p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b" + replier.getName() + " has already invited someone else!"));
												}
											} else {
												p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b" + replier.getName() + " is invited by someone else!"));
											}
										} else {
											p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bYou already invited someone else!"));
										}
									} else {
										p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b" + replier.getName() + " is already in a Party!"));
									}
								} else {
									p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bYou can not invite yourself!"));
								}
							} else {
								p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b" + replier.getName() + " disabled invitations!"));
							}
						} else {
							p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§bThis Player does not exist!"));
						}

					}
					return;
				default:
					p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§7╔═════════[§b§lCommands§7]═════════╗"));
					p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b/party invite <Name>"));
					p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b/party accept"));
					p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b/party decline"));
					p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b/party leave"));
					p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b/party owner <Name>"));
					p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b/party disband"));
					p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§b/party toggle"));
					p.sendMessage(TextComponent.fromLegacyText(plugin.prefix + "§7╚═════════════════════════╝"));
			}

		}
	}

}
