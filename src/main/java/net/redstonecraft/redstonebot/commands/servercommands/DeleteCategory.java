package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.util.Objects;

public class DeleteCategory implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length < 1) {
                return false;
            }
            try {
                Category category = Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getCategoryById(args[0]));
                String catName = category.getName();
                for (GuildChannel i : category.getChannels()) {
                    i.delete().complete();
                }
                category.delete().queue();
                eb.setColor(Color.decode("#00ff00"));
                eb.setDescription("Kategorie " + catName + " wurde erfolgreich gelöscht.");
            } catch (Exception ignored) {
                return false;
            }
        } else {
            eb.setColor(Color.decode("#ff0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " deletecategory", "Löscht eine Kategorie und alle Channel darin.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " deletecategory [catId]";
    }
}
