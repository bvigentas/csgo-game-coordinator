package bvigentas.com.subscriptions.callback;

import bvigentas.com.client.CsgoSteamClient;
import bvigentas.com.configuration.PropertyProvider;
import in.dragonbra.javasteam.steam.handlers.steamuser.LogOnDetails;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectedSubscriptionCallback {

    public ConnectedSubscriptionCallback(CsgoSteamClient client, ConnectedCallback callback) {

        log.info("[CONNECTED-SUBS-CALLBACK] Connected to Steam with USER {}", PropertyProvider.getInstance().getUser());

        LogOnDetails details = new LogOnDetails();
        details.setUsername(PropertyProvider.getInstance().getUser());
        details.setPassword(PropertyProvider.getInstance().getPassword());
        details.setLoginID(149);

        client.getSteamUser().logOn(details);

    }
}
