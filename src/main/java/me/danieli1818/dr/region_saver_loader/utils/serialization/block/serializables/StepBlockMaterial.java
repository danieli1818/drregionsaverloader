package me.danieli1818.dr.region_saver_loader.utils.serialization.block.serializables;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;

import me.danieli1818.dr.region_saver_loader.utils.serialization.BlockMaterial;

public class StepBlockMaterial implements BlockMaterial {
	
	private boolean isTop;
	
	public StepBlockMaterial(boolean isTop) {
		this.isTop = isTop;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isTop", this.isTop);
		return map;
	}
	
	public static StepBlockMaterial deserialize(Map<String, Object> serializationMap) {
		if (serializationMap.containsKey("isTop")) {
			Object isTop = serializationMap.get("isTop");
			if (isTop instanceof Boolean) {
				return new StepBlockMaterial((Boolean)isTop);
			}
		}
		return null;
	}

	public boolean addToBlock(Block block) {
		MaterialData md = block.getState().getData();
		if (md instanceof Step) {
			((Step)md).setInverted(this.isTop);
			return true;
		}
		return false;
	}

}
