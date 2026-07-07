/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.immersivecables.recipes;

import de.sanandrew.mods.immersivecables.block.ae2.BlockRegistryAE2;
import de.sanandrew.mods.immersivecables.block.ae2.FluixType;
import de.sanandrew.mods.immersivecables.util.ICConstants;
import de.sanandrew.mods.immersivecables.util.ItemBlockRegistry;
import de.sanandrew.mods.immersivecables.wire.Wires;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Objects;

public final class RecipeRegistryAE2
{
    public static void initialize(RegistryEvent.Register<IRecipe> event, ResourceLocation group) {
        ItemStack fluixCrystal = new ItemStack(Objects.requireNonNull(Item.getByNameOrId("ae2:fluix_crystal")), 1);
        ItemStack fluixBlock = new ItemStack(Objects.requireNonNull(Item.getByNameOrId("ae2:fluix_block")), 1);
        ItemStack quartzCrystal = new ItemStack(Objects.requireNonNull(Item.getByNameOrId("ae2:certus_quartz_crystal")), 1);
        ItemStack skyStone = new ItemStack(Objects.requireNonNull(Item.getByNameOrId("ae2:smooth_sky_stone_block")), 1);

        event.getRegistry().register(new ShapedOreRecipe(group, new ItemStack(ItemBlockRegistry.WIRE_COIL, 4, Wires.FLUIX.ordinal()),
                                                         " F ", "WSW", " F ",
                                                         'F', fluixCrystal,
                                                         'W', "wireAluminum",
                                                         'S', "stickWood")
                                             .setRegistryName(ICConstants.ID, "wire_coil_fluix_s"));
        event.getRegistry().register(new ShapedOreRecipe(group, new ItemStack(ItemBlockRegistry.WIRE_COIL, 4, Wires.FLUIX.ordinal()),
                                                         " F ", "WSW", " F ",
                                                         'F', fluixCrystal,
                                                         'W', "wireAluminum",
                                                         'S', "stickTreatedWood")
                                             .setRegistryName(ICConstants.ID, "wire_coil_fluix_t"));

        event.getRegistry().register(new ShapedOreRecipe(group, new ItemStack(ItemBlockRegistry.WIRE_COIL, 4, Wires.FLUIX_DENSE.ordinal()),
                                                         " F ", "WSW", " F ",
                                                         'F', fluixBlock,
                                                         'W', "wireSteel",
                                                         'S', "stickWood")
                                             .setRegistryName(ICConstants.ID, "wire_coil_dense_s"));
        event.getRegistry().register(new ShapedOreRecipe(group, new ItemStack(ItemBlockRegistry.WIRE_COIL, 4, Wires.FLUIX_DENSE.ordinal()),
                                                         " F ", "WSW", " F ",
                                                         'F', fluixBlock,
                                                         'W', "wireSteel",
                                                         'S', "stickTreatedWood")
                                             .setRegistryName(ICConstants.ID, "wire_coil_dense_t"));

        event.getRegistry().register(new ShapedOreRecipe(group, new ItemStack(ItemBlockRegistry.WIRE_COIL, 4, Wires.QUARTZ.ordinal()),
                                                         " F ", "WSW", " F ",
                                                         'F', quartzCrystal,
                                                         'W', "wireCopper",
                                                         'S', "stickWood")
                                             .setRegistryName(ICConstants.ID, "wire_coil_quartz_s"));
        event.getRegistry().register(new ShapedOreRecipe(group, new ItemStack(ItemBlockRegistry.WIRE_COIL, 4, Wires.QUARTZ.ordinal()),
                                                         " F ", "WSW", " F ",
                                                         'F', quartzCrystal,
                                                         'W', "wireCopper",
                                                         'S', "stickTreatedWood")
                                             .setRegistryName(ICConstants.ID, "wire_coil_quartz_t"));

        event.getRegistry().register(new ShapedRecipes(group.toString(), 3, 2,
                                                       CraftingHelper.parseShaped(" F ", "SFS",
                                                                                  'F', fluixCrystal,
                                                                                  'S', skyStone).input,
                                                       new ItemStack(BlockRegistryAE2.RELAY_FLUIX, 8, FluixType.FLUIX.ordinal()))
                                             .setRegistryName(ICConstants.ID, "relay_fluix"));

        event.getRegistry().register(new ShapedRecipes(group.toString(), 3, 3,
                                                       CraftingHelper.parseShaped(" F ", "SFS", "SFS",
                                                                                  'F', fluixBlock,
                                                                                  'S', skyStone).input,
                                                       new ItemStack(BlockRegistryAE2.RELAY_FLUIX, 8, FluixType.FLUIX_DENSE.ordinal()))
                                             .setRegistryName(ICConstants.ID, "relay_dense"));

        event.getRegistry().register(new ShapedOreRecipe(group, new ItemStack(BlockRegistryAE2.CONNECTOR_QUARTZ, 4),
                                                         " Q ", "GQG",
                                                         'Q', quartzCrystal,
                                                         'G', "blockGlass")
                                             .setRegistryName(ICConstants.ID, "connector_quartz"));

        event.getRegistry().register(new ShapedOreRecipe(group, new ItemStack(BlockRegistryAE2.TRANSFORMER_FLUIX, 1, FluixType.FLUIX.ordinal()),
                                                         " R ", "ICI", "III",
                                                         'R', new ItemStack(BlockRegistryAE2.RELAY_FLUIX, 1, FluixType.FLUIX.ordinal()),
                                                         'I', "ingotIron",
                                                         'C', new ItemStack(ItemBlockRegistry.BLOCK_COIL, 1, Wires.FLUIX.ordinal()))
                                             .setRegistryName(ICConstants.ID, "transformer_fluix"));

        event.getRegistry().register(new ShapedOreRecipe(group, new ItemStack(BlockRegistryAE2.TRANSFORMER_FLUIX, 1, FluixType.FLUIX_DENSE.ordinal()),
                                                         " R ", "ICI", "III",
                                                         'R', new ItemStack(BlockRegistryAE2.RELAY_FLUIX, 1, FluixType.FLUIX_DENSE.ordinal()),
                                                         'I', "ingotIron",
                                                         'C', new ItemStack(ItemBlockRegistry.BLOCK_COIL, 1, Wires.FLUIX_DENSE.ordinal()))
                                             .setRegistryName(ICConstants.ID, "transformer_dense"));
    }
}
