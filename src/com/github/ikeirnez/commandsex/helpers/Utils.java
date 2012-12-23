package com.github.ikeirnez.commandsex.helpers;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public static boolean isInt(String s){
        return s.matches("(-)?(\\d){1,10}(\\.(\\d){1,10})?");
    }
    
    public static List<String> separateCommaList(String s){
        return Arrays.asList(s.split("\\s*,\\s*"));
    }
    
}
