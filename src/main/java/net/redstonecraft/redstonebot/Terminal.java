package net.redstonecraft.redstonebot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Terminal {

    public Terminal(Main main) {
        new Thread(() -> {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while ((line = reader.readLine()) != null) {
                    switch (line) {
                        case "exit":
                        case "stop":
                            System.exit(0);
                        case "invite":
                            String url = "https://discord.com/api/oauth2/authorize?client_id=" + Main.clientId + "&scope=bot&permissions=8";
                            Main.getLogger().info("Connect your Discord Bot to your server by visiting the page: " + url);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
