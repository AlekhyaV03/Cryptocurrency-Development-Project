package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

    public int tr_count;
    public static final String start_string = "DSCoin";
    public TransactionBlock lastBlock;

    public void InsertBlock_Honest (TransactionBlock newBlock) {
        CRF obj = new CRF(64);
        String prev_dgst;
        if (lastBlock == null) {
            prev_dgst = start_string;
        } else {
            prev_dgst = lastBlock.dgst;
        }
        int nonce = 1000000001;
        newBlock.dgst = obj.Fn(prev_dgst + "#" + newBlock.trsummary + "#" + String.valueOf(nonce));
        while (!newBlock.dgst.substring(0, 4).equals("0000")) {
            nonce++;
            newBlock.dgst = obj.Fn(prev_dgst + "#" + newBlock.trsummary + "#" + String.valueOf(nonce));
        }
        newBlock.nonce = String.valueOf(nonce);
        newBlock.previous = lastBlock;
        lastBlock = newBlock;
    }
}
