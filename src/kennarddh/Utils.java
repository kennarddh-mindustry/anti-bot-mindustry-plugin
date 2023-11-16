package kennarddh;

import arc.util.Log;

import java.util.Arrays;

public class Utils {
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

    public static boolean[] intToBooleanArray(int data) {
        boolean[] bits = new boolean[32];

        for (int i = 0; i < 32; i++) {
            bits[i] = ((data & (1 << (31 - i))) != 0);
        }

        return bits;
    }

    public static int cidrMaskToSubnetMask(int cidrMask) {
        int subnetMask = 0xffffffff;
        subnetMask <<= (32 - cidrMask);

        return subnetMask;
    }

    public static void printBooleanArray(boolean[] bits) {
        StringBuilder output = new StringBuilder();

        for (boolean bit : bits) {
            output.append(bit ? "1" : "0");
        }

        Log.info(output);
    }

    public static void reverseBooleanArray(boolean[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            boolean temp = arr[i];

            arr[i] = arr[arr.length - i - 1];

            arr[arr.length - i - 1] = temp;
        }
    }
}
