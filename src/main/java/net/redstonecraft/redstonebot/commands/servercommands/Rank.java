package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.sql.ResultSet;

public class Rank implements ServerCommand {

    private static final double increaseFactor = 5;
    private static final int baseXp = 10;

    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        Member selMember = member;
        if (message.getMentionedMembers().size() != 0) {
            selMember = message.getMentionedMembers().get(0);
        }
        ResultSet rs = Main.sql.query("SELECT * FROM leveling WHERE dcId = '" + selMember.getId() + "'");
        long xp;
        EmbedBuilder eb = new EmbedBuilder();
        if (!selMember.getRoles().contains(Discord.INSTANCE.getManager().getRoleById((String) Main.config.get("verifiedRole")))) {
            eb.setTitle(Main.prefix);
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dieser Nutzer ist noch nicht registriert.");
            channel.sendMessage(eb.build()).queue();
            return true;
        }
        try {
            xp = Long.parseLong(rs.getString("xp"));
        } catch (Exception ignored) {
            xp = 0;
        }
        if (selMember.getUser().getAvatarUrl() != null) {
            eb.setAuthor(selMember.getUser().getAsTag(), selMember.getUser().getAvatarUrl(), selMember.getUser().getAvatarUrl());
        } else {
            eb.setAuthor(selMember.getUser().getAsTag(), selMember.getUser().getDefaultAvatarUrl(), selMember.getUser().getDefaultAvatarUrl());
        }
        eb.setColor(Color.decode("#3498DB"));
        eb.setTitle("Rank");
        long maxXp = baseXp;
        int level = 0;
        while (xp >= maxXp) {
            xp -= maxXp;
            level++;
            maxXp = (long) (maxXp + increaseFactor);
        }
        eb.addField("Level " + level, "Xp: " + xp + "/" + maxXp, false);
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " rank", "Zeigt dein Level.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " rank <user>";
    }
}
