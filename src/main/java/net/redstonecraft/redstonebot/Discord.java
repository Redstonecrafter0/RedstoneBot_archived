package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.redstonecraft.redstonebot.listeners.Autochannel;

import javax.security.auth.login.LoginException;

public class Discord {
    private final DefaultShardManagerBuilder builder;
    private ShardManager manager;

    private final CommandHandler commandHandler;
    private final Autochannel autochannel;

    public static Discord INSTANCE;

    public Discord(String clientId, String botToken) {
        String url = "https://discord.com/api/oauth2/authorize?client_id=" + clientId + "&scope=bot&permissions=8";
        Main.getLogger().info("Connect your Discord Bot to your server by visiting the page: " + url);

        builder = new DefaultShardManagerBuilder();
        builder.setToken(botToken);

        commandHandler = new CommandHandler();
        autochannel = new Autochannel();
        builder.addEventListeners(commandHandler, autochannel);

        builder.setActivity(Activity.playing("Redstone"));
        builder.setStatus(OnlineStatus.ONLINE);

        try {
            manager = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        autochannel.onEnable();

        INSTANCE = this;
    }

    public ShardManager getManager() {
        return manager;
    }

    public Autochannel getAutochannel() {
        return autochannel;
    }
}
