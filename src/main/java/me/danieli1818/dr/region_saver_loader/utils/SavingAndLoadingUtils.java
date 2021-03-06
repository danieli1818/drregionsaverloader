package me.danieli1818.dr.region_saver_loader.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;


public class SavingAndLoadingUtils {

	public static void saveSerializable(ConfigurationSerializable confSerializable, FileConfiguration conf, File file, String path) throws IOException {
		conf.set(path, confSerializable);
		if (file != null) {
			conf.save(file);
		}
	}
	
	public static void registerConfigurationSerializables() {
		ConfigurationSerialization.registerClass(RegionSerializable.class);
		ConfigurationSerialization.registerClass(Coordinates.class);
		
	}
	
}
