package me.danieli1818.dr.region_saver_loader.utils.serialization;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Coordinates implements ConfigurationSerializable {
	
	private double x;
	private double y;
	private double z;
	
	public Coordinates(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Coordinates fromLocation(Location location) {
		if (location == null) {
			throw new RuntimeException("Error! Location is null!");
		}
		return new Coordinates(location.getX(), location.getY(), location.getZ());
	}

	public Map<String, Object> serialize() {
		Map<String, Object> serialization = new HashMap<String, Object>();
		serialization.put("x", (Double)this.x);
		serialization.put("y", (Double)this.y);
		serialization.put("z", (Double)this.z);
		return serialization;
	}
	
	public static Coordinates deserialize(Map<String, Object> serialization) {
		if (serialization.get("x") == null || serialization.get("y") == null || serialization.get("z") == null) {
			throw new RuntimeException("Error! Invalid coordinates!");
		}
		double x = (Double)serialization.get("x");
		double y = (Double)serialization.get("y");
		double z = (Double)serialization.get("z");
		return new Coordinates(x, y, z);
	}
	
	public Location getLocation(World world) {
		return new Location(world, this.x, this.y, this.z);
	}
	
}
