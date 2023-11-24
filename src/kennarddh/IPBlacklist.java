package kennarddh;

import arc.util.Log;
import arc.util.serialization.Jval;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

public class IPBlacklist {
    public static final String awsIPsURL = "https://ip-ranges.amazonaws.com/ip-ranges.json";
    public static final String gitHubIPsURL = "https://api.github.com/meta";
    public static final String googleCloudIPsURL = "https://www.gstatic.com/ipranges/cloud.json";

    // Change number: 280. Doesn't update automatically like other cloud providers. Needs to update this url from https://www.microsoft.com/en-us/download/details.aspx?id=56519.
    public static final String azureIPsURL = "https://download.microsoft.com/download/7/1/D/71D86715-5596-4529-9B13-DA13A5DE5B63/ServiceTags_Public_20231113.json";

    public static final String digitalOceanIPsURL = "https://digitalocean.com/geo/google.csv";
    public static final String vpnIPsURL = "https://raw.githubusercontent.com/X4BNet/lists_vpn/main/output/vpn/ipv4.txt";
    public static final String linodeIPsURL = "https://geoip.linode.com/";

    private final SubnetTrie subnetTrie = new SubnetTrie();

    public IPBlacklist() {
        addAWSIPs();
        addGitHubIPs();
        addGoogleCloudIPs();
        addAzureIPs();
        addDigitalOceanIPs();
        addVPNIPs();
        addLinodeIPs();
    }

    private void addLinodeIPs() {
        try {
            String linodeIPsOutput = Utils.readStringFromURL(linodeIPsURL);

            try (CSVReader csvReader = new CSVReader(new StringReader(linodeIPsOutput))) {
                for (String[] line : csvReader.readAll()) {
                    if (line[0].startsWith("#")) continue;

                    String ip = line[0];

                    // Ignore IPv6
                    if (ip.contains(":")) continue;

                    subnetTrie.addIP(ip);
                }
            } catch (CsvException e) {
                throw new RuntimeException(e);
            } finally {
                Log.info("[AntiBot] Added Linode IPs to blacklist.");
            }
        } catch (IOException e) {
            Log.info("[AntiBot] Failed to fetch Linode IPs");
            throw new RuntimeException(e);
        }
    }

    private void addVPNIPs() {
        try {
            String vpnIPsOutput = Utils.readStringFromURL(vpnIPsURL);

            try (Scanner scanner = new Scanner(vpnIPsOutput)) {
                while (scanner.hasNextLine()) {
                    String ip = scanner.nextLine();

                    // Ignore IPv6
                    if (ip.contains(":")) continue;

                    subnetTrie.addIP(ip);
                }
            } finally {
                Log.info("[AntiBot] Added VPN IPs to blacklist.");
            }
        } catch (IOException e) {
            Log.info("[AntiBot] Failed to fetch VPN IPs");
            throw new RuntimeException(e);
        }
    }

    private void addDigitalOceanIPs() {
        try {
            String digitalOceanIPsOutput = Utils.readStringFromURL(digitalOceanIPsURL);

            try (CSVReader csvReader = new CSVReader(new StringReader(digitalOceanIPsOutput))) {
                for (String[] line : csvReader.readAll()) {
                    String ip = line[0];

                    // Ignore IPv6
                    if (ip.contains(":")) continue;

                    subnetTrie.addIP(ip);
                }
            } catch (CsvException e) {
                throw new RuntimeException(e);
            } finally {
                Log.info("[AntiBot] Added Digital Ocean IPs to blacklist.");
            }
        } catch (IOException e) {
            Log.info("[AntiBot] Failed to fetch Digital Ocean IPs");
            throw new RuntimeException(e);
        }
    }

    private void addAzureIPs() {
        try {
            String azureIPsOutput = Utils.readStringFromURL(azureIPsURL);

            Jval json = Jval.read(azureIPsOutput);

            json.get("values").asArray().each(element -> {
                element.get("properties").asObject().get("addressPrefixes").asArray().each(ipElement -> {
                    String ip = ipElement.asString();

                    // Ignore IPv6
                    if (ip.contains(":")) return;

                    subnetTrie.addIP(ip);
                });
            });

            Log.info("[AntiBot] Added Azure IPs to blacklist.");
        } catch (IOException e) {
            Log.info("[AntiBot] Failed to fetch Azure IPs");
            throw new RuntimeException(e);
        }
    }

    private void addGoogleCloudIPs() {
        try {
            String googleCloudIPsOutput = Utils.readStringFromURL(googleCloudIPsURL);

            Jval json = Jval.read(googleCloudIPsOutput);

            json.get("prefixes").asArray().each(element -> {
                if (!element.has("ipv4Prefix")) return;

                String ip = element.getString("ipv4Prefix");

                subnetTrie.addIP(ip);
            });

            Log.info("[AntiBot] Added Google Cloud IPs to blacklist.");
        } catch (IOException e) {
            Log.info("[AntiBot] Failed to fetch Google Cloud IPs");
            throw new RuntimeException(e);
        }
    }

    private void addGitHubIPs() {
        try {
            String gitHubIPsOutput = Utils.readStringFromURL(gitHubIPsURL);

            Jval json = Jval.read(gitHubIPsOutput);

            json.get("actions").asArray().each(element -> {
                String ip = element.asString();

                // Ignore IPv6
                if (ip.contains(":")) return;

                subnetTrie.addIP(ip);
            });

            Log.info("[AntiBot] Added GitHub IPs to blacklist.");
        } catch (IOException e) {
            Log.info("[AntiBot] Failed to fetch GitHub IPs");
            throw new RuntimeException(e);
        }
    }

    private void addAWSIPs() {
        try {
            String awsIPsOutput = Utils.readStringFromURL(awsIPsURL);

            Jval json = Jval.read(awsIPsOutput);

            json.get("prefixes").asArray().each(element -> {
                String service = element.getString("service");

                if (service.equals("AMAZON")) {
                    String ipPrefix = element.getString("ip_prefix");

                    subnetTrie.addIP(ipPrefix);
                }
            });

            Log.info("[AntiBot] Added AWS IPs to blacklist.");
        } catch (IOException e) {
            Log.info("[AntiBot] Failed to fetch AWS IPs");
            throw new RuntimeException(e);
        }
    }

    public boolean contains(String ipString) {
        int ip = Utils.ipIntArrayToInt(Utils.ipStringToIntArray(ipString));

        return subnetTrie.contains(ip);
    }
}
