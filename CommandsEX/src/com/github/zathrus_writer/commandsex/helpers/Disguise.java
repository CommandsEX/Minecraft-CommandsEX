package com.github.zathrus_writer.commandsex.helpers;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.DataWatcher;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet21PickupSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet31RelEntityMove;
import net.minecraft.server.Packet32EntityLook;
import net.minecraft.server.Packet33RelEntityMoveLook;
import net.minecraft.server.Packet35EntityHeadRotation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Disguise {

	public static Packet29DestroyEntity destroyEntity(Entity entity){
		int id = entity.getEntityId();
		Packet29DestroyEntity packet = new Packet29DestroyEntity(id);
		return packet;
	}
	
	static int oldPosX;
	static int oldPosY;
	static int oldPosZ;
	
	public static DataWatcher metadata = new DataWatcher();
	
	public static Packet24MobSpawn spawnMob(Location loc, int id, EntityType type) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Packet24MobSpawn packet = new Packet24MobSpawn();
		packet.a = id; //Entity UDID
		packet.b = type.getTypeId();
		packet.c = (int) Math.floor(loc.getX() * 32.0D);
		packet.d = (int) Math.floor(loc.getY() * 32.0D);
		packet.e = (int) Math.floor(loc.getZ() * 32.0D);
		
		oldPosX = MathHelper.floor(loc.getX() * 32.0D);
		oldPosY = MathHelper.floor(loc.getY() * 32.0D);
		oldPosZ = MathHelper.floor(loc.getZ() * 32.0D);
		
		packet.f = Utils.degreeToByte(loc.getYaw());
		packet.g = Utils.degreeToByte(loc.getPitch());
		packet.h = packet.f;
		 
		Field metadataField = packet.getClass().getDeclaredField("s");
		metadataField.setAccessible(true);
		metadataField.set(packet, metadata);
		
		metadata.a(0, (byte) 0);
		metadata.a(12, 0);
		metadata.a(17, (byte) 0);
		
		return packet;
	}
	
	public static Packet31RelEntityMove movePacket(Location move, int id){
		Packet31RelEntityMove packet = new Packet31RelEntityMove();
		
		packet.a = id;
		
		packet.b
	}
	
	public static Packet33RelEntityMoveLook relMovePacket(Location move, int id){
		Packet33RelEntityMoveLook packet = new Packet33RelEntityMoveLook();
		packet.a = id;
		
		packet.b = (byte) (MathHelper.floor(move.getX() * 32.0D) - oldPosX);
		packet.c = (byte) (MathHelper.floor(move.getY() * 32.0D) - oldPosY);
		packet.d = (byte) (MathHelper.floor(move.getZ() * 32.0D) - oldPosZ);
		
		packet.e = (byte) ((int) move.getYaw() * 256.0F  / 360.0F);
		packet.f = (byte) ((int) move.getPitch() * 256.0F  / 360.0F);
		
		oldPosX = MathHelper.floor(move.getX() * 32.0D);
		oldPosY = MathHelper.floor(move.getY() * 32.0D);
		oldPosZ = MathHelper.floor(move.getZ() * 32.0D);

		return packet;
	}
	
	public static Packet32EntityLook lookPacket(Location move, int id){
		Packet32EntityLook packet = new Packet32EntityLook();
		
		packet.a = id;
		packet.b = 0;
		packet.c = 0;
		packet.d = 0;
		packet.e = Utils.degreeToByte(move.getYaw());
		packet.f = Utils.degreeToByte(move.getPitch());
		
		return packet;
	}
	
	public static Packet35EntityHeadRotation headTurnPacket(Location move, int id){
		Packet35EntityHeadRotation packet = new Packet35EntityHeadRotation(id, Utils.degreeToByte(move.getYaw()));
		return packet;
	}
	
	private static int idCount = 1000000;
	public static HashMap<String, Integer> ids = new HashMap<String, Integer>();
	 
	public static void disguise(Player player, EntityType type) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		int id = idCount++;
		Location location = player.getLocation();
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p != player){
				((CraftPlayer) p).getHandle().netServerHandler.sendPacket(destroyEntity(player));
				((CraftPlayer) p).getHandle().netServerHandler.sendPacket(spawnMob(location, id, type));
			}
		}
		
		ids.put(player.getName(), id);
	}
	
}
