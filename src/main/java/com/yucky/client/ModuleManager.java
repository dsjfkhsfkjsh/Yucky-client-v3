package com.yucky.client.module;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    private FreecamModule freecam;
    private ItemViewModule itemView;

    public void registerDefaults() {
        modules.clear();
        modules.add(new StorageFinderModule());
        modules.add(new SpawnerFinderModule());
        freecam = new FreecamModule();
        modules.add(freecam);
        itemView = new ItemViewModule();
        modules.add(itemView);
    }

    public List<Module> all() { return modules; }
    public StorageFinderModule storage() { return (StorageFinderModule) modules.get(0); }
    public SpawnerFinderModule spawner() { return (SpawnerFinderModule) modules.get(1); }
    public FreecamModule freecam() { return freecam; }
    public ItemViewModule itemView() { return itemView; }

    public void tick() {
        for (Module module : modules) {
            if (module.enabled()) module.onClientTick();
        }
    }
}
