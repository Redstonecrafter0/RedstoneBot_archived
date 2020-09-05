package net.redstonecraft.redstonebot.listeners;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Leveling extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        if (event.getMessage().getContentDisplay().startsWith(Main.commandPrefix)) {
            return;
        }
        if (Objects.requireNonNull(event.getMember()).getRoles().contains(Discord.INSTANCE.getManager().getRoleById((String) Main.config.get("verifiedRole")))) {
            ResultSet rs = Main.sql.query("SELECT * FROM leveling WHERE dcId = '" + event.getMember().getId() + "'");
            try {
                if (rs.isClosed()) {
                    Main.sql.update("INSERT INTO leveling VALUES ('" + event.getMember().getId() + "', '1')");
                } else {
                    long x = (long) (1 + (Math.random() * 4));
                    Main.sql.update("UPDATE leveling SET xp = '" + (Long.parseLong(rs.getString("xp")) + x) + "' WHERE dcId = '" +  event.getMember().getId() + "'");
                }
            } catch (SQLException ignored) {
            }
        }
    }
}
