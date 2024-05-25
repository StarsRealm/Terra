package com.dfsek.terra.bukkit.nms;

import org.bukkit.Bukkit;

import com.dfsek.terra.bukkit.PlatformImpl;


public class NMSInitializer implements Initializer {
    @Override
    public void initialize(PlatformImpl platform) {
        AwfulBukkitHacks.registerBiomes(platform.getRawConfigRegistry());
        Bukkit.getPluginManager().registerEvents(new NMSInjectListener(), platform.getPlugin());
    }
}
