package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;

public class AtomZeit implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        String date = getAtomicTime();
        if (date == null) {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Es gab einen Fehler");
        } else {
            eb.setColor(Color.decode("#00FF00"));
            eb.setDescription(date);
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " atomzeit", "Zeigt die aktuelle Atomzeit an.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " atomzeit";
    }

    private static String getAtomicTime() {
        try {
            TimeInfo timeInfo;
            long offset;
            NTPUDPClient client = new NTPUDPClient();
            client.setDefaultTimeout(10_000);
            InetAddress inetAddress = InetAddress.getByName("ptbtime2.ptb.de");
            timeInfo = client.getTime(inetAddress);
            timeInfo.computeDetails();
            offset = timeInfo.getOffset();
            long currentTime = System.currentTimeMillis();
            TimeStamp atomicNtpTime = TimeStamp.getNtpTime(currentTime + offset);
            return atomicNtpTime.toDateString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
