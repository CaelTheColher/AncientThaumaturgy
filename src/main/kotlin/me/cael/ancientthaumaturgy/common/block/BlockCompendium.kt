package me.cael.ancientthaumaturgy.common.block

import me.cael.ancientthaumaturgy.AncientThaumaturgy.creativeGroupSettings
import me.cael.ancientthaumaturgy.utils.RegistryCompendium
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object BlockCompendium: RegistryCompendium<Block>(Registry.BLOCK) {
    private fun crystalBlockSettings(color: MapColor): FabricBlockSettings = FabricBlockSettings.of(Material.GLASS, color).strength(0.3F).sounds(
        BlockSoundGroup.GLASS).luminance{ 3 }.postProcess{ _, _, _ -> true }.emissiveLighting{ _, _, _ -> true }

    val AIR_CRYSTAL_BLOCK = register("crystal_block/air", Block(crystalBlockSettings(MapColor.YELLOW)))
    val EARTH_CRYSTAL_BLOCK = register("crystal_block/earth", Block(crystalBlockSettings(MapColor.GREEN)))
    val FIRE_CRYSTAL_BLOCK = register("crystal_block/fire", Block(crystalBlockSettings(MapColor.RED)))
    val WATER_CRYSTAL_BLOCK = register("crystal_block/water", Block(crystalBlockSettings(MapColor.BLUE)))
    val MAGIC_CRYSTAL_BLOCK = register("crystal_block/magic", Block(crystalBlockSettings(MapColor.PINK)))
    val CORRUPTION_CRISTAL_BLOCK = register("crystal_block/corruption", Block(crystalBlockSettings(MapColor.PURPLE)))

    val TANK_BLOCK = register("tank_block", TankBlock(FabricBlockSettings.of(Material.GLASS, MapColor.ORANGE)))
    val SEAL_BLOCK = register("seal_block", SealBlock())
    val TUBE_BLOCK = register("tube_block", TubeBlock(FabricBlockSettings.of(Material.GLASS)))
    val INFUSER_BLOCK = register("infuser_block", InfuserBlock(FabricBlockSettings.of(Material.STONE)))
    val CRUCIBLE_BLOCK = register("crucible_block", CrucibleBlock(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).nonOpaque()))

    val AIR_CRYSTAL_CLUSTER = register("crystal_cluster/air", Block(crystalBlockSettings(MapColor.YELLOW).nonOpaque()))
    val EARTH_CRYSTAL_CLUSTER = register("crystal_cluster/earth", Block(crystalBlockSettings(MapColor.GREEN).nonOpaque()))
    val FIRE_CRYSTAL_CLUSTER = register("crystal_cluster/fire", Block(crystalBlockSettings(MapColor.RED).nonOpaque()))
    val WATER_CRYSTAL_CLUSTER = register("crystal_cluster/water", Block(crystalBlockSettings(MapColor.BLUE).nonOpaque()))
    val MAGIC_CRYSTAL_CLUSTER = register("crystal_cluster/magic", Block(crystalBlockSettings(MapColor.PINK).nonOpaque()))
    val CORRUPTION_CRISTAL_CLUSTER = register("crystal_cluster/corruption", Block(crystalBlockSettings(MapColor.PURPLE).nonOpaque()))

    fun registerBlockItems(itemMap: MutableMap<Identifier, Item>) {
        map.forEach { (identifier, block) ->
            itemMap[identifier] = when(block) {
                AIR_CRYSTAL_CLUSTER, EARTH_CRYSTAL_CLUSTER, FIRE_CRYSTAL_CLUSTER, WATER_CRYSTAL_CLUSTER, MAGIC_CRYSTAL_CLUSTER, CORRUPTION_CRISTAL_CLUSTER -> return
                else -> BlockItem(block, creativeGroupSettings())
            }
        }
    }


}