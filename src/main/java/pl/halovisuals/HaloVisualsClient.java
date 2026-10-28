package pl.halovisuals;

import net.fabricmc.api.ClientModInitializer;

public class HaloVisualsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModuleManager.load();
        Features.init();
    }
}
