package net.planetgeeks.minecraft.widget;

import lombok.Getter;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;

@Mod(modid = WidgetFramework.MODID, version = WidgetFramework.VERSION, clientSideOnly = true)
public class WidgetFramework
{
    public static final String MODID = "widget";
    public static final String VERSION = "0.1dev";

    @Getter @Instance
    private static WidgetFramework instance;
    
    /**
     * BUG TO FIX:
     * - TextField - selection with mouse doesn't work.
     * - TextField - add text alignment support. 
     * 
     * TO DO:
     * - The new introduced shape render system must be improved (e.g. supporting intersection between different shapes).
     * 
     * FUTURE FEATURES:
     * - Advanced logger system.
     * - Floating Screen - a screen that can be rendered in the game into a space (xyz), it seems floating into the air! See the RenderGameOverlayEvent :O!
     * - ToolTip - a label that describes a component.
     * 
     * FUTURE COMPONENTS:
     * - Radio button.
     * - Combo box.
     * - Password field.
     * - List.
     * - Progress bar.
     * - Scroll Panel.
     * - Slider.
     * - Tabbed panel.
     * - Menu (when right-click on a label for example).
     */
}
