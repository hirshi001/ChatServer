package common;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.networking.packet.Packet;

import java.nio.charset.StandardCharsets;

public class KickPacket extends Packet {

    public String message;

    public KickPacket(String message) {
        this.message = message;
    }

    public KickPacket() {
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        ByteBufUtil.writeStringToBuf(StandardCharsets.UTF_8, message, out);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        message = ByteBufUtil.readStringFromBuf(StandardCharsets.UTF_8, in);
    }
}
