package me.danieli1818.dr.region_saver_loader.utils.serialization.block.serializables;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;

import me.danieli1818.dr.region_saver_loader.utils.serialization.BlockMaterialData;

public class SignBlockMaterialData extends DirectionalBlockMaterialData implements BlockMaterialData {
	
	private String[] text;
	
	public SignBlockMaterialData(BlockFace blockFace, String[] text) {
		super(blockFace);
		this.text = text;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("text", this.text);
		return map;
	}
	
	public static SignBlockMaterialData deserialize(Map<String, Object> serializationMap) {
		DirectionalBlockMaterialData md = DirectionalBlockMaterialData.deserialize(serializationMap);
		if (md == null) {
			return null;
		}
		if (serializationMap.containsKey("text")) {
			Object text = serializationMap.get("text");
			if (text instanceof String[]) {
				return new SignBlockMaterialData(md.getBlockFace(), (String[])text);
			}
		}
		return null;
	}

	public boolean addToBlock(Block block) {
		super.addToBlock(block);
		BlockState state = block.getState();
		if (state instanceof Sign) {
			Sign sign = ((Sign)state);
			for (int i = 0; i < this.text.length; i++) {
				sign.setLine(i, this.text[i]);
			}
			return true;
		}
		return false;
	}

}
