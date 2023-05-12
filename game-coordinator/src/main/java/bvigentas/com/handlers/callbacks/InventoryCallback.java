package bvigentas.com.handlers.callbacks;

import in.dragonbra.javasteam.enums.EResult;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackMsg;
import lombok.Getter;

@Getter
public class InventoryCallback extends CallbackMsg {

    private final EResult result;

    public InventoryCallback(EResult result) {
        this.result = result;
    }

}