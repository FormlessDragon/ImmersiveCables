/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.immersivecables.client.util;

import de.sanandrew.mods.immersivecables.block.BlockRegistryAE2;
import de.sanandrew.mods.immersivecables.block.FluixType;
import de.sanandrew.mods.immersivecables.client.render.RenderTileIWConnectable;
import de.sanandrew.mods.immersivecables.tileentity.TileConnectorQuartz;
import de.sanandrew.mods.immersivecables.tileentity.TileRelayFluix;
import de.sanandrew.mods.immersivecables.tileentity.TileTransformerFluix;
import de.sanandrew.mods.immersivecables.util.ICConstants;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public final class ModelRegistryAE2
{
    public static void registerModels() {
        ModelRegistry.registerModelBlockItems(BlockRegistryAE2.RELAY_FLUIX, new HashMap<>() {{
            put(0, new ModelResourceLocation(ICConstants.ID + ":relay_" + FluixType.FLUIX, "inventory"));
            put(1, new ModelResourceLocation(ICConstants.ID + ":relay_" + FluixType.FLUIX_DENSE, "inventory"));
        }});

        ModelRegistry.registerModelBlockItems(BlockRegistryAE2.CONNECTOR_QUARTZ, new HashMap<>() {{
            put(0, new ModelResourceLocation(ICConstants.ID + ":connector_quartz", "inventory"));
        }});

        ModelRegistry.registerModelBlockItems(BlockRegistryAE2.TRANSFORMER_FLUIX, new HashMap<>() {{
            put(0, new ModelResourceLocation(ICConstants.ID + ":transformer_" + FluixType.FLUIX, "inventory"));
            put(1, new ModelResourceLocation(ICConstants.ID + ":transformer_" + FluixType.FLUIX_DENSE, "inventory"));
        }});

        ClientRegistry.bindTileEntitySpecialRenderer(TileTransformerFluix.class, new RenderTileIWConnectable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRelayFluix.class, new RenderTileIWConnectable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileConnectorQuartz.class, new RenderTileIWConnectable());
    }
}
