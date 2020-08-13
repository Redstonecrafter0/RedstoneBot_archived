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
import java.util.concurrent.TimeUnit;

public class Delete implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.MESSAGE_MANAGE)) {
            if (args.length == 0) {
                return false;
            }
            try {
                Integer.parseInt(args[0]);
            } catch (Exception ignored) {
                return false;
            }
            channel.deleteMessages(channel.getHistory().retrievePast(Integer.parseInt(args[0]) + 1).complete()).queue();
            eb.setColor(Color.decode("#00FF00"));
            eb.setDescription("Die letzten " + args[0] + " Nachrichten wurden gelöscht.");
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigun Message Manage.");
        }
        channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " delete", "Löscht dir letzten n Nachrichten.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " delete [amount]";
    }
}
