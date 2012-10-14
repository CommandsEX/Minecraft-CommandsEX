package com.github.zathrus_writer.commandsex.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.github.zathrus_writer.commandsex.CommandsEX;

/**
 * This class will convert all aspects of CommandBook to CommandsEX
 * This converter is capable of converting, Homes, Warps, Muted Players, Spawn Location and Bans
 * @author iKeirNez
 */

public class CommandBookConverter {

	public static void init(CommandsEX plugin){

		/**
		 * Frequently Used Variables
		 */

		// create the oldConversionsFolder if it doesn't already exist
		File oldConversionsFolder = new File(plugin.getDataFolder() + "/old-conversions");
		if (!oldConversionsFolder.exists()){
			oldConversionsFolder.mkdir();
		}

		// generate part of the filename by the year, month, day, hour, minute, second and millisecond
		// this gives us unique names and makes sure that there are no filename clashes
		Calendar cal = Calendar.getInstance();
		String dateString = cal.get(Calendar.YEAR) + "" + cal.get(Calendar.MONTH) + "" + cal.get(Calendar.DAY_OF_MONTH) + "" 
				+ cal.get(Calendar.HOUR_OF_DAY) + "" + cal.get(Calendar.MINUTE) + "" + cal.get(Calendar.SECOND) + "" + cal.get(Calendar.MILLISECOND);

		/**
		 * Home Importing
		 */

		File cmdBkHomes = new File(plugin.getDataFolder() + "/convert/CommandBookHomes.csv");
		if (cmdBkHomes.exists()){
			try {
				LogHelper.logInfo("Started CommandBook Home Conversion");
				File oldConversion = new File(plugin.getDataFolder() + "/old-conversions/CommandBookHomes" + dateString + ".csv");
				// begin reading the CSV file
				BufferedReader CSVFile = new BufferedReader(new FileReader(cmdBkHomes.getAbsolutePath()));
				// Read the first line
				String dataRow = CSVFile.readLine();

				// run this, while there are more rows
				while (dataRow != null){
					// split the rows values into an array
					String[] dataArray = dataRow.split(",");

					// remove the first and last " on all arguments
					for (int i = 0; i < dataArray.length; i++){
						dataArray[i] = dataArray[i].substring(1, dataArray[i].length() - 1);
					}
					
					// get some variables from the CSV file
					String homeName = dataArray[0];
					String ownerName = dataArray[2];
					String worldName = dataArray[1];
					World world = Bukkit.getWorld(worldName);

					// if the world does not exist, don't continue
					if (world == null){
						LogHelper.logWarning("Home for " + ownerName + " could not be converted from CommandBook -> CommandsEX");
						LogHelper.logWarning("ERROR: World " + worldName + " does not exist, skipping...");
					} else {
						double x = Double.parseDouble(dataArray[3]);
						double y = Double.parseDouble(dataArray[4]);
						double z = Double.parseDouble(dataArray[5]);
						float yaw = Float.parseFloat(dataArray[6]);
						float pitch = Float.parseFloat(dataArray[7]);

						// construct the location
						Location l = new Location(world, x, y, z, yaw, pitch);

						// only convert to home if it is the players PRIMARY home as CommandsEX
						// otherwise we will try to convert it to a private warp
						if (homeName.equals(ownerName)){
							// This sets the players home, deleting all other homes IF allowMultiworldHomes is false
							Home.setHome(ownerName, l);
							// alert to console
							LogHelper.logInfo("Set home for " + ownerName + " successfully!");
						} else {
							LogHelper.logInfo("Home named " + homeName + " is not the primary home of " + ownerName);
							if (CommandsEX.loadedClasses.contains("Command_cex_warp")){
								LogHelper.logInfo("Instead we will convert this home to a warp, owned by " + ownerName);
								Warps.createWarp(ownerName, homeName, false, l);
								LogHelper.logInfo("Successfully created private warp called " + homeName + " for " + ownerName);
							} else {
								LogHelper.logInfo("Warps have not been enabled, therefore we cannot convert this home to a warp");
							}
						}
					}

					// Read next line of data
					dataRow = CSVFile.readLine();
				}
				// Close the file once all data has been read.
				CSVFile.close();

				// Copy CommandBookHomes.csv to the oldConversion folder, with a unique suffix
				Utils.copyFile(cmdBkHomes, oldConversion);
				cmdBkHomes.delete();

				// alert to console
				LogHelper.logInfo("CommandBook Home Conversion Finished, CommandBookHomes.csv moved to old-conversions");
			} catch (FileNotFoundException e) {
				e.printStackTrace();// this should never be thrown as we check if the file exists above
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Warp Importing
		 */

		File cmdBkWarps = new File(plugin.getDataFolder() + "/convert/CommandBookWarps.csv");

		if (cmdBkWarps.exists()){
			try {
				LogHelper.logInfo("Started CommandBook Warp Conversion");
				File oldConversion = new File(plugin.getDataFolder() + "/old-conversions/CommandBookWarps" + dateString + ".csv");
				// begin reading the CSV file
				BufferedReader CSVFile = new BufferedReader(new FileReader(cmdBkWarps.getAbsolutePath()));
				// Read the first line
				String dataRow = CSVFile.readLine();

				// run this, while there are more rows
				while (dataRow != null){
					// split the rows values into an array
					String[] dataArray = dataRow.split(",");

					// remove the first and last " on all arguments
					for (int i = 0; i < dataArray.length; i++){
						dataArray[i] = dataArray[i].substring(1, dataArray[i].length() - 1);
					}
					
					// get some variables from the CSV file
					String warpName = dataArray[0];
					String ownerName = dataArray[2];
					String worldName = dataArray[1];
					World world = Bukkit.getWorld(worldName);

					// if the world does not exist, don't continue
					if (world == null){
						LogHelper.logWarning("Warp named " + warpName + " could not be converted from CommandBook -> CommandsEX");
						LogHelper.logWarning("ERROR: World " + worldName + " does not exist, skipping...");
					} else {
						double x = Double.parseDouble(dataArray[3]);
						double y = Double.parseDouble(dataArray[4]);
						double z = Double.parseDouble(dataArray[5]);
						float yaw = Float.parseFloat(dataArray[6]);
						float pitch = Float.parseFloat(dataArray[7]);

						// construct the location
						Location l = new Location(world, x, y, z, yaw, pitch);

						// This creates the public warp
						Warps.createWarp(ownerName, warpName, true, l);
						// alert to console
						LogHelper.logInfo("Created warp " + warpName + " successfully!");
					}
					
					// Read next row of data
					dataRow = CSVFile.readLine();
				}

				// Close the file once all data has been read.
				CSVFile.close();

				// Copy CommandBookWarps.csv to the oldConversion folder, with a unique suffix
				Utils.copyFile(cmdBkWarps, oldConversion);
				cmdBkWarps.delete();

				// alert to console
				LogHelper.logInfo("CommandBook Warp Conversion Finished, CommandBookWarps.csv moved to old-conversions");
			} catch (FileNotFoundException e) {
				e.printStackTrace();// this should never be thrown as we check if the file exists above
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}