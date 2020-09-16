package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Server implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        if (channel.getGuild().retrieveInvites().complete().size() == 0) {
            channel.createInvite().complete();
        }
        eb.setTitle(channel.getGuild().getName(), channel.getGuild().retrieveInvites().complete().get(0).getUrl());
        eb.setColor(Color.decode("#3498DB"));
        eb.addField("Owner", Objects.requireNonNull(channel.getGuild().getOwner()).getAsMention(), false);
        List<Member> members = new ArrayList<>();
        List<Member> onlineMembers = new ArrayList<>();
        List<Member> bots = new ArrayList<>();
        List<Member> onlineBots = new ArrayList<>();
        for (Member i : channel.getGuild().getMembers()) {
            if (i.getUser().isBot()) {
                if (i.getOnlineStatus().equals(OnlineStatus.ONLINE) || i.getOnlineStatus().equals(OnlineStatus.IDLE) || i.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)) {
                    onlineBots.add(i);
                } else {
                    bots.add(i);
                }
            } else {
                if (i.getOnlineStatus().equals(OnlineStatus.ONLINE) || i.getOnlineStatus().equals(OnlineStatus.IDLE) || i.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)) {
                    onlineMembers.add(i);
                } else {
                    members.add(i);
                }
            }
        }
        eb.addField("Nutzer", onlineMembers.size() + "/" + (onlineMembers.size() + members.size()) + " Nutzer online\n" + onlineBots.size() + "/" + (onlineBots.size() + bots.size()) + " Bots online", false);
        eb.addField("Channel", channel.getGuild().getCategories().size() + " Kategorien\n" + channel.getGuild().getTextChannels().size() + " Textchannel\n" + channel.getGuild().getVoiceChannels().size() + " Voicechannel", false);
        List<Emote> emotes = new ArrayList<>();
        List<Emote> animatedEmotes = new ArrayList<>();
        for (Emote i : channel.getGuild().getEmotes()) {
            if (i.isAnimated()) {
                animatedEmotes.add(i);
            } else {
                emotes.add(i);
            }
        }
        eb.addField("Emotes", (emotes.size() + animatedEmotes.size()) + "/" + channel.getGuild().getMaxEmotes() + " Serveremotes\n" + animatedEmotes.size() + "/" + channel.getGuild().getEmotes().size() + " Emotes animiert", false);
        if (channel.getGuild().getAfkChannel() != null) {
            eb.addField("AFK Timeout", channel.getGuild().getAfkTimeout().getSeconds() + " Sekunden\nChannel " + channel.getGuild().getAfkChannel().getName(), false);
        } else {
            eb.addField("AFK Timeout", channel.getGuild().getAfkTimeout().getSeconds() + " Sekunden\nChannel nicht festgelegt", false);
        }
        eb.addField("Max Presences", String.valueOf(channel.getGuild().getMaxPresences()), false);
        eb.addField("Max Bitrate", (channel.getGuild().getMaxBitrate() / 1000) + " kbps", false);
        eb.addField("Max Filesize", renderFileSize(channel.getGuild().getMaxFileSize()), false);
        eb.addField("Erstellt", "Datum: " + channel.getGuild().getTimeCreated().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + "\nUhrzeit: " + channel.getGuild().getTimeCreated().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)), false);
        eb.setThumbnail(channel.getGuild().getIconUrl());
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " server", "Zeigt die Serverstats.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " server";
    }

    private String renderFileSize(long size) {
        int count = 0;
        long finalSize = size;
        while (finalSize >= 1024) {
            count++;
            finalSize = (long) Math.floor((float) finalSize / 1024);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(finalSize).append(" ");
        switch (count) {
            case 0:
                sb.append("Bytes");
                break;
            case 1:
                sb.append("KB");
                break;
            case 2:
                sb.append("MB");
                break;
            case 3:
                sb.append("GB");
                break;
            case 4:
                sb.append("TB");
                break;
            case 5:
                sb.append("PB");
                break;
            case 6:
                sb.append("EB");
                break;
            default:
                sb = new StringBuilder("Vielleicht unendlich?");
        }
        return sb.toString();
    }
}
