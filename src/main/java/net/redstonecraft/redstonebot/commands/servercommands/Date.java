package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.text.SimpleDateFormat;

public class Date implements ServerCommand {

    private final String[] days = new String[]{"Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};

    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        eb.setColor(Color.decode("#3498DB"));
        java.util.Date date = new java.util.Date();
        eb.setDescription("Es ist " + days[date.getDay()] + ", der " + new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()) + ".");
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " date", "Zeigt den heutigen Tag.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " date";
    }
}
