package bvigentas.com.subscriptions.callback;

import bvigentas.com.client.CsgoSteamClient;
import in.dragonbra.javasteam.steam.steamclient.callbacks.DisconnectedCallback;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DisconnectedSubscriptionCallback {

    public DisconnectedSubscriptionCallback(CsgoSteamClient client, DisconnectedCallback callback) {
        log.info("[DISCONNECTED-SUBS-CALLBACK] Disconnected from Steam");

        client.setRunning(false);
    }
}
