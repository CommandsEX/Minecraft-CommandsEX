package com.github.ikeirnez.commandsex.helpers;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static boolean isInt(String s){
        return s.matches("(-)?(\\d){1,10}(\\.(\\d){1,10})?");
    }
    
    public static List<String> stringArrayToList(String[] array){
        List<String> list = new ArrayList<String>();
        
        for (String s : array){
            list.add(s);
        }
        
        return list;
    }
    
}
