package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Chat implements ServerCommand {

    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(Main.prefix);
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            if (args.length == 0) {
                return false;
            } else {
                switch (args[0]) {
                    case "setup":
                        if (args.length == 4) {
                            try {
                                int allow = sqlBool(args[3]);
                                String channelId = Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getTextChannelById(args[2])).getId();
                                if (message.getMentionedRoles().size() == 0) {
                                    Permission permission = Permission.valueOf(args[0]);
                                    if (permission.equals(Permission.UNKNOWN)) {
                                        return false;
                                    }
                                    ResultSet rs = Main.sql.query("SELECT * FROM chatfilterpermission WHERE permission = '" + permission.name() + "' AND channelId = '" + channelId + "'");
                                    if (rs.isClosed()) {
                                        if (allow == 1) {
                                            Main.sql.update("INSERT INTO chatfilterpermission VALUES ('" + permission.name() + "', '" + channelId + "')");
                                        } else {
                                            eb.setColor(Color.decode("#ff0000"));
                                            eb.setDescription("Diese Einstellung ist bereits auf deny eingestellt.");
                                        }
                                    } else if (!rs.isClosed()) {
                                        if (allow == 0) {
                                            Main.sql.update("DELETE FROM chatfilterpermission WHERE permission = '" + permission.name() + "' AND channelId = '" + channelId + "'");
                                        } else {
                                            eb.setColor(Color.decode("#ff0000"));
                                            eb.setDescription("Diese Einstellung ist bereits auf allow eingestellt.");
                                        }
                                    }
                                } else {
                                    Role role = Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getRoleById(args[0]));
                                    ResultSet rs = Main.sql.query("SELECT * FROM chatfilterrole WHERE roleId = '" + role.getId() + "' AND channelId = '" + channelId + "'");
                                    if (rs.isClosed()) {
                                        if (allow == 1) {
                                            Main.sql.update("INSERT INTO chatfilterrole VALUES ('" + role.getId() + "', '" + channelId + "')");
                                        } else {
                                            eb.setColor(Color.decode("#ff0000"));
                                            eb.setDescription("Diese Einstellung ist bereits auf deny eingestellt.");
                                        }
                                    } else if (!rs.isClosed()) {
                                        if (allow == 0) {
                                            Main.sql.update("DELETE FROM chatfilterrole WHERE roleId = '" + role.getId() + "' AND channelId = '" + channelId + "'");
                                        } else {
                                            eb.setColor(Color.decode("#ff0000"));
                                            eb.setDescription("Diese Einstellung ist bereits auf allow eingestellt.");
                                        }
                                    }
                                }
                            } catch (Exception ignored) {
                                return false;
                            }
                        } else {
                            if (args.length == 2) {
                                if (args[1].equals("list")) {
                                    eb.setColor(Color.decode("#00ff00"));
                                    boolean s = false;
                                    ResultSet rs1 = Main.sql.query("SELECT * FROM chatfilterpermission");
                                    ResultSet rs2 = Main.sql.query("SELECT * FROM chatfilterrole");
                                    try {
                                        if (!rs1.isClosed()) {
                                            s = true;
                                            while (rs1.next()) {
                                                eb.addField(Permission.valueOf(rs1.getString("permission")).getName(), Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getTextChannelById(rs1.getString("channelId"))).getAsMention(), false);
                                            }
                                        }
                                        if (!rs2.isClosed()) {
                                            s = true;
                                            while (rs2.next()) {
                                                eb.addField(Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getRoleById(rs2.getString("roleId"))).getName(), Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getTextChannelById(rs2.getString("channelId"))).getAsMention(), false);
                                            }
                                        }
                                    } catch (SQLException ignored) {
                                    }
                                    if (s) {
                                        eb.setDescription("Die Einstellungen sind:");
                                    } else {
                                        eb.setDescription("Es wurde noch nichts eingerichtet.");
                                    }
                                } else {
                                    eb.setColor(Color.decode("#ff0000"));
                                    eb.setDescription("```diff\n- " + Main.commandPrefix + " chat setup [ @role | permission ] [channelid] [ allow | deny ]```");
                                }
                            } else {
                                eb.setColor(Color.decode("#ff0000"));
                                eb.setDescription("```diff\n- " + Main.commandPrefix + " chat setup [ @role | permission | list ] <channelid> < allow | deny >```");
                            }
                        }
                        break;
                    case "tokens":
                        try {
                            Member m = message.getMentionedMembers().get(0);
                            ResultSet rs = Main.sql.query("SELECT * FROM chatfiltertokens WHERE dcId = '" + m.getId() + "'");
                            if (((args.length == 3) || (args.length == 4)) && (message.getMentionedMembers().size() == 1)) {
                                int tokens = 0;
                                if (!rs.isClosed()) {
                                    tokens = rs.getInt("tokens");
                                }
                                switch (args[1]) {
                                    case "set":
                                        if (args.length == 4) {
                                            try {
                                                eb.setColor(Color.decode("#00ff00"));
                                                if ((Integer.parseInt(args[3]) != 0) && (args[3].contains("-"))) {
                                                    if (rs.isClosed()) {
                                                        Main.sql.update("INSERT INTO chatfiltertokens VALUES ('" + m.getId() + "', '" + Integer.parseInt(args[3]) + "')");
                                                    } else {
                                                        Main.sql.update("UPDATE chatfiltertokens SET tokens = '" + Integer.parseInt(args[3]) + "' WHERE dcId = '" + m.getId() + "'");
                                                    }
                                                    eb.setDescription(m.getAsMention() + " hat nun " + Integer.parseInt(args[3]) + " Tokens.");
                                                } else {
                                                    if (Integer.parseInt(args[3]) == 0) {
                                                        Main.sql.update("DELETE FROM chatfiltertokens WHERE dcId = '" + m.getId() + "'");
                                                        eb.setDescription(m.getAsMention() + " hat nun 0 Tokens.");
                                                    } else {
                                                        return false;
                                                    }
                                                }
                                            } catch (Exception ignored) {
                                                return false;
                                            }
                                        } else {
                                            return false;
                                        }
                                        break;
                                    case "add":
                                        if (args.length == 4) {
                                            try {
                                                eb.setColor(Color.decode("#00ff00"));
                                                int c = Integer.parseInt(args[3]) + tokens;
                                                if (c <= 0) {
                                                    if (rs.isClosed()) {
                                                        Main.sql.update("INSERT INTO chatfiltertokens VALUES ('" + m.getId() + "', '" + c + "')");
                                                    } else {
                                                        Main.sql.update("UPDATE chatfiltertokens SET tokens = '" + c + "' WHERE dcId = '" + m.getId() + "'");
                                                    }
                                                    eb.setDescription(m.getAsMention() + " hat nun " + c + " Tokens.");
                                                } else {
                                                    if (Integer.parseInt(args[3]) == 0) {
                                                        Main.sql.update("DELETE FROM chatfiltertokens WHERE dcId = '" + m.getId() + "'");
                                                        eb.setDescription(m.getAsMention() + " hat nun 0 Tokens.");
                                                    } else {
                                                        return false;
                                                    }
                                                }
                                            } catch (Exception ignored) {
                                                return false;
                                            }
                                        } else {
                                            return false;
                                        }
                                        break;
                                    case "remove":
                                        if (args.length == 4) {
                                            try {
                                                eb.setColor(Color.decode("#00ff00"));
                                                int c = tokens - Integer.parseInt(args[3]);
                                                if (c <= 0) {
                                                    if (rs.isClosed()) {
                                                        Main.sql.update("INSERT INTO chatfiltertokens VALUES ('" + m.getId() + "', '" + c + "')");
                                                    } else {
                                                        Main.sql.update("UPDATE chatfiltertokens SET tokens = '" + c + "' WHERE dcId = '" + m.getId() + "'");
                                                    }
                                                    eb.setDescription(m.getAsMention() + " hat nun " + c + " Tokens.");
                                                } else {
                                                    if (Integer.parseInt(args[3]) == 0) {
                                                        Main.sql.update("DELETE FROM chatfiltertokens WHERE dcId = '" + m.getId() + "'");
                                                        eb.setDescription(m.getAsMention() + " hat nun 0 Tokens.");
                                                    } else {
                                                        return false;
                                                    }
                                                }
                                            } catch (Exception ignored) {
                                                return false;
                                            }
                                        } else {
                                            return false;
                                        }
                                        break;
                                    case "get":
                                        if (args.length == 4) {
                                            try {
                                                eb.setColor(Color.decode("#00ff00"));
                                                if (rs.isClosed()) {
                                                    eb.setDescription(m.getAsMention() + " hat 0 Tokens.");
                                                } else {
                                                    eb.setDescription(m.getAsMention() + " hat " + rs.getInt("tokens") + " Tokens.");
                                                }
                                            } catch (Exception ignored) {
                                                return false;
                                            }
                                        } else {
                                            return false;
                                        }
                                        break;
                                    default:
                                        eb.setColor(Color.decode("#ff0000"));
                                        eb.setDescription("```diff\n- " + Main.commandPrefix + " chat tokens [ set | get | add | remove | list ] <@member> <amount>```");
                                        break;
                                }
                            } else {
                                if (args.length == 2) {
                                    if (args[1].equals("list")) {
                                        eb.setColor(Color.decode("#00ff00"));
                                        boolean s = false;
                                        ResultSet rs1 = Main.sql.query("SELECT * FROM chatfiltertokens");
                                        try {
                                            if (!rs1.isClosed()) {
                                                s = true;
                                                while (rs1.next()) {
                                                    eb.addField(Objects.requireNonNull(Objects.requireNonNull(Discord.INSTANCE.getJda().getGuildById((String) Main.config.get("guild"))).getMemberById(rs1.getString("dcId"))).getUser().getAsTag(), "hat gerade " + rs.getInt("tokens") + " Tokens.", false);
                                                }
                                            }
                                        } catch (SQLException ignored) {
                                        }
                                        if (s) {
                                            eb.setDescription("Es hat noch niemand Tokens.");
                                        } else {
                                            eb.setDescription("Die Spieler die Tokens haben haben so viele Tokens:");
                                        }
                                    } else {
                                        eb.setColor(Color.decode("#ff0000"));
                                        eb.setDescription("```diff\n- " + Main.commandPrefix + " chat tokens [ set | get | add | remove | list ] <@member> <amount>```");
                                    }
                                } else {
                                    eb.setColor(Color.decode("#ff0000"));
                                    eb.setDescription("```diff\n- " + Main.commandPrefix + " chat tokens [ set | get | add | remove | list ] <@member> <amount>```");
                                }
                            }
                        } catch (Exception ignored) {
                            return false;
                        }
                        break;
                    case "filter":
                        if (((args.length == 2) || (args.length == 3) || (args.length == 5)) && (message.getMentionedMembers().size() == 1)) {
                            switch (args[1]) {
                                case "add":
                                    if (args.length == 5) {
                                        try {
                                            ResultSet rs = Main.sql.query("SELECT * FROM chatfilter WHERE cost = '" + args[2] + "' AND text = '" + args[3] + "'");
                                            if (rs.isClosed()) {
                                                Main.sql.update("INSERT INTO chatfilter VALUES ('" + args[2] + "', '" + args[3] + "')");
                                                eb.setColor(Color.decode("#00ff00"));
                                                eb.setDescription("Ein Chatfilter wurde hinzugefügt.");
                                            } else {
                                                eb.setColor(Color.decode("#ff0000"));
                                                eb.setDescription("Dieser Filter wurde schon eingestellt.");
                                            }
                                        } catch (Exception ignored) {
                                            return false;
                                        }
                                    } else {
                                        return false;
                                    }
                                    break;
                                case "remove":
                                    if (args.length == 5) {
                                        try {
                                            ResultSet rs = Main.sql.query("SELECT * FROM chatfilter WHERE cost = '" + args[2] + "' AND text = '" + args[3] + "'");
                                            if (!rs.isClosed()) {
                                                Main.sql.update("DELETE FROM chatfilter WHERE cost = '" + args[2] + "' AND text = '" + args[3] + "'");
                                                eb.setColor(Color.decode("#00ff00"));
                                                eb.setDescription("Der Chatfilter wurde entfernt.");
                                            } else {
                                                eb.setColor(Color.decode("#ff0000"));
                                                eb.setDescription("Dieser Filter existiert nicht.");
                                            }
                                        } catch (Exception ignored) {
                                            return false;
                                        }
                                    } else {
                                        return false;
                                    }
                                    break;
                                case "list":
                                    if (args.length == 3) {
                                        ResultSet rs = Main.sql.query("SELECT * FROM chatfiler");
                                        try {
                                            while (rs.next()) {
                                                eb.addField(args[2].toUpperCase(), rs.getString("text"), false);
                                            }
                                            if (eb.getFields().size() == 0) {
                                                eb.setDescription("Es wurde noch nichts eingestellt.");
                                            }
                                        } catch (Exception ignored) {
                                            return false;
                                        }
                                    } else if (args.length == 2) {
                                        ResultSet rs = Main.sql.query("SELECT * FROM chatfiler");
                                        try {
                                            while (rs.next()) {
                                                eb.addField(rs.getString("cost") + " Tokens", rs.getString("text"), false);
                                            }
                                        } catch (Exception ignored) {
                                            return false;
                                        }
                                    } else {
                                        return false;
                                    }
                                    break;
                                default:
                                    return false;
                            }
                        } else {
                            eb.setColor(Color.decode("#ff0000"));
                            eb.setDescription("```diff\n- " + Main.commandPrefix + " chat filter [ add | remove | list ] <cost> <text>```");
                        }
                        break;
                    default:
                        return false;
                }
            }
        } else {
            eb.setColor(Color.decode("#ff0000"));
            eb.setDescription("Dir fehlt die Berechtigung Administrator.");
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " chat", "Stellt den Chatfilter ein.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " chat [ setup | tokens | filter ] [ list | ... ]";
    }

    private int sqlBool(String input) throws Exception {
        if (input.equals("allow")) {
            return 1;
        } else if (input.equals("deny")) {
            return 0;
        } else {
            throw new Exception("Value not accepted");
        }
    }

}
