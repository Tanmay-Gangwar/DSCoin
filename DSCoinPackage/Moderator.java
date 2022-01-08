package DSCoinPackage;
import HelperClasses.Pair;

public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
    long latestCoinID = Long.parseLong(DSObj.latestCoinID);
    Members moderator = new Members();
    moderator.UID = "Moderator";
    Transaction[] transactions = new Transaction[DSObj.bChain.tr_count];
    for (int i = 0, j = 0; i < coinCount; i++){
      latestCoinID++;
      DSObj.latestCoinID = String.valueOf(latestCoinID);
      transactions[j] = new Transaction();
      transactions[j].coinID = DSObj.latestCoinID;
      transactions[j].Source = moderator;
      transactions[j].Destination = DSObj.memberlist[i % DSObj.memberlist.length];
      j++;
      if (j == DSObj.bChain.tr_count){
        j = 0;
        TransactionBlock block = new TransactionBlock(transactions);
        for (int k = 0; k < transactions.length; k++){
          transactions[k].Destination.mycoins.add(new Pair<String, TransactionBlock>(transactions[k].coinID, block));
        }
        DSObj.bChain.InsertBlock_Honest(block);
      }
    }
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
    long latestCoinID = Long.parseLong(DSObj.latestCoinID);
    Members moderator = new Members();
    moderator.UID = "Moderator";
    Transaction[] transactions = new Transaction[DSObj.bChain.tr_count];
    for (int i = 0, j = 0; i < coinCount; i++){
      latestCoinID++;
      DSObj.latestCoinID = String.valueOf(latestCoinID);
      transactions[j] = new Transaction();
      transactions[j].coinID = DSObj.latestCoinID;
      transactions[j].Source = moderator;
      transactions[j].Destination = DSObj.memberlist[i % DSObj.memberlist.length];
      j++;
      if (j == DSObj.bChain.tr_count){
        j = 0;
        TransactionBlock block = new TransactionBlock(transactions);
        for (int k = 0; k < transactions.length; k++){
          transactions[k].Destination.mycoins.add(new Pair<String, TransactionBlock>(transactions[k].coinID, block));
        }
        DSObj.bChain.InsertBlock_Malicious(block);
      }
    }
  }
}