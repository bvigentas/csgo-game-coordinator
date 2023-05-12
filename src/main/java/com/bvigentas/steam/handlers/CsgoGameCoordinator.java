package com.bvigentas.steam.handlers;

import in.dragonbra.javasteam.base.ClientMsgProtobuf;
import in.dragonbra.javasteam.base.IPacketGCMsg;
import in.dragonbra.javasteam.base.IPacketMsg;
import in.dragonbra.javasteam.enums.EMsg;
import in.dragonbra.javasteam.protobufs.steamclient.SteammessagesClientserver2.CMsgGCClient;
import in.dragonbra.javasteam.protobufs.steamclient.SteammessagesClientserver.CMsgClientP2PConnectionInfo;
import in.dragonbra.javasteam.protobufs.steamclient.SteammessagesClientserver2.CMsgClientRichPresenceInfo;
import in.dragonbra.javasteam.steam.handlers.steamgamecoordinator.SteamGameCoordinator;
import in.dragonbra.javasteam.steam.handlers.steamgamecoordinator.callback.MessageCallback;
import in.dragonbra.javasteam.steam.steamclient.SteamClient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CsgoGameCoordinator extends SteamGameCoordinator {

    private final Integer appId = 730;
    private Map<EMsg, Consumer<IPacketMsg>> dispatchMap;
    private Map<Class<? extends ClientGCMsgHandler>, ClientGCMsgHandler> handlers = new HashMap<>();

    public CsgoGameCoordinator() {
        this.dispatchMap = new HashMap<>();
        this.dispatchMap.put(EMsg.ClientFromGC, packetMsg -> handleFromGC(packetMsg));
        this.dispatchMap.put(EMsg.ClientRichPresenceInfo, packetMsg -> handleRichPresenceInfo(packetMsg));
        this.dispatchMap.put(EMsg.ClientP2PConnectionInfo, packetMsg -> handleConnectionInfo(packetMsg));
    }

    public void handleFromGC(IPacketMsg packetMsg) {
        var msg = new ClientMsgProtobuf<CMsgGCClient.Builder>(CMsgGCClient.class, packetMsg);
        var callback = new MessageCallback(msg.getTargetJobID(), msg.getBody());

        if (callback.getAppID() != this.appId) {
            return;
        }

        handleFromGC(callback.getMessage());
    }

    private void handleFromGC(IPacketGCMsg packetMsg) {
        for (var  entry : handlers.entrySet()) {
            try {
                entry.getValue().handleGCMsg(packetMsg);
            } catch (Exception e) {
                //Throw exception
            }
        }
    }

    private void handleRichPresenceInfo(IPacketMsg packetMsg) {
        var protobuf = new ClientMsgProtobuf<CMsgClientRichPresenceInfo.Builder>(CMsgClientRichPresenceInfo.class, packetMsg);
        var callback = new ClientRichPresenceInfoCallback(protobuf.getTargetJobID(), protobuf.getBody());

        client.postCallback(callback);
    }

    private void handleConnectionInfo(IPacketMsg packetMsg) {
        new ClientMsgProtobuf<CMsgClientP2PConnectionInfo.Builder>(CMsgClientP2PConnectionInfo.class, packetMsg);
    }

    @Override
    public CsgoClient getClient() {
        return (CsgoClient) super.getClient();
    }
}
