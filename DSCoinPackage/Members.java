package DSCoinPackage;

import java.util.*;
import HelperClasses.*;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    Transaction transaction = new Transaction();
    transaction.coinID = mycoins.get(0).first;
    transaction.Source = this;
    for (int i = 0; i < DSobj.memberlist.length; i++){
      if (DSobj.memberlist[i].UID.equals(destUID)){
        transaction.Destination = DSobj.memberlist[i];
        break;
      }
    }
    transaction.coinsrc_block = mycoins.get(0).second;
    mycoins.remove(0);
    if (in_process_trans == null) in_process_trans = new Transaction[100];
    for (int i = 0; i < in_process_trans.length; i++){
      if (in_process_trans[i] == null){
        in_process_trans[i] = transaction;
        break;
      }
    }
    DSobj.pendingTransactions.AddTransactions(transaction);
  }

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj){
    Transaction transaction = new Transaction();
    transaction.coinID = mycoins.get(0).first;
    transaction.Source = this;
    for (int i = 0; i < DSobj.memberlist.length; i++){
      if (DSobj.memberlist[i].UID.equals(destUID)){
        transaction.Destination = DSobj.memberlist[i];
        break;
      }
    }
    transaction.coinsrc_block = mycoins.get(0).second;
    mycoins.remove(0);
    for (int i = 0; i < in_process_trans.length; i++){
      if (in_process_trans[i] == null){
        in_process_trans[i] = transaction;
        break;
      }
    }
    DSobj.pendingTransactions.AddTransactions(transaction);
  }


  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    boolean found = false;
    int i = 0;
    TransactionBlock curr = DSObj.bChain.lastBlock;
    while (curr != null && !found){
      for (i = 0; i < curr.trarray.length; i++){
        if (curr.trarray[i].coinID.equals(tobj.coinID)){
          found = true;
          break;
        }
      }
      curr = curr.previous;
    }
    if (!found) throw new MissingTransactionException();
    List<Pair<String, String>> Proof = new ArrayList<Pair<String, String>>();
    curr = DSObj.bChain.lastBlock;
    while (!curr.trarray[i].coinID.equals(tobj.coinID)){
      String previous = "DSCoin";
      if (curr.previous != null) previous = curr.previous.dgst;
      Proof.add(new Pair<String, String>(curr.dgst, previous + "#" + curr.trsummary + "#" + curr.nonce));
      curr = curr.previous;
    }
    String previous = "DSCoin";
    if (curr.previous != null) previous = curr.previous.dgst;
    Proof.add(new Pair<String, String>(curr.dgst, previous + "#" + curr.trsummary + "#" + curr.nonce));
    Proof.add(new Pair<String, String>(previous, null));
    Collections.reverse(Proof);
    int l = 1, r = curr.trarray.length;
    for (i = 0; i < curr.trarray.length; i++){
      if (curr.trarray[i].coinID.equals(tobj.coinID)) break;
    }
    i++;

		TreeNode node = curr.Tree.rootnode;
		while (r > l){
			int m = (l + r) / 2;
			if (i <= m) {
				node = node.left;
				r = m;
			}
			else {
				node = node.right;
				l = m + 1;
			}
		}
		List<Pair<String, String>> pathToRoot = new ArrayList<Pair<String, String>>();
		while (node.parent != null){
			node = node.parent;
			pathToRoot.add(new Pair<String, String>(node.left.val, node.right.val));
		}
		pathToRoot.add(new Pair<String, String>(node.val, null));

    tobj.Destination.mycoins.add(new Pair<String, TransactionBlock>(tobj.coinID, curr));
    for (i = tobj.Destination.mycoins.size() - 1; i > 0; i--){
      if (Long.parseLong(tobj.Destination.mycoins.get(i).first) < Long.parseLong(tobj.Destination.mycoins.get(i - 1).first)){
        Pair<String, TransactionBlock> temp = tobj.Destination.mycoins.get(i - 1);
        tobj.Destination.mycoins.set(i - 1, tobj.Destination.mycoins.get(i));
        tobj.Destination.mycoins.set(i, temp);
      }
    }
    for (i = 0; i < in_process_trans.length; i++){
      if (in_process_trans[i] != null &&  in_process_trans[i].coinID.equals(tobj.coinID)){
        for (int j = i; j < in_process_trans.length - 1; j++){
          in_process_trans[j] = in_process_trans[j + 1];
        }
        in_process_trans[in_process_trans.length - 1] = null;
        break;
      }
    }
    return new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(pathToRoot, Proof);  }

  public void MineCoin(DSCoin_Honest DSObj) {
    Transaction[] transactions = new Transaction[DSObj.bChain.tr_count];
    for (int i = 0; i < transactions.length - 1; i++){
      while (transactions[i] == null){
        try{
          Transaction transaction = DSObj.pendingTransactions.RemoveTransaction();
          boolean found = false;
          for (int j = 0; transactions[j] != null && j < transactions.length - 1; j++){
            if (transactions[j].coinID.equals(transaction.coinID)){
              found = true;
              break;
            }
          }
          if (!found && DSObj.bChain.lastBlock.checkTransaction(transaction)) transactions[i] = transaction;
        }catch(EmptyQueueException e){
          e.printStackTrace();
          return;
        }
      }
    }
    Transaction transaction = new Transaction();
    Long latestCoinID = Long.parseLong(DSObj.latestCoinID);
    latestCoinID++;
    DSObj.latestCoinID = String.valueOf(latestCoinID);
    transaction.coinID = DSObj.latestCoinID;
    transaction.Destination = this;
    transactions[transactions.length - 1] = transaction;
    TransactionBlock tB = new TransactionBlock(transactions);
    DSObj.bChain.InsertBlock_Honest(tB);
    mycoins.add(new Pair<String, TransactionBlock>(DSObj.latestCoinID, tB));
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
    Transaction[] transactions = new Transaction[DSObj.bChain.tr_count];
    for (int i = 0; i < transactions.length - 1; i++){
      while (transactions[i] == null){
        try{
          Transaction transaction = DSObj.pendingTransactions.RemoveTransaction();
          boolean found = false;
          for (int j = 0; transactions[j] != null && j < transactions.length - 1; j++){
            if (transactions[j].coinID.equals(transaction.coinID)){
              found = true;
              break;
            }
          }
          boolean valid = false;
          for (int j = 0; j < transaction.coinsrc_block.trarray.length; j++){
            if (transaction.coinID.equals(transaction.coinsrc_block.trarray[j].coinID) && transaction.Source.UID.equals(transaction.coinsrc_block.trarray[j].Destination.UID)){
              valid = true;
              break;
            }
          }
          if (!found && valid) transactions[i] = transaction;
        }catch(EmptyQueueException e){
          e.printStackTrace();
          return;
        }
      }
    }
    Transaction transaction = new Transaction();
    Long latestCoinID = Long.parseLong(DSObj.latestCoinID);
    latestCoinID++;
    DSObj.latestCoinID = String.valueOf(latestCoinID);
    transaction.coinID = DSObj.latestCoinID;
    transaction.Destination = this;
    transactions[transactions.length - 1] = transaction;
    TransactionBlock tB = new TransactionBlock(transactions);
    DSObj.bChain.InsertBlock_Malicious(tB);
    mycoins.add(new Pair<String, TransactionBlock>(DSObj.latestCoinID, tB));
  }  
}