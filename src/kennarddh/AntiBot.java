package kennarddh;

import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.mod.Plugin;

@SuppressWarnings("unused")
public class AntiBot extends Plugin {
    private IPBlacklist ipBlacklist;

    @Override
    public void init() {
        Log.info("[AntiBot] Loaded");

//        SubnetTrie subnetTrie = new SubnetTrie();
//
//        subnetTrie.addIP(Utils.ipIntArrayToInt(Utils.ipStringToIntArray("255.255.255.255")), Utils.cidrMaskToSubnetMask(24));
//
//        Log.info(subnetTrie.contains(Utils.ipIntArrayToInt(Utils.ipStringToIntArray("255.255.255.1"))));

        ipBlacklist = new IPBlacklist();
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("is_bot_ip", "<ip>", "Check is bot ip", arg -> {
            Log.info(ipBlacklist.contains(arg[0]));
        });
    }
}