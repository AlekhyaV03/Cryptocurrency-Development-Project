package DSCoinPackage;

import java.util.HashMap;

public class DSCoin_Honest {

    public TransactionQueue pendingTransactions;
    public BlockChain_Honest bChain;
    public Members[] memberlist;
    public String latestCoinID;

    //extra attributes
    //will be created during Moderator initializeDSCoin()
    public HashMap<String, Members> uidToMemberMap;
}
