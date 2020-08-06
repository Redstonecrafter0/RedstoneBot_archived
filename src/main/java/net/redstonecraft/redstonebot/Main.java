package net.redstonecraft.redstonebot;

import net.dv8tion.jda.api.OnlineStatus;
import net.redstonecraft.redstonebot.commands.servercommands.SetActivity;
import net.redstonecraft.redstonebot.commands.servercommands.SetAutoChannel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    public static Main INSTANCE;

    public static JSONObject config;

    public static String prefix;
    public static String commandPrefix;
    public static String clientId;

    public static CommandManager commandManager;

    private Terminal terminal;

    public static void main(String[] args) {
        try {
            JSONObject rootConfig = (JSONObject) new JSONParser().parse(new FileReader("config.json"));
            INSTANCE = new Main((String) rootConfig.get("clientId"), (String) rootConfig.get("botToken"));
            prefix = (String) rootConfig.get("prefix");
            commandPrefix = (String) rootConfig.get("commandPrefix");
            clientId = (String) rootConfig.get("clientId");
            config = (JSONObject) rootConfig.get("config");
            commandManager = new CommandManager();
            registerCommands();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread () {
            @Override
            public void run() {
                Discord.INSTANCE.getManager().setStatus(OnlineStatus.OFFLINE);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                Discord.INSTANCE.getManager().shutdown();
            }
        });
    }

    public Main(String clientId, String botToken) {
        new Discord(clientId, botToken);
        new Terminal(this);
    }

    public static void registerCommands() {
        getCommandManager().registerServerCommand("setactivity", new SetActivity());
        getCommandManager().registerServerCommand("setautochannel", new SetAutoChannel());
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static Logger getLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
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
