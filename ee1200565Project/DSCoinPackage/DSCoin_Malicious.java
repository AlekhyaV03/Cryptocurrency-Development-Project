package DSCoinPackage;

import java.util.HashMap;

public class DSCoin_Malicious {

    public TransactionQueue pendingTransactions;
    public BlockChain_Malicious bChain;
    public Members[] memberlist;
    public String latestCoinID;

    //extra attributes
    //will be created during Moderator initializeDSCoin()
    public HashMap<String, Members> uidToMemberMap;
}
