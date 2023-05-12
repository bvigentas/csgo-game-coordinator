package com.bvigentas.steam.clients;

import com.bvigentas.steam.config.SteamClientConfig;
import com.bvigentas.steam.utils.EncoderUtils;
import in.dragonbra.javasteam.base.IClientMsg;
import in.dragonbra.javasteam.enums.EFriendRelationship;
import in.dragonbra.javasteam.enums.EPersonaState;
import in.dragonbra.javasteam.enums.EResult;
import in.dragonbra.javasteam.steam.handlers.steamfriends.SteamFriends;
import in.dragonbra.javasteam.steam.handlers.steamfriends.callback.FriendsListCallback;
import in.dragonbra.javasteam.steam.handlers.steamuser.LogOnDetails;
import in.dragonbra.javasteam.steam.handlers.steamuser.MachineAuthDetails;
import in.dragonbra.javasteam.steam.handlers.steamuser.OTPDetails;
import in.dragonbra.javasteam.steam.handlers.steamuser.SteamUser;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.*;
import in.dragonbra.javasteam.steam.steamclient.SteamClient;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackManager;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackMsg;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.callbacks.DisconnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.types.JobID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Setter
public class CsgoSteamClient extends SteamClient {

    public static final Long DEFAULT_TIMEOUT = 10l;
    protected SteamClientConfig config;
    protected SteamUser steamUser;
    protected SteamFriends steamFriends;
    protected CallbackManager manager;
    protected boolean running;
    protected boolean logged;
    protected EResult result;
    protected EResult extendedResult;
    private CompletableFuture<Void> managerLoop;
    private Map<Object, CompletableFuture<Object>> subscribers = new HashMap<>();

    public CsgoSteamClient(SteamClientConfig config, String steamWebApi) {
        super(steamWebApi != null ? SteamConfiguration.create(c -> c.withWebAPIKey(steamWebApi)) : SteamConfiguration.createDefault());
        this.config = config;

        this.init();
    }
    protected void init() {
        steamUser = getHandler(SteamUser.class);
        steamFriends = getHandler(SteamFriends.class);

        manager = new CallbackManager(this);

        manager.subscribe(ConnectedCallback.class, this::onConnected);
        manager.subscribe(DisconnectedCallback.class, this::onDisconnected);

        manager.subscribe(LoggedOnCallback.class, this::onLoggedOn);
        manager.subscribe(LoggedOffCallback.class, this::onLoggedOff);

        manager.subscribe(UpdateMachineAuthCallback.class, this::onMachineAuth);
        manager.subscribe(LoginKeyCallback.class, this::onLoginKey);

        manager.subscribe(AccountInfoCallback.class, this::onAccountInfo);
        manager.subscribe(FriendsListCallback.class, this::onFriendList);
    }

    private void onConnected(ConnectedCallback callback) {
        log.info("Connected to Steam! Logging in " + config.getUser() + "...");

        var details = new LogOnDetails();
        details.setUsername(config.getUser());

        var loginKeyFile = new File(config.getLoginKey());

        if (loginKeyFile.exists()) {
            try (var s = new Scanner(loginKeyFile)) {
                details.setLoginKey(s.nextLine());
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            }
        } else {
            details.setPassword(config.getPass());
        }

        details.setTwoFactorCode(config.getTwoFactorAuth());
        details.setAuthCode(config.getAuthCode());
        details.setShouldRememberPassword(true);
        try {
            var sentry = new File(config.getSentry());
            if (sentry.exists()) {
                details.setSentryFileHash(EncoderUtils.calculateSHA1(sentry));
            }
        } catch (Exception e) {
            //Log error
        }

        steamUser.logOn(details);
    }

    private void onDisconnected(DisconnectedCallback callback) {
        log.info("onDisconnected" + callback.isUserInitiated());

        if (config.isReconnectOnDisconnect() && !callback.isUserInitiated()) {
            log.info("Disconnected from Steam, reconnecting in 30...");
            try {
                Thread.sleep(30000L);
            } catch (InterruptedException e) {
            }
            connect();
        } else {
            log.info("Disconnected from Steam");
        }
    }

    protected void onLoggedOn(LoggedOnCallback callback) {
        boolean isSteamGuard = callback.getResult() == EResult.AccountLogonDenied;
        boolean is2Fa = callback.getResult() == EResult.AccountLoginDeniedNeedTwoFactor;
        result = callback.getResult();
        extendedResult = callback.getExtendedResult();

        if (isSteamGuard || is2Fa) {
            log.info("This account is SteamGuard protected.");
            disconnect();
            return;
        }
        if (callback.getResult() != EResult.OK) {
            log.info("Unable to logon to Steam: " + callback.getResult() + " / " + callback.getExtendedResult());
            disconnect();
            if (EResult.ServiceUnavailable.equals(callback.getResult())
                    || EResult.TryAnotherCM.equals(callback.getResult())) {
                try {
                    Thread.sleep(30000L);
                } catch (InterruptedException e) {
                }
                if (running == false)
                    connect();
            }
            return;
        }

        log.info("Successfully logged on! ");
        logged = true;
    }

    private void onLoggedOff(LoggedOffCallback callback) {
        log.info("Logged off of Steam: " + callback.getResult());
        result = callback.getResult();
        extendedResult = null;
        logged = false;
        if (config.isReconnectOnDisconnect()) {
            log.info("Logged off from Steam, reconnecting in 5...");
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
            }
            connect();
        } else {
            log.info("Logged off from Steam");
        }
    }

    private void onMachineAuth(UpdateMachineAuthCallback callback) {
        var sentry = new File(config.getSentry());

        try (var fos = new FileOutputStream(sentry)) {

            var channel = fos.getChannel();
            channel.position(callback.getOffset());
            channel.write(ByteBuffer.wrap(callback.getData(), 0, callback.getBytesToWrite()));

            var otpDetails = new OTPDetails();
            otpDetails.setIdentifier(callback.getOneTimePassword().getIdentifier());
            otpDetails.setType(callback.getOneTimePassword().getType());

            var details = new MachineAuthDetails();
            details.setJobID(callback.getJobID());
            details.setFileName(callback.getFileName());
            details.setBytesWritten(callback.getBytesToWrite());
            details.setFileSize((int) sentry.length());
            details.setOffset(callback.getOffset());
            details.seteResult(EResult.OK);
            details.setLastError(0);
            details.setOneTimePassword(otpDetails);
            details.setSentryFileHash(EncoderUtils.calculateSHA1(sentry));

            steamUser.sendMachineAuthResponse(details);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("!!onMachineAuth: ", e);
        }
    }

    private void onLoginKey(LoginKeyCallback callback) {
        try (var fw = new FileWriter(config.getLoginKey())) {
            fw.write(callback.getLoginKey());
            steamUser.acceptNewLoginKey(callback);
        } catch (IOException e) {
            log.error("!!onLoginKey: ", e);
        }
    }

    private void onAccountInfo(AccountInfoCallback callback) {
        steamFriends.setPersonaState(EPersonaState.Online);
        if (config.getPersonaName() != null && !config.getPersonaName().equals(callback.getPersonaName())) {
            steamFriends.setPersonaName(config.getPersonaName());
        }
    }

    private void onFriendList(FriendsListCallback callback) {
        for (var friend : callback.getFriendList()) {
            if (friend.getRelationship() == EFriendRelationship.RequestRecipient) {
                steamFriends.addFriend(friend.getSteamID());
            }
        }
    }

    @Override
    public void connect() {
        log.info("Connecting");

        super.connect();
        running = true;
        if (managerLoop == null) {
            log.info("managerLoop");
            managerLoop = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            manager.runWaitCallbacks(1000L);
                        } catch (Exception e) {
                            log.error("!!managerLoop: ", e);
                        }
                    }
                }
            });
        }
        log.info("Connected");
    }

    @Override
    public void disconnect() {
        running = false;
        super.disconnect();
    }

    @Override
    public void postCallback(CallbackMsg msg) {
        if (!msg.getJobID().equals(JobID.INVALID)) {
            postCallback(msg.getJobID(), msg);
        } else {
            super.postCallback(msg);
        }
    }

    public void postCallback(Object key, CallbackMsg msg) {
        submitResponse(key, msg);
        super.postCallback(msg);
    }

    public void submitResponse(Object key, Object result) {
        log.info(">>submitResponse: " + key);
        if (this.subscribers.get(key) != null) {
            this.subscribers.get(key).complete(result);
        } else {
            CompletableFuture<Object> future = new CompletableFuture<>();
            future.complete(result);
            subscribers.put(key, future);
        }
    }

    public <T> T sendJobAndWait(IClientMsg msg, long timeout) {
        sendJob(msg);
        return registerAndWait(msg.getSourceJobID(), timeout);
    }

    public JobID sendJob(IClientMsg msg) {
        JobID jobID = getNextJobID();
        msg.setSourceJobID(jobID);
        send(msg);
        return jobID;
    }

    public <T> T registerAndWait(Object key, Long timeout) {
        log.trace(">>registerAndWait: " + key);
        T value = null;
        try {
            if (this.subscribers.get(key) != null) {
                return (T) this.subscribers.get(key).get(timeout, TimeUnit.SECONDS);
            }
            CompletableFuture<Object> future = new CompletableFuture<>();
            subscribers.put(key, future);
            value = (T) future.get(timeout, TimeUnit.SECONDS);
            return value;
        } catch (Exception e) {
            log.error("!! registerAndWait ", e);
            return null;
        } finally {
            subscribers.remove(key);
            log.trace("<<registerAndWait: " + value);
        }
    }

    public <T> T registerAndWait(Object key) {
        return registerAndWait(key, DEFAULT_TIMEOUT);
    }
}
