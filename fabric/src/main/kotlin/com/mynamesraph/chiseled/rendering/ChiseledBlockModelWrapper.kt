package com.mynamesraph.chiseled.rendering

import com.mynamesraph.chiseled.block.ChiseledBlock
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier
import net.minecraft.client.renderer.block.model.BlockStateModel

object ChiseledBlockModelWrapper : ModelModifier.AfterBakeBlock {

    init {
        ModelLoadingPlugin.register { it.modifyBlockModelAfterBake().register(this)}
    }

    override fun modifyModelAfterBake(
        model: BlockStateModel,
        context: ModelModifier.AfterBakeBlock.Context
    ): BlockStateModel {
        if (context.state().block is ChiseledBlock) {
            //Constants.LOG.info("$model")

            return ChiseledBlockModel(model)
        }

        return model    }
}