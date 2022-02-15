package meldexun.reachfix;

import meldexun.reachfix.config.ReachFixConfig;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ReachFix.MOD_ID)
public class ReachFix {

	public static final String MOD_ID = "reachfix";

	public ReachFix() {
		ModLoadingContext.get().registerConfig(Type.SERVER, ReachFixConfig.SERVER_SPEC);
		MinecraftForge.EVENT_BUS.addListener(this::onFMLServerStartingEvent);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedInEvent);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerChangedDimensionEvent);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerRespawnEvent);
	}

	public void onFMLServerStartingEvent(FMLServerStartingEvent event) {
		ReachFixUtil.setEnabled(ReachFixConfig.SERVER_CONFIG.enabled.get());
		ReachFixUtil.setReach(ReachFixConfig.SERVER_CONFIG.reach.get());
		ReachFixUtil.setReachCreative(ReachFixConfig.SERVER_CONFIG.reachCreative.get());
	}

	public void onPlayerLoggedInEvent(PlayerLoggedInEvent event) {
		ReachFixUtil.updateBaseReachModifier(event.getPlayer());
	}

	public void onPlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
		ReachFixUtil.updateBaseReachModifier(event.getPlayer());
	}

	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		ReachFixUtil.updateBaseReachModifier(event.getPlayer());
	}

}
