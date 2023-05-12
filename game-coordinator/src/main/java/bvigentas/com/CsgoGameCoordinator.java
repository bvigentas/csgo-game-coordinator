package bvigentas.com;

import bvigentas.com.client.CsgoSteamClient;
import bvigentas.com.configuration.PropertyProvider;
import bvigentas.com.handlers.ClientGCMsgHandler;
import bvigentas.com.handlers.CustomHandler;
import in.dragonbra.javasteam.base.IClientGCMsg;
import in.dragonbra.javasteam.steam.handlers.steamgamecoordinator.SteamGameCoordinator;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CsgoGameCoordinator extends SteamGameCoordinator {

    private Map<Class<? extends ClientGCMsgHandler>, ClientGCMsgHandler> handlers = new HashMap<>();

    public void addCustomHandler(CustomHandler handler) {
        handler.setup(this);
        addHandler(handler);
    }

    public void addHandler(ClientGCMsgHandler handler) {
        if (handlers.containsKey(handler.getClass())) {
            throw new IllegalArgumentException("A handler of type " + handler.getClass() + " is already registered.");
        }

        handlers.put(handler.getClass(), handler);
    }

    public void send(IClientGCMsg message) {
        super.send(message, PropertyProvider.getInstance().getCsgoAppId());
    }

    @Override
    public CsgoSteamClient getClient() {
        return (CsgoSteamClient) super.getClient();
    }




}
