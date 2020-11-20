package me.danieli1818.dr.region_saver_loader.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.YAMLConfiguration;

import me.danieli1818.dr.region_saver_loader.RegionSaverLoader;
import me.danieli1818.dr.region_saver_loader.utils.RegionSerializable;
import me.danieli1818.dr.region_saver_loader.utils.SavingAndLoadingUtils;

public class RegionSaverLoaderCommands implements CommandExecutor {

	private static WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

	private static RegionSaverLoader plugin = RegionSaverLoader.getPlugin(RegionSaverLoader.class);

	private static FileConfiguration stationsConfig = plugin.getConfig();

	private static File stationsConfigFile = plugin.getConfigFile();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("drregionsaverloader")) {
			sender.sendMessage("You don't have permission to run this command!");
			return false;
		}
		if (args.length <= 0) {
			sender.sendMessage("Invalid command! Use /help for help!");
			return false;
		}
		String subcommand = args[0].toLowerCase();
		if (subcommand.equals("reload")) { 
			if (!reloadConfig()) {
				sender.sendMessage("The has been an error reloading the config file!");
				return false;
			} else {
				sender.sendMessage("Successfully reloaded the config file!");
			}
		}
		if (args.length != 2) {
			sender.sendMessage("Invalid command! Use /help for help!");
			return false;
		}
		String name = args[1].toLowerCase();
		if (subcommand.equals("save")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You have to be a player to run this command!");
				return false;
			}
			Player player = (Player) sender;
			if (!saveRegion(name, player)) {
				sender.sendMessage("Error saving region \"" + name + "\"!");
				return false;
			} else {
				sender.sendMessage("Successfully saved region \"" + name + "\"!");
			}
		} else if (subcommand.equals("load")) {
			if (!loadRegion(name)) {
				sender.sendMessage("Error! No region named \"" + name + "\" exists!");
				return false;
			} else {
				sender.sendMessage("Successfully loaded region \"" + name + "\"!");
			}
		} else {
			sender.sendMessage("Invalid command! Use /help for help!");
			return false;
		}
		return true;
	}

	public boolean saveRegion(String name, Player player) {
		LocalSession session = wep.getSession(player);
		Region region = null;
		try {
			region = session.getSelection(session.getSelectionWorld());
		} catch (IncompleteRegionException e) {

		}
		if (region == null) {
			player.sendMessage("Invalid region selected!");
			return false;
		}
		try {
			SavingAndLoadingUtils.saveSerializable(new RegionSerializable(region), this.stationsConfig, this.stationsConfigFile, name);
		} catch (IOException e) {
			e.printStackTrace();
			player.sendMessage("There has been an error saving the region!");
		}
		return true;
	}

	public boolean loadRegion(String name) {
		this.stationsConfig.getSerializable(name, RegionSerializable.class);
		return true;
	}

	private boolean reloadConfig() {
		try {
			this.stationsConfigFile = plugin.getConfigFile();
			this.stationsConfig = new YamlConfiguration();
			this.stationsConfig.load(this.stationsConfigFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
