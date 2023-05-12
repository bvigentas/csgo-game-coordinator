package bvigentas.com.handlers;

import bvigentas.com.CsgoGameCoordinator;
import bvigentas.com.client.CsgoSteamClient;
import in.dragonbra.javasteam.base.IClientGCMsg;
import in.dragonbra.javasteam.types.JobID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class CustomHandler implements ClientGCMsgHandler {

    protected CsgoSteamClient client;

    private CsgoGameCoordinator gameCoordinator;

    public void setup(CsgoGameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
        this.client = gameCoordinator.getClient();
    }

    public <T> T sendJobAndAwait(IClientGCMsg message) {
        sendJob(message);
        return client.registerAndWait(message.getSourceJobID());
    }

    public JobID sendJob(IClientGCMsg message) {
        var jobId = client.getNextJobID();
        message.setSourceJobID(jobId);
        send(message);

        return jobId;
    }

    public void send(IClientGCMsg message) {
        if (client.isReady()) {
            gameCoordinator.send(message);
        } else {
            log.error("[MAIN-HANDLER] Game Coordinator is not ready!");
            throw new RuntimeException("Game Coordinator is not ready!");
        }
    }
}
