package kennarddh;

public class SubnetTrieNode {
    /**
     * Is this an inserted node
     */
    public boolean value;

    /**
     * Bit 0
     */
    public SubnetTrieNode leftNode;

    /**
     * Bit 1
     */
    public SubnetTrieNode rightNode;

    public SubnetTrieNode(boolean value, SubnetTrieNode leftNode, SubnetTrieNode rightNode) {
        this.value = value;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }
}
