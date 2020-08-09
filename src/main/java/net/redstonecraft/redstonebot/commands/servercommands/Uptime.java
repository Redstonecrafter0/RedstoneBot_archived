package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.util.Date;

public class Uptime implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        long time = System.currentTimeMillis() - Main.startTime;
        Date date = new Date(time);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        eb.setColor(Color.decode("#3498DB"));
        int years = date.getYear() - 70;
        int months = date.getMonth();
        int days = date.getDate() - 1;
        int hours = date.getHours() - 1;
        int minutes = date.getMinutes();
        int seconds = date.getSeconds();
        if (years == 0) {
            if (months == 0) {
                if (days == 0) {
                    if (hours == 0) {
                        if (minutes == 0) {
                            eb.setDescription("Der Bot ist schon " + seconds + " Sekunden online.");
                        } else {
                            eb.setDescription("Der Bot ist schon " + minutes + " Minuten und " + seconds + " Sekunden online.");
                        }
                    } else {
                        eb.setDescription("Der Bot ist schon " + hours + " Stunden, " + minutes + " Minuten und " + seconds + " Sekunden online.");
                    }
                } else {
                    eb.setDescription("Der Bot ist schon " + days + " Tage, " + hours + " Stunden, " + minutes + " Minuten und " + seconds + " Sekunden online.");
                }
            } else {
                eb.setDescription("Der Bot ist schon " + months + " Monate, " + days + " Tage, " + hours + " Stunden, " + minutes + " Minuten und " + seconds + " Sekunden online.");
            }
        } else {
            eb.setDescription("Der Bot ist schon " + years + " Jahre, " + months + " Monate, " + days + " Tage, " + hours + " Stunden, " + minutes + " Minuten und " + seconds + " Sekunden online.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " uptime", "Zeigt die vergangene Zeit seit dem letzten Botstart.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " uptime";
    }
}
