package kennarddh;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.HashSet;

public class IPList {
    private final BloomFilter<Integer> bloomFilter = BloomFilter.create(
            Funnels.integerFunnel(),
            5000000,
            0.01);

    private final Multimap<String, Integer> trie;

    private final HashSet<Integer> hashSet = new HashSet<>();

    private int size = 0;

    public IPList() {
        trie = TreeMultimap.create();
    }

    public void addCidrRange(String cidrIpAddress) {
        String ip = cidrIpAddress.split("/")[0];
        String maskString = cidrIpAddress.split("/")[1];

        int ipInt = Utils.ipIntArrayToInt(Utils.ipStringToIntArray(ip));

        int maskBits = Integer.parseInt(maskString);
        int mask = 0xffffffff;
        mask <<= (32 - maskBits);

        int ipStart = ipInt & mask;
        int ipEnd = ipInt | (~mask);

        for (int ipIter = ipStart; ipIter <= ipEnd; ipIter++) {
            bloomFilter.put(ipIter);
//            hashSet.add(ipIter);
            size += 1;
        }
    }


    public boolean contains(String ip) {
        int ipInt = Utils.ipIntArrayToInt(Utils.ipStringToIntArray(ip));

        boolean bloomFilterContain = bloomFilter.mightContain(ipInt);

        if (!bloomFilterContain) {
            return false;
        }

        return hashSet.contains(ipInt);
    }

    public int getSize() {
        return size;
    }
}
