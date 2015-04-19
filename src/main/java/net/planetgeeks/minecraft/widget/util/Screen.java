package net.planetgeeks.minecraft.widget.util;

public interface Screen
{
	/**
	 * Called when the screen is resized. It's also called after
	 * {@link #onScreenInit()}
	 * <p>
	 * Usually is used to set components size and location.
	 */
	void onScreenResize();

	/**
	 * Called when the screen is created.
	 * <p>
	 * Usually is used to add components to the screen in question.
	 */
	void onScreenInit();
}
