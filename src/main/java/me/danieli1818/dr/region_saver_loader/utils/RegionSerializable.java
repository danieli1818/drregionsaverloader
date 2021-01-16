package me.danieli1818.dr.region_saver_loader.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;

import me.danieli1818.dr.region_saver_loader.RegionSaverLoader;
import me.danieli1818.dr.region_saver_loader.utils.serialization.Coordinates;
import net.minecraft.server.v1_12_R1.ChunkSection;
import net.minecraft.server.v1_12_R1.IBlockData;

public class RegionSerializable implements ConfigurationSerializable {

	private Region region;
	
	private static MemorySection materialsVersionConversionMap = (MemorySection) RegionSaverLoader.getPlugin(RegionSaverLoader.class).getVersionConversionConfig().get("map");
	
	public RegionSerializable(final Region region) {
		this.region = region;
		
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> serialization = new HashMap<String, Object>();
		Vector minimumPoint = getMinimumPoint();
		Vector maximumPoint = getMaximumPoint();
		serialization.put("world", getWorld().getName());
		serialization.put("min_x", minimumPoint.getBlockX());
		serialization.put("min_y", minimumPoint.getBlockY());
		serialization.put("min_z", minimumPoint.getBlockZ());
		serialization.put("max_x", maximumPoint.getBlockX());
		serialization.put("max_y", maximumPoint.getBlockY());
		serialization.put("max_z", maximumPoint.getBlockZ());
		serialization.put("blocks", getBlocksMaterialsMap());
		return serialization;
	}
	
	public static RegionSerializable deserialize(final Map<String, Object> serialization) {
		String worldName = (String) serialization.get("world");
		int min_x = (Integer) serialization.get("min_x");
		int min_y = (Integer) serialization.get("min_y");
		int min_z = (Integer) serialization.get("min_z");
		int max_x = (Integer) serialization.get("max_x");
		int max_y = (Integer) serialization.get("max_y");
		int max_z = (Integer) serialization.get("max_z");
//		World world = BukkitUtil.getLocalWorld(Bukkit.getWorld(worldName));
		final org.bukkit.World world = Bukkit.getWorld(worldName);
		Vector minimumPoint = new Vector(min_x, min_y, min_z);
		Vector maximumPoint = new Vector(max_x, max_y, max_z);
		Region region = new CuboidRegion((World)BukkitUtil.getLocalWorld(world), minimumPoint, maximumPoint);
		Bukkit.getScheduler().scheduleSyncDelayedTask(RegionSaverLoader.getPlugin(RegionSaverLoader.class), new Runnable() {
			
			public void run() {
				buildBlocks((Map<String, List<Coordinates>>) serialization.get("blocks"), world);
				
			}
		});
		return new RegionSerializable(region);
	}
	
	public Vector getMinimumPoint() {
		return this.region.getMinimumPoint();
	}
	
	public Vector getMaximumPoint() {
		return this.region.getMaximumPoint();
	}
	
	public World getWorld() {
		return this.region.getWorld();
	}
	
	public boolean contains(Vector vector) {
		return this.region.contains(vector);
	}
	
	public boolean contains(Location location) {
		Vector vector = new Vector(location.getX(), location.getY(), location.getZ());
		return (this.region.getWorld().getName() == location.getWorld().getName()) && contains(vector);
	}
	
	public void replace(final Material prevMaterial, final Material newMaterial) {
		this.region.forEach(new Consumer<Vector>() {

			public void accept(Vector t) {
				Location location = new Location(Bukkit.getWorld(region.getWorld().getName()), t.getBlockX(), t.getBlockY(), t.getBlockZ());
				
				if (location.getBlock().getType() == prevMaterial) {
					location.getBlock().setType(newMaterial);
				}
				
				
			}
			
		});
	}
	
	private Map<String, List<Coordinates>> getBlocksMaterialsMap() {
		
		final Map<String, List<Coordinates>> blocksMaterialsMap = new HashMap<String, List<Coordinates>>();

		this.region.forEach(new Consumer<Vector>() {

			public void accept(Vector t) {
				Location location = new Location(Bukkit.getWorld(region.getWorld().getName()), t.getBlockX(), t.getBlockY(), t.getBlockZ());
				
				String material = location.getBlock().getType().toString();
				
				if (!blocksMaterialsMap.containsKey(material)) {
					blocksMaterialsMap.put(material, new ArrayList<Coordinates>());
				}
				
				blocksMaterialsMap.get(material).add(Coordinates.fromLocation(location));
				
				
			}
			
		});
		return blocksMaterialsMap;
	}
	
	private static boolean buildBlocks(Map<String, List<Coordinates>> blocksMaterialsMap, org.bukkit.World world) {
		if (blocksMaterialsMap == null) {
			return false;
		}
//		org.bukkit.World world = blocksMaterialsMap.get(blocksMaterialsMap.keySet().iterator().next()).get(0).getWorld();
		for (String material : blocksMaterialsMap.keySet()) {
			Material materialEnum = Material.getMaterial(material);
			System.out.println(RegionSerializable.materialsVersionConversionMap);
			Integer data = null;
			if (RegionSerializable.materialsVersionConversionMap.contains(material)) {
				String current_version_material_and_data = RegionSerializable.materialsVersionConversionMap.getString(material);
				String[] material_and_data = current_version_material_and_data.split("\\|");
				System.out.println(material_and_data[0]);
				materialEnum = Material.getMaterial(material_and_data[0]);
				if (material_and_data.length > 1) {
					try {
						System.out.println(material_and_data[1]);
						data = Integer.parseInt(material_and_data[1]);
					} catch(NumberFormatException e) {
						
					}
					
				}
			}
//			if (material.equals("STONE_BRICKS")) {
//				materialEnum = Material.getMaterial("SMOOTH_BRICK");
//			}
			if (materialEnum == null) { // Check if there has been a problem.
				System.out.println(material);
				continue;
			}
			if (data != null) {
				for (Coordinates coordinates : blocksMaterialsMap.get(material)) {
//					if (location.getBlock().getType() != materialEnum) {
					Block block = coordinates.getLocation(world).getBlock();
					block.setType(materialEnum);
					block.setData(data.byteValue());
//					setBlockInNativeDataPalette(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), materialEnum.getId(), (byte) 0, true);
//					}
				}
				
			} else {
				for (Coordinates coordinates : blocksMaterialsMap.get(material)) {
//					if (location.getBlock().getType() != materialEnum) {
					coordinates.getLocation(world).getBlock().setType(materialEnum);
//					setBlockInNativeDataPalette(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), materialEnum.getId(), (byte) 0, true);
//					}
				}
			}
		}
		return true;
	}
	
	public static void setBlockInNativeDataPalette(org.bukkit.World world, int x, int y, int z, int blockId, byte data, boolean applyPhysics) {
	    net.minecraft.server.v1_12_R1.World nmsWorld = ((CraftWorld) world).getHandle();
	    net.minecraft.server.v1_12_R1.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
	    IBlockData ibd = net.minecraft.server.v1_12_R1.Block.getByCombinedId(blockId + (data << 12));

	    ChunkSection cs = nmsChunk.getSections()[y >> 4];
	    if (cs == nmsChunk.a) {
	        cs = new ChunkSection(y >> 4 << 4, applyPhysics);
	        nmsChunk.getSections()[y >> 4] = cs;
	    }

	    if (applyPhysics)
	        cs.getBlocks().setBlock(x & 15, y & 15, z & 15, ibd);
	    else
	    	cs.getBlocks().setBlock(x & 15, y & 15, z & 15, ibd);
//	       cs.getBlocks().b(x & 15, y & 15, z & 15, ibd);
	}
	
	public static void reloadConfig() {
		RegionSerializable.materialsVersionConversionMap = (MemorySection) RegionSaverLoader.getPlugin(RegionSaverLoader.class).getVersionConversionConfig().get("map");
	}
	
}
