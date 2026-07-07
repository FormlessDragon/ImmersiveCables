/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.immersivecables.tileentity.ae;

import ae2.api.AECapabilities;
import ae2.api.networking.GridFlags;
import ae2.api.networking.GridHelper;
import ae2.api.networking.IGridConnection;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IGridNodeListener;
import ae2.api.networking.IInWorldGridNodeHost;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.security.IActionHost;
import ae2.api.util.AECableType;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.WireApi;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.util.Utils;
import de.sanandrew.mods.immersivecables.util.ICConstants;
import de.sanandrew.mods.immersivecables.util.ImmersiveCables;
import net.minecraft.block.BlockDirectional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TileFluixConnectable
        extends TileEntityImmersiveConnectable
        implements IActionHost, IInWorldGridNodeHost, ITickable, IEBlockInterfaces.IAdvancedSelectionBounds
{
    protected static final String MAIN_NODE_TAG = "proxy";
    private static final IGridNodeListener<TileFluixConnectable> NODE_LISTENER = new FluixGridNodeListener();

    private final Map<BlockPos, IGridConnection> connections = new ConcurrentHashMap<>();
    private final Map<String, NBTTagCompound> managedNodeData = new HashMap<>();
    private IManagedGridNode mainNode = this.createMainNode();
    public EntityPlayer ownerCache;

    List<AxisAlignedBB> cachedSelectionBounds;

    private boolean loaded = false;

    protected IManagedGridNode createMainNode() {
        return createManagedFluixNode(this)
                       .setTagName(MAIN_NODE_TAG)
                       .setInWorldNode(true);
    }

    protected static IManagedGridNode createManagedFluixNode(TileFluixConnectable owner) {
        return GridHelper.createManagedNode(owner, NODE_LISTENER);
    }

    protected final IManagedGridNode getMainNode() {
        return this.mainNode;
    }

    @Override
    public IGridNode getGridNode(@Nullable EnumFacing dir) {
        return this.mainNode.getNode();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        this.resetManagedNodes();
    }

    public void securityBreak() {
        this.world.destroyBlock(this.pos, true);
    }

    protected void onReady() {
        this.configureMainNode();
        this.mainNode.create(this.world, this.pos);
    }

    protected void configureMainNode() {
        this.mainNode.setVisualRepresentation(this.getMachineRepresentation());
        this.mainNode.setIdlePowerUsage(this.getIdlePowerUsage());
        this.mainNode.setExposedOnSides(this.getConnectableSides());
        this.mainNode.setFlags(this.getFlags());
        if( this.ownerCache != null ) {
            this.mainNode.setOwningPlayer(this.ownerCache);
        }
    }

    @Override
    public boolean canConnect() {
        return true;
    }

    public ItemStack getMachineRepresentation() {
        return new ItemStack(this.getBlockType(), 1, this.getBlockMetadata() & 1);
    }

    @Override
    public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other) {
        super.connectCable(cableType, target, other);
        this.connectTo(Utils.toCC(other));
    }

    @Override
    public boolean allowEnergyToPass(ImmersiveNetHandler.Connection con) {
        return false;
    }

    @Override
    public boolean canConnectCable(WireType cableType, TargetingInfo target, Vec3i offset) {
        String category = cableType.getCategory();
        return this.getWireCategory().equals(category) && (this.limitType == null || this.isRelay() && WireApi.canMix(this.limitType, cableType));
    }

    @Override
    public void removeCable(ImmersiveNetHandler.Connection connection) {
        if( !this.world.isRemote && connection != null ) {
            BlockPos opposite = this.getOppositeConnectionEnd(connection);
            if( opposite != null ) {
                IGridConnection gridConnection = this.connections.remove(opposite);
                if( gridConnection != null ) {
                    this.destroyTrackedConnection(gridConnection);
                }
            }
        }

        super.removeCable(connection);
    }

    public abstract double getIdlePowerUsage();
    public abstract EnumSet<EnumFacing> getConnectableSides();
    public abstract GridFlags[] getFlags();
    @Override
    public abstract AECableType getCableConnectionType(EnumFacing dir);
    protected abstract String getWireCategory();

    @Override
    public void update() {
        if( !this.loaded && this.hasWorld() ) {
            this.loaded = true;
            if( !this.world.isRemote ) {
                this.onReady();

                Set<ImmersiveNetHandler.Connection> connections = ImmersiveNetHandler.INSTANCE.getConnections(this.world, Utils.toCC(this));
                if( connections != null ) {
                    for( ImmersiveNetHandler.Connection connection : connections ) {
                        BlockPos opposite = this.getOppositeConnectionEnd(connection);
                        if( opposite != null ) {
                            this.connectTo(opposite);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.resetManagedNodes();
    }

    private void resetManagedNodes() {
        NBTTagCompound nodeData = new NBTTagCompound();
        this.saveManagedNodes(nodeData);
        this.destroyVirtualConnections();
        this.destroyManagedNodes();
        this.createManagedNodes();
        this.loadManagedNodes(nodeData);
        this.loaded = false;
    }

    protected void saveManagedNodes(NBTTagCompound compound) {
        this.saveManagedNode(compound, this.mainNode, MAIN_NODE_TAG);
    }

    protected void destroyManagedNodes() {
        this.mainNode.destroy();
    }

    protected void createManagedNodes() {
        this.mainNode = this.createMainNode();
    }

    protected void loadManagedNodes(NBTTagCompound compound) {
        this.loadManagedNode(compound, this.mainNode, MAIN_NODE_TAG);
    }

    protected final void saveManagedNode(NBTTagCompound compound, IManagedGridNode node, String tagName) {
        if( node.getNode() != null ) {
            node.saveToNBT(compound);
            this.storeManagedNodeData(compound, tagName);
        } else {
            this.writeStoredManagedNodeData(compound, tagName);
        }
    }

    protected final void loadManagedNode(NBTTagCompound compound, IManagedGridNode node, String tagName) {
        this.storeManagedNodeData(compound, tagName);
        node.loadFromNBT(compound);
    }

    private void storeManagedNodeData(NBTTagCompound compound, String tagName) {
        if( compound.hasKey(tagName, 10) ) {
            this.managedNodeData.put(tagName, compound.getCompoundTag(tagName).copy());
        } else {
            this.managedNodeData.remove(tagName);
        }
    }

    private void writeStoredManagedNodeData(NBTTagCompound compound, String tagName) {
        NBTTagCompound nodeData = this.managedNodeData.get(tagName);
        if( nodeData != null ) {
            compound.setTag(tagName, nodeData.copy());
        }
    }

    public void gridChanged() {
        IGridNode node = this.mainNode.getNode();
        if( node == null ) {
            this.connections.clear();
            return;
        }

        for( Map.Entry<BlockPos, IGridConnection> entry : this.connections.entrySet() ) {
            BlockPos opposite = entry.getKey();
            IGridConnection conn = entry.getValue();
            if( !node.getConnections().contains(conn) ) {
                this.connections.remove(opposite, conn);
            } else if( !ImmersiveCables.isChunkLoaded(this.world.getChunkProvider(), opposite.getX() >> 4, opposite.getZ() >> 4) ) {
                this.destroyTrackedConnection(conn);
                this.connections.remove(opposite, conn);
            }
        }
    }

    protected void onGridNodeStateChanged(IGridNodeListener.State state) {
        this.gridChanged();
    }

    private void destroyVirtualConnections() {
        for( IGridConnection connection : this.connections.values() ) {
            this.destroyTrackedConnection(connection);
        }
        this.connections.clear();
    }

    private void destroyTrackedConnection(IGridConnection connection) {
        IGridNode node = this.mainNode.getNode();
        if( node != null && node.getConnections().contains(connection) ) {
            connection.destroy();
        }
    }

    @Nullable
    private BlockPos getOppositeConnectionEnd(ImmersiveNetHandler.Connection connection) {
        BlockPos here = Utils.toCC(this);
        if( connection.start.equals(here) ) {
            return connection.end;
        } else if( connection.end.equals(here) ) {
            return connection.start;
        }

        ICConstants.LOG.log(Level.DEBUG, "Ignoring AE2 grid removal for unrelated IE connection at " + this.pos);
        return null;
    }

    @Override
    public IGridNode getActionableNode() {
        return this.mainNode.getNode();
    }

    @Nullable
    private static IGridNode getConnectableGridNode(IInWorldGridNodeHost host) {
        if( host instanceof TileFluixConnectable ) {
            return ((TileFluixConnectable) host).getMainNode().getNode();
        }

        for( EnumFacing facing : EnumFacing.VALUES ) {
            IGridNode node = host.getGridNode(facing);
            if( node != null ) {
                return node;
            }
        }

        return host.getGridNode(null);
    }

    public void connectTo(BlockPos pos) {
        if( this.world == null || this.world.isRemote ) {
            return;
        }

        if( ImmersiveCables.isChunkLoaded(this.world.getChunkProvider(), pos.getX() >> 4, pos.getZ() >> 4) ) {
            IGridNode nodeB = this.getGridNode(null);
            if( nodeB == null ) {
                ICConstants.LOG.log(Level.DEBUG, "Cannot create AE2 grid connection from " + this.pos + " to " + pos + ": local node is not ready");
                return;
            }

            IInWorldGridNodeHost host = GridHelper.getNodeHost(this.world, pos);
            if( host == null ) {
                return;
            }

            IGridNode nodeA = getConnectableGridNode(host);
            if( nodeA == null ) {
                ICConstants.LOG.log(Level.DEBUG, "Cannot create AE2 grid connection from " + this.pos + " to " + pos + ": remote node is not ready");
                return;
            }

            IGridConnection existing = this.connections.get(pos);
            if( existing != null && nodeB.getConnections().contains(existing) ) {
                return;
            } else if( existing != null ) {
                this.connections.remove(pos, existing);
            }

            try {
                IGridConnection conn = GridHelper.createConnection(nodeA, nodeB);
                this.connections.put(pos, conn);
            } catch( IllegalStateException ex ) {
                ICConstants.LOG.log(Level.DEBUG, "AE2 grid connection already exists between " + this.pos + " and " + pos, ex);
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == AECapabilities.IN_WORLD_GRID_NODE_HOST || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if( capability == AECapabilities.IN_WORLD_GRID_NODE_HOST ) {
            return AECapabilities.IN_WORLD_GRID_NODE_HOST.cast(this);
        }

        return super.getCapability(capability, facing);
    }

    protected EnumFacing getFacing() {
        return this.world != null && !this.world.isAirBlock(this.pos) ? this.world.getBlockState(this.pos).getValue(BlockDirectional.FACING) : EnumFacing.UP;
    }

    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        this.saveManagedNodes(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.loadManagedNodes(compound);
    }

    @Override
    public boolean isOverrideBox(AxisAlignedBB box, EntityPlayer entityPlayer, RayTraceResult ray, ArrayList<AxisAlignedBB> arrayList) {
        return box.grow(0.002D, 0.002D, 0.002D).contains(ray.hitVec);
    }

    @Override
    public float[] getBlockBounds() {
        return null;
    }

    private static final class FluixGridNodeListener
            implements IGridNodeListener<TileFluixConnectable>
    {
        @Override
        public void onSaveChanges(TileFluixConnectable nodeOwner, IGridNode node) {
            nodeOwner.markDirty();
        }

        @Override
        public void onGridChanged(TileFluixConnectable nodeOwner, IGridNode node) {
            nodeOwner.gridChanged();
        }

        @Override
        public void onStateChanged(TileFluixConnectable nodeOwner, IGridNode node, State state) {
            nodeOwner.onGridNodeStateChanged(state);
        }
    }
}
