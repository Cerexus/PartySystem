package me.cerexus.partysystem.commands;

import me.cerexus.partysystem.PartySystem;
import me.cerexus.partysystem.PartySystemAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyCommand extends Command {

	private PartySystemAPI api = new PartySystemAPI();
	private final PartySystem plugin;

	public PartyCommand(PartySystem plugin) {
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
						this.plugin.partyToggle.togglePartyInvites(p.getUniqueId());
						if (this.plugin.partyToggle.isPartyInviteDisabled(p.getUniqueId())) {
							sendMessage(p, "§bParty Invites are now: §aon");
						} else {
							sendMessage(p, "§bParty Invites are now: §coff");
						}
					} else if (args[0].equalsIgnoreCase("disband")) {
						String partyname = this.api.getPartyNameFromUUID(p.getUniqueId());
						if (partyname != null) {
							if (partyname.equalsIgnoreCase(p.getName())) {
								this.api.disbandParty(partyname);
								sendMessage(p, "§bYou have disband the Party!");
							} else {
								sendMessage(p, "§bYou are not Owner of the Party!");
							}
						} else {
							sendMessage(p, "§bYou are not in a Party!");
						}

					} else if (args[0].equalsIgnoreCase("accept")) {
						if (plugin.requesterAndReplier.containsValue(p.getUniqueId())) {
							this.api.acceptParty(p.getUniqueId());
						} else {
							sendMessage(p, "§bYou have no invitation!");
						}
					} else if (args[0].equalsIgnoreCase("decline")) {
						if (plugin.requesterAndReplier.containsValue(p.getUniqueId())) {
							this.api.declineParty(p.getUniqueId());
						} else {
							sendMessage(p, "§bYou have no invitation!");
						}
					} else if (args[0].equalsIgnoreCase("leave")) {
						String partyname = this.api.getPartyNameFromUUID(p.getUniqueId());
						if (partyname != null) {
							this.api.leaveParty(p.getUniqueId(), p.getName());
							sendMessage(p, "§bYou left the Party!");
						} else {
							sendMessage(p, "§bYou are not in a Party!");
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
										sendMessage(p, "§bNew Owner is now set!");
									} else {
										sendMessage(p, "§bThis Player is not in your Party!");
									}
								} else {
									sendMessage(p, "§bThis Player does not exist!");
								}
							} else {
								sendMessage(p, "§bYou are not Owner of the Party!");
							}
						} else {
							sendMessage(p, "§bYou are not in a Party!");
						}
					} else if (args[0].equalsIgnoreCase("invite")) {
						ProxiedPlayer replier = ProxyServer.getInstance().getPlayer(args[1]);
						if (replier != null) {
							if (!PartySystem.getInstance().partyToggle.isPartyInviteDisabled(replier.getUniqueId())) {
								if (!replier.getName().equals(p.getName())) {
									String partyname = this.api.getPartyNameFromUUID(p.getUniqueId());
									if (partyname == null) {
										if (!plugin.requesterAndReplier.containsKey(p.getUniqueId())) {
											if (!plugin.requesterAndReplier.containsValue(replier.getUniqueId())) {
												if (!plugin.requesterAndReplier.containsKey(replier.getUniqueId())) {
													this.api.invitePlayer(p.getUniqueId(), replier.getUniqueId());
												} else {
													sendMessage(p, "§b" + replier.getName() + " has already invited someone else!");
												}
											} else {
												sendMessage(p, "§b" + replier.getName() + " is invited by someone else!");
											}
										} else {
											sendMessage(p, "§bYou already invited someone else!");
										}
									} else {
										sendMessage(p, "§b" + replier.getName() + " is already in a Party!");
									}
								} else {
									sendMessage(p, "§bYou can not invite yourself!");
								}
							} else {
								sendMessage(p, "§b" + replier.getName() + " disabled invitations!");
							}
						} else {
							sendMessage(p, "§bThis Player does not exist!");
						}

					}
					return;
				default:
					sendMessage(p, "§7╔═════════[§b§lCommands§7]═════════╗");
					sendMessage(p, "§b/party invite <Name>");
					sendMessage(p, "§b/party accept");
					sendMessage(p, "§b/party decline");
					sendMessage(p, "§b/party leave");
					sendMessage(p, "§b/party owner <Name>");
					sendMessage(p, "§b/party disband");
					sendMessage(p, "§b/party toggle");
					sendMessage(p, "§7╚═════════════════════════╝");
			}

		}
	}

	private void sendMessage(ProxiedPlayer player, String message) {
		player.sendMessage(TextComponent.fromLegacyText(plugin.prefix + message));
	}

}
