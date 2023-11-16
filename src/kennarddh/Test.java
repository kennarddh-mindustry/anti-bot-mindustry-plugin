package kennarddh;

import java.net.UnknownHostException;

public class Test {
    public static void main(String[] args) {
        try {
            new IPList().addCidrRange("198.51.100.14/24");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
