package client;

import com.hirshi001.networking.packet.Packet;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;
import com.hirshi001.networking.util.defaultpackets.primitivepackets.BooleanPacket;
import com.hirshi001.networking.util.defaultpackets.primitivepackets.StringPacket;
import common.CheckExistancePacket;
import common.RegisterPacket;

import java.util.concurrent.ExecutionException;

public class RegisterScreen extends Screen{

    @Override
    public void tick() throws ExecutionException, InterruptedException {


        String username, password;
        while(true) {

            while (true) {
                System.out.println("Enter your desired username: ");
                username = scanner.nextLine();

                boolean exists = client.sendTCPWithResponse(new CheckExistancePacket(username, "username"), null, 1000)
                        .map((ctx) -> ((BooleanPacket) ctx.packet).value).perform().get();
                if (exists) System.out.println("Username already exists");
                else break;
            }

            System.out.println("Enter your desired password: ");
            password = scanner.nextLine();

            Packet packet = client.sendTCPWithResponse(new RegisterPacket(username, password), null, 1000).
                    map(ctx -> ctx.packet).perform().get();

            if (packet instanceof BooleanPacket) {
                if (((BooleanPacket) packet).value) {
                    System.out.println("Registration successful!");
                    ChatClient.username = username;
                    ChatClient.password = password;
                    setScreen(new ChatScreen());
                    return;
                } else {
                    System.out.println("Registration failed!");
                }
            } else if (packet instanceof StringPacket) {
                System.out.println("Registration failed!:[Server]" + ((StringPacket) packet).value);
            } else {
                System.out.println("Registration failed!" + packet);
            }

        }


    }
}
