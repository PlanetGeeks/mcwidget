package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

public class WidgetActionEvent extends WidgetEvent
{
	private final int actionID;

	/**
	 * Called when a generic action is performed.
	 * 
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
}
