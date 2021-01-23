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
import org.bukkit.block.data.type.Slab;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;

import me.danieli1818.dr.region_saver_loader.RegionSaverLoader;
import me.danieli1818.dr.region_saver_loader.utils.serialization.BlockSerializable;
import me.danieli1818.dr.region_saver_loader.utils.serialization.Coordinates;

public class RegionSerializable implements ConfigurationSerializable {

	private Region region;
	
	private static MemorySection materialsVersionConversionMap = (MemorySection) RegionSaverLoader.getPlugin(RegionSaverLoader.class).getVersionConversionConfig().get("map");
	
	public RegionSerializable(final Region region) {
		this.region = region;
		
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> serialization = new HashMap<String, Object>();
		BlockVector3 minimumPoint = getMinimumPoint();
		BlockVector3 maximumPoint = getMaximumPoint();
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
		BlockVector3 minimumPoint = BlockVector3.at(min_x, min_y, min_z);
		BlockVector3 maximumPoint = BlockVector3.at(max_x, max_y, max_z);
		Region region = new CuboidRegion(new BukkitWorld(world), minimumPoint, maximumPoint);
		Bukkit.getScheduler().scheduleSyncDelayedTask(RegionSaverLoader.getPlugin(RegionSaverLoader.class), new Runnable() {
			
			public void run() {
				buildBlocks((Map<String, List<BlockSerializable>>) serialization.get("blocks"), world);
				
			}
		});
		return new RegionSerializable(region);
	}
	
	public BlockVector3 getMinimumPoint() {
		return this.region.getMinimumPoint();
	}
	
	public BlockVector3 getMaximumPoint() {
		return this.region.getMaximumPoint();
	}
	
	public World getWorld() {
		return this.region.getWorld();
	}
	
	public boolean contains(BlockVector3 vector) {
		return this.region.contains(vector);
	}
	
	public boolean contains(Location location) {
		BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
		return (this.region.getWorld().getName() == location.getWorld().getName()) && contains(vector);
	}
	
	public void replace(final Material prevMaterial, final Material newMaterial) {
		this.region.forEach(new Consumer<BlockVector3>() {

			public void accept(BlockVector3 t) {
				Location location = new Location(Bukkit.getWorld(region.getWorld().getName()), t.getBlockX(), t.getBlockY(), t.getBlockZ());
				
				if (location.getBlock().getType() == prevMaterial) {
					location.getBlock().setType(newMaterial);
				}
				
				
			}
			
		});
	}
	
	private Map<String, List<BlockSerializable>> getBlocksMaterialsMap() {
		
		final Map<String, List<BlockSerializable>> blocksMaterialsMap = new HashMap<String, List<BlockSerializable>>();

		this.region.forEach(new Consumer<BlockVector3>() {

			public void accept(BlockVector3 t) {
				Location location = new Location(Bukkit.getWorld(region.getWorld().getName()), t.getBlockX(), t.getBlockY(), t.getBlockZ());
				
				String material = location.getBlock().getType().toString();
				
				if (location.getBlock().getBlockData() instanceof Slab) {
					Slab slab = (Slab) location.getBlock().getBlockData();
					if (slab.getType() == Slab.Type.DOUBLE) {
						material = "DOUBLE_" + material;
					}
				}
				
				if (!blocksMaterialsMap.containsKey(material)) {
					blocksMaterialsMap.put(material, new ArrayList<BlockSerializable>());
				}
				
				blocksMaterialsMap.get(material).add(new BlockSerializable(location.getBlock()));
				
				
			}
			
		});
		return blocksMaterialsMap;
	}
	
	private static boolean buildBlocks(Map<String, List<BlockSerializable>> blocksMaterialsMap, org.bukkit.World world) {
		if (blocksMaterialsMap == null) {
			return false;
		}
//		org.bukkit.World world = blocksMaterialsMap.get(blocksMaterialsMap.keySet().iterator().next()).get(0).getWorld();
		for (String material : blocksMaterialsMap.keySet()) {
			Material materialEnum = Material.getMaterial(material);
			System.out.println(RegionSerializable.materialsVersionConversionMap);
			Byte data = null;
			if (RegionSerializable.materialsVersionConversionMap.contains(material)) {
				String current_version_material_and_data = RegionSerializable.materialsVersionConversionMap.getString(material);
				String[] material_and_data = current_version_material_and_data.split("\\|");
				System.out.println(material_and_data[0]);
				materialEnum = Material.getMaterial(material_and_data[0]);
				if (material_and_data.length > 1) {
					try {
						System.out.println(material_and_data[1]);
						data = Byte.parseByte(material_and_data[1]);
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
			for (BlockSerializable blockSerializable : blocksMaterialsMap.get(material)) {
//				if (location.getBlock().getType() != materialEnum) {
				blockSerializable.setBlock(world, materialEnum, data);
//				block.setType(materialEnum);
//				block.setData(data.byteValue());
//				setBlockInNativeDataPalette(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), materialEnum.getId(), (byte) 0, true);
//				}
			}
		}
		return true;
	}
	
	public static void reloadConfig() {
		RegionSerializable.materialsVersionConversionMap = (MemorySection) RegionSaverLoader.getPlugin(RegionSaverLoader.class).getVersionConversionConfig().get("map");
	}
	
}
