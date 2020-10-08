package com.dfsek.terra.config.genconfig;

import com.dfsek.terra.Debug;
import com.dfsek.terra.config.TerraConfig;
import com.dfsek.terra.config.base.ConfigPack;
import com.dfsek.terra.config.base.ConfigUtil;
import com.dfsek.terra.config.exception.ConfigException;
import com.dfsek.terra.config.exception.NotFoundException;
import com.dfsek.terra.structure.GaeaStructure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.polydev.gaea.math.ProbabilityCollection;
import org.polydev.gaea.tree.Tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class TreeConfig extends TerraConfig implements Tree {
    private final Set<Material> spawnable;
    private final String id;
    private final ProbabilityCollection<GaeaStructure> structure = new ProbabilityCollection<>();
    public TreeConfig(File file, ConfigPack config) throws IOException, InvalidConfigurationException {
        super(file, config);
        spawnable = ConfigUtil.toBlockData(getStringList("spawnable"), "spawnable", getID());
        if(!contains("id")) throw new ConfigException("No ID specified!", "null");
        id = getString("id");
        if(!contains("files")) throw new ConfigException("No files specified!", getID());
        try {
            for(Map.Entry<String, Object> e : Objects.requireNonNull(getConfigurationSection("files")).getValues(false).entrySet()) {
                try {
                    File structureFile = new File(config.getDataFolder() + File.separator + "trees" + File.separator + "data", e.getKey() + ".tstructure");
                    structure.add(GaeaStructure.load(structureFile), (Integer) e.getValue());
                } catch(FileNotFoundException ex) {
                    Debug.stack(ex);
                    throw new NotFoundException("Tree Structure File", e.getKey(), getID());
                } catch(ClassCastException ex) {
                    Debug.stack(ex);
                    throw new ConfigException("Unable to parse Tree configuration! Check YAML syntax.", getID());
                }
            }
        } catch(IOException | NullPointerException e) {
            if(ConfigUtil.debug) {
                e.printStackTrace();
            }
            throw new NotFoundException("Tree Structure", getString("file"), getID());
        }
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean plant(Location location, Random random, boolean b, JavaPlugin javaPlugin) {
        return false;
    }
}
