package kennarddh;

import arc.util.Log;
import arc.util.serialization.Jval;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IPBlacklist {
    public static final String awsIPsURL = "https://ip-ranges.amazonaws.com/ip-ranges.json";
    public static final String gitHubIPsURL = "https://api.github.com/meta";
    public static final String googleCloudIPsURL = "https://www.gstatic.com/ipranges/cloud.json";

    // Change number: 280. Doesn't update automatically like other cloud providers. Needs to update this url from https://www.microsoft.com/en-us/download/details.aspx?id=56519.
    public static final String azureIpsURL = "https://download.microsoft.com/download/7/1/D/71D86715-5596-4529-9B13-DA13A5DE5B63/ServiceTags_Public_20231113.json";

    private final SubnetTrie subnetTrie = new SubnetTrie();

    public IPBlacklist() {
        addAWSIPs();
        addGitHubIPs();
        addGoogleCloudIPs();
        addAzureIPs();
    }

    private void addAzureIPs() {
        try {
            String azureIPsOutput = readStringFromURL(azureIpsURL);

            Jval json = Jval.read(azureIPsOutput);

            json.get("values").asArray().each(element -> {
                element.get("properties").asObject().get("addressPrefixes").asArray().each(ipElement -> {
                    String ip = ipElement.asString();

                    // Ignore IPv6
                    if (ip.contains(":")) return;

                    String ipString = ip.split("/")[0];
                    String maskString = ip.split("/")[1];

                    int maskInt = Integer.parseInt(maskString);

                    int ipInt = Utils.ipIntArrayToInt(Utils.ipStringToIntArray(ipString));
                    int subnetMask = Utils.cidrMaskToSubnetMask(maskInt);

                    subnetTrie.addIP(ipInt, subnetMask);
                });
            });

            Log.info("Added Azure IPs to blacklist.");
        } catch (IOException e) {
            Log.info("Failed to fetch Azure IPs");
            throw new RuntimeException(e);
        }
    }

    private void addGoogleCloudIPs() {
        try {
            String googleCloudIPsOutput = readStringFromURL(googleCloudIPsURL);

            Jval json = Jval.read(googleCloudIPsOutput);

            json.get("prefixes").asArray().each(element -> {
                if (!element.has("ipv4Prefix")) return;

                String ip = element.getString("ipv4Prefix");

                String ipString = ip.split("/")[0];
                String maskString = ip.split("/")[1];

                int maskInt = Integer.parseInt(maskString);

                int ipInt = Utils.ipIntArrayToInt(Utils.ipStringToIntArray(ipString));
                int subnetMask = Utils.cidrMaskToSubnetMask(maskInt);

                subnetTrie.addIP(ipInt, subnetMask);
            });

            Log.info("Added Google Cloud IPs to blacklist.");
        } catch (IOException e) {
            Log.info("Failed to fetch Google Cloud IPs");
            throw new RuntimeException(e);
        }
    }

    private void addGitHubIPs() {
        try {
            String gitHubIPsOutput = readStringFromURL(gitHubIPsURL);

            Jval json = Jval.read(gitHubIPsOutput);

            json.get("actions").asArray().each(element -> {
                String ip = element.asString();

                // Ignore IPv6
                if (ip.contains(":")) return;

                String ipString = ip.split("/")[0];
                String maskString = ip.split("/")[1];

                int maskInt = Integer.parseInt(maskString);

                int ipInt = Utils.ipIntArrayToInt(Utils.ipStringToIntArray(ipString));
                int subnetMask = Utils.cidrMaskToSubnetMask(maskInt);

                subnetTrie.addIP(ipInt, subnetMask);
            });

            Log.info("Added GitHub IPs to blacklist.");
        } catch (IOException e) {
            Log.info("Failed to fetch GitHub IPs");
            throw new RuntimeException(e);
        }
    }

    private void addAWSIPs() {
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
