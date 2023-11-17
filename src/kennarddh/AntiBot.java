package kennarddh;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.mod.Plugin;

@SuppressWarnings("unused")
public class AntiBot extends Plugin {
    private final IPBlacklist ipBlacklist = new IPBlacklist();

    private static final long oneYearMS = 1000L * 60 * 60 * 24 * 365;

    @Override
    public void init() {
        Log.info("[AntiBot] Loaded");

        Events.on(EventType.ConnectionEvent.class, con -> {
            if (ipBlacklist.contains(con.connection.address)) {
                con.connection.kick("Your IP is detected as bot. If you can read this message I can assume you are not a bot.", oneYearMS);
            }
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("is_bot_ip", "<ip>", "Check is bot ip", arg -> {
            Log.info(ipBlacklist.contains(arg[0]));
        });
    }
}