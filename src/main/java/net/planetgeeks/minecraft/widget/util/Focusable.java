package net.planetgeeks.minecraft.widget.util;

public interface Focusable
{
	/**
	 * @return true if the component can lose focus by clicking elsewhere on the
	 *         screen.
	 */
	boolean canLoseFocus();

	/**
	 * @param canLoseFocus - true if you want the component to lose focus by
	 *            clicking elsewhere on the screen.
	 */
	void setCanLoseFocus(boolean canLoseFocus);

	/**
	 * Get component's focus status.
	 * 
	 * @return true if the component is focused.
	 */
	boolean isFocused();

	/**
	 * Set component's focus status.
	 * 
	 * @param focused - true to set the component focused.
	 */
	void setFocused(boolean focused);
}
