package mr.wruczek.supercensor3.utils.classes;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.IOUtils;
import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.StringUtils;

/**
 * Found somewhere without a license. Modified by Wruczek for SuperCensor.
 */
public class SCConfig extends YamlConfiguration {
    private static Plugin plugin = SCMain.getInstance();
    private String fileName;
    private File configFile;

    /**
     * Create a new Config.
     *
     * @param fileName The name of the file being created.
     */
    public SCConfig(String fileName) {
        this("configs/" + fileName, new File(plugin.getDataFolder(), fileName + (fileName.endsWith(".yml") ? "" : ".yml")));
    }

    public SCConfig(String folder, String fileName) {
        this(folder + (folder.endsWith("/") ? "" : "/") + fileName, new File(plugin.getDataFolder(), fileName + (fileName.endsWith(".yml") ? "" : ".yml")));
    }

    /**
     * Creates a new Config.
     *
     * @param fileName The name of the file being created.
     * @param file     The output file.
     */
    private SCConfig(String fileName, File file) {
        this.fileName = fileName + (fileName.endsWith(".yml") ? "" : ".yml");
        this.configFile = file;

        create();
    }

    /**
     * Saves the FileConfiguration to the file.
     */
    public void save() {
        try {
            save(configFile);
        } catch (IOException e) {
            SCLogger.logError("Error saving config file \"" + fileName + "\"!", LoggerUtils.LogType.PLUGIN);
            LoggerUtils.handleException(e);
        }
    }

    /**
     * Load the contents of the file to the FileConfiguration.
     */
    public void reload() {
        try {
            load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            SCLogger.logError("Error creating config file \"" + fileName + "\"!", LoggerUtils.LogType.PLUGIN);
            LoggerUtils.handleException(e);
        }
    }

    private void create() {
        try {
            if (!configFile.exists()) {
                if (plugin.getResource(fileName) != null) {
                    IOUtils.copyResource(fileName, configFile, true);
                    load(configFile);
                } else {
                    save(configFile);
                }
            } else {
                load(configFile);
                save(configFile);
            }
        } catch (Exception e) {
            SCLogger.logError("Error creating config file \"" + fileName + "\"!", LoggerUtils.LogType.PLUGIN);
            LoggerUtils.handleException(e);
        }
    }

    /**
     * Gets a string from the config and translates all of the colors.
     *
     * @param path The path to get the colored string from.
     * @return The string from the config at specified path, with replaced colors using SCUtils.color method.
     */
    public String getColored(String path) {
        return StringUtils.color(getString(path));
    }

    public void setLocation(String path, Location location) {
        ConfigurationSection section = getConfigurationSection(path);
        if (section == null) section = createSection(path);
        setLocation(section, location);
    }

    public void setLocation(ConfigurationSection section, Location location) {
        if (section == null) return;
        section.set(section + ".world", location.getWorld());
        section.set(section + ".x", location.getX());
        section.set(section + ".y", location.getY());
        section.set(section + ".z", location.getZ());
        section.set(section + ".yaw", location.getYaw());
        section.set(section + ".pitch", location.getPitch());
    }

    /**
     * Gets a Location from a specified ConfigurationSection.
     *
     * @param section The ConfigurationSection in which the Location is stored.
     * @return The Location stored in the specified ConfigurationSection.
     */
    public Location getLocation(ConfigurationSection section) {
        if (section == null) return null;
        return new Location(Bukkit.getWorld(section.getString("world")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                section.getInt("yaw"),
                section.getInt("pitch"));
    }

    /**
     * Gets a Location from a specified path.
     *
     * @param path The path in which the Location is stored.
     * @return The Location stored at specified path.
     */
    public Location getLocation(String path) {
        return getLocation(getConfigurationSection(path));
    }
}