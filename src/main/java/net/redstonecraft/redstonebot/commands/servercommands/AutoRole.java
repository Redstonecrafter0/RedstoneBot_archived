package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;
import org.json.simple.JSONArray;

import java.awt.*;
import java.util.Objects;

public class AutoRole implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length > 0) {
                eb.setColor(Color.decode("#00ff00"));
                switch (args[0]) {
                    case "list":
                        if (((JSONArray) Main.config.get("autoRoles")).size() == 0) {
                            eb.setDescription("Es gibt noch keine AutoRole.");
                        } else {
                            eb.setDescription("Die AutoRoles sind:");
                            for (Object o : (JSONArray) Main.config.get("autoRoles")) {
                                String i = (String) o;
                                eb.addField("AutoRole", Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getRoleById(i)).getAsMention(), false);
                            }
                        }
                        break;
                    case "add":
                        if (message.getMentionedRoles().size() == 1) {
                            eb.setDescription("AutoRole hinzugefügt");
                            JSONArray autoRoles = (JSONArray) Main.config.get("autoRoles");
                            autoRoles.add(message.getMentionedRoles().get(0).getId());
                            Main.saveConfig();
                            Role role = Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getRoleById((String) Main.config.get("verifiedRole")));
                            for (Member m : Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getMembers()) {
                                if (m.getRoles().contains(role)) {
                                    Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).addRoleToMember(m, role).queue();
                                }
                            }
                        } else {
                            return false;
                        }
                        break;
                    case "remove":
                        JSONArray autoRoles = (JSONArray) Main.config.get("autoRoles");
                        for (Object i : autoRoles) {
                            if (Objects.requireNonNull(Discord.INSTANCE.getManager().getGuildById((String) Main.config.get("guild"))).getRoleById((String) i) == null) {
                                autoRoles.remove((String) i);
                            }
                        }
                        boolean c = true;
                        if (message.getMentionedRoles().size() == 1) {
                            c = false;
                            autoRoles.remove(message.getMentionedRoles().get(0).getId());
                            eb.setDescription("AutoRole entfernt.");
                        }
                        Main.saveConfig();
                        if (c) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
            } else {
                return false;
            }
        } else {
            eb.setDescription("Dir fehlt die Berechtigung Administrator");
            eb.setColor(Color.decode("#ff0000"));
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " autorole", "Fügt eine Rolle hinzu die automatisch vergeben wird.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " autorole [ add | remove | list ] <@role>";
    }
}
