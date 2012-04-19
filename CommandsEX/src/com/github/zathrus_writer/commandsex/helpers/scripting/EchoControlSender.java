package com.github.zathrus_writer.commandsex.helpers.scripting;

import java.util.Set;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 */
public class EchoControlSender extends EchoControl {
	
	public EchoControlSender(CommandSender sender) {
		super(sender);
	}

	@Override public String getName() {
		return wrappedSender.getName();
	}

	@Override public Server getServer() {
		return wrappedSender.getServer();
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin) {
		return wrappedSender.addAttachment(plugin);
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return wrappedSender.addAttachment(plugin, ticks);
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin, String name,
			boolean value) {
		return wrappedSender.addAttachment(plugin, name, value);
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin, String name,
			boolean value, int ticks) {
		return wrappedSender.addAttachment(plugin, name, value, ticks);
	}

	@Override public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return wrappedSender.getEffectivePermissions();
	}

	@Override public boolean hasPermission(String name) {
		return wrappedSender.hasPermission(name);
	}

	@Override public boolean hasPermission(Permission perm) {
		return wrappedSender.hasPermission(perm);
	}

	@Override public boolean isPermissionSet(String name) {
		return wrappedSender.isPermissionSet(name);
	}

	@Override public boolean isPermissionSet(Permission perm) {
		return wrappedSender.isPermissionSet(perm);
	}

	@Override public void recalculatePermissions() {
		wrappedSender.recalculatePermissions();
	}

	@Override public void removeAttachment(PermissionAttachment attachment) {
		wrappedSender.removeAttachment(attachment);
	}

	@Override public boolean isOp() {
		return wrappedSender.isOp();
	}

	@Override public void setOp(boolean arg0) {
		wrappedSender.setOp(arg0);
	}
}
