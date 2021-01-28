package me.danieli1818.dr.region_saver_loader.utils.serialization.block.serializables;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
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
	
	public static DirectionalBlockMaterialData deserialize(Map<String, Object> serializationMap) {
		if (serializationMap.containsKey("blockFace")) {
			System.out.println("yay block face 1");
			Object blockFace = serializationMap.get("blockFace");
			if (blockFace instanceof String) {
				System.out.println("yay block face 2");
				return new DirectionalBlockMaterialData(BlockFace.valueOf((String)blockFace));
			}
		}
		return null;
	}

	public boolean addToBlock(Block block) {
		BlockState state = block.getState();
		MaterialData data = state.getData();
		if (data instanceof Directional) {
			System.out.println(this.blockFace.toString());
			((Directional)data).setFacingDirection(this.blockFace);
			state.update();
			return true;
		}
		return false;
	}
	
	public BlockFace getBlockFace() {
		return this.blockFace;
	}
	
	public boolean setBlockFace(BlockFace face) {
		if (face == null) {
			return false;
		}
		this.blockFace = face;
		return true;
	}

}
