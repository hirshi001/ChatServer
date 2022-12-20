package client;

import com.hirshi001.javanetworking.client.JavaClientChannel;
import com.hirshi001.networking.network.channel.AbstractChannelListener;
import com.hirshi001.networking.network.channel.Channel;
import com.hirshi001.networking.network.channel.ChannelInitializer;
import com.hirshi001.networking.network.channel.ChannelOption;
import com.hirshi001.networking.network.client.Client;
import com.hirshi001.networking.networkdata.DefaultNetworkData;
import com.hirshi001.networking.networkdata.NetworkData;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;
import com.hirshi001.networking.packetregistrycontainer.PacketRegistryContainer;
import com.hirshi001.networking.packetregistrycontainer.SinglePacketRegistryContainer;
import common.*;
import server.ChatServer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ChatClient {

    public static Screen screen;
    public static String username;
    public static String password;

    public static Thread mainThread;

    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter host ip: ");
        String host = scanner.nextLine();

        mainThread = Thread.currentThread();
        PacketRegistryContainer packetRegistryContainer = new SinglePacketRegistryContainer();

        packetRegistryContainer.getDefaultRegistry()
                .registerDefaultPrimitivePackets()
                .registerDefaultArrayPrimitivePackets()
                .register(LoginPacket::new, null, LoginPacket.class, 1)
                .register(RegisterPacket::new, null, RegisterPacket.class, 2)
                .register(CheckExistancePacket::new, null, CheckExistancePacket.class, 3)
                .register(KickPacket::new, ChatClient::handleKickPacket, KickPacket.class, 4)
                .register(LoginResultPacket::new, null, LoginResultPacket.class, 5)
                .register(ChatPacket::new, ChatClient::handleChatPacket, ChatPacket.class, 6);

        NetworkData networkData = new DefaultNetworkData(Common.packetEncoderDecoder, packetRegistryContainer);

        Client client = Common.networkFactory.createClient(networkData, Common.bufferFactory, host, Common.port);

        client.setChannelInitializer(new ChannelInitializer() {
            @Override
            public void initChannel(Channel channel) {
                channel.setChannelOption(ChannelOption.DEFAULT_SWITCH_PROTOCOL, true);
                channel.setChannelOption(ChannelOption.TCP_AUTO_FLUSH, true);
                channel.setChannelOption(ChannelOption.UDP_AUTO_FLUSH, true);
            }
        });

        client.startUDP().perform();
        client.startTCP().perform();


        Field field =  JavaClientChannel.class.getDeclaredField("localPort");
        field.setAccessible(true);
        int localPort = (int) field.get(client.getChannel());
        System.out.println(localPort);

        Screen.client = client;

        screen = new StartScreen();
        while(true){
            try {
                screen.tick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void handleKickPacket(PacketHandlerContext<KickPacket> context){
        System.out.println("Kicked");
        System.out.println("Reason: " + context.packet.message);
        screen = new StartScreen();
        mainThread.interrupt();
    }

    public static void handleChatPacket(PacketHandlerContext<ChatPacket> context){
        System.out.println(context.packet.username + ": " + context.packet.message);
    }

}
