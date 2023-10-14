package me.cael.ancientthaumaturgy.common.blockentity.sealcombination.magic.portal

import me.cael.ancientthaumaturgy.common.blockentity.SealBlockEntity
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.AbstractSealCombination
import me.cael.ancientthaumaturgy.utils.PortalDestination
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Box
import java.util.*
import kotlin.math.asin
import kotlin.math.atan2


class Portal(val frequency: Char) : AbstractSealCombination() {

    data class PortalNetwork(var portalA: SealBlockEntity? = null, var portalB: SealBlockEntity? = null) {
        fun contains(portal: SealBlockEntity?): Boolean {
            return portalA == portal || portalB == portal
        }
        override fun toString(): String {
            return "${portalA?.pos}:${portalB?.pos}"
        }
    }

    val registry = HashMap<UUID, PortalNetwork>()

    override fun beforeRuneChange(seal: SealBlockEntity) {

        seal.findPartner()?.removePortal()
        seal.removePortal()

        if (seal.owner == null) return
        val portals = registry[seal.owner] ?: return
        if (!portals.contains(seal)) return

        if (portals.portalA == seal) {
            portals.portalA = null
        } else if (portals.portalB == seal) {
            portals.portalB = null
        }

        registry[seal.owner!!] = portals

    }

    override fun afterRuneChange(seal: SealBlockEntity) {
        if (seal.owner == null) return
        val portals = registry[seal.owner] ?: PortalNetwork()
        if (portals.portalA == null) {
            portals.portalA = seal
            seal.partner = portals.portalB
        }
        if (portals.portalB == null && portals.portalA != seal) {
            portals.portalB = seal
            seal.partner = portals.portalA
        }

        registry[seal.owner!!] = portals

    }

    override fun tick(seal: SealBlockEntity) {
        if (seal.owner == null) return

        seal.partner = seal.findPartner() ?: return

        if (seal.partner!!.partner != seal) {
            seal.partner!!.partner = seal
        }

        seal.portalDestination = PortalDestination(seal.partner!!.pos, seal.partner!!.world!!.registryKey)
        val facing = seal.getDirection().vector
        val nearbyPlayers = seal.world!!.getEntitiesByClass(ServerPlayerEntity::class.java, Box.of(seal.pos.toCenterPos(),
            facing.x.toDouble(), facing.y.toDouble(), facing.z.toDouble()
        )) { true }
        val partnerPos = seal.partner!!.pos.toCenterPos()
        val partnerDir = seal.partner!!.getFacing()
        val exitPos = partnerPos.offset(partnerDir, 1.0)
        val pitch = Math.toDegrees(asin(-partnerDir.unitVector.y).toDouble())
        val yaw = Math.toDegrees(atan2(partnerDir.unitVector.x, partnerDir.unitVector.z).toDouble())
        nearbyPlayers.forEach { player ->
                player.teleport(
                    seal.partner!!.world as ServerWorld,
                    exitPos.x, exitPos.y, exitPos.z, -yaw.toFloat(), pitch.toFloat()
                )
        }
    }

}