package meldexun.reachfix.config;

import meldexun.reachfix.ReachFix;
import net.minecraftforge.common.config.Config;

@Config(modid = ReachFix.MOD_ID)
public class ReachFixConfig {

	public static boolean enabled = true;

	@Config.RangeDouble(min = 0.0D, max = 1024.0D)
	public static double reach = 4.5D;
	@Config.RangeDouble(min = 0.0D, max = 1024.0D)
	public static double reachCreative = 5.0D;

}
