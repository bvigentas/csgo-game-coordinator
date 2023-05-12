package com.bvigentas.steam.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SteamClientConfig {

    private String user;
    private String pass;
    private String authCode;
    private String twoFactorAuth;
    private String loginKey;
    private String sentry;
    private String personaName;
    private boolean connectOnStart;
    private boolean reconnectOnDisconnect;

}
