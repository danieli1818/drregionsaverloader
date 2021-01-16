package me.danieli1818.dr.region_saver_loader.utils.serialization;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class BlockSerializable implements ConfigurationSerializable {

	private Coordinates coordinates;
	private BlockMaterialData data;
	
	public BlockSerializable(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	public BlockSerializable(Coordinates coordinates, BlockMaterialData data) {
		this(coordinates);
		this.data = data;
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("coordinates", this.coordinates);
		map.put("data", this.data);
		return map;
	}
	
	public static BlockSerializable deserialize(Map<String, Object> serializationMap) {
		if (!serializationMap.containsKey("coordinates")) {
			return null;
		}
		Object coordinates = serializationMap.get("coordinates");
		if (coordinates instanceof Coordinates) {
			BlockSerializable bs = new BlockSerializable((Coordinates)coordinates);
			if (serializationMap.containsKey("data")) {
				Object data = serializationMap.get("data");
				if (data instanceof BlockMaterialData) {
					bs.data = (BlockMaterialData)data;
				}
			}
			return bs;
		}
		return null;
	}
	
	public boolean setBlock(World world, Material material, Byte data) {
		if (world == null) {
			return false;
		}
		Location location = this.coordinates.getLocation(world);
		Block block = location.getBlock();
		block.setType(material);
		if (data != null) {
			block.setData(data);
		}
		this.data.addToBlock(block);
		return true;
	}

}
