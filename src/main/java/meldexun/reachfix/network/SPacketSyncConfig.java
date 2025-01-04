package meldexun.reachfix.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.NoArgsConstructor;
import meldexun.configutil.ConfigUtil;
import meldexun.reachfix.config.ReachFixConfig;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
public class SPacketSyncConfig implements IMessage {

    private ByteBuf buffer;

    public SPacketSyncConfig(ReachFixConfig config) throws ReflectiveOperationException {
        buffer = Unpooled.buffer();
        ConfigUtil.writeServerSettings(config, buffer);
    }

    @Override
    public void fromBytes(@NotNull ByteBuf buf) {
        int bytes = buf.readableBytes();
        buffer = buf.readBytes(bytes);
    }

    @Override
    public void toBytes(@NotNull ByteBuf buf) {
        buf.writeBytes(buffer);
    }
}
