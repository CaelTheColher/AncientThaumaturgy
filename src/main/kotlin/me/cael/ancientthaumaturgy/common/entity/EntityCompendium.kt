package me.cael.ancientthaumaturgy.common.entity

import me.cael.ancientthaumaturgy.utils.RegistryCompendium
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.world.World

object EntityCompendium : RegistryCompendium<EntityType<*>>(Registries.ENTITY_TYPE) {

    val FIRE_PARTICLE = register("fire_particle", FabricEntityTypeBuilder.create(SpawnGroup.MISC) {
        type: EntityType<FireParticleEntity>, world: World -> FireParticleEntity(type, world)
    }.dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build())

    override fun initialize() {
        super.initialize()
    }

}