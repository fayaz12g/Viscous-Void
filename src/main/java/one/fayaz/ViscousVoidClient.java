package one.fayaz;

import net.fabricmc.api.ClientModInitializer;

public class ViscousVoidClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[Viscous Void] Client initialized!");
        // Put any client-side setup here (e.g. rendering, particles, keybinds)
    }
}
