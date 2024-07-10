package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {

    public int tr_count;
    public static final String start_string = "DSCoin";
    public TransactionBlock[] lastBlocksList;

    public static boolean checkTransactionBlock (TransactionBlock tB) {
        //condition 1
        if (!tB.dgst.substring(0, 4).equals("0000")) {
            return false;
        }

        //condition 2
        CRF obj = new CRF(64);
        String prev_dgst;
        if (tB.previous == null) {
            prev_dgst = start_string;
        } else {
            prev_dgst = tB.previous.dgst;
        }
        String computedDgst = obj.Fn(prev_dgst + "#" + tB.trsummary + "#" + tB.nonce);
        if (!tB.dgst.equals(computedDgst)) {
            return false;
        }

        //condition 3
        MerkleTree tempTree = new MerkleTree();
        String computedTrSummary = tempTree.Build(tB.trarray);
        if (!tB.trsummary.equals(computedTrSummary)) {
            return false;
        }

        //condition 4
        Transaction[] tB_trarray = tB.trarray;
        for (int i=0; i<tB_trarray.length; i++) {
            Transaction trans = tB_trarray[i];
            if (!tB.checkTransaction(trans)) {
                return false;
            }
        }

        //it means all 4 conditions are true, so return true
        return true;
    }

    public TransactionBlock FindLongestValidChain () {
        int longestChainLength = 0;
        TransactionBlock longestChainValidLastBlock = null;
        for (int i=0; i<lastBlocksList.length; i++) {
            TransactionBlock lastBlock = lastBlocksList[i];
            if (lastBlock == null) {
                break;
            }
            int currentChainLength = 0;
            TransactionBlock currentChainValidLastBlock = null;
            TransactionBlock temp = lastBlock;
            while (temp != null) {
                if (checkTransactionBlock(temp)) {
                    currentChainLength++;
                    if (currentChainLength == 1) {
                        currentChainValidLastBlock = temp;
                    }
                } else {
                    currentChainLength = 0;
                    currentChainValidLastBlock = null;
                }
                temp = temp.previous;
            }
            if (currentChainLength > longestChainLength) {
                longestChainLength = currentChainLength;
                longestChainValidLastBlock = currentChainValidLastBlock;
            }
        }
        return longestChainValidLastBlock;
    }

    public void InsertBlock_Malicious (TransactionBlock newBlock) {
        TransactionBlock lastBlock = FindLongestValidChain();

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

        boolean isInserted = false;
        int i;
        for (i=0; i<lastBlocksList.length; i++) {
            TransactionBlock tb = lastBlocksList[i];
            if (tb == null) {
                break;
            }
            if (lastBlock == tb) {
                lastBlocksList[i] = newBlock;
                isInserted = true;
                break;
            }
        }
        if (!isInserted) {
            lastBlocksList[i] = newBlock;
        }
    }
}
