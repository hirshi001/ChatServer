package server;

import com.hirshi001.networking.network.channel.Channel;
import com.hirshi001.networking.network.channel.ChannelInitializer;
import com.hirshi001.networking.network.channel.ChannelOption;
import com.hirshi001.networking.network.server.AbstractServerListener;
import com.hirshi001.networking.network.server.Server;
import com.hirshi001.networking.networkdata.DefaultNetworkData;
import com.hirshi001.networking.networkdata.NetworkData;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;
import com.hirshi001.networking.packetregistrycontainer.PacketRegistryContainer;
import com.hirshi001.networking.packetregistrycontainer.SinglePacketRegistryContainer;
import com.hirshi001.networking.util.defaultpackets.primitivepackets.BooleanPacket;
import common.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatServer {

    public static final Accounts ACCOUNTS = new Accounts();

    public static void main(String[] args) throws IOException {

        loadAccounts();


        PacketRegistryContainer packetRegistryContainer = new SinglePacketRegistryContainer();

        packetRegistryContainer.getDefaultRegistry()
                .registerDefaultPrimitivePackets()
                .registerDefaultArrayPrimitivePackets()
                .register(LoginPacket::new, ChatServer::handleLoginPacket, LoginPacket.class, 1)
                .register(RegisterPacket::new, ChatServer::handleRegisterPacket, RegisterPacket.class, 2)
                .register(CheckExistancePacket::new, ChatServer::handleCheckExistencePacket, CheckExistancePacket.class, 3)
                .register(KickPacket::new, null, KickPacket.class, 4)
                .register(LoginResultPacket::new, null, LoginResultPacket.class, 5)
                .register(ChatPacket::new, ChatServer::handleChatPacket, ChatPacket.class, 6);

        NetworkData networkData = new DefaultNetworkData(Common.packetEncoderDecoder, packetRegistryContainer);
        Server server = Common.networkFactory.createServer(networkData, Common.bufferFactory, Common.port);

        server.setChannelInitializer(new ChannelInitializer() {
            @Override
            public void initChannel(Channel channel) {
                channel.setChannelOption(ChannelOption.DEFAULT_SWITCH_PROTOCOL, true);
                channel.setChannelOption(ChannelOption.TCP_AUTO_FLUSH, true);
                channel.setChannelOption(ChannelOption.UDP_AUTO_FLUSH, true);
            }
        });


        server.addServerListener(new AbstractServerListener() {
            @Override
            public void onClientConnect(Server server, Channel clientChannel) {
                super.onClientConnect(server, clientChannel);
                System.out.println("Client connected " + clientChannel.getIp() + " : " + clientChannel.getPort());
            }

            @Override
            public void onReceived(PacketHandlerContext<?> context) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.onReceived(context);
            }
        });

        System.out.println("Starting Server...");

        server.startUDP().perform();
        server.startTCP().perform();

        System.out.println("Server started");

    }

    public static void loadAccounts() throws IOException {

        File file = new File("accounts.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<String> lines = Files.readAllLines(file.toPath());

        Iterator<String> iterator = lines.iterator();
        while(iterator.hasNext()){
            String username = iterator.next();
            String password = iterator.next();

            ACCOUNTS.addAccount(username, password);
        }

    }

    public static synchronized void saveAccounts() throws FileNotFoundException {
        File file = new File("accounts.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter pw = new PrintWriter(new FileOutputStream(file));
        for(Map.Entry<String, String> entry: ACCOUNTS.getAccounts().entrySet()){
            pw.println(entry.getKey());
            pw.println(entry.getValue());
        }
        pw.flush();
        pw.close();
    }

    public static void handleLoginPacket(PacketHandlerContext<LoginPacket> context){
        System.out.println(context.channel.getPort());

        String username = context.packet.username;
        String password = context.packet.password;


        boolean loggedIn = ACCOUNTS.login(context.channel, username, password, true);
        if(loggedIn){
            context.channel.sendTCP(new LoginResultPacket(true, "Login with name " + username + " successful")
                    .setResponsePacket(context.packet), null).perform();
            loginSuccessful(context.networkSide.asServer(), username);
        }else{
            System.out.println("Login failed");
            context.channel.sendTCP(new LoginResultPacket(false, "Login failed")
                    .setResponsePacket(context.packet), null).perform();
        }
    }

    public static void handleRegisterPacket(PacketHandlerContext<RegisterPacket> ctx){
        System.out.println("RegisterPacket received");
        System.out.println("Username: " + ctx.packet.username);
        System.out.println("Password: " + ctx.packet.password);
        System.out.println(ctx.channel.getPort());

        String username = ctx.packet.username;
        String password = ctx.packet.password;

        if(ACCOUNTS.addAccount(username, password)){
            System.out.println("Register successful");
            ACCOUNTS.login(ctx.channel, username, password, true);
            try {
                saveAccounts();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ctx.channel.sendTCP(new BooleanPacket(true).setResponsePacket(ctx.packet), null).perform();
            loginSuccessful(ctx.networkSide.asServer(), username);
        }
        else{
            System.out.println("Registration failed");
            ctx.channel.sendTCP(new BooleanPacket(false).setResponsePacket(ctx.packet), null).perform();
        }
    }

    private static void loginSuccessful(Server server, String username){
        server.getClients().sendTCPToAll(new ChatPacket("SERVER", username + " has logged in"), null).perform();
    }

    public static void handleCheckExistencePacket(PacketHandlerContext<CheckExistancePacket> ctx){

        boolean exists;
        String type = ctx.packet.type;
        Object object = ctx.packet.object;
        try {
            switch (type) {
                case "username":
                    exists = ACCOUNTS.usernameExists((String) object);
                    break;
                default:
                    exists = false;
                    break;
            }
        }catch(ClassCastException e){
            exists = false;
        }

        ctx.channel.sendTCP(new BooleanPacket(exists).setResponsePacket(ctx.packet), null).perform();
    }

    public static void handleChatPacket(PacketHandlerContext<ChatPacket> ctx){
        if(!ACCOUNTS.loggedIn(ctx.channel) || !ACCOUNTS.loggedIn(ctx.packet.username)) return;

        System.out.println(ctx.packet.username + ": " + ctx.packet.message);

        ACCOUNTS.loggedInChannels().forEach(channel -> {
            channel.sendTCP(new ChatPacket(ctx.packet.username, ctx.packet.message), null).perform();
        });
    }

}
