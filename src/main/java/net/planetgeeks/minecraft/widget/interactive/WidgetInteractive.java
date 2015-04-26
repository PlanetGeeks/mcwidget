package net.planetgeeks.minecraft.widget.interactive;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.events.WidgetDisableEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEnableEvent;
import net.planetgeeks.minecraft.widget.events.WidgetKeyTypeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseDragEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEnterEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseButtonEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseExitEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseMoveEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressOutsideEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseReleaseEvent;
import net.planetgeeks.minecraft.widget.util.Point;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

import com.google.common.eventbus.Subscribe;

/**
 * Represents a Widget that can handle mouse/keyboard inputs.
 * <p>
 * <b>Complete list of supported events.</b>
 * <ul>
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetEnableEvent WidgetEnableEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetDisableEvent WidgetDisableEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetMouseEvent WidgetMouseEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetMouseButtonEvent WidgetMouseButtonEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetMouseExitEvent WidgetMouseExitEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetMouseEnterEvent WidgetMouseEnterEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent WidgetMousePressEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetMouseReleaseEvent WidgetMouseReleaseEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetMousePressOutsideEvent WidgetMousePressOutsideEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetMouseMoveEvent WidgetMouseMoveEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetMouseDragEvent WidgetMouseDragEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetKeyEvent WidgetKeyEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetKeyTypeEvent WidgetKeyTypeEvent}
 * </ul>
 * 
 * @author Vincenzo Fortunato (Flood)
 */
public abstract class WidgetInteractive extends Widget
{
	@Getter
	@Setter
	private WidgetHitbox hitbox = new WidgetHitbox.Dynamic(this);
	private boolean enabled = true;
	private boolean pressed = false;
	private boolean mouseEntered = false;
	private static Point latestMousePosition = null;
	private Set<Integer> pressMap = new HashSet<>();

	public WidgetInteractive(int xPosition, int yPosition, int width, int height)
	{
		super(xPosition, yPosition, width, height);

		class InteractiveHandler
		{
			@Subscribe
			public void onEvent(WidgetMousePressEvent event)
			{
				synchronized(pressMap)
				{
					pressMap.add(event.getMouseButton());	
				}
				
				if (event.isLeftButton())
					pressed = true;
			}

			@Subscribe
			public void onEvent(WidgetMouseReleaseEvent event)
			{
				synchronized(pressMap)
				{
					pressMap.remove(event.getMouseButton());
				}
				
				if (event.isLeftButton())
					pressed = false;
			}
		}

		getEventBus().register(new InteractiveHandler());
	}

	public WidgetInteractive(int width, int height)
	{
		this(0, 0, width, height);
	}

	/**
	 * Return true if the component reacts to mouse/keyboard events.
	 * <p>
	 * A disabled component will not influence its children components. They
	 * will react to events if they are enabled.
	 * 
	 * <strong>Important!</strong> A non visible component is considered
	 * disabled!
	 * 
	 * @return true if the component is enabled.
	 */
	public boolean isEnabled()
	{
		return isVisible() && enabled;
	}

	/**
	 * Set the component enabled or disabled.
	 * 
	 * @param enabled - the component status to set.
	 * @see {@link #isEnabled()} for further informations about this.
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		this.getEventBus().post(enabled ? new WidgetEnableEvent(this) : new WidgetDisableEvent(this));
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		if (!isEnabled())
			return;

		List<WidgetInteractive> componentsAt = getComponentsAt(mouseX, mouseY);

		if (!componentsAt.isEmpty())
		{
			WidgetInteractive topComponent = componentsAt.get(0);

			WidgetMousePressEvent event = new WidgetMousePressEvent(topComponent, mouseX, mouseY, mouseButton);

			for (WidgetInteractive component : componentsAt)
				component.getEventBus().post(event);

			checkPressOutside(componentsAt, new WidgetMousePressOutsideEvent(event.getComponent(), mouseX, mouseY, mouseButton));
			return;
		}

		this.getEventBus().post(new WidgetMousePressOutsideEvent(null, mouseX, mouseY, mouseButton));
	}

	private void checkPressOutside(List<WidgetInteractive> clickedComponents, WidgetMousePressOutsideEvent event)
	{
		if (!clickedComponents.contains(this))
			getEventBus().post(event);

		Set<Widget> children = getChildren();

		synchronized (children)
		{
			for (Widget component : children)
			{
				if (component instanceof WidgetInteractive)
					((WidgetInteractive) component).checkPressOutside(clickedComponents, event);
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton)
	{
		Set<Widget> children = getChildren();

		synchronized (children)
		{
			for (Widget component : children)
				component.mouseReleased(mouseX, mouseY, mouseButton);
		}

		this.getEventBus().post(new WidgetMouseReleaseEvent(this, mouseX, mouseY, mouseButton));
	}

	@Override
	public void keyTyped(char typedChar, int keyCode)
	{
		if (!isEnabled())
			return;

		WidgetKeyTypeEvent event = new WidgetKeyTypeEvent(this, typedChar, keyCode);

		Set<Widget> children = getChildren();

		synchronized (children)
		{
			for (Widget component : children)
				component.keyTyped(typedChar, keyCode);
		}

		this.getEventBus().post(event);
	}

	@Override
	protected void onUpdate()
	{
		if (getParent() == null) // This is true only if it's the root widget (rendered and updated directly from a WidgetScreen).
		{
			Point mousePos = WidgetUtil.getMousePosition();

			if (latestMousePosition == null)
				latestMousePosition = mousePos.clone();
			
			List<WidgetInteractive> componentsAt = getComponentsAt(mousePos);

			if (!componentsAt.isEmpty())
			{
				WidgetInteractive topComponent = componentsAt.get(0);
				WidgetMouseEnterEvent enterEvent = new WidgetMouseEnterEvent(topComponent, mousePos.getX(), mousePos.getY());
                WidgetMouseMoveEvent moveEvent = latestMousePosition.equals(mousePos) ? new WidgetMouseMoveEvent(topComponent, mousePos.getX(), mousePos.getY(), latestMousePosition.getX(), latestMousePosition.getY()) : null;
				WidgetMouseDragEvent dragEvent = moveEvent != null ? new WidgetMouseDragEvent(moveEvent.getComponent(), moveEvent.getMouseX(), moveEvent.getMouseY(), WidgetMouseButtonEvent.LEFT_BUTTON, moveEvent.getLatestMouseX(), moveEvent.getLatestMouseY()) : null;
                
				for (WidgetInteractive component : componentsAt)
				{
					if (!component.mouseEntered)
					{
						component.mouseEntered = true;
						component.getEventBus().post(enterEvent);
					}
					
					component.handleMouseMotion(moveEvent, dragEvent);
				}
			}

			checkExit(componentsAt, mousePos);
		}

		super.onUpdate();
	}

	private void handleMouseMotion(WidgetMouseMoveEvent moveEvent, WidgetMouseDragEvent dragEvent)
	{
		if(moveEvent == null)
			return;
		
		getEventBus().post(moveEvent);
		
		synchronized(pressMap)
		{
			if(pressMap.contains(WidgetMouseButtonEvent.LEFT_BUTTON))
			{
				getEventBus().post(dragEvent);
			}
		}
	}
	
	private void checkExit(List<WidgetInteractive> componentsAt, Point mousePos)
	{
		if (mouseEntered && !componentsAt.contains(this))
		{
			mouseEntered = false;
			getEventBus().post(new WidgetMouseExitEvent(this, mousePos.getX(), mousePos.getY()));
		}

		Set<Widget> children = getChildren();

		synchronized (children)
		{
			for (Widget component : children)
			{
				if (component instanceof WidgetInteractive)
					((WidgetInteractive) component).checkExit(componentsAt, mousePos);
			}
		}
	}

	/**
	 * Check if the component is currently pressed with the mouse left-button.
	 * 
	 * @return true if the component is pressed.
	 */
	public boolean isPressed()
	{
		return pressed;
	}

	/**
	 * Check if the mouse is over the component.
	 * 
	 * @param mouseX - cursor X coordinate.
	 * @param mouseY - cursor Y coordinate.
	 * @return true if the component is hovered.
	 */
	public boolean isHovered(int mouseX, int mouseY)
	{
		return getHitbox().isPointInside(mouseX, mouseY);
	}

	/**
	 * Get the component at the given screen position.
	 * <p>
	 * This method will consider only WidgetInteractive or its sub-types.
	 * <p>
	 * This method will return the component rendered on top at that position
	 * and it's choose between this and its child components.
	 * 
	 * @param x - the x coordinate on the screen.
	 * @param y - the y coordinate on the screen.
	 * 
	 * @return the WidgetInteractive at this position, or null.
	 */
	public WidgetInteractive getTopComponentAt(Point point)
	{
		LinkedHashSet<Widget> children = getChildren();
		WidgetInteractive component = null;

		synchronized (children)
		{
			LinkedList<Widget> list = new LinkedList<>(children);
			Iterator<Widget> it = list.descendingIterator();

			while (it.hasNext())
			{
				Widget next = it.next();

				if (next instanceof WidgetInteractive)
				{
					WidgetInteractive componentAt = ((WidgetInteractive) next).getTopComponentAt(point);

					if (componentAt != null)
						component = componentAt;
				}
			}
		}

		return component == null && getHitbox().isPointInside(point) ? this : component;
	}
	
	/**
	 * Wrapper method of {@link #getTopComponentAt(Point)}.
	 * 
	 * @param x - the x coordinate on the screen.
	 * @param y - the y coordinate on the screen.
	 * @return the WidgetInteractive at the given position, or null.
	 */
	public WidgetInteractive getTopComponentAt(int x, int y)
	{
		return getTopComponentAt(new Point(x, y));
	}

	/**
	 * Get the complete list of components at the given screen position.
	 * <p>
	 * This method will consider only WidgetInteractive or its sub-typed.
	 * <p>
	 * The first element of the list is the top component on the screen.
	 *
	 * @param point - the position on screen.
	 * 
	 * @return an ordered List of found components, or an empty List.
	 */
	public List<WidgetInteractive> getComponentsAt(Point point)
	{
		List<WidgetInteractive> widgets = new ArrayList<>();
		Set<Widget> children = getChildren();

		synchronized (children)
		{
			Iterator<Widget> it = children.iterator();

			while (it.hasNext())
			{
				Widget next = it.next();

				if (next instanceof WidgetInteractive)
					widgets.addAll(((WidgetInteractive) next).getComponentsAt(point));
			}
		}

		if (getHitbox().isPointInside(point))
			widgets.add(this);

		return widgets;
	}

	/**
	 * Wrapper method of {@link #getComponentsAt(Point)}.
	 * 
	 * @param x - the x coordinate on the screen.
	 * @param y - the y coordinate on the screen.
	 * @return the List returned by {@link #getComponentsAt(Point)}.
	 */
	public List<WidgetInteractive> getComponentsAt(int x, int y)
	{
		return getComponentsAt(new Point(x, y));
	}
}
