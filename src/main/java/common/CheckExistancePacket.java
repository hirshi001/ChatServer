package common;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.networking.packet.Packet;
import com.hirshi001.networking.util.defaultpackets.objectpackets.ObjectPacket;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class CheckExistancePacket extends ObjectPacket {

    public String type;

    public CheckExistancePacket(Serializable object, String type) {
        super(object);
        this.type = type;
    }

    public CheckExistancePacket() {
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        ByteBufUtil.writeStringToBuf(StandardCharsets.UTF_8, type, out);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        type = ByteBufUtil.readStringFromBuf(StandardCharsets.UTF_8, in);
    }

}
