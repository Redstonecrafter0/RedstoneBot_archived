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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class SetMutedRole implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length <= 0) {
                return false;
            }
            if (message.getMentionedRoles().size() == 0) {
                return false;
            }
            if (message.getMentionedRoles().get(0) == null) {
                return false;
            }
            try {
                ResultSet rs = Main.sql.query("SELECT * FROM muted");
                if (!rs.isClosed()) {
                    while (rs.next()) {
                        channel.getGuild().removeRoleFromMember(Objects.requireNonNull(channel.getGuild().getMemberById(rs.getString("dcId"))), Objects.requireNonNull(channel.getGuild().getRoleById((String) Main.config.get("mutedRole")))).queue();
                        channel.getGuild().addRoleToMember(Objects.requireNonNull(channel.getGuild().getMemberById(rs.getString("dcId"))), message.getMentionedRoles().get(0)).queue();
                    }
                }
            } catch (SQLException e) {
                Main.getLogger().warning(e.getMessage());
            }
            Main.config.remove(Main.config.get("mutedRole"));
            Main.config.put("mutedRole", message.getMentionedRoles().get(0).getId());
            Main.saveConfig();
            eb.setColor(Color.decode("#00FF00"));
            eb.setDescription("Die neue Muterolle ist " + message.getMentionedRoles().get(0).getAsMention() + ".");
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " setmutedrole", "Setzt die Muted Rolle", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " setmutedrole [role]";
    }
}
