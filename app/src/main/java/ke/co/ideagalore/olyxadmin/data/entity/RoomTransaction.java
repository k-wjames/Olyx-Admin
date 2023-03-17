package ke.co.ideagalore.olyxadmin.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RoomTransaction {

    @PrimaryKey
    public int uid;
    @ColumnInfo(name = "transaction_id")
    public String transactionId;
    @ColumnInfo(name = "transaction_type")
    public String transactionType;
    @ColumnInfo(name = "product_id")
    public String productId;
    @ColumnInfo(name = "product")
    public String product;
    @ColumnInfo(name = "store")
    public String store;
    @ColumnInfo(name = "time")
    public String time;
    @ColumnInfo(name = "attendant")
    public String attendant;
    @ColumnInfo(name = "terminal_id")
    public String terminalId;
    @ColumnInfo(name = "quantity")
    public int quantity;
    @ColumnInfo(name = "buying_price")
    public int buyingPrice;
    @ColumnInfo(name = "selling_price")
    public int sellingPrice;
    @ColumnInfo(name = "total_price")
    public int totalPrice;
    @ColumnInfo(name = "profit")
    public int profit;
    @ColumnInfo(name = "updated_stock")
    public int updatedStock;
    @ColumnInfo(name = "date")
    public long date;

    public RoomTransaction(int uid, String transactionId, String transactionType, String productId, String product, String store, String time, String attendant, String terminalId, int quantity, int buyingPrice, int sellingPrice, int totalPrice, int profit, int updatedStock, long date) {
        this.uid = uid;
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.productId = productId;
        this.product = product;
        this.store = store;
        this.time = time;
        this.attendant = attendant;
        this.terminalId = terminalId;
        this.quantity = quantity;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.totalPrice = totalPrice;
        this.profit = profit;
        this.updatedStock = updatedStock;
        this.date = date;
    }
}
