package meldexun.reachfix.asm;

import java.util.Map;

import meldexun.reachfix.ReachFix;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("meldexun.reachfix.asm")
public class ReachFixPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { ReachFixClassTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return ReachFix.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
