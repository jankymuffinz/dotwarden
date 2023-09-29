package raeeeee.dotwarden.networking;

import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import raeeeee.dotwarden.DOTWarden;
import raeeeee.dotwarden.extensions.PlayerExtensions;

public class DOTWNetworking {
    public static final Identifier POWERLEVEL_PACKET_ID = new Identifier(DOTWarden.ID,"sync_power");
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(POWERLEVEL_PACKET_ID, (client, handler, buf, responseSender) -> {
            int x = buf.readInt();
            client.execute(() -> {
                assert client.player != null;
                ((PlayerExtensions)client.player).dotwarden$setPowerLevel(x);
            });
        });
    }
}
