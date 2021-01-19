package me.danieli1818.dr.region_saver_loader.utils.serialization;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.material.Attachable;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;

import me.danieli1818.dr.region_saver_loader.utils.serialization.block.serializables.DirectionalBlockMaterialData;
import me.danieli1818.dr.region_saver_loader.utils.serialization.block.serializables.SignBlockMaterialData;
import me.danieli1818.dr.region_saver_loader.utils.serialization.block.serializables.StepBlockMaterialData;

public class BlockSerializable implements ConfigurationSerializable {

	private Coordinates coordinates;
	private BlockMaterialData data;
	
	public BlockSerializable(Block block) {
		
		this.coordinates = Coordinates.fromLocation(block.getLocation());
		MaterialData md = block.getState().getData();
		if (md instanceof Step) {
			this.data = new StepBlockMaterialData(((Step) md).isInverted());
		} else if (block.getState() instanceof Sign) {
			this.data = new SignBlockMaterialData((Sign)block.getState());
		} else if (md instanceof Directional) {
			if (md instanceof Attachable) {
				this.data = new DirectionalBlockMaterialData(((Attachable)md).getAttachedFace());
			} else {
				this.data = new DirectionalBlockMaterialData(((Directional)md).getFacing());
			}
		} else {
			this.data = null;
		}
	}
	
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
		if (this.data != null) {
			this.data.addToBlock(block);
		}
		return true;
	}
	
	public Location getLocation(World world) {
		return this.coordinates.getLocation(world);
	}

}
