package client;

import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;
import com.hirshi001.networking.util.defaultpackets.primitivepackets.BooleanPacket;
import com.hirshi001.restapi.RestFuture;
import common.LoginPacket;
import common.LoginResultPacket;

import java.util.concurrent.ExecutionException;

public class LoginScreen extends Screen{
    @Override
    public void tick() throws ExecutionException, InterruptedException {
        System.out.println("Preparing to login...");

        while(true) {
            System.out.println("Enter your username: ");
            final String username = scanner.nextLine();
            System.out.println("Enter your password: ");
            final String password = scanner.nextLine();

            LoginResultPacket result = client.sendTCPWithResponse(new LoginPacket(username, password), null, 100)
                    .map((ctx) -> {
                        System.out.println(ctx.packet);
                        return ((LoginResultPacket)ctx.packet);
                    }).perform().get();

            System.out.println("Result received: " + result);
            if(result!=null && result.success){
                ChatClient.username = username;
                ChatClient.password = password;
                setScreen(new ChatScreen());
                return;
            }else{
                System.out.println("Login failed!");
            }
        }
    }
}
