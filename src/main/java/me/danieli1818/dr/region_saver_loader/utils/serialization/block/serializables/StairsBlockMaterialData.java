package me.danieli1818.dr.region_saver_loader.utils.serialization.block.serializables;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;

import me.danieli1818.dr.region_saver_loader.utils.serialization.BlockMaterialData;

public class StairsBlockMaterialData extends DirectionalBlockMaterialData implements BlockMaterialData {
	
	private boolean isTop;

	public StairsBlockMaterialData(BlockFace blockFace, boolean isTop) {
		super(blockFace);
		this.isTop = isTop;
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("isTop", this.isTop);
		return map;
	}
	
	public static StairsBlockMaterialData deserialize(Map<String, Object> serializationMap) {
		DirectionalBlockMaterialData md = DirectionalBlockMaterialData.deserialize(serializationMap);
		if (md == null) {
			System.out.println("md is null!");
			return null;
		}
		if (serializationMap.containsKey("isTop")) {
			Object isTop = serializationMap.get("isTop");
			if (isTop instanceof Boolean) {
				return new StairsBlockMaterialData(md.getBlockFace(), (Boolean)isTop);
			}
		}
		return null;
	}

	public boolean addToBlock(Block block) {
		super.addToBlock(block);
		BlockState state = block.getState();
		MaterialData md = state.getData();
		if (md instanceof Stairs) {
			((Stairs)md).setInverted(this.isTop);
			state.update();
			return true;
		}
		return false;
	}

}
