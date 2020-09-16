package net.redstonecraft.redstonebot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.redstonebot.Main;

import java.sql.ResultSet;
import java.util.Objects;

public class ChatFilter extends ListenerAdapter {

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
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
            try {
                ResultSet rs = Main.sql.query("SELECT * FROM chatfilter");
                while (rs.next()) {
                    if (event.getMessage().getContentDisplay().contains(rs.getString("text"))) {
                        try {
                            String channelId = event.getChannel().getId();
                            Member member = event.getMember();
                            ResultSet perm = Main.sql.query("SELECT * FROM chatfilterpermission WHERE channelId = '" + channelId + "'");
                            if (!member.hasPermission(Permission.valueOf(perm.getString("permission")))) {
                                ResultSet role = Main.sql.query("SELECT * FROM chatfilterrole WHERE channelId = '" + channelId + "'");
                                if (!event.getGuild().getMembersWithRoles(event.getGuild().getRoleById(role.getString("roleId"))).contains(member)) {
                                    ResultSet tokens = Main.sql.query("SELECT * FROM chatfiltertokens WHERE dcId = '" + member.getId() + "'");
                                    if (tokens.isClosed()) {
                                        event.getMessage().delete().queue();
                                    } else {
                                        if (tokens.getInt("tokens") < 1) {
                                            event.getMessage().delete().queue();
                                            Main.sql.update("DELETE FROM chatfiltertokens WHERE dcId = '" + member.getId() + "'");
                                        } else if (tokens.getInt("tokens") == 1) {
                                            Main.sql.update("DELETE FROM chatfiltertokens WHERE dcId = '" + member.getId() + "'");
                                        } else {
                                            Main.sql.update("UPDATE chatfiltertokens SET tokens = '" + (tokens.getInt("tokens") - 1) + "' WHERE dcId = '" + member.getId() + "'");
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                            event.getMessage().delete().queue();
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
