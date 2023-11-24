package kennarddh;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.gen.KickCallPacket;
import mindustry.mod.Plugin;
import mindustry.net.NetConnection;

@SuppressWarnings("unused")
public class AntiBot extends Plugin {
    private final IPBlacklist ipBlacklist = new IPBlacklist();

    @Override
    public void init() {
        Log.info("[AntiBot] Loaded");

        Events.on(EventType.ConnectionEvent.class, con -> {
            if (ipBlacklist.contains(con.connection.address)) {
                kickConnectionWithoutLogging(con.connection, "Your IP is detected as bot. If you can read this message I can assume you are not a bot.");
            }
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("is_bot_ip", "<ip>", "Check is bot ip", arg -> {
            Log.info(ipBlacklist.contains(arg[0]));
        });
    }

    /**
     * Now you can Kick a connection with a reason but without logging it in the console. No more spam.
     **/
    private static void kickConnectionWithoutLogging(NetConnection connection, String reason) {
        KickCallPacket packet = new KickCallPacket();

        packet.reason = reason;

        connection.send(packet, true);
        connection.close();
    }
}