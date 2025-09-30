package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import tsetspawn.TSetSpawn;

import java.util.HashMap;
import java.util.Map;

public class SpawnDataService {

    private final TSetSpawn plugin;
    private final Gson gson;

    public SpawnDataService(TSetSpawn plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void addSpawnFromJson(String json) throws Exception {
        DebugLogger.log("Received JSON for SpawnDataService.addSpawnFromJson: " + json);
        Map data = gson.fromJson(json, Map.class);
        if (data == null) throw new Exception("Invalid JSON.");

        String worldName = (String) data.get("world");
        Double x = getAsDouble(data.get("x"));
        Double y = getAsDouble(data.get("y"));
        Double z = getAsDouble(data.get("z"));
        Double yawD = getAsDouble(data.get("yaw"));
        Double pitchD = getAsDouble(data.get("pitch"));

        if (worldName == null || x == null || y == null || z == null || yawD == null || pitchD == null) {
            throw new Exception("Mandatory fields are missing in the JSON.");
        }

        if (plugin.getServer().getWorld(worldName) == null) {
            throw new Exception("The world '" + worldName + "' does not exist.");
        }

        int id;
        if (data.get("id") instanceof Number) {
            id = ((Number) data.get("id")).intValue();
            if (plugin.getSpawnsManager().getSpawn(id) != null) {
                throw new Exception("A spawn with ID " + id + " already exists.");
            }
        } else {
            id = plugin.getSpawnsManager().getAllSpawnIds().stream()
                    .max(Integer::compareTo).orElse(0) + 1;
        }

        Location loc = new Location(
                plugin.getServer().getWorld(worldName),
                x, y, z,
                yawD.floatValue(), pitchD.floatValue()
        );

        plugin.getSpawnsManager().setSpawn(id, loc, true);
        DebugLogger.log("Spawn added: ID=" + id + " World=" + worldName + " X=" + x + " Y=" + y + " Z=" + z);
    }

    private Double getAsDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        if (obj instanceof String) {
            try {
                return Double.parseDouble((String) obj);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    public String toJsonAllSpawns() {
        Map<Integer, Map<String, Object>> simpleMap = new HashMap<>();
        for (int id : plugin.getSpawnsManager().getAllSpawnIds()) {
            Location loc = plugin.getSpawnsManager().getSpawn(id);
            if (loc == null) continue;

            Map<String, Object> locMap = new HashMap<>();
            locMap.put("world", loc.getWorld().getName());
            locMap.put("x", loc.getX());
            locMap.put("y", loc.getY());
            locMap.put("z", loc.getZ());
            locMap.put("yaw", loc.getYaw());
            locMap.put("pitch", loc.getPitch());
            simpleMap.put(id, locMap);
        }
        return gson.toJson(simpleMap);
    }
}
