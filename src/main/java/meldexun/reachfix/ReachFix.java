package meldexun.reachfix;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import meldexun.reachfix.util.ReachAttributeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReachFix extends DummyModContainer {

	public static final String MOD_ID = "reachfix";

	public ReachFix() {
		super(new ModMetadata());
		ModMetadata meta = this.getMetadata();
		meta.name = "Reach Fix";
		meta.version = "1.0.0";
		meta.modId = MOD_ID;
		meta.authorList = Arrays.asList("Meldexun");
		meta.url = "https://github.com/Meldexun/ReachFix";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}

	@Subscribe
	public void onFMLConstructionEvent(FMLConstructionEvent event) {
		ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(MOD_ID)) {
			ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
			for (WorldServer world : DimensionManager.getWorlds()) {
				for (EntityPlayer player : world.playerEntities) {
					ReachAttributeUtil.onConfigChanged(player);
				}
			}
			if (FMLCommonHandler.instance().getSide().isClient()) {
				this.onConfigChangedClient();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void onConfigChangedClient() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.player != null) {
			ReachAttributeUtil.onConfigChanged(mc.player);
		}
	}

}
