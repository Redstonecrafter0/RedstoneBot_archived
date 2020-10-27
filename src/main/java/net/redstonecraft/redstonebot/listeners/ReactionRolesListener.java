package net.redstonecraft.redstonebot.listeners;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;

import java.sql.ResultSet;
import java.util.Objects;

public class ReactionRolesListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        try {
            if (event.getUser().isBot()) {
                return;
            }
            Member member = event.getMember();
            Emote emote = event.getReactionEmote().getEmote();
            TextChannel channel = event.getChannel();
            String messageId = event.getMessageId();
            ResultSet rs = Main.sql.query("SELECT * FROM reactionroles WHERE channelId = '" + channel.getId() + "' AND messageId = '" + messageId + "' AND emoteId = '" + emote.getId() + "'");
            if (!rs.isClosed()) {
                Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).addRoleToMember(member, Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getRoleById(rs.getString("roleId")))).queue();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        try {
            if (Objects.requireNonNull(event.getUser()).isBot()) {
                return;
            }
            Member member = Objects.requireNonNull(event.getMember());
            Emote emote = event.getReactionEmote().getEmote();
            TextChannel channel = event.getChannel();
            String messageId = event.getMessageId();
            ResultSet rs = Main.sql.query("SELECT * FROM reactionroles WHERE channelId = '" + channel.getId() + "' AND messageId = '" + messageId + "' AND emoteId = '" + emote.getId() + "'");
            if (!rs.isClosed()) {
                Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).removeRoleFromMember(member, Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getRoleById(rs.getString("roleId")))).queue();
            }
        } catch (Exception ignored) {
        }
    }

}
