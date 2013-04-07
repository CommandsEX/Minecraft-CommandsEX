package com.github.zathrus_writer.commandsex.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot.Type;
import org.bukkit.entity.Villager.Profession;

public class ClosestMatches {

	public static List<Material> material(String input) {
		ArrayList<Material> values = new ArrayList<Material>();
		ArrayList<Material> matches = new ArrayList<Material>();
        
		for (Material mat : Material.values()){
			if (!values.contains(mat)){
				if (mat == Material.PISTON_STICKY_BASE && !values.contains(Material.PISTON_BASE)){
					values.add(Material.PISTON_BASE);
					values.add(Material.PISTON_STICKY_BASE);
				} else if (mat == Material.REDSTONE_TORCH_OFF && !values.contains(Material.REDSTONE_TORCH_ON)){
					values.add(Material.REDSTONE_TORCH_ON);
					values.add(Material.REDSTONE_TORCH_OFF);
				} else {
					values.add(mat);
				}
			}
		}
		
        for (Material mat : values){
        	if ((mat.name().replace("_", "").toLowerCase().equals(input.toLowerCase()) || String.valueOf(mat.getId()).equals(input))){
        		return Arrays.asList(mat);
            } else if (mat.name().replace("_", "").toLowerCase().contains(input.toLowerCase())){
            	matches.add(mat);
            } else if ("stonebrick".contains(input.toLowerCase())){
        		return Arrays.asList(Material.SMOOTH_BRICK);
        	} else if ("doubleslab".contains(input.toLowerCase())){
        		return Arrays.asList(Material.DOUBLE_STEP);
        	} else if ("clock".contains(input.toLowerCase())){
        		return Arrays.asList(Material.WATCH);
        	}
        }
        
        return matches;
    }
	
	public static List<Profession> villagerProfessions(String input){
		ArrayList<Profession> matches = new ArrayList<Profession>();
		for (Profession prof : Profession.values()){
        	if ((prof.name().replace("_", "").toLowerCase().equals(input.toLowerCase()) || String.valueOf(prof.getId()).equals(input))){
        		return Arrays.asList(prof);
            } else if (prof.name().replace("_", "").toLowerCase().contains(input.toLowerCase())){
            	matches.add(prof);
            }
        }
		
		return matches;
	}
	
	public static List<EntityType> spawnableEntity(String input) {
        ArrayList<EntityType> matches = new ArrayList<EntityType>();
        
        for (EntityType en : EntityType.values()){
        	if (en.isSpawnable()){
        		if ((en.name().replace("_", "").toLowerCase().equals(input.toLowerCase()) || String.valueOf(en.getTypeId()).equals(input))){
            		return Arrays.asList(en);
                } else if (en.name().replace("_", "").toLowerCase().contains(input.toLowerCase())){
                	matches.add(en);
                }
        	}
        }
        
        return matches;
    }
	
	public static List<EntityType> livingEntity(String input) {
        ArrayList<EntityType> matches = new ArrayList<EntityType>();
        
        for (EntityType en : EntityType.values()){
        	if (en.isAlive() && en.isSpawnable()){
        		if ((en.name().replace("_", "").toLowerCase().equals(input.toLowerCase()) || String.valueOf(en.getTypeId()).equals(input))){
            		return Arrays.asList(en);
                } else if (en.name().replace("_", "").toLowerCase().contains(input.toLowerCase())){
                	matches.add(en);
                }
        	}
        }
        
        return matches;
    }
	
	public static List<DyeColor> dyeColor(String input){
		ArrayList<DyeColor> matches = new ArrayList<DyeColor>();
		byte b = -1;
		try {
			b = Byte.parseByte(input);
		} catch (NumberFormatException e){}
		
        for (DyeColor dye : DyeColor.values()){
        	if (dye.name().replace("_", "").toLowerCase().equals(input.toLowerCase())){
        		return Arrays.asList(dye);
            } else if ((b != -1 ? dye.getDyeData() == b : false)){
            	return Arrays.asList(dye);
            } else if (dye.name().replace("_", "").toLowerCase().contains(input.toLowerCase())){
            	matches.add(dye);
            }
        }
        
        return matches;
	}
	
	public static List<Type> catType(String input){
		ArrayList<Type> matches = new ArrayList<Type>();
        
        for (Type type : Type.values()){
        	if ((type.name().replace("_", "").toLowerCase().equals(input.toLowerCase()) || String.valueOf(type.getId()).equals(input))){
        		return Arrays.asList(type);
            } else if (type.name().replace("_", "").toLowerCase().contains(input.toLowerCase())){
            	matches.add(type);
            }
        }
        
        return matches;
	}
	
	public static List<World> world(String input){
		List<World> matches = new ArrayList<World>();
		
        for (World w : Bukkit.getWorlds()){
        	if ((w.getName().toLowerCase().equals(input.toLowerCase()) || String.valueOf(w.getUID()).equals(input))){
        		return Arrays.asList(w);
            } else if (w.getName().toLowerCase().contains(input.toLowerCase())){
            	matches.add(w);
            }
        }
        
        return matches;
	}
	
	public static List<World> intellWorld(String input, World currWorld){
		List<World> matches = new ArrayList<World>();
		input = input.toLowerCase();
		
		if (input.equals("nether") || input.equals("thenether") || input.equals("the_nether")
				|| input.equals("end") || input.equals("theend") || input.equals("the_end")
				|| input.equals("overworld") || input.equals("theoverworld") || input.equals("the_overworld")){
			String cWorld = currWorld.getName();
			String wBase = cWorld.split("_")[0];
			String toWorld = null;
			
			if (input.contains("nether")){
				toWorld = wBase + "_nether";
			}
			
			if (input.contains("end")){
				toWorld = wBase + "_the_end";
			}
			
			if (input.contains("overworld")){
				toWorld = wBase;
			}
			
			World toSend = Bukkit.getWorld(toWorld);
			if (toSend != null){
				matches.add(toSend);
			}
		}
		
		if (matches.size() == 0){
			matches = world(input);
		}
		
		return matches;
	}
	
	public static List<Enchantment> enchantments(String input){
		List<Enchantment> matches = new ArrayList<Enchantment>();
		
		for (Enchantment ench : Enchantment.values()){
			if (ench.getName().replaceAll("_", "").equalsIgnoreCase(input) || String.valueOf(ench.getId()).equals(input)){
				return Arrays.asList(ench);
			} else if (ench.getName().toLowerCase().replaceAll("_", "").contains(input.toLowerCase())){
				matches.add(ench);
			}
		}
		
		return matches;
	}
}
