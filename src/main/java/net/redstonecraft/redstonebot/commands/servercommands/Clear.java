package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Clear implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        try {
            channel.deleteMessages(channel.getHistory().retrievePast(Integer.parseInt(args[0]) + 1).complete()).queue();
            eb.setColor(Color.decode("#00FF00"));
            eb.setDescription("Die letzten " + args[0] + " Nachrichten wurden gelöscht.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " clear", "Leert den Chat um n Nachrichten", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " clear <amount>";
    }
}
