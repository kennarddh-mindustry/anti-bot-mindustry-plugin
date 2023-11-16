package kennarddh;

import arc.util.Log;
import mindustry.mod.Plugin;

import java.net.UnknownHostException;

@SuppressWarnings("unused")
public class AntiBot extends Plugin {
    @Override
    public void init() {
        Log.info("[AntiBot] Loaded");

        try {
            new IPList().addCidrRange("198.51.100.14/24");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}