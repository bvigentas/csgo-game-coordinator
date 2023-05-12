package bvigentas.com.subscriptions.callback;

import bvigentas.com.client.CsgoSteamClient;
import in.dragonbra.javasteam.enums.EResult;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggedOnSubscriptionCallback {
    public LoggedOnSubscriptionCallback(CsgoSteamClient client, LoggedOnCallback callback) {
        if (callback.getResult() != EResult.OK) {
            if (callback.getResult() == EResult.AccountLogonDenied) {

                log.error("[LOGGED-ON-SUBS-CALLBACK] Unable to logon on Steam. This account is protected by SteamGuard.");

                client.setRunning(false);
                return;
            }

            log.error("[LOGGED-ON-SUBS-CALLBACK] Unable to logon on Steam. {}/{}", callback.getResult(), callback.getExtendedResult());

            client.setRunning(false);
            return;
        }

        log.info("[LOGGED-ON-SUBS-CALLBACK] Successfully logged on!");
    }
}
