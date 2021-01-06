package me.danieli1818.dr.region_saver_loader.utils.serialization;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class BlockSerializable implements ConfigurationSerializable {

	private Coordinates coordinates;
	private BlockMaterialData data;
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("coordinates", this.coordinates);
		map.put("data", this.data);
		return map;
	}

}
