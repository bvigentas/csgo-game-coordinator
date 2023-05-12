package bvigentas.com.services.impl;

import bvigentas.com.services.InventoryService;
import com.bvigentas.protobuf.steam.SteammessagesInventorySteamclient;
import in.dragonbra.javasteam.steam.handlers.steamunifiedmessages.SteamUnifiedMessages;
import in.dragonbra.javasteam.steam.handlers.steamunifiedmessages.UnifiedService;
import in.dragonbra.javasteam.types.JobID;

public class InventoryServiceImpl extends UnifiedService implements InventoryService {

    public InventoryServiceImpl(SteamUnifiedMessages steamUnifiedMessages) {
        super(steamUnifiedMessages);
    }

    @Override
    public JobID getInventory(SteammessagesInventorySteamclient.CInventory_GetInventory_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID exchangeItem(SteammessagesInventorySteamclient.CInventory_ExchangeItem_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID getEligiblePromoItemDefIDs(SteammessagesInventorySteamclient.CInventory_GetEligiblePromoItemDefIDs_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID addPromoItem(SteammessagesInventorySteamclient.CInventory_AddItem_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID safeModifyItems(SteammessagesInventorySteamclient.CInventory_ModifyItems_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID consumePlaytime(SteammessagesInventorySteamclient.CInventory_ConsumePlaytime_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID consumeItem(SteammessagesInventorySteamclient.CInventory_ConsumeItem_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID devGenerateItem(SteammessagesInventorySteamclient.CInventory_AddItem_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID devSetNextDrop(SteammessagesInventorySteamclient.CInventory_DevSetNextDrop_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID splitItemStack(SteammessagesInventorySteamclient.CInventory_SplitItemStack_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID combineItemStacks(SteammessagesInventorySteamclient.CInventory_CombineItemStacks_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID getItemDefMeta(SteammessagesInventorySteamclient.CInventory_GetItemDefMeta_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID getUserPurchaseInfo(SteammessagesInventorySteamclient.CInventory_GetUserPurchaseInfo_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID purchaseInit(SteammessagesInventorySteamclient.CInventory_PurchaseInit_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID purchaseFinalize(SteammessagesInventorySteamclient.CInventory_PurchaseFinalize_Request request) {
        return this.sendMessage(request);
    }

    @Override
    public JobID inspectItem(SteammessagesInventorySteamclient.CInventory_InspectItem_Request request) {
        return this.sendMessage(request);
    }
}
