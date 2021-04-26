public class Block&lt;T extends Tx&gt; {
public long timeStamp;
private int index;
private List&lt;T&gt; transactions = new ArrayList&lt;T&gt;();
private String hash;
private String previousHash;
private String merkleRoot;
private String nonce = "0000";
public Map&lt;String,T&gt; map = new HashMap&lt;String,T&gt;();
public void computeHash() {

     Gson parser = new Gson();

     String serializedData = parser.toJson(transactions);  

     setHash(SHA256.generateHash(timeStamp + index + merkleRoot + serializedData + nonce + previousHash));

     }
public class SimpleBlockchain<T extends Tx> {
public static final int BLOCK_SIZE = 10;
public List<Block<T>> chain = new ArrayList<Block<T>>();
public SimpleBlockchain() {
chain.add(newBlock());
}
public Block<T> newBlock() {
int count = chain.size();
String previousHash = "root";
if (count > 0)
previousHash = blockChainHash();
Block<T> block = new Block<T>();
block.setTimeStamp(System.currentTimeMillis());
block.setIndex(count);
block.setPreviousHash(previousHash);
return block;
}
public void addAndValidateBlock(Block<T> block) {
Block<T> current = block;
for (int i = chain.size() - 1; i >= 0; i--) {
Block<T> b = chain.get(i);
if (b.getHash().equals(current.getPreviousHash())) {
current = b;
} else {
throw new RuntimeException("Block Invalid");
}
}
this.chain.add(block);
}
public boolean validate() {
String previousHash = null;
for (Block<T> block : chain) {
String currentHash = block.getHash();
if (!currentHash.equals(previousHash)) {
return false;
}
previousHash = currentHash;
}
return true;
}
public List<String> merkleTree() {
ArrayList<String> tree = new ArrayList<>();
for (T t : transactions) {
tree.add(t.hash());
}
int levelOffset = 0; 
for (int levelSize = transactions.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
for (int left = 0; left < levelSize; left += 2) {
int right = Math.min(left + 1, levelSize - 1);
String tleft = tree.get(levelOffset + left);
String tright = tree.get(levelOffset + right);
tree.add(SHA256.generateHash(tleft + tright));
}
levelOffset += levelSize;
}
return tree;
}
@Test
public void merkleTreeTest() {
SimpleBlockchain<Transaction> chain1 = new SimpleBlockchain<Transaction>();
chain1.add(new Transaction("A")).add(new Transaction("B")).add(new Transaction("C")).add(new Transaction("D"));
Block<Transaction> block = chain1.getHead();
System.out.println("Merkle Hash tree :" + block.merkleTree());
Transaction tx = block.getTransactions().get(0);
block.transasctionsValid();
assertTrue(block.transasctionsValid());
tx.setValue("Z");
assertFalse(block.transasctionsValid());
}
private String proofOfWork(Block block) {
String nonceKey = block.getNonce();
long nonce = 0;
boolean nonceFound = false;
String nonceHash = "";
Gson parser = new Gson();
String serializedData = parser.toJson(transactionPool);
String message = block.getTimeStamp() + block.getIndex() + block.getMerkleRoot() + serializedData
+ block.getPreviousHash();
while (!nonceFound) {
nonceHash = SHA256.generateHash(message + nonce);
nonceFound = nonceHash.substring(0, nonceKey.length()).equals(nonceKey);
nonce++;
}
return nonceHash;
}



