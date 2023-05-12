package com.bvigentas.steam;

import com.bvigentas.steam.clients.CsgoSteamClient;
import in.dragonbra.javasteam.steam.handlers.steamunifiedmessages.SteamUnifiedMessages;

public class UnifiedMessagesService {

    private String user;

    private String password;

    public void run() {
        CsgoSteamClient client = new CsgoSteamClient();

        SteamUnifiedMessages steamUnifiedMessages = client.getHandler(SteamUnifiedMessages.class);

        client.connect();

        while (client.isRunning()) {
            client.getManager().runWaitCallbacks(1000L);
        }

        CInventory
    }

}
