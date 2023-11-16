package kennarddh;

import arc.util.Log;
import arc.util.serialization.Jval;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IPBlacklist {
    public static final String awsIPsURL = "https://ip-ranges.amazonaws.com/ip-ranges.json";
    private final IPList ipList = new IPList();


    public IPBlacklist() {
        try {
            String awsIPsOutput = readStringFromURL(awsIPsURL);

            Jval json = Jval.read(awsIPsOutput);

            json.get("prefixes").asArray().each(element -> {
                String service = element.getString("service");

                if (service.equals("AMAZON")) {
                    ipList.addCidrRange(element.getString("ip_prefix"));
                }
            });

            Log.info("Added AWS IPs to blacklist.");

            Log.info("Size");
            Log.info(ipList.getSize());
        } catch (IOException e) {
            Log.info("Failed to fetch AWS IPs");
            throw new RuntimeException(e);
        }
    }

    public boolean contains(String ip) {
        return ipList.contains(ip);
    }

    public static String readStringFromURL(String requestURL) throws IOException {
        URL u = new URL(requestURL);

        try (InputStream in = u.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
