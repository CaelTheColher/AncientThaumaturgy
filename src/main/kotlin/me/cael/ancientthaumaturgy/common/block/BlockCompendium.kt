@file:Suppress("unused")

package me.cael.ancientthaumaturgy.common.block

import me.cael.ancientthaumaturgy.utils.RegistryCompendium
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.AmethystBlock
import net.minecraft.block.AmethystClusterBlock
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier

object BlockCompendium: RegistryCompendium<Block>(Registries.BLOCK) {
    private fun crystalBlockSettings(color: MapColor): FabricBlockSettings =
        FabricBlockSettings.create().mapColor(color).strength(1.5F).sounds(
            BlockSoundGroup.AMETHYST_BLOCK
        ).luminance { 5 }.postProcess { _, _, _ -> true }.emissiveLighting { _, _, _ -> true }

    val AIR_CRYSTAL_BLOCK = register("crystal_block/air", AmethystBlock(crystalBlockSettings(MapColor.YELLOW)))
    val EARTH_CRYSTAL_BLOCK = register("crystal_block/earth", AmethystBlock(crystalBlockSettings(MapColor.GREEN)))
    val FIRE_CRYSTAL_BLOCK = register("crystal_block/fire", AmethystBlock(crystalBlockSettings(MapColor.RED)))
    val WATER_CRYSTAL_BLOCK = register("crystal_block/water", AmethystBlock(crystalBlockSettings(MapColor.BLUE)))
    val MAGIC_CRYSTAL_BLOCK = register("crystal_block/magic", AmethystBlock(crystalBlockSettings(MapColor.PINK)))
    val CORRUPTION_CRYSTAL_BLOCK = register("crystal_block/corruption", AmethystBlock(crystalBlockSettings(MapColor.PURPLE)))

    val AIR_CRYSTAL_CLUSTER =
        register("crystal_cluster/air", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.YELLOW).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER).requiresTool()))
    val EARTH_CRYSTAL_CLUSTER =
        register("crystal_cluster/earth", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.GREEN).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER).requiresTool()))
    val FIRE_CRYSTAL_CLUSTER =
        register("crystal_cluster/fire", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.RED).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER).requiresTool()))
    val WATER_CRYSTAL_CLUSTER =
        register("crystal_cluster/water", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.BLUE).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER).requiresTool()))
    val MAGIC_CRYSTAL_CLUSTER =
        register("crystal_cluster/magic", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.PINK).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER).requiresTool()))
    val CORRUPTION_CRYSTAL_CLUSTER =
        register("crystal_cluster/corruption", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.PURPLE).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER).requiresTool()))

    val TANK_BLOCK = register("tank_block", TankBlock(FabricBlockSettings.create().mapColor(MapColor.ORANGE).strength(1.0f).sounds(BlockSoundGroup.GLASS)))
    val SEAL_BLOCK = register("seal_block", SealBlock())
    val TUBE_BLOCK = register("tube_block", TubeBlock(FabricBlockSettings.create()))
    val INFUSER_BLOCK = register("infuser_block", InfuserBlock(FabricBlockSettings.create().nonOpaque().strength(3.0F, 6.0F).sounds(BlockSoundGroup.COPPER).requiresTool()))
    val CRUCIBLE_BLOCK = register("crucible_block", CrucibleBlock(FabricBlockSettings.create().mapColor(MapColor.ORANGE).strength(3.0F, 6.0F).requiresTool().sounds(BlockSoundGroup.COPPER)))

    val PORTABLE_HOLE_BLOCK = register("portable_hole_block", PortableHoleBlock(FabricBlockSettings.create().noCollision().noBlockBreakParticles().dropsNothing().strength(-1.0F, 3600000.0F)))

    fun registerBlockItems(itemMap: MutableMap<Identifier, Item>) {
        map.forEach { (identifier, block) ->
            itemMap[identifier] = when (block) {
                PORTABLE_HOLE_BLOCK -> return@forEach
                else -> BlockItem(block, Item.Settings())
            }
        }
    }

}