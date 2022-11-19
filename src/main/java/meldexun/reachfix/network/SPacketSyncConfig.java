package meldexun.reachfix.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SPacketSyncConfig implements IMessage {

	private boolean enabled;
	private double entityReach;
	private double entityReachCreative;
	private double reach;
	private double reachCreative;

	public SPacketSyncConfig() {

	}

	public SPacketSyncConfig(boolean enabled, double entityReach, double entityReachCreative, double reach,
			double reachCreative) {
		this.enabled = enabled;
		this.entityReach = entityReach;
		this.entityReachCreative = entityReachCreative;
		this.reach = reach;
		this.reachCreative = reachCreative;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		enabled = buf.readBoolean();
		entityReach = buf.readDouble();
		entityReachCreative = buf.readDouble();
		reach = buf.readDouble();
		reachCreative = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(enabled);
		buf.writeDouble(entityReach);
		buf.writeDouble(entityReachCreative);
		buf.writeDouble(reach);
		buf.writeDouble(reachCreative);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public double getEntityReach() {
		return entityReach;
	}

	public double getEntityReachCreative() {
		return entityReachCreative;
	}

	public double getReach() {
		return reach;
	}

	public double getReachCreative() {
		return reachCreative;
	}

}
