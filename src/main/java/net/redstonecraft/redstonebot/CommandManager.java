package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstonebot.defaultcommands.servercommands.*;
import net.redstonecraft.redstonebot.defaultcommands.privatecommands.*;
import net.redstonecraft.redstonebot.interfaces.*;

import java.awt.*;
import java.util.HashMap;

public class CommandManager {

    public HashMap<String, ServerCommand> serverCommands = new HashMap<>();
    public HashMap<String, PrivateCommand> privateCommands = new HashMap<>();

    public void registerServerCommand(String name, ServerCommand command) {
        serverCommands.put(name, command);
    }

    public void registerPrivateCommand(String name, PrivateCommand command) {
        privateCommands.put(name, command);
    }

    public void performServerCommand(String command, TextChannel channel, Member member, Message message, String[] args) {
        if (serverCommands.containsKey(command)) {
            if (!serverCommands.get(command).onCommand(channel, member, message, args)) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(Main.prefix);
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("```" + serverCommands.get(command).usage() + "```");
                channel.sendMessage(eb.build()).queue();
            }
        } else {
            new ServerHelp(serverCommands).onCommand(channel, member, message, args);
        }
    }

    public void performPrivateCommand(String command, PrivateChannel channel, User user, Message message, String[] args) {
        if (privateCommands.containsKey(command)) {
            if (!privateCommands.get(command).onCommand(channel, user, message, args)) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(Main.prefix);
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("```" + privateCommands.get(command).usage() + "```");
                channel.sendMessage(eb.build()).queue();
            }
        } else {
            new PrivateHelp(privateCommands).onCommand(channel, user, message, args);
        }
    }

}
