package com.github.ikeirnez.commandsex.api;

public interface Module {
	public boolean isLoadable();
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
}
