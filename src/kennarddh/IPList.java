package kennarddh;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

public class IPList {
    private final BloomFilter<Integer> bloomFilter = BloomFilter.create(
            Funnels.integerFunnel(),
            5000000,
            0.01);

    private final HashSet<Integer> hashSet = new HashSet<>();

    public void addCidrRange(String cidrIpAddress) throws UnknownHostException {
        String ip = cidrIpAddress.split("/")[0];
        String maskString = cidrIpAddress.split("/")[1];

        int ipInt = ipIntArrayToInt(ipStringToIntArray(ip));

        int maskBits = Integer.parseInt(maskString);
        int mask = 0xffffffff;
        mask <<= (32 - maskBits);

        int ipStart = ipInt & mask;
        int ipEnd = ipInt | (~mask);

        for (int ipIter = ipStart; ipIter <= ipEnd; ipIter++) {
            bloomFilter.put(ipIter);
            hashSet.add(ipIter);
        }
    }

    public static int[] intToIPIntArray(int ip) {
        return new int[]{
                (ip >> 24 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 8 & 0xff),
                (ip & 0xff)
        };
    }

    public static int ipIntArrayToInt(int[] intArray) {
        return (intArray[0] << 24) | (intArray[1] << 16) | (intArray[2] << 8) | intArray[3];
    }

    public static int[] ipStringToIntArray(String ip) {
        return Arrays.stream(ip.split("\\.")).mapToInt(Integer::parseInt).toArray();
    }

    public boolean contains(String ip) {
        int ipInt = ipIntArrayToInt(ipStringToIntArray(ip));

        boolean bloomFilterContain = bloomFilter.mightContain(ipInt);

        if (!bloomFilterContain) {
            return false;
        }

        return hashSet.contains(ipInt);
    }

    public int getSize() {
        return hashSet.size();
    }
}
