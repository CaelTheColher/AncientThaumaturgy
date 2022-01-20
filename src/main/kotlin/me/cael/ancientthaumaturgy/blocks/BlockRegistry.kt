package me.cael.ancientthaumaturgy.blocks

import me.cael.ancientthaumaturgy.AncientThaumaturgy.CLIENT
import me.cael.ancientthaumaturgy.AncientThaumaturgy.NAMESPACE
import me.cael.ancientthaumaturgy.blocks.machines.crucible.CrucibleBlock
import me.cael.ancientthaumaturgy.blocks.machines.infuser.InfuserBlock
import me.cael.ancientthaumaturgy.blocks.machines.infuser.InfuserBlockEntity
import me.cael.ancientthaumaturgy.blocks.machines.infuser.InfuserScreen
import me.cael.ancientthaumaturgy.blocks.machines.infuser.InfuserScreenHandler
import me.cael.ancientthaumaturgy.blocks.machines.tank.TankBlock
import me.cael.ancientthaumaturgy.blocks.machines.tube.TubeBlock
import me.cael.ancientthaumaturgy.blocks.machines.tube.TubeBlockEntity
import me.cael.ancientthaumaturgy.blocks.seal.SealBlock
import me.cael.ancientthaumaturgy.blocks.seal.SealBlockEntity
import me.cael.ancientthaumaturgy.blocks.seal.SealRenderer
import me.cael.ancientthaumaturgy.items.ItemRegistry
import me.cael.ancientthaumaturgy.utils.identifier
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Supplier
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
object BlockRegistry {

    class ContainerInfo<T: ScreenHandler>(
            handlerClass: KClass<*>,
            screenClass: Supplier<KClass<*>>,
            val identifier: Identifier? = null
    ){

        val handlerClass = handlerClass as KClass<T>
        val screenClass = screenClass as Supplier<KClass<HandledScreen<T>>>

        var handlerType: ScreenHandlerType<T>? = null
        var handler: T? = null

        var title: Text = LiteralText("")

        fun init(blockIdentifier: Identifier) {
            val id = identifier ?: blockIdentifier
            title = TranslatableText("screen.$NAMESPACE.${id.path}")
            handlerType = ScreenHandlerRegistry.registerExtended(id) { i, playerInventory, packetByteBuf ->
                val pos = packetByteBuf.readBlockPos()
                val player = playerInventory.player
                val world = player.world
                val be = world.getBlockEntity(pos)
                //handler = handlerClass.java.constructors[0].newInstance(i, playerInventory, be, ScreenHandlerContext.create(world, pos)) as T
                handler = handlerClass.java.constructors[0].newInstance(i, playerInventory, be) as T
                handler
            }
        }

        fun initClient() {
            ScreenRegistry.register(handlerType) { handler, playerInventory, title -> screenClass.get().java.constructors[0].newInstance(handler, playerInventory, title) as HandledScreen<T> }
        }

    }
    class BlockInfo<T: BlockEntity>(
            val identifier: Identifier,
            val block: Block,
            val hasItem: Boolean,
            var hasOre: Boolean,
            val item: KClass<BlockItem>?,
            val entity: BlockEntityType<T>?,
            val renderer: KClass<BlockEntityRenderer<T>>?,
            val renderLayer: RenderLayer?,
            var containers: List<ContainerInfo<*>>,
    ) {
        fun register() {
            Registry.register(Registry.BLOCK, identifier, block)
            if (hasItem) {
                if (item != null)
                    ItemRegistry.register(identifier.path, item.java.constructors[0].newInstance(block, Item.Settings().group(ItemRegistry.ITEM_GROUP)) as BlockItem)
                else
                    ItemRegistry.register(identifier.path, BlockItem(block, Item.Settings().group(ItemRegistry.ITEM_GROUP)))
            }
//            if (hasOre) {
//                val feature = Feature.ORE.configure(OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, block.defaultState, 4)).decorate(
//                    Decorator.RANGE.configure(RangeDecoratorConfig(0, 0, 64))).spreadHorizontally().repeat(2)
//                val featureKey = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, identifier)
//                Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, featureKey.value, feature)
//                BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, featureKey)
//            }
            if (entity != null) Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, entity)
            containers.forEach { it.init(identifier) }
        }

        fun registerClient() {
            containers.forEach { it.initClient() }
            if (renderer != null) {
                BlockEntityRendererRegistry.INSTANCE.register(entity) {
                    renderer.java.constructors[0].newInstance() as BlockEntityRenderer<T>
                }
            }
            if (renderLayer != null) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, renderLayer)
            }
        }
    }
    val registry = linkedMapOf<Block, BlockInfo<*>>()

    fun getBlockEntity(block: Block) = registry[block]?.entity

    fun getContainerInfo(block: Block) = registry[block]?.containers?.get(0)
    fun getContainerInfo(block: Block, identifier: Identifier): ContainerInfo<*>? {
        registry[block]?.containers?.forEach {
            if(it.identifier == identifier)
                return it
        }
        return null
    }

    fun register(id: String, block: Block, hasItem: Boolean = true, hasOre: Boolean = false, renderLayer: RenderLayer? = null): Block {
        registry[block] = BlockInfo<BlockEntity>(identifier(id), block, hasItem, hasOre, null, null, null, renderLayer, listOf())
        return block
    }

    fun <T: BlockEntity> registerWithEntity(id: String, block: Block, hasItem: Boolean = true, hasOre: Boolean = false, item: KClass<*>? = null, renderer: Supplier<KClass<*>>? = null, renderLayer: RenderLayer? = null, containers: List<ContainerInfo<*>> = listOf()): Block {
        val blockItem = item as? KClass<BlockItem>
        val entity = (block as? BlockEntityProvider)?.let { BlockEntityType.Builder.create({ blockPos, blockState -> block.createBlockEntity(blockPos, blockState) }, block).build(null) as BlockEntityType<T> }
        val render = if (CLIENT) renderer?.let { it.get() as KClass<BlockEntityRenderer<T>> } else null
        registry[block] = BlockInfo(identifier(id), block, hasItem, hasOre, blockItem, entity, render, renderLayer, containers)
        return block
    }

    private fun crystalBlockSettings(color: MapColor): FabricBlockSettings = FabricBlockSettings.of(Material.GLASS, color).strength(0.3F).sounds(BlockSoundGroup.GLASS).luminance{ 3 }.postProcess{ _, _, _ -> true }.emissiveLighting{ _, _, _ -> true }
    val AIR_CRYSTAL_BLOCK = register("crystal_block/air", Block(crystalBlockSettings(MapColor.YELLOW)))
    val EARTH_CRYSTAL_BLOCK = register("crystal_block/earth", Block(crystalBlockSettings(MapColor.GREEN)))
    val FIRE_CRYSTAL_BLOCK = register("crystal_block/fire", Block(crystalBlockSettings(MapColor.RED)))
    val WATER_CRYSTAL_BLOCK = register("crystal_block/water", Block(crystalBlockSettings(MapColor.BLUE)))
    val MAGIC_CRYSTAL_BLOCK = register("crystal_block/magic", Block(crystalBlockSettings(MapColor.PINK)))
    val CORRUPTION_CRISTAL_BLOCK = register("crystal_block/corruption", Block(crystalBlockSettings(MapColor.PURPLE)))

    val AIR_CRYSTAL_CLUSTER = register("crystal_cluster/air", Block(crystalBlockSettings(MapColor.YELLOW).nonOpaque()), hasItem = false, hasOre = true, RenderLayer.getCutout())
    val EARTH_CRYSTAL_CLUSTER = register("crystal_cluster/earth", Block(crystalBlockSettings(MapColor.GREEN).nonOpaque()), hasItem = false, hasOre = true, RenderLayer.getCutout())
    val FIRE_CRYSTAL_CLUSTER = register("crystal_cluster/fire", Block(crystalBlockSettings(MapColor.RED).nonOpaque()), hasItem = false, hasOre = true, RenderLayer.getCutout())
    val WATER_CRYSTAL_CLUSTER = register("crystal_cluster/water", Block(crystalBlockSettings(MapColor.BLUE).nonOpaque()), hasItem = false, hasOre = true, RenderLayer.getCutout())
    val MAGIC_CRYSTAL_CLUSTER = register("crystal_cluster/magic", Block(crystalBlockSettings(MapColor.PINK).nonOpaque()), hasItem = false, hasOre = true, RenderLayer.getCutout())
    val CORRUPTION_CRISTAL_CLUSTER = register("crystal_cluster/corruption", Block(crystalBlockSettings(MapColor.PURPLE).nonOpaque()), hasItem = false, hasOre = true, RenderLayer.getCutout())


    val CRUCIBLE_BLOCK = register("crucible_block", CrucibleBlock(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE)), renderLayer = RenderLayer.getCutout())
    val TANK_BLOCK = register("tank_block", TankBlock(FabricBlockSettings.of(Material.GLASS, MapColor.ORANGE)), renderLayer = RenderLayer.getTranslucent())

    val SEAL_BLOCK = registerWithEntity<SealBlockEntity>("seal_block", SealBlock(), renderer = { SealRenderer::class }, renderLayer = RenderLayer.getCutout())
    val TUBE_BLOCK = registerWithEntity<TubeBlockEntity>("tube_block", TubeBlock(FabricBlockSettings.of(Material.GLASS)), renderLayer = RenderLayer.getTranslucent())
    val INFUSER_BLOCK = registerWithEntity<InfuserBlockEntity>("infuser_block", InfuserBlock(FabricBlockSettings.of(Material.STONE)), renderLayer = RenderLayer.getCutout(), containers = listOf(ContainerInfo<InfuserScreenHandler>(InfuserScreenHandler::class, {  InfuserScreen::class })))

    fun registerBlocks() {
        registry.forEach { it.value.register() }
    }

    fun registerBlocksClient() {
        registry.forEach { it.value.registerClient() }
    }
}