package raeeeee.dotwarden;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import raeeeee.dotwarden.networking.DOTWNetworking;

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
