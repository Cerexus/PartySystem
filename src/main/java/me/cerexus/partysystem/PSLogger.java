package me.cerexus.partysystem;

import net.md_5.bungee.api.ProxyServer;

class PSLogger {

	public void enable() {
		ProxyServer.getInstance().getConsole().sendMessage("§5+========== §1[§9PartySystem§1] §5==========+");
		ProxyServer.getInstance().getConsole().sendMessage("§5| §6Status: §aEnabled!       §5|  ");
		ProxyServer.getInstance().getConsole().sendMessage("§5+================================+");
	}

	public void disable() {
		ProxyServer.getInstance().getConsole().sendMessage("§5+========== §1[§9PartySystem§1] §5==========+");
		ProxyServer.getInstance().getConsole().sendMessage("§5| §6Status: §cDisabled!     §5| ");
		ProxyServer.getInstance().getConsole().sendMessage("§5+================================+");
	}

	public void info(String s) {
		ProxyServer.getInstance().getConsole().sendMessage("§6[§9PartySystem§6]:§e " + s);
	}

	public void error(String s) {
		ProxyServer.getInstance().getConsole().sendMessage("§6[§9PartySystem§6]:§4 " + s);
	}

}
