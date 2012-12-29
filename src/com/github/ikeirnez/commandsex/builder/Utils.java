package com.github.ikeirnez.commandsex.builder;

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Utils {

    public static void closeWindow(Window window) {
        WindowEvent wev = new WindowEvent(window, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    }

    public static int getPercentFromTotal(long total, long subtotal) {
        return (int) (((float) subtotal / (float) total) * 100);
    }

    public static String readUrl(String address){
        BufferedReader reader = null;
        try {
            URL url = new URL(address);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1){
                buffer.append(chars, 0, read); 
            }

            return buffer.toString();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static URL getLatestJenkinsDownload(){
        try {
            String json = readUrl("http://build.yu8.me:8090/job/CommandsEX/api/json");
            
            if (json == null){
                return null;
            }
            
            JsonElement jElement = new JsonParser().parse(json);
            JsonObject  jObject = jElement.getAsJsonObject().getAsJsonObject("lastStableBuild");
            int number = jObject.getAsJsonPrimitive("number").getAsInt();
            String baseUrl = jObject.getAsJsonPrimitive("url").getAsString();
            
            StringBuilder sb = new StringBuilder();
            sb.append(baseUrl);
            sb.append("artifact/CommandsEX-");
            sb.append(number);
            sb.append(".jar");
            
            URL url = new URL(sb.toString());
            
            return url;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}