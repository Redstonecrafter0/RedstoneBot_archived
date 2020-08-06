package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SetAutoChannel implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length <= 0) {
                return false;
            }
            try {
                Main.config.remove(Main.config.get("autochannel"));
                Main.config.put("autochannel", args[0]);
                JSONObject rootConfig = (JSONObject) new JSONParser().parse(new FileReader("config.json"));
                rootConfig.remove("config");
                rootConfig.put("config", Main.config);
                FileWriter writer = new FileWriter("config.json");
                writer.write(Main.prettyPrintJSON(rootConfig.toJSONString()));
                writer.close();
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
            eb.setColor(Color.decode("#00FF00"));
            eb.setDescription("Der neue Autochannel channel ist " + channel.getGuild().getVoiceChannelById(args[0]).getName() + ".");
        } else {
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " setautochannel", "Setzt den Autochannel Channel.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + "setautochannel [channelId]";
    }
}
