package DSCoinPackage;

import HelperClasses.Pair;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Moderator {

    public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
        Members[] memberlist = DSObj.memberlist;
        DSObj.uidToMemberMap = createUidToMemberMap(memberlist);
        DSObj.latestCoinID = "100000";
        Members moderator = new Members();
        moderator.UID = "Moderator";

        Transaction[] trarray = new Transaction[coinCount];
        int addedCount = 0;
        while (addedCount != coinCount) {
            for (int i = 0; i < memberlist.length; i++) {
                Members m = memberlist[i];
                Transaction tobj = new Transaction();
                tobj.coinID = DSObj.latestCoinID;
                tobj.Source = moderator;
                tobj.Destination = memberlist[i];
                tobj.coinsrc_block = null;
                trarray[addedCount] = tobj;
                addedCount++;

                //increment latestCoinID
                int latestCoinIdInt = Integer.parseInt(DSObj.latestCoinID);
                latestCoinIdInt++;
                DSObj.latestCoinID = String.valueOf(latestCoinIdInt);
            }
        }

        int trCount = DSObj.bChain.tr_count;
        for (int i=0; i<coinCount; i+=trCount) {
            Transaction[] tempArr = new Transaction[trCount];
            for (int j=0; j<trCount; j++) {
                tempArr[j] = trarray[i+j];
            }
            TransactionBlock tB = new TransactionBlock(tempArr);
            DSObj.bChain.InsertBlock_Honest(tB);

            for (int j=0; j<trCount; j++) {
                //add coin to member's mycoins list
                addCoinFromTrxToDestCoinList(tempArr[j], tB);
            }
        }
    }

    public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
        Members[] memberlist = DSObj.memberlist;
        DSObj.uidToMemberMap = createUidToMemberMap(memberlist);
        DSObj.latestCoinID = "100000";
        Members moderator = new Members();
        moderator.UID = "Moderator";

        Transaction[] trarray = new Transaction[coinCount];
        int addedCount = 0;
        while (addedCount != coinCount) {
            for (int i = 0; i < memberlist.length; i++) {
                Members m = memberlist[i];
                Transaction tobj = new Transaction();
                tobj.coinID = DSObj.latestCoinID;
                tobj.Source = moderator;
                tobj.Destination = memberlist[i];
                tobj.coinsrc_block = null;
                trarray[addedCount] = tobj;
                addedCount++;

                //increment latestCoinID
                int latestCoinIdInt = Integer.parseInt(DSObj.latestCoinID);
                latestCoinIdInt++;
                DSObj.latestCoinID = String.valueOf(latestCoinIdInt);
            }
        }

        int trCount = DSObj.bChain.tr_count;
        for (int i=0; i<coinCount; i+=trCount) {
            Transaction[] tempArr = new Transaction[trCount];
            for (int j=0; j<trCount; j++) {
                tempArr[j] = trarray[i+j];
            }
            TransactionBlock tB = new TransactionBlock(tempArr);
            DSObj.bChain.InsertBlock_Malicious(tB);

            for (int j=0; j<trCount; j++) {
                //add coin to member's mycoins list
                addCoinFromTrxToDestCoinList(tempArr[j], tB);
            }
        }
    }

    //extra methods
    public HashMap<String, Members> createUidToMemberMap(Members[] memberlist) {
        HashMap<String, Members> uidToMemberMap = new HashMap<String, Members>();
        for (int i = 0; i < memberlist.length; i++) {
            if (memberlist[i] == null) {
                break;
            }
            Members m = memberlist[i];
            uidToMemberMap.put(m.UID, m);
        }
        return uidToMemberMap;
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
