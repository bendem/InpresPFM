package be.hepl.benbear.accounting_db;

import be.hepl.benbear.commons.db.PrimaryKey;

/**
 * @todo Checkout which PKs are needed
 */
public class BillItem {

    @PrimaryKey private final int itemId;
    private final int billId;
    private final int movementId;
    private final int containerId;
    private final int destinationId;
    private final int price;

    public BillItem(int itemId, int billId, int movementId, int containerId, int destinationId, int price) {
        this.itemId = itemId;
        this.billId = billId;
        this.movementId = movementId;
        this.containerId = containerId;
        this.destinationId = destinationId;
        this.price = price;
    }

    public int getItemId() {
        return itemId;
    }

    public int getBillId() {
        return billId;
    }

    public int getMovementId() {
        return movementId;
    }

    public int getContainerId() {
        return containerId;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public int getPrice() {
        return price;
    }

}
