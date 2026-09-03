package ru.bamchik.mixins;

import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bamchik.BamchikClient;
import ru.bamchik.Module;
import ru.bamchik.modules.XRayModule;

@Mixin(BlockRenderManager.class)
public class MixinBlockRenderManager {
    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    private void onRenderBlock(Block block, BlockPos pos, BlockRenderView world, MatrixStack matrices, CallbackInfo ci) {
        boolean xrayOn = false;
        if (BamchikClient.getInstance() != null && BamchikClient.getInstance().isKeyValid()) {
            for (Module m : BamchikClient.getInstance().getModuleManager().getModules()) {
                if (m.getName().equals("XRay") && m.isEnabled()) {
                    xrayOn = true;
                    break;
                }
            }
        }
        if (xrayOn && !XRayModule.ORE_BLOCKS.contains(block)) {
            ci.cancel();
        }
    }
}