package me.cael.ancientthaumaturgy.blocks

import me.cael.ancientthaumaturgy.AncientThaumaturgy.CLIENT
import me.cael.ancientthaumaturgy.blocks.machines.crucible.CrucibleBlock
import me.cael.ancientthaumaturgy.blocks.seal.SealBlock
import me.cael.ancientthaumaturgy.blocks.seal.SealBlockEntity
import me.cael.ancientthaumaturgy.blocks.seal.SealRenderer
import me.cael.ancientthaumaturgy.blocks.machines.tank.TankBlock
import me.cael.ancientthaumaturgy.blocks.machines.tube.TubeBlock
import me.cael.ancientthaumaturgy.items.ItemRegistry
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.Material
import net.minecraft.block.MaterialColor
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Supplier
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object BlockRegistry {
    class BlockInfo<T: BlockEntity>(
            val identifier: Identifier,
            val block: Block,
            val hasItem: Boolean,
            val item: KClass<BlockItem>?,
            val entity: BlockEntityType<T>?,
            val renderer: KClass<BlockEntityRenderer<T>>?,
            val renderLayer: RenderLayer?
    ) {
        fun register() {
            Registry.register(Registry.BLOCK, identifier, block)
            if (hasItem) {
                if (item != null)
                    ItemRegistry.register(identifier.path, item.java.constructors[0].newInstance(block, Item.Settings().group(ItemRegistry.ITEM_GROUP)) as BlockItem)
                else
                    ItemRegistry.register(identifier.path, BlockItem(block, Item.Settings().group(ItemRegistry.ITEM_GROUP)))
            }
            if (entity != null) Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, entity)
        }

        fun registerClient() {
            if (renderer != null) {
                BlockEntityRendererRegistry.INSTANCE.register(entity) {
                    renderer.java.constructors[0].newInstance(it) as BlockEntityRenderer<T>
                }
            }
            if (renderLayer != null) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, renderLayer)
            }
        }
    }
    val registry = linkedMapOf<Block, BlockInfo<*>>()

    fun getBlockEntity(block: Block) = registry[block]?.entity

    fun register(id: String, block: Block, hasItem: Boolean = true, renderLayer: RenderLayer? = null): Block {
        registry[block] = BlockInfo<BlockEntity>(identifier(id), block, hasItem, null, null, null, renderLayer)
        return block
    }

    fun <T: BlockEntity> registerWithEntity(id: String, block: Block, hasItem: Boolean = true, item: KClass<*>? = null, renderer: Supplier<KClass<*>>? = null, renderLayer: RenderLayer? = null): Block {
        val blockItem = item as? KClass<BlockItem>
        val entity = (block as? BlockEntityProvider)?.let { BlockEntityType.Builder.create({it.createBlockEntity(null)}, block).build(null) as BlockEntityType<T> }
        val render = if (CLIENT) renderer?.let { it.get() as KClass<BlockEntityRenderer<T>> } else null
        registry[block] = BlockInfo(identifier(id), block, hasItem, blockItem, entity, render, renderLayer)
        return block
    }


    private fun crystalBlockSettings(color: MaterialColor): FabricBlockSettings = FabricBlockSettings.of(Material.GLASS, color).strength(0.3F).sounds(BlockSoundGroup.GLASS).luminance{ 3 }.postProcess{ _, _, _ -> true }.emissiveLighting{ _, _, _ -> true }
    val AIR_CRYSTAL_BLOCK = register("crystal_block/air", Block(crystalBlockSettings(MaterialColor.YELLOW)))
    val EARTH_CRYSTAL_BLOCK = register("crystal_block/earth", Block(crystalBlockSettings(MaterialColor.GREEN)))
    val FIRE_CRYSTAL_BLOCK = register("crystal_block/fire", Block(crystalBlockSettings(MaterialColor.RED)))
    val WATER_CRYSTAL_BLOCK = register("crystal_block/water", Block(crystalBlockSettings(MaterialColor.BLUE)))
    val MAGIC_CRYSTAL_BLOCK = register("crystal_block/magic", Block(crystalBlockSettings(MaterialColor.PINK)))
    val CORRUPTION_CRISTAL_BLOCK = register("crystal_block/corruption", Block(crystalBlockSettings(MaterialColor.PURPLE)))

    val AIR_CRYSTAL_CLUSTER = register("crystal_cluster/air", Block(crystalBlockSettings(MaterialColor.YELLOW).nonOpaque()), false, RenderLayer.getCutout())
    val EARTH_CRYSTAL_CLUSTER = register("crystal_cluster/earth", Block(crystalBlockSettings(MaterialColor.GREEN).nonOpaque()), false, RenderLayer.getCutout())
    val FIRE_CRYSTAL_CLUSTER = register("crystal_cluster/fire", Block(crystalBlockSettings(MaterialColor.RED).nonOpaque()), false, RenderLayer.getCutout())
    val WATER_CRYSTAL_CLUSTER = register("crystal_cluster/water", Block(crystalBlockSettings(MaterialColor.BLUE).nonOpaque()), false, RenderLayer.getCutout())
    val MAGIC_CRYSTAL_CLUSTER = register("crystal_cluster/magic", Block(crystalBlockSettings(MaterialColor.PINK).nonOpaque()), false, RenderLayer.getCutout())
    val CORRUPTION_CRISTAL_CLUSTER = register("crystal_cluster/corruption", Block(crystalBlockSettings(MaterialColor.PURPLE).nonOpaque()), false, RenderLayer.getCutout())

    val SEAL_BLOCK = registerWithEntity<SealBlockEntity>("seal_block", SealBlock(), renderer = { SealRenderer::class }, renderLayer = RenderLayer.getCutout())

    val TUBE_BLOCK = register("tube_block", TubeBlock(FabricBlockSettings.of(Material.GLASS)), true, renderLayer = RenderLayer.getTranslucent())
    val CRUCIBLE_BLOCK = register("crucible_block", CrucibleBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.ORANGE)), true, RenderLayer.getCutout())
    val TANK_BLOCK = register("tank_block", TankBlock(FabricBlockSettings.of(Material.GLASS, MaterialColor.ORANGE)), true, RenderLayer.getTranslucent())

    fun registerBlocks() {
        registry.forEach { it.value.register() }
    }

    fun registerBlocksClient() {
        registry.forEach { it.value.registerClient() }
    }
}