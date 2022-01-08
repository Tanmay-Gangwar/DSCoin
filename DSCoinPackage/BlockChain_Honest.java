package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;
  public CRF crf_4 = new CRF(4);
  public CRF crf = new CRF(64);

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    String previous = start_string;
    if (lastBlock != null) previous = lastBlock.dgst;
    long nonce = 1000000000L;
    do{
      nonce++;
    }while (!crf_4.Fn(previous + "#" + newBlock.trsummary + "#" + String.valueOf(nonce)).equals("0000"));
    newBlock.nonce = String.valueOf(nonce);
    newBlock.dgst = crf.Fn(previous + "#" + newBlock.trsummary + "#" + newBlock.nonce);
    newBlock.previous = lastBlock;
    lastBlock = newBlock;
  }
}