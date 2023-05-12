package bvigentas.com.services;

import in.dragonbra.javasteam.types.JobID;

import static com.bvigentas.protobuf.steam.SteammessagesInventorySteamclient.*;

public interface InventoryService {


    JobID getInventory(CInventory_GetInventory_Request request);

    JobID exchangeItem(CInventory_ExchangeItem_Request request);

    JobID getEligiblePromoItemDefIDs(CInventory_GetEligiblePromoItemDefIDs_Request request);

    JobID addPromoItem(CInventory_AddItem_Request request);

    JobID safeModifyItems(CInventory_ModifyItems_Request request);

    JobID consumePlaytime(CInventory_ConsumePlaytime_Request request);

    JobID consumeItem(CInventory_ConsumeItem_Request request);

    JobID devGenerateItem(CInventory_AddItem_Request request);

    JobID devSetNextDrop(CInventory_DevSetNextDrop_Request request);

    JobID splitItemStack(CInventory_SplitItemStack_Request request);

    JobID combineItemStacks(CInventory_CombineItemStacks_Request request);

    JobID getItemDefMeta(CInventory_GetItemDefMeta_Request request);

    JobID getUserPurchaseInfo(CInventory_GetUserPurchaseInfo_Request request);

    JobID purchaseInit(CInventory_PurchaseInit_Request request);

    JobID purchaseFinalize(CInventory_PurchaseFinalize_Request request);

    JobID inspectItem(CInventory_InspectItem_Request request);

}
