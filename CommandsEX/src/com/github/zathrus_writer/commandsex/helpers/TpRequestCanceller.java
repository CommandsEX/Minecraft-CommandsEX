package com.github.zathrus_writer.commandsex.helpers;

import java.lang.reflect.Method;

public class TpRequestCanceller implements Runnable {

	private String id;
	private String tp;
	
	public TpRequestCanceller(String tpClass, String tId) {
		this.id = tId;
		this.tp = tpClass;
	}
	
	@Override
	public void run() {
		// calls cancellation function of the teleportation class specified
		Class<?>[] proto = new Class[] {String.class};
		//Object[] params = new Object[] {this.id};
		try {
			Class<?> c = Class.forName("com.github.zathrus_writer.commandsex.commands.Command_cex_" + this.tp);
			Method method = c.getDeclaredMethod("cancelRequest", proto);
			method.invoke(null, this.id);
		} catch (Throwable e) {
			LogHelper.logSevere("[CommandsEX] Couldn't auto-cancel teleportation request via class: Command_cex_" + this.tp);
			LogHelper.logDebug("Message: " + e.getMessage() + ", cause: "+e.getCause());
		}
	}
}
