package io.github.Tors_0.dotwarden;

import io.github.Tors_0.dotwarden.networking.DOTWNetworking;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class DOTWardenClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     *
     * @param mod the mod which is initialized
     */
    @Override
    public void onInitializeClient(ModContainer mod) {
        DOTWNetworking.init();
    }
}
