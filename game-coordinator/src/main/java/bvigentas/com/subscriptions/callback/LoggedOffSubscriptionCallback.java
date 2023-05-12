package bvigentas.com.subscriptions.callback;

import bvigentas.com.client.CsgoSteamClient;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOffCallback;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggedOffSubscriptionCallback {
    public LoggedOffSubscriptionCallback(CsgoSteamClient client, LoggedOffCallback callback) {
        log.info("[LOGGED-OFF-SUBS-CALLBACK] Logged off of Steam. {}", callback.getResult());
        client.setRunning(false);
    }
}
