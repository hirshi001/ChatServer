package common;

import com.hirshi001.buffer.bufferfactory.BufferFactory;
import com.hirshi001.buffer.bufferfactory.DefaultBufferFactory;
import com.hirshi001.javanetworking.JavaNetworkFactory;
import com.hirshi001.networking.network.NetworkFactory;
import com.hirshi001.networking.packetdecoderencoder.PacketEncoderDecoder;
import com.hirshi001.networking.packetdecoderencoder.SimplePacketEncoderDecoder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Common {

    public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
    public static NetworkFactory networkFactory = new JavaNetworkFactory(executor);
    public static int port = 5555;
    public static PacketEncoderDecoder packetEncoderDecoder = new SimplePacketEncoderDecoder();
    public static BufferFactory bufferFactory = new DefaultBufferFactory();


}
