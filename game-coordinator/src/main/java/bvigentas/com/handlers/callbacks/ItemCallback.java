package bvigentas.com.handlers.callbacks;

import com.bvigentas.protobuf.csgo.Cstrike15Gcmessages.CMsgGCCStrike15_v2_Client2GCEconPreviewDataBlockResponse.Builder;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackMsg;
import lombok.Getter;

@Getter
public class ItemCallback extends CallbackMsg {

    private final Builder body;

    public ItemCallback(Builder body) {
        this.body = body;
    }

}
