package fr.ward.weconomy.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class GeneratedYML {
    private final String path;
    private final File ymlfile;
    private FileConfiguration fc;

    public GeneratedYML(String name, JavaPlugin jp) {
        this.path = "plugins/" + jp.getName();
        this.ymlfile = new File(path + File.separatorChar + name + ".yml");
        this.fc = YamlConfiguration.loadConfiguration(this.ymlfile);
    }

    public GeneratedYML(String name, JavaPlugin jp, String customPath) {
        this.path = "plugins/" + jp.getName() + customPath;
        this.ymlfile = new File(path + File.separatorChar + name + ".yml");
        this.fc = YamlConfiguration.loadConfiguration(this.ymlfile);
    }

    @Nullable
    public <T> T getConfigField(String path) {
        FileConfiguration cfg = this.getConfig();
        Object o = cfg.get(path);
        if (o != null) {
            return (T) o;
        } else {
            cfg.set(path, "undefined");
            save();
        }
        return null;
    }

    public boolean setField(String path, Object o) {
        getConfig().set(path, o);
        getConfig().contains("");
        return save();
    }

    public boolean contains(String path) {
        return getConfig().contains(path);
    }

    @Nullable
    public <T> T getConfigField(String path, T whenNotFound) {
        FileConfiguration cfg = this.getConfig();
        Object o = cfg.get(path);
        if (o != null) {
            try {
                return (T) o;
            } catch (Exception e) {
                return null;
            }
        } else {
            cfg.set(path, whenNotFound);
            save();
        }
        return whenNotFound;
    }

    public FileConfiguration getConfig() {
        return this.fc;
    }

    public void loadConfiguration() {
        try {
            getConfig().load(this.ymlfile);
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + this.ymlfile, e);
        }
    }

    public File getFile() {
        return this.ymlfile;
    }

    public boolean exists(){
        return this.ymlfile.exists();
    }

    public void delete(){
        this.ymlfile.deleteOnExit();
    }

    public boolean save() {
        File folder = new File(path);
        if (!folder.exists())
            folder.mkdirs();
        try {
            this.fc.save(this.ymlfile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}