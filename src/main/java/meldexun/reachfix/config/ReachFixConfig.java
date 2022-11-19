package meldexun.reachfix.config;

import meldexun.reachfix.ReachFix;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

@Config(modid = ReachFix.MODID)
public class ReachFixConfig {

	@RequiresWorldRestart
	@RangeDouble(min = 0.0D, max = 1024.0D)
	public static double entityReach = 3.0D;
	@RequiresWorldRestart
	@RangeDouble(min = 0.0D, max = 1024.0D)
	public static double entityReachCreative = 3.5D;

	@RequiresWorldRestart
	@RangeDouble(min = 0.0D, max = 1024.0D)
	public static double reach = 4.5D;
	@RequiresWorldRestart
	@RangeDouble(min = 0.0D, max = 1024.0D)
	public static double reachCreative = 5.0D;

}
