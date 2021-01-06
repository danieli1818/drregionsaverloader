package me.danieli1818.dr.region_saver_loader.utils.serialization.block.serializables;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import me.danieli1818.dr.region_saver_loader.utils.serialization.BlockMaterialData;

public class DirectionalBlockMaterialData implements BlockMaterialData {

	private BlockFace blockFace;
	
	public DirectionalBlockMaterialData(BlockFace blockFace) {
		this.blockFace = blockFace;
		if (this.blockFace == null) {
			throw new RuntimeException("Block Face Is Null!");
		}
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("blockFace", blockFace.toString());
		return map;
	}
	
	public DirectionalBlockMaterialData deserialize(Map<String, Object> serializationMap) {
		if (serializationMap.containsKey("blockFace")) {
			Object blockFace = serializationMap.get("blockFace");
			if (blockFace instanceof String) {
				return new DirectionalBlockMaterialData(BlockFace.valueOf());
			}
		}
		return null;
	}

	public boolean addToBlock(Block block) {
		MaterialData data = block.getState().getData();
		if (data instanceof Directional) {
			((Directional)data).setFacingDirection(this.blockFace);
			return true;
		}
		return false;
	}

}
