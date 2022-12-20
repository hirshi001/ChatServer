package common;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.networking.packet.Packet;

import java.nio.charset.StandardCharsets;

public class ChatPacket extends Packet {

        public String message;
        public String username;

        public ChatPacket(String username, String message) {
            this.username = username;
            this.message = message;
        }

        public ChatPacket() {
        }

        @Override
        public void writeBytes(ByteBuffer out) {
            super.writeBytes(out);
            ByteBufUtil.writeStringToBuf(StandardCharsets.UTF_8, username, out);
            ByteBufUtil.writeStringToBuf(StandardCharsets.UTF_8, message, out);
        }

        @Override
        public void readBytes(ByteBuffer in) {
            super.readBytes(in);
            username = ByteBufUtil.readStringFromBuf(StandardCharsets.UTF_8, in);
            message = ByteBufUtil.readStringFromBuf(StandardCharsets.UTF_8, in);
        }
}
