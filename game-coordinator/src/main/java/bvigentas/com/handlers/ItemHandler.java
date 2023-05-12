package bvigentas.com.handlers;

import bvigentas.com.handlers.callbacks.ItemCallback;
import com.bvigentas.protobuf.csgo.Cstrike15Gcmessages.CMsgGCCStrike15_v2_Client2GCEconPreviewDataBlockRequest;
import com.bvigentas.protobuf.csgo.Cstrike15Gcmessages.CMsgGCCStrike15_v2_Client2GCEconPreviewDataBlockResponse;
import com.bvigentas.protobuf.csgo.Cstrike15Gcmessages.ECsgoGCMsg;
import in.dragonbra.javasteam.base.ClientGCMsgProtobuf;
import in.dragonbra.javasteam.base.IPacketGCMsg;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Getter
public class ItemHandler extends CustomHandler {

    private Map<Integer, Consumer<IPacketGCMsg>> dispatchMap;

    public ItemHandler() {
        dispatchMap = new HashMap<>();

        dispatchMap.put(ECsgoGCMsg.k_EMsgGCCStrike15_v2_Client2GCEconPreviewDataBlockResponse_VALUE, iPacketGCMsg -> handleItemInspect(iPacketGCMsg));
    }

    private void handleItemInspect(IPacketGCMsg message) {
        var protobuf = new ClientGCMsgProtobuf<CMsgGCCStrike15_v2_Client2GCEconPreviewDataBlockResponse.Builder>(CMsgGCCStrike15_v2_Client2GCEconPreviewDataBlockResponse.class, message);

        log.debug("[ITEM-HANDLER] handleItem: {}", protobuf.getBody());

        client.postCallback(new ItemCallback(protobuf.getBody()));
    }

    public ItemCallback requestItemInspect(String inspectUrl) {
        var protobuf = new ClientGCMsgProtobuf<CMsgGCCStrike15_v2_Client2GCEconPreviewDataBlockRequest.Builder>(CMsgGCCStrike15_v2_Client2GCEconPreviewDataBlockRequest.class, ECsgoGCMsg.k_EMsgGCCStrike15_v2_Client2GCEconPreviewDataBlockRequest_VALUE);

        var paramD = inspectUrl.substring(inspectUrl.lastIndexOf("D") + 1, inspectUrl.length());
        var paramA = inspectUrl.substring(inspectUrl.lastIndexOf("A") + 1, inspectUrl.indexOf("D"));
        var paramS = inspectUrl.substring(inspectUrl.lastIndexOf("S") + 1, inspectUrl.indexOf("A"));

        protobuf.getBody()
                .setParamA(Long.parseLong(paramA))
                .setParamD(Long.parseLong(paramD))
                .setParamM(0)
                .setParamS(Long.parseLong(paramS));

        log.debug("[ITEM-HANDLER] requestItemInspect: {}", protobuf.getBody());

        return sendJobAndAwait(protobuf);
    }

    @Override
    public void handleGCMsg(IPacketGCMsg packetGCMsg) {
        var dispatch = dispatchMap.get(packetGCMsg.getMsgType());

        if (dispatch != null) {
            log.debug("[ITEM-HANDLER] handleGCMsg: {}", packetGCMsg.getMsgType());
            dispatch.accept(packetGCMsg);
        }
    }
}
