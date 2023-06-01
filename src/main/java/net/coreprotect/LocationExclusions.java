package net.coreprotect;

import org.bukkit.Location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationExclusions {
    private final List<ExclusionZone> exclusionZones = new ArrayList<>();

    public LocationExclusions(CoreProtect coreProtect) {
        File file = new File(coreProtect.getDataFolder(), "exclusion-zones.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                while (line != null) {
                    if (!line.isEmpty()) {
                        // Format: world;x1,z1;z2,z2
                        String[] split = line.split(";");
                        String world = split[0];
                        int x1 = Integer.parseInt(split[1].split(",")[0]);
                        int z1 = Integer.parseInt(split[1].split(",")[1]);
                        int x2 = Integer.parseInt(split[2].split(",")[0]);
                        int z2 = Integer.parseInt(split[2].split(",")[1]);
                        boolean hard = split.length > 3 && split[3].equalsIgnoreCase("hard");

                        exclusionZones.add(new ExclusionZone(world, x1, x2, z1, z2, hard));
                    }
                    line = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isExcluded(Location location) {
        for (ExclusionZone exclusionZone : exclusionZones) {
            if (exclusionZone.contains(location)) {
                return true;
            }
        }
        return false;
    }

    public boolean isHardExcluded(Location location) {
        for (ExclusionZone exclusionZone : exclusionZones) {
            if (exclusionZone.hard && exclusionZone.contains(location)) {
                return true;
            }
        }
        return false;
    }

    private static class ExclusionZone {
        private final String world;
        private final int x1,x2,z1,z2;
        private final boolean hard;

        private ExclusionZone(String world, int x1, int x2, int z1, int z2, boolean hard) {
            this.world = world;
            this.x1 = x1;
            this.x2 = x2;
            this.z1 = z1;
            this.z2 = z2;
            this.hard = hard;
        }

        public boolean contains(Location location) {
            if (location.getWorld().getName().equalsIgnoreCase(world)) {
                return location.getBlockX() <= Math.max(x1, x2) &&
                        location.getBlockX() >= Math.min(x1, x2) &&
                        location.getBlockZ() <= Math.max(z1, z2) &&
                        location.getBlockZ() >= Math.min(z1, z2);
            }
            return false;
        }

    }

}
