package common;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.networking.packet.Packet;

import java.nio.charset.StandardCharsets;

public class RegisterPacket extends Packet {

    public String username, password;

    public RegisterPacket() {
        super();
    }

    public RegisterPacket(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        ByteBufUtil.writeStringToBuf(StandardCharsets.UTF_8, username, out);
        ByteBufUtil.writeStringToBuf(StandardCharsets.UTF_8, password, out);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        username = ByteBufUtil.readStringFromBuf(StandardCharsets.UTF_8, in);
        password = ByteBufUtil.readStringFromBuf(StandardCharsets.UTF_8, in);
    }
}
