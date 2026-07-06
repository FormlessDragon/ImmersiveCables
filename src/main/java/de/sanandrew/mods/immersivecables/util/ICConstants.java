/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.immersivecables.util;

import de.sanandrew.mods.immersivecables.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ICConstants
{
    static final String COMPAT_APPLIEDENERGISTICS = "ae2";
    static final String COMPAT_REFINEDSTORAGE = "refinedstorage";

    public static final String ID = Tags.MOD_ID;
    public static final Logger LOG = LogManager.getLogger(ID);
    public static final String VERSION = Tags.VERSION;
    public static final String NAME = Tags.MOD_NAME;
    public static final String DEPENDENCIES = "required-after:forge@[14.23.4.2739,];required-after:immersiveengineering@[0.12-78,];" +
                                                      "after:" + COMPAT_APPLIEDENERGISTICS + ';' +
                                                      "after:" + COMPAT_REFINEDSTORAGE;
}
