package kennarddh;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class IPList {
    private BloomFilter<Integer> bloomFilter = BloomFilter.create(
            Funnels.integerFunnel(),
            5000000,
            0.01);

    public void addCidrRange(String cidrIpAddress) throws UnknownHostException {
        String ip = cidrIpAddress.split("/")[0];
        String maskString = cidrIpAddress.split("/")[1];

        int[] parts = Arrays.stream(ip.split("\\.")).mapToInt(Integer::parseInt).toArray();

        int ipInt = (parts[0] << 24) | (parts[1] << 16) | (parts[2] << 8) | parts[3];

        int maskbits = Integer.parseInt(maskString);
        int mask = 0xffffffff;
        mask <<= (32 - maskbits);

        int ipStart = ipInt & mask;
        int ipEnd = ipInt | (~mask);

        System.out.println(Arrays.toString(parts));
        System.out.println(Arrays.toString(intToIPIntArray(ipStart)));
        System.out.println(Arrays.toString(intToIPIntArray(ipEnd)));

        System.out.println("------------");

        for (int ipIter = ipStart; ipIter <= ipEnd; ipIter++) {
            System.out.println(Arrays.toString(intToIPIntArray(ipIter)));
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

    public boolean isIpInBloomFilter(String ipAddress) throws Exception {
        InetAddress ip = InetAddress.getByName(ipAddress);
        int ipAddressInt = ip.getAddress()[0] << 24 | ip.getAddress()[1] << 16 | ip.getAddress()[2] << 8 | ip.getAddress()[3];

        return bloomFilter.mightContain(ipAddressInt);
    }
}
