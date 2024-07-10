package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;

public class Members {

    public String UID;
    public List<Pair<String, TransactionBlock>> mycoins;
    public Transaction[] in_process_trans;

    public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
        Pair<String, TransactionBlock> coinPair = mycoins.get(0);
        mycoins.remove(0);
        Transaction tobj = new Transaction();
        tobj.coinID = coinPair.first;
        tobj.coinsrc_block = coinPair.second;
        tobj.Source = DSobj.uidToMemberMap.get(UID);
        tobj.Destination = DSobj.uidToMemberMap.get(destUID);
        for (int i=0; i<in_process_trans.length; i++) {
            if (in_process_trans[i] == null) {
                in_process_trans[i] = tobj;
                break;
            }
        }
        DSobj.pendingTransactions.AddTransactions(tobj);
    }

    public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
        //find transBlock containing the transaction
        TransactionBlock temp = DSObj.bChain.lastBlock;
        TransactionBlock tB = null;
        int trxId = 0;
        while (temp != null) {
            for (int i=0; i<temp.trarray.length; i++) {
                if (tobj == temp.trarray[i] || tobj.equals(temp.trarray[i])) {
                    tB = temp;
                    trxId = i;
                    break;
                }
            }
            temp = temp.previous;
        }

        if (tB == null) {
            //tobj is not found in any transBlock in bChain
            throw new MissingTransactionException();
        }

        //find sibling-coupled-path-to-root in merkle tree of tB
        List<Pair<String, String>> siblingCoupledPath = tB.Tree.getSiblingCoupledPathToRoot(trxId);

        //find dgst pairs list
        List<Pair<String, String>> dgstPairsList = getDgstPairList(tB, DSObj);

        //delete tobj from in_process_trans
        for (int i=0; i<in_process_trans.length; i++) {
            if (tobj == in_process_trans[i]) {
                in_process_trans[i] = null;
                break;
            }
        }

        //adds the sent coin to the Destination’s mycoins list
        addCoinFromTrxToDestCoinList(tobj, tB);

        return new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(siblingCoupledPath, dgstPairsList);
    }

    public void MineCoin(DSCoin_Honest DSObj) throws EmptyQueueException {
        int trCount = DSObj.bChain.tr_count;
        TransactionBlock lastBlock = DSObj.bChain.lastBlock;

        Transaction[] trarray = new Transaction[trCount];
        int addedCount = 0;
        Set<String> addedCoinIds = new HashSet<String>();
        while (addedCount != trCount - 1) {
            Transaction tobj = DSObj.pendingTransactions.RemoveTransaction();
            if (lastBlock.checkTransaction(tobj) && !addedCoinIds.contains(tobj.coinID)) {
                trarray[addedCount] = tobj;
                addedCount++;
                addedCoinIds.add(tobj.coinID);
            }
        }
        Transaction minerRewardTransaction = new Transaction();
        minerRewardTransaction.coinID = DSObj.latestCoinID;
        minerRewardTransaction.Source = null;
        minerRewardTransaction.Destination = this;
        minerRewardTransaction.coinsrc_block = null;
        trarray[addedCount] = minerRewardTransaction;

        TransactionBlock tB = new TransactionBlock(trarray);
        DSObj.bChain.InsertBlock_Honest(tB);

        addCoinFromTrxToDestCoinList(minerRewardTransaction, tB);

        int latestCoinIdInt = Integer.parseInt(DSObj.latestCoinID);
        latestCoinIdInt++;
        DSObj.latestCoinID = String.valueOf(latestCoinIdInt);
    }

    public void MineCoin(DSCoin_Malicious DSObj) throws EmptyQueueException {
        int trCount = DSObj.bChain.tr_count;
        TransactionBlock lastBlock = DSObj.bChain.FindLongestValidChain();

        Transaction[] trarray = new Transaction[trCount];
        int addedCount = 0;
        Set<String> addedCoinIds = new HashSet<String>();
        while (addedCount != trCount - 1) {
            Transaction tobj = DSObj.pendingTransactions.RemoveTransaction();
            if (lastBlock.checkTransaction(tobj) && !addedCoinIds.contains(tobj.coinID)) {
                trarray[addedCount] = tobj;
                addedCount++;
                addedCoinIds.add(tobj.coinID);
            }
        }
        Transaction minerRewardTransaction = new Transaction();
        minerRewardTransaction.coinID = DSObj.latestCoinID;
        minerRewardTransaction.Source = null;
        minerRewardTransaction.Destination = this;
        minerRewardTransaction.coinsrc_block = null;
        trarray[addedCount] = minerRewardTransaction;

        TransactionBlock tB = new TransactionBlock(trarray);
        DSObj.bChain.InsertBlock_Malicious(tB);

        addCoinFromTrxToDestCoinList(minerRewardTransaction, tB);

        int latestCoinIdInt = Integer.parseInt(DSObj.latestCoinID);
        latestCoinIdInt++;
        DSObj.latestCoinID = String.valueOf(latestCoinIdInt);
    }

    //extra method
    public List<Pair<String,String>> getDgstPairList(TransactionBlock tB, DSCoin_Honest DSObj) {
        List<Pair<String,String>> dgstPairsList = new ArrayList<Pair<String,String>>();
        TransactionBlock temp = DSObj.bChain.lastBlock;
        while (temp != tB.previous && temp != null) {
            String prev_dgst;
            if (temp.previous == null) {
                prev_dgst = DSObj.bChain.start_string;
            } else {
                prev_dgst = temp.previous.dgst;
            }
            dgstPairsList.add(new Pair<String, String>(temp.dgst, prev_dgst + "#" + temp.trsummary + "#" + temp.nonce));
            temp = temp.previous;
        }
        if (temp == null) {
            //previous block is tB is null
            dgstPairsList.add(new Pair<String, String>(DSObj.bChain.start_string, null));
        } else {
            dgstPairsList.add(new Pair<String, String>(temp.dgst, null));
        }
        Collections.reverse(dgstPairsList);
        return dgstPairsList;
    }

    public void addCoinFromTrxToDestCoinList(Transaction tobj, TransactionBlock coinsource_block) {
        Members dest = tobj.Destination;
        for (int i=0; i<dest.mycoins.size(); i++) {
            if (dest.mycoins.get(i).first.equals(tobj.coinID)) {
                return;
            }
        }
        Pair<String, TransactionBlock> coinPair = new Pair<String, TransactionBlock>(tobj.coinID, coinsource_block);
        dest.mycoins.add(coinPair);
        Collections.sort(dest.mycoins, new Comparator<Pair<String, TransactionBlock>>() {
            @Override
            public int compare(Pair<String, TransactionBlock> o1, Pair<String, TransactionBlock> o2) {
                return Integer.parseInt(o1.first) - Integer.parseInt(o2.first);
            }
        });
    }
}
