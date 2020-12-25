package me.danieli1818.dr.region_saver_loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import me.danieli1818.dr.region_saver_loader.commands.RegionSaverLoaderCommands;
import me.danieli1818.dr.region_saver_loader.utils.SavingAndLoadingUtils;

public class RegionSaverLoader extends JavaPlugin {
	
	private File configFile;
	private FileConfiguration config;
	
	private File versionConversionConfigFile;
	private FileConfiguration versionConversionConfig;
	
	@Override
	public void onEnable() {
		
		if (getWorldEditPlugin() == null) {
			System.out.println("WorldEdit Plugin Is Missing!");
			return;
		}
		
		SavingAndLoadingUtils.registerConfigurationSerializables();
		
		createConfig();
		
		getCommand("drregion").setExecutor(new RegionSaverLoaderCommands());
		
		System.out.println("Plugin has been successfully loaded!!!!");
		
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		System.out.println("Plugin has been successfully disabled.");
		super.onDisable();
	}
	
	private WorldEditPlugin getWorldEditPlugin() {
		return (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
	}
	
	private void createConfig() {
		
		AbstractMap.Entry<File, FileConfiguration> versionConversionConfigs = createConfigurationFile("versionConversionConfig.yml");
		this.versionConversionConfigFile = versionConversionConfigs.getKey();
		this.versionConversionConfig = versionConversionConfigs.getValue();
		AbstractMap.Entry<File, FileConfiguration> configs = createConfigurationFile("config.yml");
		this.configFile = configs.getKey();
		this.config = configs.getValue();
		
	}
	
	private AbstractMap.Entry<File, FileConfiguration> createConfigurationFile(String name) {
		File configFile = new File(getDataFolder(), name);
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			saveResource(name, false);
		}
		
		FileConfiguration config = new YamlConfiguration();
		try {
			config.load(configFile);
			return new AbstractMap.SimpleEntry(configFile, config);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public File getConfigFile() {
		this.configFile = new File(getDataFolder(), "config.yml");
		return this.configFile;
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	public File getVersionConversionConfigFile() {
		this.versionConversionConfigFile = new File(getDataFolder(), "versionConversionConfig.yml");
		return this.versionConversionConfigFile;
	}
	
	public FileConfiguration getVersionConversionConfig() {
		return this.versionConversionConfig;
	}
	
	public void reloadConfigFiles() throws FileNotFoundException, IOException, InvalidConfigurationException {
		this.configFile = getConfigFile();
		this.config = new YamlConfiguration();
		this.config.load(this.configFile);
		this.versionConversionConfigFile = getVersionConversionConfigFile();
		this.versionConversionConfig = new YamlConfiguration();
		this.versionConversionConfig.load(this.versionConversionConfigFile);
	}
	
}
