package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.util.Arrays;

public class SetActivity implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length <= 1) {
                return false;
            }
            switch (args[0]) {
                case "playing":
                    Discord.INSTANCE.getManager().setActivity(Activity.playing(String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                    eb.setDescription("Botaktivität zu Playing " + String.join(" ", Arrays.copyOfRange(args, 1, args.length)) + " gesetzt.");
                    eb.setColor(Color.decode("#00FF00"));
                    break;
                case "listening":
                    Discord.INSTANCE.getManager().setActivity(Activity.listening(String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                    eb.setDescription("Botaktivität zu Listening " + String.join(" ", Arrays.copyOfRange(args, 1, args.length)) + " gesetzt.");
                    eb.setColor(Color.decode("#00FF00"));
                    break;
                case "watching":
                    Discord.INSTANCE.getManager().setActivity(Activity.watching(String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                    eb.setDescription("Botaktivität zu Watching " + String.join(" ", Arrays.copyOfRange(args, 1, args.length)) + " gesetzt.");
                    eb.setColor(Color.decode("#00FF00"));
                    break;
                case "streaming":
                    if (args.length <= 2) {
                        eb.setDescription("r!setactivity streaming [name] [url]");
                        eb.setColor(Color.decode("#FF0000"));
                    } else {
                        Discord.INSTANCE.getManager().setActivity(Activity.streaming(String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1)), args[2]));
                        eb.setDescription("Botaktivität zu Streaming " + String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1)) + " gesetzt.");
                        eb.setColor(Color.decode("#00FF00"));
                    }
                    break;
            }
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " setactivity", "Setzt die Botaktivität", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " setactivity [ playing | listening | watching | streaming ] [name] <url>";
    }
}
