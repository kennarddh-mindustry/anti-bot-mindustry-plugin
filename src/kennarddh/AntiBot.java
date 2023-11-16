package kennarddh;

import arc.util.Log;
import mindustry.mod.Plugin;

@SuppressWarnings("unused")
public class AntiBot extends Plugin {
//    private IPBlacklist ipBlacklist;

    @Override
    public void init() {
        Log.info("[AntiBot] Loaded");

        SubnetTrie subnetTrie = new SubnetTrie();

        subnetTrie.addIP(Utils.ipIntArrayToInt(Utils.ipStringToIntArray("255.255.255.255")), Utils.cidrMaskToSubnetMask(32));

//        ipBlacklist = new IPBlacklist();
    }
}