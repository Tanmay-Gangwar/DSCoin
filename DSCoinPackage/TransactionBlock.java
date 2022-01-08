package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
    trarray = new Transaction[t.length];
    for (int i = 0; i < t.length; i++) trarray[i] = t[i];
    Tree = new MerkleTree();
    Tree.Build(trarray);
    trsummary = Tree.rootnode.val;
  }

  public boolean checkTransaction (Transaction t) {
    if (t.coinsrc_block == null) return true;
    boolean found = false;
    for (int i = 0; i < t.coinsrc_block.trarray.length; i++){
      if (t.coinID.equals(t.coinsrc_block.trarray[i].coinID) && t.Source.UID.equals(t.coinsrc_block.trarray[i].Destination.UID)){
        found = true;
        break;
      }
    }
    if (!found) return false;
    TransactionBlock curr;
    if (dgst.equals(t.coinsrc_block.dgst)) return true;
    for (curr = previous; curr != null && !curr.dgst.equals(t.coinsrc_block.dgst); curr = curr.previous){
      for (int i = 0; i < curr.trarray.length; i++){
        if (t.coinID.equals(curr.trarray[i].coinID)) return false;
      }
    }
    return true;
  }
}