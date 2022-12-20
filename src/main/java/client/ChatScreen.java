package client;

import common.ChatPacket;

public class ChatScreen extends Screen {

    String help = """
            \t1. Type /help to see this message
            \t2. Type /quit to quit
            """;

    @Override
    public void tick() {
        System.out.println("Welcome to chat room");
        System.out.println(help);
        while(true) {
            String message = scanner.nextLine();
            if(message.equals("/help")) {
                System.out.println(help);
                continue;
            }
            if(message.equals("/quit")) {
                setScreen(new LoginScreen());
                return;
            }

            try {
                client.sendTCP(new ChatPacket(ChatClient.username, message), null).perform();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
