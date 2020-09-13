package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.sql.ResultSet;

public class Tokens implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        eb.setColor(Color.decode("#00ff00"));
        ResultSet rs = Main.sql.query("SELECT * FROM chatfiltertokens WHERE dcId = '" + member.getId() + "'");
        try {
            eb.setColor(Color.decode("#00ff00"));
            if (rs.isClosed()) {
                eb.setDescription(member.getAsMention() + " hat 0 Tokens.");
            } else {
                eb.setDescription(member.getAsMention() + " hat " + rs.getInt("tokens") + " Tokens.");
            }
        } catch (Exception ignored) {
            return false;
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " tokens", "Zeigt wie viele tokens du hast mit denen du den Chatfilter umgehen kannst.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " tokens";
    }
}
