package DSCoinPackage;

import HelperClasses.*;
import java.util.*;

public class BlockChain_Malicious {

  public int tr_count, index_of_longest_chain;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;
  public CRF crf_4 = new CRF(4);
  public CRF crf = new CRF(64);

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    for (int i = 0; i < tB.trarray.length; i++){
      if (!tB.checkTransaction(tB.trarray[i])) return false;
    }
    MerkleTree tree = new MerkleTree();
    tree.Build(tB.trarray);
    if (!tree.rootnode.val.equals(tB.trsummary)) return false;
    String previous = start_string;
    if (tB.previous != null) previous = tB.previous.dgst;
    CRF crf = new CRF(64);
    String dgst = crf.Fn(previous + "#" + tB.trsummary + "#" + tB.nonce);
    if (!dgst.substring(0, 4).equals("0000")) return false;
    return tB.dgst.equals(dgst);
  }

  public TransactionBlock FindLongestValidChain () {
    int longest_streak = 0;
    TransactionBlock longest = null;
    index_of_longest_chain = -1;
    for (int i = 0; i < lastBlocksList.length; i++){
      if (lastBlocksList[i] != null){
        Pair<Integer, TransactionBlock> temp = FindLongestValidChain(lastBlocksList[i]);
        if (temp.first > longest_streak){
          longest_streak = temp.first;
          longest = temp.second;
          index_of_longest_chain = i;
        }
      }
    }
    return longest;
  }

  public Pair<Integer, TransactionBlock> FindLongestValidChain(TransactionBlock block){
    List<TransactionBlock> blocks = new ArrayList<TransactionBlock>();
    while (block != null){
      blocks.add(block);
      block = block.previous;
    }
    Integer longest_streak = 0, curr_streak = 0;
    TransactionBlock longest = null;
    for (int i = blocks.size() - 1; i >= 0; i--){
      if (checkTransactionBlock(blocks.get(i))) curr_streak++;
      else curr_streak = 0;
      if (curr_streak > longest_streak){
        longest_streak = curr_streak;
        longest = blocks.get(i);
      }
    }
    return new Pair<Integer, TransactionBlock>(longest_streak, longest);
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
    TransactionBlock lastBlock = FindLongestValidChain();
    String previous;
    if (lastBlock == null) previous = start_string;
    else previous = lastBlock.dgst;
    long nonce = 1000000000L;
    do{
      nonce++;
    }while (!crf_4.Fn(previous + "#" + newBlock.trsummary + "#" + String.valueOf(nonce)).equals("0000"));
    newBlock.nonce = String.valueOf(nonce);
    newBlock.dgst = crf.Fn(previous + "#" + newBlock.trsummary + "#" + newBlock.nonce);
    newBlock.previous = lastBlock;
    if (index_of_longest_chain == -1) lastBlocksList[0] = newBlock;
    else if (lastBlocksList[index_of_longest_chain].dgst.equals(lastBlock.dgst)) lastBlocksList[index_of_longest_chain] = newBlock;
    else {
      for (int i = 0; i < lastBlocksList.length; i++){
        if (lastBlocksList[i] == null){
          lastBlocksList[i] = newBlock;
          break;
        }
      }
    }
  }
}