package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The widget action event class.
 * <p>
 * <b>Important!</b> Not all components support this event.
 * <p>
 * Supported components:
 * <ul>
 * <li> WidgetButton
 * </ul>
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetActionEvent extends WidgetEvent
{
	private final int actionID;

	/**
	 * Construct a new instance of the event.
	 * 
	 * @see {@link #getActionID()} for further informations about actionID parameter.
	 * @param component - the component involved.
	 * @param actionID - the action identifier.
	 */
	public WidgetActionEvent(Widget component, int actionID)
	{
		super(component);
		this.actionID = actionID;
	}

	/**
	 * The WidgetActionEvent is a generic action event. The action ID can be used to
	 * find out the correct action to perform into a listener.
	 * 
	 * @return the action identifier.
	 */
	public int getActionID()
	{
		return actionID;
	}
	
	public static interface WidgetActionListener
	{
		@Subscribe public void onAction(WidgetActionEvent event);
	}
}
