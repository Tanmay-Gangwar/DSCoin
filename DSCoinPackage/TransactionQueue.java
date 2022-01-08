package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions = 0;

  public void AddTransactions (Transaction transaction) {
    if (numTransactions == 0) firstTransaction = transaction;
    else lastTransaction.nextInQueue = transaction;
    lastTransaction = transaction;
    numTransactions++;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if (numTransactions == 0) throw new EmptyQueueException();
    if (numTransactions == 1) lastTransaction = null;
    Transaction transaction = firstTransaction;
    firstTransaction = firstTransaction.nextInQueue;
    numTransactions--;
    return transaction;
  }

  public int size() {
    return numTransactions;
  }
}