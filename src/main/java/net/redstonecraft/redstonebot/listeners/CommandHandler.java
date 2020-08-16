package net.redstonecraft.redstonebot.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.defaultcommands.privatecommands.PrivateHelp;
import net.redstonecraft.redstonebot.defaultcommands.servercommands.ServerHelp;

import java.util.*;

public class CommandHandler extends ListenerAdapter {

    public static ListenerAdapter INSTANCE;

    public CommandHandler() {
        INSTANCE = this;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)  {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getMessage().getContentDisplay().startsWith(Main.commandPrefix)) {
            return;
        }
        TextChannel channel = event.getTextChannel();
        Member member = event.getMember();
        Message message = event.getMessage();
        String msg = message.getContentDisplay();
        if (!msg.startsWith(Main.commandPrefix + " ") && !(msg.equals(Main.commandPrefix) || msg.equals(Main.commandPrefix + " "))) {
            msg = Main.commandPrefix + " " + msg.substring(Main.commandPrefix.length());
        }
        String[] oriArgs = msg.split(" ");
        String command = "help";
        String[] args = new String[]{};
        if (oriArgs.length == 0) {
            args = new String[]{};
        } else {
            if (oriArgs[0].equals(Main.commandPrefix)) {
                if (!(oriArgs.length == 1)) {
                    command = oriArgs[1];
                    if (!(oriArgs.length == 2)) {
                        args = Arrays.copyOfRange(oriArgs, 2, oriArgs.length);
                    }
                }
            }
        }
        String finalCommand = command;
        String[] finalArgs = args;
        new Thread(() -> {
            Main.commandManager.performServerCommand(finalCommand, channel, member, message, finalArgs);
        }).start();
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getMessage().getContentDisplay().startsWith(Main.commandPrefix)) {
            return;
        }
        PrivateChannel channel = event.getChannel();
        User member = event.getAuthor();
        Message message = event.getMessage();
        String msg = message.getContentDisplay();
        if (!msg.startsWith(Main.commandPrefix + " ") && !(msg.equals(Main.commandPrefix) || msg.equals(Main.commandPrefix + " "))) {
            msg = Main.commandPrefix + " " + msg.substring(Main.commandPrefix.length());
        }
        String[] oriArgs = msg.split(" ");
        String command = "help";
        String[] args = new String[]{};
        if (oriArgs.length == 0) {
            args = new String[]{};
        } else {
            if (oriArgs[0].equals(Main.commandPrefix)) {
                if (!(oriArgs.length == 1)) {
                    command = oriArgs[1];
                    if (!(oriArgs.length == 2)) {
                        args = Arrays.copyOfRange(oriArgs, 2, oriArgs.length);
                    }
                }
            }
        }
        String finalCommand = command;
        String[] finalArgs = args;
        new Thread(() -> {
            Main.commandManager.performPrivateCommand(finalCommand, channel, member, message, finalArgs);
        }).start();
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getMember().getUser().isBot()) {
            return;
        }
        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        if (message.getEmbeds().size() == 0) {
            return;
        }
        if (message.getEmbeds().get(0).getTitle() == null) {
            return;
        }
        if (!message.getEmbeds().get(0).getTitle().startsWith(Main.prefix + " - Help - ")) {
            return;
        }
        int page = Integer.parseInt(message.getEmbeds().get(0).getTitle().substring(Main.prefix.length() + 10));
        page--;
        if (event.getReaction().getReactionEmote().getEmoji().equals("➡")) {
            page++;
            new ServerHelp(Main.getCommandManager().serverCommands, page, message).onCommand(event.getChannel(), event.getMember(), null, new String[]{});
        } else if (event.getReaction().getReactionEmote().getEmoji().equals("⬅")) {
            page--;
            new ServerHelp(Main.getCommandManager().serverCommands, page, message).onCommand(event.getChannel(), event.getMember(), null, new String[]{});
        } else {
            event.getReaction().removeReaction().queue();
        }
    }

    @Override
    public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) {
        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        if (message.getEmbeds().size() == 0) {
            return;
        }
        if (message.getEmbeds().get(0).getTitle() == null) {
            return;
        }
        if (!message.getEmbeds().get(0).getTitle().startsWith(Main.prefix + " - Help - ")) {
            return;
        }
        int page = Integer.parseInt(message.getEmbeds().get(0).getTitle().substring(Main.prefix.length() + 10));
        page--;
        if (event.getReaction().getReactionEmote().getEmoji().equals("➡")) {
            page++;
            new PrivateHelp(Main.getCommandManager().privateCommands, page, message).onCommand(event.getChannel(), event.getUser(), null, new String[]{});
        } else if (event.getReaction().getReactionEmote().getEmoji().equals("⬅")) {
            page--;
            new PrivateHelp(Main.getCommandManager().privateCommands, page, message).onCommand(event.getChannel(), event.getUser(), null, new String[]{});
        } else {
            event.getReaction().removeReaction().queue();
        }
    }
}
