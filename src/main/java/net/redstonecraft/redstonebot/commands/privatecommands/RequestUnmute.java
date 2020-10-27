package net.redstonecraft.redstonebot.commands.privatecommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.PrivateCommand;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class RequestUnmute implements PrivateCommand {
    @Override
    public boolean onCommand(PrivateChannel channel, User user, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).isMember(user)) {
            try {
                ResultSet rs = Main.sql.query("SELECT * FROM muted WHERE dcId = '" + user.getId() + "'");
                if (rs.isClosed()) {
                    eb.setColor(Color.decode("#FF0000"));
                    eb.setDescription("Du bist nicht gemuted.");
                } else {
                    if (rs.getString("dcId").equals(user.getId())) {
                        if (rs.getLong("until") < (System.currentTimeMillis() / 1000)) {
                            Member member = Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getMember(user);
                            Role role = Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getRoleById((String) Main.config.get("mutedRole"));
                            Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).removeRoleFromMember(Objects.requireNonNull(member), Objects.requireNonNull(role)).queue();
                            Main.sql.update("DELETE FROM muted WHERE dcId = '" + user.getId() + "'");
                            eb.setColor(Color.decode("#00FF00"));
                            eb.setDescription("Du wurdest entmuted.");
                        } else {
                            eb.setColor(Color.decode("#FF0000"));
                            eb.setDescription("Du bist noch bis zum " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(rs.getLong("until") * 1000) + " gemuted.");
                        }
                    } else {
                        eb.setColor(Color.decode("#FF0000"));
                        eb.setDescription("Du bist nicht gemuted.");
                    }
                }
            } catch (SQLException e) {
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("Es ist ein Fehler aufgetreten");
                Main.getLogger().warning(e.getMessage());
            }
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Du bist nicht auf dem Discord Server.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " requestunmute", "Fordert die Entmutung an.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " requestunmute";
    }
}
