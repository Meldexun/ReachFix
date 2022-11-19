package meldexun.reachfix.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SPacketSyncConfig implements IMessage {

	private double entityReach;
	private double entityReachCreative;
	private double reach;
	private double reachCreative;

	public SPacketSyncConfig() {

	}

	public SPacketSyncConfig(double entityReach, double entityReachCreative, double reach, double reachCreative) {
		this.entityReach = entityReach;
		this.entityReachCreative = entityReachCreative;
		this.reach = reach;
		this.reachCreative = reachCreative;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityReach = buf.readDouble();
		entityReachCreative = buf.readDouble();
		reach = buf.readDouble();
		reachCreative = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(entityReach);
		buf.writeDouble(entityReachCreative);
		buf.writeDouble(reach);
		buf.writeDouble(reachCreative);
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
