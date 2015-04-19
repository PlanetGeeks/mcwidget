package net.planetgeeks.minecraft.widget.util;

import net.planetgeeks.minecraft.widget.render.WidgetRenderer;

public interface Drawable
{
	/**
	 * Draw the object.
	 * 
	 * @param mouseX - the mouse cursor X position.
	 * @param mouseY - the mouse cursor Y position.
	 * @param partialTicks - the .
	 * @param renderer WidgetRenderer
	 * @throws Exception
	 */
    void draw(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer) throws Exception;
}
