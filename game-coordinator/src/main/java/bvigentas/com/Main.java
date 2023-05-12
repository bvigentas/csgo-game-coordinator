package bvigentas.com;

import bvigentas.com.client.CsgoSteamClient;
import bvigentas.com.handlers.callbacks.ItemCallback;

public class Main {

    public static void main(String[] args) {
        var client = new CsgoSteamClient();
        client.connect();

        ItemCallback itemCallback = client.getItemHandler().requestItemInspect("steam://rungame/730/76561202255233023/+csgo_econ_action_preview%20S76561198078813416A29348950476D13838473314060488595");

        System.out.println(itemCallback.getBody());

    }

}
