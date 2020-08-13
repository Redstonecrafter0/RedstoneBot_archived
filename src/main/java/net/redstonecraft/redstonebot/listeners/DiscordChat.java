package net.redstonecraft.redstonebot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.redstonebot.Main;

import java.awt.*;
import java.util.Objects;

public class DiscordChat extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getChannel().getId().equals((String) Main.config.get("liveChatChannel"))) {
            return;
        }
        String authorName = Objects.requireNonNull(event.getMember()).getUser().getName();
        String message = event.getMessage().getContentDisplay();
        String profileUrl;
        if (Objects.requireNonNull(event.getMember()).getUser().getAvatarUrl() != null) {
            profileUrl = Objects.requireNonNull(event.getMember()).getUser().getAvatarUrl();
        } else {
            profileUrl = Objects.requireNonNull(event.getMember()).getUser().getDefaultAvatarUrl();
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Discord");
        eb.setAuthor(authorName, profileUrl, profileUrl);
        eb.setDescription(message);
        eb.setColor(Color.decode("#7289da"));
        event.getChannel().sendMessage(eb.build()).queue();
        event.getMessage().delete().queue();
    }

}
