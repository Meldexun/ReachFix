package meldexun.reachfix.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public class ReachFixConfig {

	public static final ReachFixConfig SERVER_CONFIG;
	public static final ForgeConfigSpec SERVER_SPEC;
	static {
		final Pair<ReachFixConfig, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(ReachFixConfig::new);
		SERVER_CONFIG = serverSpecPair.getLeft();
		SERVER_SPEC = serverSpecPair.getRight();
	}

	public final BooleanValue enabled;
	public final DoubleValue reach;
	public final DoubleValue reachCreative;

	public ReachFixConfig(ForgeConfigSpec.Builder builder) {
		this.enabled = builder.comment("").define("enabled", true);
		this.reach = builder.comment("").defineInRange("reach", 4.5D, 0.0D, 1024.0D);
		this.reachCreative = builder.comment("").defineInRange("reachCreative", 5.0D, 0.0D, 1024.0D);
	}

}
