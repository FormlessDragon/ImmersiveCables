/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.immersivecables.tileentity.ae;

import ae2.api.networking.GridFlags;
import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IManagedGridNode;
import ae2.api.util.AECableType;
import ae2.me.energy.IEnergyOverlayGridConnection;
import ae2.me.service.EnergyService;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import de.sanandrew.mods.immersivecables.util.ICConfiguration;
import de.sanandrew.mods.immersivecables.wire.Wires;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TileConnectorQuartz
        extends TileFluixConnectable
{
    private static final String OUTER_NODE_TAG = "outer";

    private IManagedGridNode outerNode = this.createOuterNode();

    @Override
    protected IManagedGridNode createMainNode() {
        return super.createMainNode()
                    .addService(IEnergyOverlayGridConnection.class, this::getOuterEnergyServices);
    }

    protected IManagedGridNode createOuterNode() {
        return createManagedFluixNode(this)
                       .setTagName(OUTER_NODE_TAG)
                       .setInWorldNode(true)
                       .setIdlePowerUsage(ICConfiguration.ae2QuartzConnectorPowerDrain)
                       .setFlags(GridFlags.CANNOT_CARRY)
                       .addService(IEnergyOverlayGridConnection.class, this::getMainEnergyServices);
    }

    @Override
    protected void onReady() {
        super.onReady();
        this.configureOuterNode();
        this.outerNode.create(this.world, this.pos);
    }

    private void configureOuterNode() {
        this.outerNode.setVisualRepresentation(this.getMachineRepresentation());
        this.outerNode.setExposedOnSides(EnumSet.of(this.getFacing().getOpposite()));
        if( this.ownerCache != null ) {
            this.outerNode.setOwningPlayer(this.ownerCache);
        }
    }

    @Override
    public IGridNode getGridNode(EnumFacing dir) {
        if( dir == this.getFacing().getOpposite() ) {
            return this.outerNode.getNode();
        }

        return super.getGridNode(dir);
    }

    @Override
    protected void saveManagedNodes(NBTTagCompound compound) {
        super.saveManagedNodes(compound);
        this.saveManagedNode(compound, this.outerNode, OUTER_NODE_TAG);
    }

    @Override
    protected void destroyManagedNodes() {
        super.destroyManagedNodes();
        this.outerNode.destroy();
    }

    @Override
    protected void createManagedNodes() {
        super.createManagedNodes();
        this.outerNode = this.createOuterNode();
    }

    @Override
    protected void loadManagedNodes(NBTTagCompound compound) {
        super.loadManagedNodes(compound);
        this.loadManagedNode(compound, this.outerNode, OUTER_NODE_TAG);
    }

    @Override
    public double getIdlePowerUsage() {
        return 0.0D;
    }

    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        return EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public GridFlags[] getFlags() {
        return new GridFlags[] { GridFlags.CANNOT_CARRY };
    }

    @Override
    public AECableType getCableConnectionType(EnumFacing dir) {
        return AECableType.GLASS;
    }

    @Override
    protected String getWireCategory() {
        return Wires.QUARTZ.category;
    }

    @Override
    protected boolean isRelay() {
        return true;
    }

    @Override
    public Vec3d getConnectionOffset(ImmersiveNetHandler.Connection con) {
        EnumFacing facing = this.getFacing();
        return new Vec3d(0.5D - facing.getXOffset() * 0.1D, 0.5D - facing.getYOffset() * 0.1D, 0.5D - facing.getZOffset() * 0.1D);
    }

    @Override
    public List<AxisAlignedBB> getAdvancedSelectionBounds() {
        if( this.cachedSelectionBounds == null ) {
            EnumFacing facing = this.getFacing();
            switch( facing ) {
                case UP:
                    this.cachedSelectionBounds = Collections.singletonList(new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.4375D, 0.6875).offset(this.pos));
                    break;
                case DOWN:
                    this.cachedSelectionBounds = Collections.singletonList(new AxisAlignedBB(0.3125D, 0.5625D, 0.3125D, 0.6875D, 1.0D, 0.6875).offset(this.pos));
                    break;
                case NORTH:
                    this.cachedSelectionBounds = Collections.singletonList(new AxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.6875D, 1.0D).offset(this.pos));
                    break;
                case SOUTH:
                    this.cachedSelectionBounds = Collections.singletonList(new AxisAlignedBB(0.3125D, 0.3125D, 0.0D, 0.6875D, 0.6875D, 0.4375D).offset(this.pos));
                    break;
                case EAST:
                    this.cachedSelectionBounds = Collections.singletonList(new AxisAlignedBB(0.0D, 0.3125D, 0.3125D, 0.4375D, 0.6875D, 0.6875).offset(this.pos));
                    break;
                case WEST:
                    this.cachedSelectionBounds = Collections.singletonList(new AxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 1.0D, 0.6875D, 0.6875).offset(this.pos));
                    break;
            }
        }

        return this.cachedSelectionBounds;
    }

    private Collection<EnergyService> getMainEnergyServices() {
        IGrid grid = this.getMainNode().getGrid();
        if( grid == null ) {
            return Collections.emptyList();
        }

        return Collections.singletonList((EnergyService) grid.getEnergyService());
    }

    private Collection<EnergyService> getOuterEnergyServices() {
        IGrid grid = this.outerNode.getGrid();
        if( grid == null ) {
            return Collections.emptyList();
        }

        return Collections.singletonList((EnergyService) grid.getEnergyService());
    }

    @Override
    public ItemStack getMachineRepresentation() {
        return new ItemStack(this.getBlockType(), 1);
    }
}
