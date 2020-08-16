package net.redstonecraft.redstonebot.commands.servercommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.redstonebot.Discord;
import net.redstonecraft.redstonebot.Main;
import net.redstonecraft.redstonebot.interfaces.ServerCommand;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.xbill.DNS.*;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class ServerStatus implements ServerCommand {
    @Override
    public boolean onCommand(TextChannel channel, Member member, Message message, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String host;
        int port = 25565;
        if (args[0].contains(":")) {
            host = args[0].split(":")[0];
            try {
                port = Integer.parseInt(args[0].split(":")[1]);
            } catch (Exception ignored) {
                return false;
            }
        } else {
            host = args[0];
        }
        try {
            SRVRecord srvRecord = (SRVRecord) lookupRecord("_minecraft._tcp." + host, Type.SRV);
            host = srvRecord.getTarget().toString().replaceFirst("\\.$","");
            final Socket socket = new Socket(host, port);
            final DataInputStream in = new DataInputStream(socket.getInputStream());
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
            DataOutputStream handshake = new DataOutputStream(handshake_bytes);
            handshake.writeByte(0x00);
            writeVarInt(handshake, 4);
            writeVarInt(handshake, host.length());
            handshake.writeBytes(host);
            handshake.writeShort(port);
            writeVarInt(handshake, 1);
            writeVarInt(out, handshake_bytes.size());
            out.write(handshake_bytes.toByteArray());
            out.writeByte(0x01);
            out.writeByte(0x00);
            int length = readVarInt(in);
            byte[] data = new byte[length];
            in.readFully(data);
            String json = new String(data, "UTF-8").substring(3);
            JSONObject root = (JSONObject) new JSONParser().parse(json);
            JSONObject version = (JSONObject) root.get("version");
            String versionName = (String) version.get("name");
            JSONObject players = (JSONObject) root.get("players");
            String online = String.valueOf((Long) players.get("online"));
            String maxPlayers = String.valueOf((Long) players.get("max"));
            StringBuilder sb = new StringBuilder();
            if (root.get("description") instanceof JSONObject) {
                JSONObject description = (JSONObject) root.get("description");
                sb.append(removeColor((String) description.get("text")));
                JSONArray extra = (JSONArray) description.get("extra");
                if (extra != null) {
                    for (Object o : extra) {
                        JSONObject i = (JSONObject) o;
                        sb.append(removeColor((String) i.get("text")));
                    }
                }
            } else if (root.get("description") instanceof String) {
                sb.append(removeColor((String) root.get("description")));
            }
            String b64image = (String) root.get("favicon");
            File file = new File("tmp3.png");
            if (!file.exists()) {
                file.createNewFile();
            }
            ImageIO.write(b64toImage(b64image), "png", file);
            EmbedBuilder eb = new EmbedBuilder();
            if (args[0].contains(":")) {
                eb.setTitle(args[0].split(":")[0]);
            } else {
                eb.setTitle(args[0]);
            }
            eb.setDescription("**Version: " + versionName + "**\n" + sb.toString());
            eb.addField("Spieler", online + "/" + maxPlayers, false);
            eb.setColor(Color.decode("#00FF00"));
            eb.setThumbnail(Objects.requireNonNull(Discord.INSTANCE.getManager().getUserById((String) Main.config.get("trashId"))).openPrivateChannel().complete().sendFile(file).complete().getAttachments().get(0).getUrl());
            channel.sendMessage(eb.build()).queue();
        } catch (Exception ignored) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(Main.prefix);
            eb.setColor(Color.decode("#FF0000"));
            eb.setDescription("Der Server wurde nicht gefunden oder es gab einen Fehler.");
            channel.sendMessage(eb.build()).queue();
        }
        return true;
    }

    @Override
    public MessageEmbed.Field help() {
        return new MessageEmbed.Field(Main.commandPrefix + " serverstatus", "Gibt Informationen zum angegebenen Minecraft Server.", false);
    }

    @Override
    public String usage() {
        return Main.commandPrefix + " serverstatus [ ip | ip:port ]";
    }

    private static RenderedImage b64toImage(String base64) throws IOException {
        String imageString = base64.split(",")[1];
        byte[] imageBytes = new BASE64Decoder().decodeBuffer(imageString);
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }

    private static void writeVarInt(DataOutputStream out, int paramInt) {
        try {
            while (true) {
                if ((paramInt & 0xFFFFFF80) == 0) {
                    out.writeByte(paramInt);
                    return;
                }
                out.writeByte(paramInt & 0x7F | 0x80);
                paramInt >>>= 7;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int readVarInt(DataInputStream in) {
        int i = 0;
        int j = 0;
        while (true) {
            try {
                int k = in.readByte();
                i |= (k & 0x7F) << j++ * 7;
                if (j > 5) {
                    return 0;
                }
                if ((k & 0x80) != 128)
                    break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    private static Record lookupRecord(String hostName, int type) throws UnknownHostException {
        Record record;
        Lookup lookup;
        int result;
        try {
            lookup = new Lookup(hostName, type);
        } catch (TextParseException e) {
            throw new UnknownHostException("FormatExeption");
        }
        lookup.run();
        result = lookup.getResult();
        if (result == Lookup.SUCCESSFUL) {
            return lookup.getAnswers()[0];
        } else {
            throw new UnknownHostException("SRV Lookup error");
        }
    }

    private static String removeColor(String in) {
        in = in.replaceAll("§0", "");
        in = in.replaceAll("§1", "");
        in = in.replaceAll("§2", "");
        in = in.replaceAll("§3", "");
        in = in.replaceAll("§4", "");
        in = in.replaceAll("§5", "");
        in = in.replaceAll("§6", "");
        in = in.replaceAll("§7", "");
        in = in.replaceAll("§8", "");
        in = in.replaceAll("§9", "");
        in = in.replaceAll("§a", "");
        in = in.replaceAll("§b", "");
        in = in.replaceAll("§c", "");
        in = in.replaceAll("§d", "");
        in = in.replaceAll("§e", "");
        in = in.replaceAll("§f", "");
        in = in.replaceAll("§k", "");
        in = in.replaceAll("§l", "");
        in = in.replaceAll("§m", "");
        in = in.replaceAll("§n", "");
        in = in.replaceAll("§o", "");
        in = in.replaceAll("§r", "");
        return in;
    }
}
