package kennarddh;

import arc.util.Log;
import arc.util.serialization.Jval;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IPBlacklist {
    public static final String awsIPsURL = "https://ip-ranges.amazonaws.com/ip-ranges.json";
    private final SubnetTrie subnetTrie = new SubnetTrie();


    public IPBlacklist() {
        try {
            String awsIPsOutput = readStringFromURL(awsIPsURL);

            Jval json = Jval.read(awsIPsOutput);

            json.get("prefixes").asArray().each(element -> {
                String service = element.getString("service");

                if (service.equals("AMAZON")) {
                    String ipPrefix = element.getString("ip_prefix");

                    String ipString = ipPrefix.split("/")[0];
                    String maskString = ipPrefix.split("/")[1];
                    int maskInt = Integer.parseInt(maskString);

                    int ip = Utils.ipIntArrayToInt(Utils.ipStringToIntArray(ipString));
                    int subnetMask = Utils.cidrMaskToSubnetMask(maskInt);

                    subnetTrie.addIP(ip, subnetMask);
                }
            });

            Log.info("Added AWS IPs to blacklist.");
        } catch (IOException e) {
            Log.info("Failed to fetch AWS IPs");
            throw new RuntimeException(e);
        }
    }

    public boolean contains(String ipString) {
        int ip = Utils.ipIntArrayToInt(Utils.ipStringToIntArray(ipString));
        
        return subnetTrie.contains(ip);
    }

    public static String readStringFromURL(String requestURL) throws IOException {
        URL u = new URL(requestURL);

        try (InputStream in = u.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
