package minealex.tsetspawn.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParticleUtils {
    public static void spawnParticles(Player player, String particleType, Location location, int count) {
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        
        try {
            if (serverVersion.startsWith("v1_8")) {
                spawnParticles18(player, particleType, location, count);
            } else if (serverVersion.startsWith("v1_9") || serverVersion.startsWith("v1_10")) {
                spawnParticles19(player, particleType, location, count);
            } else {
                spawnParticlesNew(player, particleType, location, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void spawnParticles18(Player player, String particleType, Location location, int count) throws Exception {
        // Use reflection to access the particle-related classes and methods for Spigot 1.8.x
    }

    private static void spawnParticles19(Player player, String particleType, Location location, int count) throws Exception {
        // Use reflection to access the particle-related classes and methods for Spigot 1.9.x and 1.10.x
    }

    private static void spawnParticlesNew(Player player, String particleType, Location location, int count) throws Exception {
        // Use reflection to access the particle-related classes and methods for newer versions (1.11 and above)
    }

	public static ParticleUtils valueOf(String upperCase) {
		// TODO Auto-generated method stub
		return null;
	}
}
