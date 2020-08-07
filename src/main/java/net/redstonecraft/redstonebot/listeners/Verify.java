package net.redstonecraft.redstonebot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.redstonebot.Main;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Verify extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        try {
            if (event.getGuild().getRoleById((String) Main.config.get("unverifiedRole")) != null) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(Main.commandPrefix);
                eb.setColor(Color.decode("#FF0000"));
                event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById((String) Main.config.get("unverifiedRole")))).queue();
                String verifyId = Main.randomString("QWERTZIUOPASDFGHJKLYXCVBNMqwertzuioplkjhgfdsayxcvbnm0123456789", 32);
                ResultSet rs = Main.sql.query("SELECT * FROM members WHERE verifyId = '" + verifyId + "'");
                while (rs.getString("verifyId").equals(verifyId)) {
                    verifyId = Main.randomString("QWERTZIUOPASDFGHJKLYXCVBNMqwertzuioplkjhgfdsayxcvbnm0123456789", 32);
                    rs = Main.sql.query("SELECT * FROM members WHERE verifyId = '" + verifyId + "'");
                }
                Main.sql.update("INSERT INTO members VALUES ('" + event.getMember().getId() + "', '" + verifyId + "', 0)");
                eb.setDescription("Verifiziere dich in dem du in den Verifychannel von " + event.getGuild().getName() + " den Verifizierungscode schreibst.\nDamit akzeptierst du die Serverregeln.");
                eb.addField("Verifizierungscode", verifyId, false);
                event.getMember().getUser().openPrivateChannel().complete().sendMessage(eb.build()).queue();
            }
        } catch (SQLException e) {
            Main.getLogger().warning(e.getMessage());
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        try {
            ResultSet rs = Main.sql.query("SELECT * FROM members WHERE dcId = '" + event.getUser().getId() + "'");
            if (rs.getString("dcId") != null) {
                for (Role i : Objects.requireNonNull(event.getMember()).getRoles()) {
                    event.getGuild().removeRoleFromMember(event.getMember(), i).queue();
                }
                Main.sql.update("DELETE FROM members WHERE dcId = '" + event.getUser().getId() + "'");
            }
        } catch (SQLException e) {
            Main.getLogger().warning(e.getMessage());
        }
    }
}
