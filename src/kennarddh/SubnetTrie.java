package kennarddh;

public class SubnetTrie {
    private SubnetTrieNode root;

    public SubnetTrie() {
        root = new SubnetTrieNode(false, null, null);
    }

    /**
     * Add ip to the trie
     *
     * @param ip     32 bit int ip representation
     * @param subnet 32 bit int subnet mask representation
     */
    public void addIP(int ip, int subnet) {
        boolean[] ipBits = Utils.intToBooleanArray(ip);
        boolean[] subnetBits = Utils.intToBooleanArray(subnet);

        Utils.printBooleanArray(ipBits);
        Utils.printBooleanArray(subnetBits);
    }

}
