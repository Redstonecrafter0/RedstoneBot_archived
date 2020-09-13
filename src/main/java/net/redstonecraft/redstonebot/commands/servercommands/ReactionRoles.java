package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReactionRoles implements ServerCommand {

    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length > 0) {
                try {
                    switch (args[0]) {
                        case "list":
                            ResultSet rs = Main.sql.query("SELECT * FROM reactionroles");
                            int c = 0;
                            Guild guild = Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild")));
                            while (rs.next()) {
                                c++;
                                eb.addField(Objects.requireNonNull(Objects.requireNonNull(guild).getRoleById(rs.getString("roleId"))).getName(), Objects.requireNonNull(guild.getTextChannelById(rs.getString("channelId"))).getAsMention() + " " + Objects.requireNonNull(guild.getEmoteById(rs.getString("emoteId"))).getAsMention(), false);
                            }
                            if (c == 0) {
                                eb.setColor(Color.decode("#FF0000"));
                                eb.setDescription("Es gibt noch keine Reactionroles.");
                            } else {
                                eb.setColor(Color.decode("#00FF00"));
                                eb.setDescription("Die Reactionroles sind:");
                            }
                            break;
                        case "add":
                            if ((args.length < 5) || (message.getMentionedRoles().size() == 0) || (message.getEmotes().size() == 0)) {
                                eb.setColor(Color.decode("#ff0000"));
                                eb.setDescription("```diff\n- " + Main.commandPrefix + " rr add [channelid] [messageid] [emote] [@role]```");
                            } else {
                                ResultSet r = Main.sql.query("SELECT * FROM reactionroles WHERE channelId = '" + args[1] + "' AND messageId = '" + args[2] + "' AND emoteID = '" + message.getEmotes().get(0).getId() + "'");
                                try {
                                    if (r.isClosed()) {
                                        TextChannel tmpChannel = Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getTextChannelById(args[1]));
                                        eb.setColor(Color.decode("#ff0000"));
                                        eb.setDescription("Die Nachricht wurde nicht gefunden.");
                                        Message msg = tmpChannel.retrieveMessageById(args[2]).complete();
                                        if (msg != null) {
                                            String roleId = message.getMentionedRoles().get(0).getId();
                                            String emoteId = message.getEmotes().get(0).getId();
                                            msg.addReaction(message.getEmotes().get(0)).complete();
                                            Main.sql.update("INSERT INTO reactionroles VALUES ('" + tmpChannel.getId() + "', '" + msg.getId() + "', '" + roleId + "', '" + emoteId + "')");
                                            eb.setColor(Color.decode("#00ff00"));
                                            eb.setDescription("ReactionRole erfolgreich hinzugefügt.");
                                        }
                                        break;
                                    } else {
                                        eb.setColor(Color.decode("#ff0000"));
                                        eb.setDescription("Diese ReactionRole gibt es schon.");
                                    }
                                } catch (Exception ignored) {
                                    eb.setColor(Color.decode("#ff0000"));
                                    eb.setDescription("Es gab einen Fehler.");
                                }
                            }
                            break;
                        case "remove":
                            if ((args.length < 4) || (message.getEmotes().size() == 0)) {
                                eb.setColor(Color.decode("#ff0000"));
                                eb.setDescription("```diff\n- " + Main.commandPrefix + " rr remove [channelid] [messageid] [emote]```");
                            } else {
                                try {
                                    TextChannel tmpChannel = Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getTextChannelById(args[1]));
                                    eb.setColor(Color.decode("#ff0000"));
                                    eb.setDescription("Die Nachricht wurde nicht gefunden.");
                                    Message msg = tmpChannel.retrieveMessageById(args[2]).complete();
                                    if (msg != null) {
                                        msg.removeReaction(message.getEmotes().get(0)).queue();
                                        Main.sql.update("DELETE FROM reactionroles WHERE channelId = '" + args[1] + "' AND messageId = '" + args[2] + "' AND emoteID = '" + message.getEmotes().get(0).getId() + "'");
                                        eb.setColor(Color.decode("#00ff00"));
                                        eb.setDescription("Die RectionRole wurde gelöscht.");
                                        break;
                                    }
                                } catch (Exception ignored) {
                                    return false;
                                }
                            }
                            break;
                        default:
                            return false;
                    }
                } catch (SQLException ignored) {
                    eb.setColor(Color.decode("#FF0000"));
                    eb.setDescription("Es gab einen Fehler");
                }
            } else {
                return false;
            }
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " rr", "Kontrolliert die ReactionRoles.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " rr [ add | remove | list ] <channelid> <messageid> <emote> <@role>";
    }
}
