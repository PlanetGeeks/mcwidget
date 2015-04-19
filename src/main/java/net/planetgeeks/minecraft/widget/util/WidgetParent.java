package net.planetgeeks.minecraft.widget.util;

import net.planetgeeks.minecraft.widget.Widget;

public interface WidgetParent
{
	/**
	 * Add the given component to this one.
	 * 
	 * @param component - the component.
	 * 
	 * @return true if the component has been added.
	 */
	boolean add(Widget component);

	/**
	 * Remove the given child component.
	 * 
	 * @param component - the component.
	 * 
	 * @return true if the component has been removed.
	 */
	boolean remove(Widget component);
}
