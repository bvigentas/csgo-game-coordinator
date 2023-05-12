package bvigentas.com.client;

import bvigentas.com.CsgoGameCoordinator;
import bvigentas.com.configuration.PropertyProvider;
import bvigentas.com.enums.CustomResponseJobs;
import bvigentas.com.handlers.InventoryHandler;
import bvigentas.com.handlers.ItemHandler;
import bvigentas.com.subscriptions.callback.*;
import in.dragonbra.javasteam.steam.handlers.steamfriends.SteamFriends;
import in.dragonbra.javasteam.steam.handlers.steamfriends.callback.FriendsListCallback;
import in.dragonbra.javasteam.steam.handlers.steamunifiedmessages.callback.ServiceMethodResponse;
import in.dragonbra.javasteam.steam.handlers.steamuser.SteamUser;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOffCallback;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback;
import in.dragonbra.javasteam.steam.steamclient.SteamClient;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackManager;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.callbacks.DisconnectedCallback;
import in.dragonbra.javasteam.types.JobID;
import in.dragonbra.javasteam.types.SteamID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Setter
public class CsgoSteamClient extends SteamClient {

    private Map<Object, CompletableFuture<Object>> subscribers = new HashMap<>();

    private boolean ready = false;

    private CompletableFuture<Void> managerLoop;

    private boolean running = false;

    protected CallbackManager manager;
    //---
    private SteamUser steamUser;

    protected SteamFriends steamFriends;

    private List<SteamID> friendsSteamId;

    //Csgo
    public CsgoGameCoordinator gameCoordinator;

    private ItemHandler itemHandler;

    private Map<CustomResponseJobs, JobID> customResposeJobIds;

    public CsgoSteamClient() {
        steamUser =getHandler(SteamUser.class);
        steamFriends = getHandler(SteamFriends.class);

        manager = new CallbackManager(this);

        //Default Callbacks
        manager.subscribe(ConnectedCallback.class, callback -> new ConnectedSubscriptionCallback(this, callback));
        manager.subscribe(DisconnectedCallback.class, callback -> new DisconnectedSubscriptionCallback(this, callback));
        manager.subscribe(LoggedOnCallback.class, callback -> new LoggedOnSubscriptionCallback(this, callback));
        manager.subscribe(LoggedOffCallback.class, callback -> new LoggedOffSubscriptionCallback(this, callback));
        manager.subscribe(FriendsListCallback.class, callback -> new FriendsListSubscriptionCallback(this, callback));

        manager.subscribe(ServiceMethodResponse.class, callback -> new MethodResponseSubscriptionCallback(this, callback));

        customResposeJobIds = new HashMap<>();
        customResposeJobIds.put(CustomResponseJobs.INVENTARY, JobID.INVALID);

        //Custom Callbacks
        gameCoordinator.addCustomHandler(itemHandler = new ItemHandler());
        gameCoordinator.addCustomHandler(new InventoryHandler());
    }

    public <T> T registerAndWait(Object key) {
        return registerAndWait(key, PropertyProvider.getInstance().getClientTimeout());
    }

    public <T> T registerAndWait(Object key, Long timeout) {
        log.debug("[CSGO-STEAM-CLIENT] registerAndWait key: {}", key);

        T value = null;

        try {
            if (subscribers.get(key) != null) {
                return (T) subscribers.get(key).get(timeout, TimeUnit.SECONDS);
            }

            var completableFuture = new CompletableFuture<>();
            subscribers.put(key, completableFuture);

            value = (T)completableFuture.get(timeout, TimeUnit.SECONDS);
            return value;
        } catch (Exception e) {
            log.error("[CSGO-STEAM-CLIENT] registerAndWait: {}", e);
            return null;
        } finally {
            subscribers.remove(key);
            log.debug("[CSGO-STEAM-CLIENT] registerAndWait value: {}", value);
        }
    }

    @Override
    public void connect() {
        log.debug("[CSGO-STEAM-CLIENT] Connecting...");

        super.connect();

        running = true;

        if (managerLoop == null) {
            log.debug("[CSGO-STEAM-CLIENT] managerLoop");
            managerLoop = CompletableFuture.runAsync(() -> {
                while (true) {
                    try {
                        manager.runWaitCallbacks(1000L);
                    } catch (Exception e) {
                        log.error("[CSGO-STEAM-CLIENT]: {}", e);
                    }
                }
            });
        }
        log.debug("[CSGO-STEAM-CLIENT] Connected");
    }

}
