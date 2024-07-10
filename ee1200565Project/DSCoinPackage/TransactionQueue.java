package DSCoinPackage;

public class TransactionQueue {

    public Transaction firstTransaction;
    public Transaction lastTransaction;
    public int numTransactions;

    public void AddTransactions (Transaction transaction) {
        transaction.next = lastTransaction;
        if (lastTransaction != null) {
            lastTransaction.previous = transaction;
        }
        lastTransaction = transaction;
        if (firstTransaction == null) {
            firstTransaction = transaction;
        }
        numTransactions++;
    }

    public Transaction RemoveTransaction () throws EmptyQueueException {
        if (numTransactions == 0 || firstTransaction == null) {
            throw new EmptyQueueException();
        }
        Transaction first = firstTransaction;
        firstTransaction = firstTransaction.previous;
        if (lastTransaction == first) {
            lastTransaction = null;
        }
        numTransactions--;
        return first;
    }

    public int size() {
        return numTransactions;
    }
}
