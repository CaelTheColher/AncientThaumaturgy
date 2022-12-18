package me.cael.ancientthaumaturgy.client.model

import me.cael.ancientthaumaturgy.common.block.BlockCompendium
import me.cael.ancientthaumaturgy.common.block.TubeBlock
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockState
import net.minecraft.client.render.model.*
import net.minecraft.client.render.model.json.ModelOverrideList
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockRenderView
import java.util.function.Function
import java.util.function.Supplier

class TubeModel : BakedModel, FabricBakedModel, UnbakedModel {

    private val spriteIdCollection = mutableListOf(
            SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, identifier("block/tube_block/center")),
            SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, identifier("block/tube_block/side")),
            SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, identifier("block/tube_block/connection")),
            SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, identifier("block/tube_block/junction"))
    )
    private val modelIdCollection = mutableListOf(
            identifier("block/tube_block/junction"),
            identifier("block/tube_block/side"),
            identifier("block/tube_block/center")
    )
    private val spriteArray = arrayOfNulls<Sprite>(4)
    private val modelArray = arrayOfNulls<BakedModel>(10)
    private lateinit var transformation: ModelTransformation

    override fun bake(
        baker: Baker,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModel {
        modelArray[0] = baker.getOrLoadModel(modelIdCollection[0]).bake(baker, textureGetter, rotationContainer, modelId)
        transformation = modelArray[0]!!.transformation
        val sideModel = baker.getOrLoadModel(modelIdCollection[1])
        modelArray[1] = sideModel.bake(baker, textureGetter, ModelRotation.X270_Y0, modelId) // NORTH
        modelArray[2] = sideModel.bake(baker, textureGetter, ModelRotation.X270_Y90, modelId) // EAST
        modelArray[3] = sideModel.bake(baker, textureGetter, ModelRotation.X270_Y180, modelId) // SOUTH
        modelArray[4] = sideModel.bake(baker, textureGetter, ModelRotation.X270_Y270, modelId) // WEST
        modelArray[5] = sideModel.bake(baker, textureGetter, ModelRotation.X180_Y0, modelId) // UP
        modelArray[6] = sideModel.bake(baker, textureGetter, ModelRotation.X0_Y0, modelId) // DOWN
        val centerModel = baker.getOrLoadModel(modelIdCollection[2])
        modelArray[7] = centerModel.bake(baker, textureGetter, rotationContainer, modelId) // NORTH-SOUTH
        modelArray[8] = centerModel.bake(baker, textureGetter, ModelRotation.X0_Y90, modelId) //EAST-WEST
        modelArray[9] = centerModel.bake(baker, textureGetter, ModelRotation.X90_Y0, modelId) //UP-DOWN

        spriteIdCollection.forEachIndexed { idx, spriteIdentifier ->
            spriteArray[idx] = textureGetter.apply(spriteIdentifier)
        }
        return this
    }

    override fun getModelDependencies(): MutableCollection<Identifier> = modelIdCollection
    override fun setParents(modelLoader: Function<Identifier, UnbakedModel>?) {
        return
    }

    override fun getQuads(state: BlockState?, face: Direction?, random: Random): MutableList<BakedQuad> = mutableListOf()

    override fun useAmbientOcclusion(): Boolean = true

    override fun hasDepth(): Boolean = false

    override fun isSideLit(): Boolean = true

    override fun isBuiltin(): Boolean = false

    override fun getParticleSprite(): Sprite = spriteArray[0]!!

    override fun getTransformation(): ModelTransformation = transformation

    override fun getOverrides(): ModelOverrideList = ModelOverrideList.EMPTY

    override fun isVanillaAdapter(): Boolean = false

    override fun emitBlockQuads(
            world: BlockRenderView,
            state: BlockState,
            pos: BlockPos,
            randSupplier: Supplier<Random>,
            context: RenderContext
    ) {
//        if (state[CableBlock.COVERED]) {
//            val blockEntity = world.getBlockEntity(pos) as? CableBlockEntity
//            if (blockEntity?.cover != null) {
//                val coverState = Registry.BLOCK.get(blockEntity.cover).defaultState
//                val model = MinecraftClient.getInstance().bakedModelManager.blockModels.getModel(coverState)
//                model.emitFromVanilla(context, randSupplier) { quad -> !quad.hasColor() }
//
//                context.pushTransform { q ->
//                    val rawColor = ColorProviderRegistry.BLOCK[coverState.block]!!.getColor(coverState, world, pos, 0)
//                    val color = 255 shl 24 or rawColor
//                    q.spriteColor(0, color, color, color, color)
//                    true
//                }
//
//                model.emitFromVanilla(context, randSupplier) { quad -> quad.hasColor() }
//                context.popTransform()
//                if (coverState.isOpaque) return
//            }
//        }

        // I don't like this either
        if (state[TubeBlock.NORTH] && state[TubeBlock.SOUTH] && !(state[TubeBlock.EAST] || state[TubeBlock.WEST] || state[TubeBlock.UP] || state[TubeBlock.DOWN])) {
            handleBakedModel(world, state, pos, randSupplier, context, modelArray[7])
        } else if (state[TubeBlock.EAST] && state[TubeBlock.WEST] && !(state[TubeBlock.NORTH] || state[TubeBlock.SOUTH] || state[TubeBlock.UP] || state[TubeBlock.DOWN])) {
            handleBakedModel(world, state, pos, randSupplier, context, modelArray[8])
        } else if (state[TubeBlock.UP] && state[TubeBlock.DOWN] && !(state[TubeBlock.NORTH] || state[TubeBlock.SOUTH] || state[TubeBlock.EAST] || state[TubeBlock.WEST])) {
            handleBakedModel(world, state, pos, randSupplier, context, modelArray[9])
        } else {
            handleBakedModel(world, state, pos, randSupplier, context, modelArray[0])
            if (state[TubeBlock.NORTH]) handleBakedModel(world, state, pos, randSupplier, context, modelArray[1])
            if (state[TubeBlock.EAST]) handleBakedModel(world, state, pos, randSupplier, context, modelArray[2])
            if (state[TubeBlock.SOUTH]) handleBakedModel(world, state, pos, randSupplier, context, modelArray[3])
            if (state[TubeBlock.WEST]) handleBakedModel(world, state, pos, randSupplier, context, modelArray[4])
            if (state[TubeBlock.UP]) handleBakedModel(world, state, pos, randSupplier, context, modelArray[5])
            if (state[TubeBlock.DOWN]) handleBakedModel(world, state, pos, randSupplier, context, modelArray[6])
        }
    }

    private fun BakedModel.emitFromVanilla(context: RenderContext, randSupplier: Supplier<Random>, shouldEmit: (BakedQuad) -> Boolean) {
        val emitter = context.emitter
        Direction.values().forEach { dir ->
            getQuads(null, dir, randSupplier.get()).forEach { quad ->
                if (shouldEmit(quad)) {
                    emitter.fromVanilla(quad.vertexData, 0, false)
                    emitter.emit()
                }
            }
        }
        getQuads(null, null, randSupplier.get()).forEach { quad ->
            if (shouldEmit(quad)) {
                emitter.fromVanilla(quad.vertexData, 0, false)
                emitter.emit()
            }
        }
    }

    private fun handleBakedModel(
            world: BlockRenderView,
            state: BlockState,
            pos: BlockPos,
            randSupplier: Supplier<Random>,
            context: RenderContext,
            bakedModel: BakedModel?) {
        if (bakedModel is FabricBakedModel) bakedModel.emitBlockQuads(world, state, pos, randSupplier, context)
        else if (bakedModel != null) context.fallbackConsumer().accept(bakedModel)
    }

    override fun emitItemQuads(stack: ItemStack?, p1: Supplier<Random>, context: RenderContext) {
        context.fallbackConsumer().accept(modelArray[0])
        val state = BlockCompendium.TUBE_BLOCK.defaultState
        if (state[TubeBlock.NORTH]) context.fallbackConsumer().accept(modelArray[1])
        if (state[TubeBlock.EAST]) context.fallbackConsumer().accept(modelArray[2])
        if (state[TubeBlock.SOUTH]) context.fallbackConsumer().accept(modelArray[3])
        if (state[TubeBlock.WEST]) context.fallbackConsumer().accept(modelArray[4])
        if (state[TubeBlock.UP]) context.fallbackConsumer().accept(modelArray[5])
        if (state[TubeBlock.DOWN]) context.fallbackConsumer().accept(modelArray[6])
    }
}