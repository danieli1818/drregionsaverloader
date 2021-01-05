package me.danieli1818.dr.region_saver_loader.utils.serialization;

import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface BlockMaterial extends ConfigurationSerializable {

	boolean addToBlock(Block block);
	
}
