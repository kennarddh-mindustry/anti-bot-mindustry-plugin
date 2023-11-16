package kennarddh;

import arc.util.Log;
import mindustry.mod.Plugin;

@SuppressWarnings("unused")
public class AntiBot extends Plugin {
    private IPBlacklist ipBlacklist;

    @Override
    public void init() {
        Log.info("[AntiBot] Loaded");

        ipBlacklist = new IPBlacklist();
    }
}