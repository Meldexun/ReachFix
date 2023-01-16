package meldexun.reachfix;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import meldexun.configutil.ConfigUtil;
import meldexun.reachfix.config.ReachFixConfig;
import meldexun.reachfix.network.CPacketHandlerSyncConfig;
import meldexun.reachfix.network.SPacketSyncConfig;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ReachFix.MODID)
public class ReachFix {

	public static final String MODID = "reachfix";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	public static boolean isSpartanWeaponryInstalled;

	@EventHandler
	public void onFMLConstructionEvent(FMLConstructionEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		NETWORK.registerMessage(CPacketHandlerSyncConfig.class, SPacketSyncConfig.class, 1, Side.CLIENT);
	}

	@EventHandler
	public void onFMLPostInitializationEvent(FMLPostInitializationEvent event) {
		isSpartanWeaponryInstalled = Loader.isModLoaded("spartanweaponry");
	}

	@EventHandler
	public void onFMLServerStartingEvent(FMLServerStartingEvent event) {
		loadLocalConfig(true);
	}

	@SubscribeEvent
	public void onPlayerLoggedInEvent(PlayerLoggedInEvent event) {
		sendServerConfig((EntityPlayerMP) event.player);
		ReachFixUtil.updateBaseReachModifier(event.player);
	}

	@SubscribeEvent
	public void onPlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
		ReachFixUtil.updateBaseReachModifier(event.player);
	}

	@SubscribeEvent
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		ReachFixUtil.updateBaseReachModifier(event.player);
	}

	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(MODID)) {
			ConfigManager.sync(MODID, Config.Type.INSTANCE);

			Minecraft mc = Minecraft.getMinecraft();
			IntegratedServer server = mc.getIntegratedServer();

			loadLocalConfig(mc.world == null || server != null);
			if (server != null) {
				sendServerConfig(null);
				server.getPlayerList().getPlayers().forEach(ReachFixUtil::updateBaseReachModifier);
			}
		}
	}

	public static void loadLocalConfig(boolean loadAllSettings) {
		try {
			if (loadAllSettings) {
				ConfigUtil.copyAllSettings(ReachFixConfig.MASTER_CONFIG, ReachFixConfig.SLAVE_CONFIG);
			} else {
				ConfigUtil.copyClientSettings(ReachFixConfig.MASTER_CONFIG, ReachFixConfig.SLAVE_CONFIG);
			}
		} catch (ReflectiveOperationException e) {
			LOGGER.error("Failed to copy config", e);
		}
	}

	public static void sendServerConfig(@Nullable EntityPlayerMP player) {
		try {
			if (player != null) {
				NETWORK.sendTo(new SPacketSyncConfig(ReachFixConfig.MASTER_CONFIG), (EntityPlayerMP) player);
			} else {
				NETWORK.sendToAll(new SPacketSyncConfig(ReachFixConfig.MASTER_CONFIG));
			}
		} catch (ReflectiveOperationException e) {
			LOGGER.error("Failed to send server config", e);
		}
	}

}
