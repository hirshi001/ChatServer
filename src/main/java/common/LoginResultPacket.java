package common;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.networking.packet.Packet;

import java.nio.charset.StandardCharsets;

public class LoginResultPacket extends Packet {
    public boolean success;
    public String message;

    public LoginResultPacket(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResultPacket() {
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        out.writeBoolean(success);
        ByteBufUtil.writeStringToBuf(StandardCharsets.UTF_8, message, out);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        success = in.readBoolean();
        message = ByteBufUtil.readStringFromBuf(StandardCharsets.UTF_8, in);
    }
}
