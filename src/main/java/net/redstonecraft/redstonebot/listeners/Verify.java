package net.redstonecraft.redstonebot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.redstonebot.Main;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Verify extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        for (Role i : Objects.requireNonNull(event.getMember()).getRoles()) {
            event.getGuild().removeRoleFromMember(event.getMember(), i).queue();
        }
        if (Main.config.get("unverifiedRole") == null) {
            return;
        }
        if (event.getGuild().getRoleById((String) Main.config.get("unverifiedRole")) != null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(Main.prefix);
            eb.setColor(Color.decode("#FF0000"));
            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById((String) Main.config.get("unverifiedRole")))).queue();
            String verifyId = Main.randomString("QWERTZIUOPASDFGHJKLYXCVBNMqwertzuioplkjhgfdsayxcvbnm0123456789", 32);
            ResultSet rs = Main.sql.query("SELECT * FROM members WHERE verifyId = '" + verifyId + "'");
            boolean c = true;
            try {
                rs.getString("verifyId");
            } catch (SQLException ignored) {
                c = false;
            }
            while (c) {
                verifyId = Main.randomString("QWERTZIUOPASDFGHJKLYXCVBNMqwertzuioplkjhgfdsayxcvbnm0123456789", 32);
                rs = Main.sql.query("SELECT * FROM members WHERE verifyId = '" + verifyId + "'");
                try {
                    rs.getString("verifyId");
                } catch (SQLException ignored) {
                    c = false;
                }
            }
            Main.sql.update("INSERT INTO members VALUES ('" + event.getMember().getId() + "', '" + verifyId + "', 0)");
            eb.setDescription("Verifiziere dich in dem du in den Verifychannel von " + event.getGuild().getName() + " den Verifizierungscode schreibst.\nDamit akzeptierst du die Serverregeln.");
            eb.addField("Verifizierungscode", verifyId, false);
            event.getMember().getUser().openPrivateChannel().complete().sendMessage(eb.build()).queue();
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if (Main.config.get("unverifiedRole") == null) {
            return;
        }
        try {
            ResultSet rs = Main.sql.query("SELECT * FROM members WHERE dcId = '" + event.getUser().getId() + "'");
            if (rs.getString("dcId") != null) {
                Main.sql.update("DELETE FROM members WHERE dcId = '" + event.getUser().getId() + "'");
            }
        } catch (SQLException e) {
            Main.getLogger().warning(e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getChannel().getId().equals((String) Main.config.get("verifyChannel"))) {
            return;
        }
        try {
            ResultSet rs = Main.sql.query("SELECT * FROM members WHERE dcId = '" + Objects.requireNonNull(event.getMember()).getId() + "'");
            if (event.getMessage().getContentDisplay().equals(rs.getString("verifyId"))) {
                event.getGuild().removeRoleFromMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById((String) Main.config.get("unverifiedRole")))).queue();
                event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById((String) Main.config.get("verifiedRole")))).queue();
                Main.sql.update("UPDATE members SET verified = '1' WHERE dcId = '" + event.getMember().getId() + "'");
            }
        } catch (SQLException e) {
            Main.getLogger().warning(e.getMessage());
        }
        event.getMessage().delete().queue();
    }
}
