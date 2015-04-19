package net.planetgeeks.minecraft.widget.util;

public interface Visible
{
	/**
	 * Return true if the object is visible.
	 * 
	 * @return the visibility set.
	 */
	boolean isVisible();

	/**
	 * Set object visibility.
	 * 
	 * @param visible - the object visibility to set.
	 */
	void setVisible(boolean visible);
}
