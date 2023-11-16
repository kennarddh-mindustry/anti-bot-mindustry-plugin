package kennarddh;

public class SubnetTrie {
    public SubnetTrieNode root;

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

        SubnetTrieNode currentNode = root;

        for (int i = 0; i < 32; i++) {
            boolean ipBit = ipBits[i];
            boolean subnetBit = subnetBits[i];
            boolean isValue = !subnetBit;

            // If is subnet already 0, or it's the end of the ip (this will happen if the subnet mask is 32)
            if (isValue || i == 31) {
                currentNode.value = true;

                break;
            }

            if (ipBit) {
                if (currentNode.rightNode == null) {
                    currentNode.rightNode = new SubnetTrieNode(isValue, null, null);
                }

                currentNode = currentNode.rightNode;
            } else {
                if (currentNode.leftNode == null) {
                    currentNode.leftNode = new SubnetTrieNode(isValue, null, null);
                }

                currentNode = currentNode.leftNode;
            }
        }
    }

    /**
     * Check is the ip is in the trie
     *
     * @param ip 32 bit int ip representation
     * @return True if the ip is in the trie
     */
    public boolean contains(int ip) {
        boolean[] ipBits = Utils.intToBooleanArray(ip);

        Utils.printBooleanArray(ipBits);

        SubnetTrieNode currentNode = root;

        for (int i = 0; i < 32; i++) {
            boolean ipBit = ipBits[i];

            if (currentNode.value) return true;

            if (ipBit) {
                if (currentNode.rightNode == null) return false;

                currentNode = currentNode.rightNode;
            } else {
                if (currentNode.leftNode == null) return false;

                currentNode = currentNode.leftNode;
            }
        }

        return false;
    }
}
