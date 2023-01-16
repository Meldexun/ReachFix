package meldexun.reachfix.config;

import meldexun.configutil.ConfigUtil;
import meldexun.reachfix.ReachFix;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;

@Config(modid = ReachFix.MODID, category = "")
public class ReachFixConfig {

	@Name("general")
	public static final ReachFixConfig MASTER_CONFIG = new ReachFixConfig();
	@Config.Ignore
	public static final ReachFixConfig SLAVE_CONFIG = new ReachFixConfig();

	public static ReachFixConfig getInstance() {
		return SLAVE_CONFIG;
	}

	@ConfigUtil.Sync
	@RangeDouble(min = 0.0D, max = 1024.0D)
	public double entityReach = 3.0D;
	@ConfigUtil.Sync
	@RangeDouble(min = 0.0D, max = 1024.0D)
	public double entityReachCreative = 3.5D;

	@ConfigUtil.Sync
	@RangeDouble(min = 0.0D, max = 1024.0D)
	public double reach = 4.5D;
	@ConfigUtil.Sync
	@RangeDouble(min = 0.0D, max = 1024.0D)
	public double reachCreative = 5.0D;

	@ConfigUtil.Sync
	@Comment("When enabled 'canRiderInteract' is ignored when the players eyes are inside the entity that is riding or being ridden by the player.")
	public boolean forceInteractionInsideVehicles = true;

}
