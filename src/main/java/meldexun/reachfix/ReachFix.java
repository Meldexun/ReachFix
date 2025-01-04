package meldexun.reachfix;

import lombok.Data;
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
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Data
@Mod(modid = ReachFix.MODID)
public class ReachFix {

    public static final String MODID = "reachfix";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static boolean isSpartanWeaponryInstalled;

    @Mod.EventHandler
    public void onFMLConstructionEvent(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        NETWORK.registerMessage(CPacketHandlerSyncConfig.class, SPacketSyncConfig.class, 1, Side.CLIENT);
    }

    @Mod.EventHandler
    public void onFMLPostInitializationEvent(FMLPostInitializationEvent event) {
        isSpartanWeaponryInstalled = Loader.isModLoaded("spartanweaponry");
    }

    @Mod.EventHandler
    public void onFMLServerStartingEvent(FMLServerStartingEvent event) {
        loadLocalConfig(true);
    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
        sendServerConfig((EntityPlayerMP) event.player);
        ReachFixUtil.updateBaseReachModifier(event.player);
    }

    @SubscribeEvent
    public void onPlayerChangedDimensionEvent(PlayerEvent.@NotNull PlayerChangedDimensionEvent event) {
        ReachFixUtil.updateBaseReachModifier(event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawnEvent(PlayerEvent.@NotNull PlayerRespawnEvent event) {
        ReachFixUtil.updateBaseReachModifier(event.player);
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.@NotNull OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID)) {
            ConfigManager.sync(MODID, Config.Type.INSTANCE);

            @NotNull Minecraft mc = Minecraft.getMinecraft();
            @Nullable IntegratedServer server = mc.getIntegratedServer();

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
        } catch (ReflectiveOperationException ex) {
            LOGGER.error("Failed to copy config", ex);
        }
    }

    public static void sendServerConfig(@Nullable EntityPlayerMP player) {
        try {
            if (player == null) {
                NETWORK.sendToAll(new SPacketSyncConfig(ReachFixConfig.MASTER_CONFIG));
            } else {
                NETWORK.sendTo(new SPacketSyncConfig(ReachFixConfig.MASTER_CONFIG), player);
            }
        } catch (ReflectiveOperationException ex) {
            LOGGER.error("Failed to send server config", ex);
        }
    }
}
