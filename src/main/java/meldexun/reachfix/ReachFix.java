package meldexun.reachfix;

import meldexun.reachfix.config.ReachFixConfig;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod(ReachFix.MOD_ID)
public class ReachFix {

	public static final String MOD_ID = "reachfix";

	public ReachFix() {
		ModLoadingContext.get().registerConfig(Type.SERVER, ReachFixConfig.SERVER_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
	}

	private void setup(FMLCommonSetupEvent event) {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigUpdated);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedInEvent);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerChangedDimensionEvent);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerRespawnEvent);
	}

	public void onConfigUpdated(ModConfigEvent event) {
		if (!event.getConfig().getModId().equals(MOD_ID)) {
			return;
		}
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			server.getAllLevels().forEach(level -> {
				level.players().forEach(ReachFixUtil::updateBaseReachModifier);
			});
		}
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

	private void setupClient(FMLClientSetupEvent event) {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigSynced);
	}

	@OnlyIn(Dist.CLIENT)
	public void onConfigSynced(ModConfigEvent event) {
		if (!event.getConfig().getModId().equals(MOD_ID)) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null) {
			mc.level.players().forEach(ReachFixUtil::updateBaseReachModifier);
		}
	}

}
