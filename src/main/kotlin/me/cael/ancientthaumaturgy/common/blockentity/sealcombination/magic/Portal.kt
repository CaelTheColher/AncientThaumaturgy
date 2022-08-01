package me.cael.ancientthaumaturgy.common.blockentity.sealcombination.magic

import me.cael.ancientthaumaturgy.common.blockentity.SealBlockEntity
import me.cael.ancientthaumaturgy.common.blockentity.sealcombination.AbstractSealCombination
import me.cael.ancientthaumaturgy.utils.PortalDestination
import me.cael.ancientthaumaturgy.utils.toDegrees
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import qouteall.imm_ptl.core.IPMcHelper
import qouteall.imm_ptl.core.portal.Portal
import java.util.*

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
//        println(portals)
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

        seal.partner = seal.findPartner()

        if (seal.partner != null) {
            seal.portalDestination = PortalDestination(seal.partner!!.pos, seal.partner!!.world!!.registryKey)
        }

        if (seal.partner == null) return
        seal.partner!!.partner = seal

        seal.portal = seal.portal ?: createPortal(seal, seal.partner!!)

//        seal.partner!!.portal = PortalManipulation.createReversePortal(seal.portal, Portal.entityType)

        if (!IPMcHelper.getNearbyPortals(seal.portal, 1.0).anyMatch { it.pos.distanceTo(seal.portal!!.originPos) <= 0.1 }) {
            seal.portal?.myUnsetRemoved()
            seal.world?.spawnEntity(seal.portal)
        }

    }

    fun createPortal(from: SealBlockEntity, to: SealBlockEntity): Portal {
        val originPos = Vec3d.ofCenter(from.pos).subtract(Vec3d.of(from.getDirection().vector).multiply(0.3))
        val destinationPos = Vec3d.ofCenter(to.pos).subtract(Vec3d.of(to.getDirection().vector).multiply(0.3))
        val rotation = from.getFacing().toDegrees()
        val degrees = 180 + to.getFacing().toDegrees() - rotation

        val portal = IPMcHelper.getNearbyPortals(from.world, originPos, 1.0).filter { it.pos.distanceTo(originPos) <= 0.1 }
            .findFirst().orElse(Portal.entityType.create(from.world)!!)

        portal.originPos = originPos
        portal.setDestinationDimension(to.world!!.registryKey)
        portal.setDestination(destinationPos)
//        portal.setRotationTransformation(DQuaternion.rotationByDegrees(Vec3d(0.0,1.0,0.0), degrees.toDouble()).toMcQuaternion())
        val xRotation = when (from.getDirection()) {
            Direction.UP -> -90
            Direction.DOWN -> 90
            else -> 0f
        }

        val dirOut = from.getDirection().opposite.vector
        val dirUp = if (dirOut.y == 0) Vec3i(0,1,0) else from.getFacing().vector
        val dirRight = dirUp.crossProduct(dirOut)

        portal.setOrientationAndSize(
            Vec3d.of(dirRight),
            Vec3d.of(dirUp).multiply(-1.0),
            1.5,
            1.5
        )
//        portal.setOrientationAndSize(
//            Vec3d(1.0,0.0,0.0).rotateY(Math.toRadians(rotation.toDouble()).toFloat()),
////            Vec3d(0.0,1.0,0.0).rotateX(Math.toRadians(xRotation.toDouble()).toFloat()).multiply(-1.0),
//            Vec3d(0.0,1.0,0.0),
//            1.5,
//            1.5
//        )
//        println("${portal.axisW}:${portal.axisH}")
//        println("createPortal: $portal")
        return portal
    }


}