package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

    public Transaction[] trarray;
    public TransactionBlock previous;
    public MerkleTree Tree;
    public String trsummary;
    public String nonce;
    public String dgst;

    TransactionBlock(Transaction[] t) {
        int len = t.length;
        trarray = new Transaction[len];
        for (int i=0; i<len; i++) {
            trarray[i] = t[i];
        }
        previous = null;
        Tree = new MerkleTree();
        trsummary = Tree.Build(trarray);
        nonce = null;
        dgst = null;
    }

    public boolean checkTransaction (Transaction t) {
        TransactionBlock coinsrc_block = t.coinsrc_block;
        if (coinsrc_block == null) {
            return true;
        }

        //check coinId present in coinsrc_block
        Transaction[] coinsrc_block_trarray = coinsrc_block.trarray;
        boolean iscoinIdPresent = false;
        for (int i=0; i<coinsrc_block_trarray.length; i++) {
            Transaction trans = coinsrc_block_trarray[i];
            if (trans.coinID.equals(t.coinID) && trans.Destination.UID.equals(t.Source.UID)) {
                iscoinIdPresent = true;
                break;
            }
        }
        if (!iscoinIdPresent) {
            //check1 is failed (coinId not present in coinsrc_block), so return false
            return false;
        }

        //check no double spending in current block to coinsrc block
        TransactionBlock temp = this;
        while (temp != coinsrc_block && temp != null) {
            Transaction[] temp_trarray = temp.trarray;
            for (int i=0; i<temp_trarray.length; i++) {
                Transaction trans = temp_trarray[i];
                if (trans != t && trans.coinID.equals(t.coinID)) {
                    //it means same coinId was spent in some different transaction so return false
                    return false;
                }
            }
            temp = temp.previous;
        }
        if (temp == coinsrc_block) {
            //no double spending in any intermediate block so return true
            return true;
        }

        return false;
    }
}
