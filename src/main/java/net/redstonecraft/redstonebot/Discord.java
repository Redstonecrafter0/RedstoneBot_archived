package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.redstonecraft.redstonebot.listeners.*;

import javax.security.auth.login.LoginException;

public class Discord {
    private JDA jda;

    private final Autochannel autochannel;

    public static Discord INSTANCE;

    public Discord(String clientId, String botToken) {
        String url = "https://discord.com/api/oauth2/authorize?client_id=" + clientId + "&scope=bot&permissions=8";
        Main.getLogger().info("Connect your Discord Bot to your server by visiting the page: " + url);

        JDABuilder builder = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);

        CommandHandler commandHandler = new CommandHandler();
        autochannel = new Autochannel();
        Verify verify = new Verify();
        DiscordChat discordChat = new DiscordChat();
        Leveling leveling = new Leveling();
        ReactionRolesListener reactionRolesListener = new ReactionRolesListener();
        ChatFilter chatFilter = new ChatFilter();
        builder.addEventListeners(
                commandHandler,
                autochannel,
                verify,
                discordChat,
                leveling,
                reactionRolesListener,
                chatFilter
        );

        builder.setActivity(Activity.playing("mit Redstone"));
        builder.setStatus(OnlineStatus.ONLINE);

        try {
            jda = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        INSTANCE = this;

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                autochannel.onEnable();
                Main.getLogger().info("Autochannel enabled");
            } catch (InterruptedException ignored) {
                System.exit(0);
            }
        }).start();
    }

    public JDA getJda() {
        return jda;
    }
}
