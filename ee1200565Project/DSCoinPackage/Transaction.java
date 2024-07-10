package DSCoinPackage;

import java.util.Objects;

public class Transaction {

    public String coinID;
    public Members Source;
    public Members Destination;
    public TransactionBlock coinsrc_block;

    //extra attributes
    public Transaction previous;
    public Transaction next;

    //extra method
    public boolean equals(Transaction t) {
        if (this == t) {
            return true;
        }
        return this.coinID.equals(t.coinID) && this.Source.UID.equals(t.Source.UID) && this.Destination.UID.equals(t.Destination.UID);
    }
}
