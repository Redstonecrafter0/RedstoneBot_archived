package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Xp implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length < 2) {
                return false;
            }
            long amount;
            try {
                amount = Long.parseLong(args[1]);
            } catch (Exception ignored) {
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("Die Zahl muss zwischen 0 und " + Long.MAX_VALUE + " sein.");
                channel.sendMessage(eb.build()).queue();
                return true;
            }
            Member m;
            if (message.getMentionedMembers().size() == 0) {
                m = member;
            } else {
                m = message.getMentionedMembers().get(0);
            }
            if (m.getRoles().contains(Discord.INSTANCE.getManager().getRoleById((String) Main.config.get("verifiedRole")))) {
                try {
                    ResultSet rs = Main.sql.query("SELECT * FROM leveling WHERE dcId = '" + m.getId() + "'");
                    if (rs.isClosed()) {
                        Main.sql.update("INSERT INTO leveling VALUES ('" + m.getId() + "', '0')");
                        rs = Main.sql.query("SELECT * FROM leveling WHERE dcId = '" + m.getId() + "'");
                    }
                    switch (args[0]) {
                        case "set":
                            Main.sql.update("UPDATE leveling SET xp = '" + amount + "' WHERE dcId = '" + m.getId() + "'");
                            eb.setColor(Color.decode("#00FF00"));
                            eb.setDescription("Die XP wurden für " + m.getUser().getAsTag() + " geupdated.");
                            break;
                        case "add":
                            Main.sql.update("UPDATE leveling SET xp = '" + (Long.parseLong(rs.getString("xp")) + amount) + "' WHERE dcId = '" + m.getId() + "'");
                            eb.setColor(Color.decode("#00FF00"));
                            eb.setDescription("Die XP wurden für " + m.getUser().getAsTag() + " geupdated.");
                            break;
                        case "remove":
                            try {
                                if (rs.getLong("xp") > amount) {
                                    eb.setColor(Color.decode("#FF0000"));
                                    eb.setDescription("Von " + m.getUser().getAsTag() + " können maximal " + rs.getString("xp") + " XP abgezogen werden.");
                                } else {
                                    Main.sql.update("UPDATE leveling SET xp = '" + (Long.parseLong(rs.getString("xp")) - amount) + "' WHERE dcId = '" + m.getId() + "'");
                                    eb.setColor(Color.decode("#00FF00"));
                                    eb.setDescription("Die XP wurden für " + m.getUser().getAsTag() + " geupdated.");
                                }
                            } catch (SQLException ignored) {
                                return false;
                            }
                            break;
                        default:
                            return false;
                    }
                } catch (SQLException ignored) {
                    return false;
                }
            } else {
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription(m.getUser().getAsTag() + " ist noch nicht registriert.");
            }
        } else {
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
            eb.setColor(Color.decode("#FF0000"));
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " xp", "Verändert die Xp eines Nutzers.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " xp [ set | add | remove ] [amount] <user>";
    }
}
