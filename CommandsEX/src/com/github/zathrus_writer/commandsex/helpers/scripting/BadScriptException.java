package com.github.zathrus_writer.commandsex.helpers.scripting;


/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 */
public class BadScriptException extends Exception {
	private static final long serialVersionUID = 8951068159356743076L;
	public BadScriptException() {super();}
	public BadScriptException(String message, Throwable cause) {super(message, cause);}
	public BadScriptException(String message) {super(message);}
	public BadScriptException(Throwable cause) {super(cause);}
}