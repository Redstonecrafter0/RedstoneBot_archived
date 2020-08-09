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

public class Say implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            message.delete().queue();
            channel.sendMessage(String.join(" ", args)).queue();
        } else {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(Main.prefix);
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator");
            channel.sendMessage(eb.build()).queue();
        }
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " say", "Lässt den Bot eine Nachricht schreiben.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " say [text] ...";
    }
}
