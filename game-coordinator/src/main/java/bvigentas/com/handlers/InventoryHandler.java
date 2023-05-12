package bvigentas.com.handlers;

import bvigentas.com.configuration.PropertyProvider;
import bvigentas.com.enums.CustomResponseJobs;
import bvigentas.com.handlers.callbacks.InventoryCallback;
import bvigentas.com.services.InventoryService;
import bvigentas.com.services.impl.InventoryServiceImpl;
import com.bvigentas.protobuf.steam.SteammessagesInventorySteamclient;
import in.dragonbra.javasteam.base.ClientGCMsgProtobuf;
import in.dragonbra.javasteam.base.IPacketGCMsg;
import in.dragonbra.javasteam.base.PacketClientMsgProtobuf;
import in.dragonbra.javasteam.enums.EMsg;
import in.dragonbra.javasteam.steam.handlers.steamunifiedmessages.SteamUnifiedMessages;
import in.dragonbra.javasteam.steam.handlers.steamunifiedmessages.callback.ServiceMethodResponse;
import in.dragonbra.javasteam.types.JobID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.bvigentas.protobuf.steam.SteammessagesInventorySteamclient.CInventory_GetInventory_Request;

@Slf4j
@Getter
public class InventoryHandler extends CustomHandler {

    private InventoryService inventoryService;

    private Map<Integer, Consumer<IPacketGCMsg>> dispatchMap;

    public InventoryHandler() {
//        dispatchMap = new HashMap<>();
//
//        //What is the type of inventory request???????????????????????????
//        dispatchMap.put(, packetMsg -> handleInventory(packetMsg));

        inventoryService = new InventoryServiceImpl( client.getHandler(SteamUnifiedMessages.class));
    }

    private void handleInventory(IPacketGCMsg packetMsg) {
        // in order to get at the message contents, we need to wrap the packet message
        // in an object that gives us access to the message body

        ServiceMethodResponse serviceMethodResponse = new ServiceMethodResponse((PacketClientMsgProtobuf) packetMsg);

        // post the callback to be consumed by user code
        client.postCallback(new InventoryCallback(serviceMethodResponse.getResult()));
    }

    private InventoryCallback requestInventoryCallback(long steamId) {
//        var protobuf = new ClientGCMsgProtobuf<CInventory_GetInventory_Request.Builder>(CInventory_GetInventory_Request.class, EMsg.Inventory);
//        protobuf.getBody()
//                .setAppid(PropertyProvider.getInstance().getCsgoAppId())
//                .setSteamid(steamId);
//        log.debug("[INVENTORY-HANDLER] requestInventoryCallback: {}", protobuf.getBody());
//        return sendJobAndAwait(protobuf);

        var inventoryRequest = CInventory_GetInventory_Request.newBuilder()
                .setAppid(730)
                .setSteamid(client.getSteamID().convertToUInt64()).build();

        var inventoryJobId = inventoryService.getInventory(inventoryRequest);
        client.getCustomResposeJobIds().put(CustomResponseJobs.INVENTARY, inventoryJobId);
    }

    @Override
    public void handleGCMsg(IPacketGCMsg packetGCMsg) {
        var dispatch = dispatchMap.get(packetGCMsg.getMsgType());

        if (dispatch != null) {
            log.debug("[INVENTORY-HANDLER] handleGCMsg: {}", packetGCMsg.getMsgType());
            dispatch.accept(packetGCMsg);
        }
    }
}
