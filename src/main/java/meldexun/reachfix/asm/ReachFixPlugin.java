package meldexun.reachfix.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"meldexun.asmutil2", "meldexun.reachfix.asm"})
public class ReachFixPlugin implements IFMLLoadingPlugin {

    @Override
    public String @NotNull [] getASMTransformerClass() {
        return new String[]{"meldexun.reachfix.asm.ReachFixClassTransformer"};
    }

    @Override
    public @Nullable String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public @Nullable String getAccessTransformerClass() {
        return null;
    }
}
