package com.bvigentas.steam.handlers;

import in.dragonbra.javasteam.protobufs.steamclient.SteammessagesClientserver2;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackMsg;
import in.dragonbra.javasteam.types.JobID;
import in.dragonbra.javasteam.types.SteamID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientRichPresenceInfoCallback extends CallbackMsg {

    private Long watchableGameID;
    private SteamID watchingServer;
    private Long steamId;
    private String all;

    public ClientRichPresenceInfoCallback(JobID jobID, SteammessagesClientserver2.CMsgClientRichPresenceInfo.Builder body) {
        setJobID(jobID);
        parse(body);
    }

    public void parse(SteammessagesClientserver2.CMsgClientRichPresenceInfo.Builder body) {
        var all = new StringBuilder();

        for (var presence : body.getRichPresenceList()) {
            if (presence.hasRichPresenceKv()) {
                setSteamId(presence.getSteamidUser());
                byte[] data = presence.getRichPresenceKv().toByteArray();

                for (int i = 1; i < data.length; i++) {
                    String value = (char) data[i] + "";
                    if (value.trim().length() > 0) {
                        all.append(value);
                    } else {
                        all.append(" ");
                    }
                }
            }
        }

        var kvMap = Arrays.asList(all.toString().split("  ")).stream().map(s -> s.trim().split(" "))
                .collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));
        if (kvMap.get("WatchableGameID") != null) {
            watchableGameID = Long.parseLong(kvMap.get("WatchableGameID"));
        }

        if (kvMap.get("watching_server") != null) {
            watchingServer = new SteamID();
            watchingServer.setFromSteam3String(kvMap.get("watching_server"));
        }
        this.all = all.toString();
    }

}
