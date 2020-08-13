package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Objects;

public class User implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        net.dv8tion.jda.api.entities.User user;
        if (message.getMentionedUsers().size() == 0) {
            user = member.getUser();
        } else {
            user = message.getMentionedUsers().get(0);
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#3498DB"));
        if (user.getAvatarUrl() != null) {
            eb.setAuthor(user.getAsTag(), user.getAvatarUrl(), user.getAvatarUrl());
        } else {
            eb.setAuthor(user.getAsTag(), user.getDefaultAvatarUrl(), user.getDefaultAvatarUrl());
        }
        if (channel.getGuild().isMember(user)) {
            eb.addField(channel.getGuild().getName() + " gejoint am", Objects.requireNonNull(channel.getGuild().getMember(user)).getTimeJoined().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)), false);
        }
        if (user.isBot()) {
            eb.setDescription("**BOT**");
        }
        eb.addField("ID", user.getId(), false);
        eb.addField("Account erstellt am", user.getTimeCreated().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)), false);
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " user", "Zeigt Nutzerinformationen an.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " user <@user>";
    }
}
