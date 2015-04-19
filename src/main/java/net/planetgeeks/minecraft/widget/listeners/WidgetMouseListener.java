package net.planetgeeks.minecraft.widget.listeners;

import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseClickOutsideEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseEnterEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseExitEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseReleaseEvent;

/**
 * The listener interface for receiving widget mouse events.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public interface WidgetMouseListener extends WidgetListener
{
	/** 
	 * Called when the mouse is pressed onto a component's HitBox or onto an HitBox that belongs to at least one of its children.
	 * <p>
	 * The component that has been directly pressed can be retrieved from the given event.
	 * 
	 * @param event - the mouse event.
	 **/
	void onMousePressed(WidgetMousePressEvent event);

	/**
	 * Called when a mouse button, previously pressed on the component, is
	 * released.
	 * <p>
	 * <b>Important!</b> It doesn't refer to the generic mouse button release
	 * event! This is called if the component was previously clicked ({@link#
	 * onMouseClicked(WidgetMouseEvent)}).
	 * 
	 * @param event - the mouse event.
	 **/
	void onMouseReleased(WidgetMouseReleaseEvent event);

	/**
	 * Called when the mouse button is pressed outside the component's HitBox.
	 * <p>
	 * The pressed component can be retrieved from the given event.
	 * <p>
	 * If the mouse is clicked on an child component, this method will not be
	 * called. Use {@link #onMousePressed(WidgetMousePressEvent)} instead and check
	 * the clicked component.
	 * 
	 * @param event - the mouse event.
	 */
	void onMouseClickedOutside(WidgetMouseClickOutsideEvent event);

	/**
	 * Called when the mouse enter into component's HitBox.
	 * 
	 * @param event - the mouse event
	 */
	void onMouseEntered(WidgetMouseEnterEvent event);

	/**
	 * Called when the mouse exit from component's HitBox.
	 * 
	 * @param event - the mouse event
	 */
	void onMouseExited(WidgetMouseExitEvent event);
}
