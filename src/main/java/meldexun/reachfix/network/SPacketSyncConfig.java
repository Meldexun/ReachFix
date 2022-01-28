package meldexun.reachfix.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SPacketSyncConfig implements IMessage {

	private boolean enabled;
	private double reach;
	private double reachCreative;

	public SPacketSyncConfig() {

	}

	public SPacketSyncConfig(boolean enabled, double reach, double reachCreative) {
		this.enabled = enabled;
		this.reach = reach;
		this.reachCreative = reachCreative;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		enabled = buf.readBoolean();
		reach = buf.readDouble();
		reachCreative = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(enabled);
		buf.writeDouble(reach);
		buf.writeDouble(reachCreative);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public double getReach() {
		return reach;
	}

	public double getReachCreative() {
		return reachCreative;
	}

}
