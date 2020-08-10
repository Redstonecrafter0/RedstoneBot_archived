package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.OnlineStatus;
import net.redstonecraft.redstonebot.commands.privatecommands.RequestUnmute;
import net.redstonecraft.redstonebot.commands.servercommands.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    public static Main INSTANCE;

    public static JSONObject config;

    public static String prefix;
    public static String commandPrefix;
    public static String clientId;

    public static CommandManager commandManager;

    public static long startTime;

    public static SQL sql;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Discord.INSTANCE.getManager().setStatus(OnlineStatus.OFFLINE);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            Discord.INSTANCE.getManager().shutdown();
        }));

        new Terminal();
        try {
            JSONObject rootConfig = (JSONObject) new JSONParser().parse(new FileReader("config.json"));
            config = (JSONObject) rootConfig.get("config");
            prefix = (String) rootConfig.get("prefix");
            commandPrefix = (String) rootConfig.get("commandPrefix");
            clientId = (String) rootConfig.get("clientId");
            commandManager = new CommandManager();
            sql = new SQL("data.db");
            sql.update("CREATE TABLE IF NOT EXISTS members (dcId string, verifyId string, verified integer)");
            sql.update("CREATE TABLE IF NOT EXISTS muted (dcId string, until long)");
            INSTANCE = new Main((String) rootConfig.get("clientId"), (String) rootConfig.get("botToken"));
            startTime = System.currentTimeMillis();
            registerCommands();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public Main(String clientId, String botToken) {
        new Discord(clientId, botToken);
    }

    public static void registerCommands() {
        getCommandManager().registerServerCommand("setactivity", new SetActivity());
        getCommandManager().registerServerCommand("setautochannel", new SetAutoChannel());
        getCommandManager().registerServerCommand("setstatus", new SetStatus());
        getCommandManager().registerServerCommand("server", new Server());
        getCommandManager().registerServerCommand("ping", new Ping());
        getCommandManager().registerServerCommand("clear", new Clear());
        getCommandManager().registerServerCommand("verify", new SetVerify());
        getCommandManager().registerServerCommand("uptime", new Uptime());
        getCommandManager().registerServerCommand("date", new Date());
        getCommandManager().registerServerCommand("time", new Time());
        getCommandManager().registerServerCommand("say", new Say());
        getCommandManager().registerServerCommand("atomuhr", new AtomUhr());
        getCommandManager().registerServerCommand("atomzeit", new AtomZeit());
        getCommandManager().registerServerCommand("mute", new Mute());
        getCommandManager().registerServerCommand("unmute", new Unmute());
        getCommandManager().registerServerCommand("setmutedrole", new SetMutedRole());

        getCommandManager().registerPrivateCommand("requestunmute", new RequestUnmute());
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static Logger getLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    public static String randomString(String nums, int lenght) {
        StringBuilder builder = new StringBuilder();
        while (lenght-- != 0) {
            int character = (int)(Math.random()*nums.length());
            builder.append(nums.charAt(character));
        }
        return builder.toString();
    }

    public static void saveConfig() {
        try {
            JSONObject rootConfig = (JSONObject) new JSONParser().parse(new FileReader("config.json"));
            rootConfig.remove("config");
            rootConfig.put("config", Main.config);
            FileWriter writer = new FileWriter("config.json");
            writer.write(Main.prettyPrintJSON(rootConfig.toJSONString()));
            writer.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String prettyPrintJSON(String unformattedJsonString) {
        StringBuilder prettyJSONBuilder = new StringBuilder();
        int indentLevel = 0;
        boolean inQuote = false;
        for(char charFromUnformattedJson : unformattedJsonString.toCharArray()) {
            switch(charFromUnformattedJson) {
                case '"':
                    inQuote = !inQuote;
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    break;
                case ' ':
                    if(inQuote) {
                        prettyJSONBuilder.append(charFromUnformattedJson);
                    }
                    break;
                case '{':
                case '[':
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    indentLevel++;
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder);
                    break;
                case '}':
                case ']':
                    indentLevel--;
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder);
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    break;
                case ',':
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    if(!inQuote) {
                        appendIndentedNewLine(indentLevel, prettyJSONBuilder);
                    }
                    break;
                default:
                    prettyJSONBuilder.append(charFromUnformattedJson);
            }
        }
        return prettyJSONBuilder.toString();
    }

    private static void appendIndentedNewLine(int indentLevel, StringBuilder stringBuilder) {
        stringBuilder.append("\n");
        for(int i = 0; i < indentLevel; i++) {
            stringBuilder.append("  ");
        }
    }
}
