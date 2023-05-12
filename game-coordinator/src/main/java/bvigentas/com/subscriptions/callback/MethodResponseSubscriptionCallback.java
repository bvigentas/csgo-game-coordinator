package bvigentas.com.subscriptions.callback;

import bvigentas.com.client.CsgoSteamClient;
import bvigentas.com.enums.CustomResponseJobs;
import com.bvigentas.protobuf.steam.SteammessagesInventorySteamclient;
import in.dragonbra.javasteam.enums.EResult;
import in.dragonbra.javasteam.steam.handlers.steamunifiedmessages.callback.ServiceMethodResponse;
import in.dragonbra.javasteam.types.JobID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MethodResponseSubscriptionCallback {

    public MethodResponseSubscriptionCallback(CsgoSteamClient client, ServiceMethodResponse callback) {

        // and check for success
        if (callback.getResult() != EResult.OK) {
            log.debug("[METHOD-RESPONSE-SUBS-CALLBACK] request failed: {}", callback.getResult());
            return;
        }

        // retrieve the deserialized response for the request we made
        // notice the naming pattern
        // for requests: CMyService_Method_Request
        // for responses: CMyService_Method_Response

        if (callback.getJobID().equals(client.getCustomResposeJobIds().get(CustomResponseJobs.INVENTARY).getValue())) {
            SteammessagesInventorySteamclient.CInventory_Response response = callback.getDeserializedResponse(SteammessagesInventorySteamclient.CInventory_Response.class);

            log.debug("[METHOD-RESPONSE-SUBS-CALLBACK] player inventory: {}", response.getItemJson());

            client.getCustomResposeJobIds().put(CustomResponseJobs.INVENTARY, JobID.INVALID);
        }

    }
}
