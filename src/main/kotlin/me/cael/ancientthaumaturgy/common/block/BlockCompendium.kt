@file:Suppress("unused")

package me.cael.ancientthaumaturgy.common.block

import me.cael.ancientthaumaturgy.utils.RegistryCompendium
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier


object BlockCompendium: RegistryCompendium<Block>(Registries.BLOCK) {
    private fun crystalBlockSettings(color: MapColor): FabricBlockSettings =
        FabricBlockSettings.of(Material.AMETHYST, color).strength(1.5F).sounds(
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
            crystalBlockSettings(MapColor.YELLOW).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER)))
    val EARTH_CRYSTAL_CLUSTER =
        register("crystal_cluster/earth", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.GREEN).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER)))
    val FIRE_CRYSTAL_CLUSTER =
        register("crystal_cluster/fire", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.RED).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER)))
    val WATER_CRYSTAL_CLUSTER =
        register("crystal_cluster/water", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.BLUE).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER)))
    val MAGIC_CRYSTAL_CLUSTER =
        register("crystal_cluster/magic", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.PINK).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER)))
    val CORRUPTION_CRYSTAL_CLUSTER =
        register("crystal_cluster/corruption", AmethystClusterBlock(7, 3,
            crystalBlockSettings(MapColor.PURPLE).nonOpaque().sounds(BlockSoundGroup.AMETHYST_CLUSTER)))

    val TANK_BLOCK = register("tank_block", TankBlock(FabricBlockSettings.of(Material.GLASS, MapColor.ORANGE)))
    val SEAL_BLOCK = register("seal_block", SealBlock())
    val TUBE_BLOCK = register("tube_block", TubeBlock(FabricBlockSettings.of(Material.GLASS)))
    val INFUSER_BLOCK = register("infuser_block", InfuserBlock(FabricBlockSettings.of(Material.STONE).nonOpaque()))
    val CRUCIBLE_BLOCK = register("crucible_block", CrucibleBlock(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE)))

    fun registerBlockItems(itemMap: MutableMap<Identifier, Item>) {
        map.forEach { (identifier, block) ->
            itemMap[identifier] = when (block) {
                AIR_CRYSTAL_CLUSTER, EARTH_CRYSTAL_CLUSTER, FIRE_CRYSTAL_CLUSTER, WATER_CRYSTAL_CLUSTER, MAGIC_CRYSTAL_CLUSTER, CORRUPTION_CRYSTAL_CLUSTER -> return@forEach
                else -> BlockItem(block, Item.Settings())
            }
        }
    }

}