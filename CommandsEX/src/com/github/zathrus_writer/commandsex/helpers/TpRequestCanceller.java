package com.github.zathrus_writer.commandsex.helpers;

import java.lang.reflect.Method;
import java.util.List;

public class TpRequestCanceller implements Runnable {

	private String id;
	private String tp;
	private List<String> requests;
	
	public TpRequestCanceller(String tpClass, String tId) {
		this.id = tId;
		this.tp = tpClass;
	}
	
	public TpRequestCanceller(String tpClass, List<String> requests){
		this.tp = tpClass;
		this.requests = requests;
	}
	
	@Override
	public void run() {
		// calls cancellation function of the teleportation class specified
		Class<?>[] proto = new Class[] {String.class};
		//Object[] params = new Object[] {this.id};
		try {
			Class<?> c = Class.forName("com.github.zathrus_writer.commandsex.commands.Command_cex_" + this.tp);
			Method method = c.getDeclaredMethod((!tp.equalsIgnoreCase("tpaall") ? "cancelRequest" : "cancelRequests"), proto);
			method.invoke(null, (!tp.equalsIgnoreCase("tpaall") ? this.id : requests));
		} catch (Throwable e) {
			LogHelper.logSevere("[CommandsEX] Couldn't auto-cancel teleportation request via class: Command_cex_" + this.tp);
			LogHelper.logDebug("Message: " + e.getMessage() + ", cause: "+e.getCause());
		}
	}
}
