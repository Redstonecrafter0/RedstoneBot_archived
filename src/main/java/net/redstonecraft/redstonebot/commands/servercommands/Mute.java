package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Mute implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (message.getMentionedMembers().size() == 0) {
                return false;
            }
            if (args.length < 3) {
                return false;
            }
            if (message.getMentionedMembers().get(0) == null) {
                return false;
            }
            try {
                if (message.getMentionedMembers().get(0).getRoles().contains(channel.getGuild().getRoleById((String) Main.config.get("mutedRole")))) {
                    eb.setColor(Color.decode("#FF0000"));
                    java.util.Date date = new Date();
                    int years = date.getYear() - 70;
                    int months = date.getMonth();
                    int days = date.getDate() - 1;
                    int hours = date.getHours() - 1;
                    int minutes = date.getMinutes();
                    int seconds = date.getSeconds();
                    if (years == 0) {
                        if (months == 0) {
                            if (days == 0) {
                                if (hours == 0) {
                                    if (minutes == 0) {
                                        eb.setDescription(message.getMentionedMembers().get(0).getAsMention() + " ist bereits gemuted. Er wird in " + seconds + " Sekunden entmuted.");
                                    } else {
                                        eb.setDescription(message.getMentionedMembers().get(0).getAsMention() + " ist bereits gemuted. Er wird in " + minutes + " Minuten und " + seconds + " Sekunden entmuted.");
                                    }
                                } else {
                                    eb.setDescription(message.getMentionedMembers().get(0).getAsMention() + " ist bereits gemuted. Er wird in " + hours + " Stunden, " + minutes + " Minuten und " + seconds + " Sekunden entmuted.");
                                }
                            } else {
                                eb.setDescription(message.getMentionedMembers().get(0).getAsMention() + " ist bereits gemuted. Er wird in " + days + " Tagen, " + hours + " Stunden, " + minutes + " Minuten und " + seconds + " Sekunden entmuted.");
                            }
                        } else {
                            eb.setDescription(message.getMentionedMembers().get(0).getAsMention() + " ist bereits gemuted. Er wird in " + months + " Monaten, " + days + " Tagen, " + hours + " Stunden, " + minutes + " Minuten und " + seconds + " Sekunden entmuted.");
                        }
                    } else {
                        eb.setDescription(message.getMentionedMembers().get(0).getAsMention() + " ist bereits gemuted. Er wird in " + years + " Jahren, " + months + " Monaten, " + days + " Tagen, " + hours + " Stunden, " + minutes + " Minuten und " + seconds + " Sekunden entmuted.");
                    }
                } else {
                    eb.setColor(Color.decode("#00FF00"));
                    int timeIn = Integer.parseInt(args[1]);
                    long time;
                    switch (args[2]) {
                        case "s":
                            time = timeIn;
                            break;
                        case "m":
                            time = timeIn * 60;
                            break;
                        case "h":
                            time = timeIn * 60 * 60;
                            break;
                        case "d":
                            time = timeIn * 60 * 60 * 24;
                            break;
                        case "M":
                            time = timeIn * 60 * 60 * 24 * 30;
                            break;
                        case "Y":
                            time = timeIn * 60 * 60 * 24 * 365;
                            break;
                        default:
                            time = 60 * 60 * 24;
                            break;
                    }
                    channel.getGuild().addRoleToMember(message.getMentionedMembers().get(0), Objects.requireNonNull(channel.getGuild().getRoleById((String) Main.config.get("mutedRole")))).queue();
                    Main.sql.update("INSERT INTO muted VALUES ('" + message.getMentionedMembers().get(0).getId() + "', '" + ((System.currentTimeMillis() / 1000) + time) +"')");
                    eb.setColor(Color.decode("#00FF00"));
                    EmbedBuilder msg = new EmbedBuilder();
                    msg.setTitle(Main.prefix);
                    msg.setColor(Color.decode("#FF0000"));
                    msg.setDescription("Du wurdest von " + member.getUser().getAsTag() + " bis zum " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(System.currentTimeMillis() + (time * 1000)) + " gemuted.\nUm dich zu entmuten musst du nach der Zeit ```" + Main.commandPrefix + " requestunmute``` schreiben.");
                    message.getMentionedMembers().get(0).getUser().openPrivateChannel().complete().sendMessage(msg.build()).queue();
                    eb.setDescription(message.getMentionedMembers().get(0).getAsMention() + " wurde bis zum " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(System.currentTimeMillis() + (time * 1000)) + " von " + member.getAsMention() + " gemuted.");
                }
            } catch (Exception ignored) {
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("Ein Fehler ist aufgetreten");
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
        return new MessageEmbed.Field(Main.commandPrefix + " mute", "Muted einen Nutzer.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " mute [user] [time] [ s | m | h | d | M | Y ]";
    }
}
