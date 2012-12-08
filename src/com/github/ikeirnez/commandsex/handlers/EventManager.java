package com.github.ikeirnez.commandsex.handlers;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.api.interfaces.IEvent;

public class EventManager implements Listener {

    public int registerEvents(){
        int ret = 0;
        Reflections reflections = new Reflections("com.github.ikeirnez.commandsex.handlers");
        Set<Class<? extends IEvent>> commandClasses = reflections.getSubTypesOf(IEvent.class);

        Iterator<Class<? extends IEvent>> it = commandClasses.iterator();
        while (it.hasNext()){
            Class<? extends IEvent> clazz = it.next();
            
            registerEvent(clazz);
            ret++;
        }
        
        return ret;
    }
    
    public void registerEvent(Class<? extends IEvent> clazz){
        try {
            registerEvent((IEvent) clazz.newInstance());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public void registerEvent(IEvent evnt){
        Bukkit.getServer().getPluginManager().registerEvents(evnt, CommandsEX.plugin);
    }
    
}
