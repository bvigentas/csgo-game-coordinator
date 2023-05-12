package bvigentas.com.subscriptions.callback;

import bvigentas.com.client.CsgoSteamClient;
import in.dragonbra.javasteam.steam.handlers.steamfriends.callback.FriendsListCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
public class FriendsListSubscriptionCallback {
    public FriendsListSubscriptionCallback(CsgoSteamClient client, FriendsListCallback callback) {
        log.info("[FRIENDS-LIST-SUBS-CALLBACK] onFriendsList: {}", callback.getFriendList());
        client.setFriendsSteamId(callback.getFriendList().stream().map(friend -> friend.getSteamID()).collect(Collectors.toList()));
    }
}
