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
import org.bukkit.material.Step;

import me.danieli1818.dr.region_saver_loader.utils.serialization.BlockMaterialData;

public class SignBlockMaterialData extends DirectionalBlockMaterialData implements BlockMaterialData {
	
	private String[] text;
	private boolean isWallSign;
	
	public SignBlockMaterialData(BlockFace blockFace, String[] text, boolean isWallSign) {
		super(blockFace);
		this.text = text;
		this.isWallSign = isWallSign;
	}
	
	public SignBlockMaterialData(Sign sign) {
		this(((org.bukkit.material.Sign)sign.getData()).getFacing(), sign.getLines()
				, ((org.bukkit.material.Sign)sign.getData()).isWallSign());
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("text", this.text);
		map.put("isWallSign", Boolean.toString(this.isWallSign));
		return map;
	}
	
	public static SignBlockMaterialData deserialize(Map<String, Object> serializationMap) {
		DirectionalBlockMaterialData md = DirectionalBlockMaterialData.deserialize(serializationMap);
		if (md == null) {
			System.out.println("md is null!");
			return null;
		}
		if (serializationMap.containsKey("text") && serializationMap.containsKey("isWallSign")) {
			Object text = serializationMap.get("text");
			Object isWallSign = serializationMap.get("isWallSign");
			System.out.println("text: " + text.toString() + ", " + text.getClass().toString());
			System.out.println("isWallSign: " + isWallSign.toString() + ", " + text.getClass().toString());
			if (text instanceof List<?> && isWallSign instanceof String) {
				List<String> textList = (List<String>)text;
				return new SignBlockMaterialData(md.getBlockFace(), Arrays.copyOf(textList.toArray(), textList.size(), String[].class)
						, Boolean.parseBoolean(isWallSign.toString()));
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
			sign.update();
			return true;
		}
		return false;
	}

}
