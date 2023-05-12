package com.bvigentas.steam.handlers;

import in.dragonbra.javasteam.base.IPacketGCMsg;

public interface ClientGCMsgHandler {

    void handleGCMsg(IPacketGCMsg packetGCMsg);

}
