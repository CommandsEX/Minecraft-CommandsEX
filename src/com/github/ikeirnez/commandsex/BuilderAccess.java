package com.github.ikeirnez.commandsex;

import java.io.InputStream;

/**
 * Functions needed by the Builder
 * This class MUST NOT use/reference anything to do with Bukkit, CraftBukkit or any included libraries
 */
public class BuilderAccess {

    public static InputStream getPluginYML(){
        return BuilderAccess.class.getClassLoader().getResourceAsStream("plugin.yml");
    }
    
}
