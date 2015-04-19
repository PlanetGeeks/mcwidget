package net.planetgeeks.minecraft.widget.interactive;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.adapters.WidgetMouseAdapter;
import net.planetgeeks.minecraft.widget.events.WidgetEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetDisableEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetEnableEvent;
import net.planetgeeks.minecraft.widget.events.WidgetKeyEvent;
import net.planetgeeks.minecraft.widget.events.WidgetKeyEvent.WidgetKeyTypeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseClickOutsideEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseDragEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseEnterEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseExitEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseMoveEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseReleaseEvent;
import net.planetgeeks.minecraft.widget.listeners.WidgetChangeListener;
import net.planetgeeks.minecraft.widget.listeners.WidgetKeyListener;
import net.planetgeeks.minecraft.widget.listeners.WidgetMouseListener;
import net.planetgeeks.minecraft.widget.listeners.WidgetMouseMotionListener;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public abstract class WidgetInteractive extends Widget
{
	@Getter
	@Setter
	private WidgetHitbox hitbox = new WidgetHitbox.Dynamic(this);
	private boolean enabled = true;
	private boolean pressed = false;
	private boolean entered = false;

	public WidgetInteractive(int xPosition, int yPosition, int width, int height)
	{
		super(xPosition, yPosition, width, height);

		this.getListenerRegistry().registerHandler(new ListenerHandler<WidgetChangeListener>(WidgetChangeListener.class)
		{
			@Override
			public void handle(WidgetChangeListener listener, WidgetEvent event)
			{
				if (event instanceof WidgetEnableEvent)
					listener.onComponentEnabled((WidgetEnableEvent) event);

				else if (event instanceof WidgetDisableEvent)
					listener.onComponentDisabled((WidgetDisableEvent) event);
			}
		});

		this.getListenerRegistry().registerHandler(new ListenerHandler<WidgetMouseListener>(WidgetMouseListener.class)
		{
			@Override
			public void handle(WidgetMouseListener listener, WidgetEvent event)
			{
				if (event instanceof WidgetMousePressEvent)
					listener.onMousePressed((WidgetMousePressEvent) event);

				else if (event instanceof WidgetMouseReleaseEvent)
					listener.onMouseReleased((WidgetMouseReleaseEvent) event);

				else if (event instanceof WidgetMouseClickOutsideEvent)
					listener.onMouseClickedOutside((WidgetMouseClickOutsideEvent) event);

				else if (event instanceof WidgetMouseEnterEvent)
					listener.onMouseEntered((WidgetMouseEnterEvent) event);

				else if (event instanceof WidgetMouseExitEvent)
					listener.onMouseExited((WidgetMouseExitEvent) event);
			}
		});

		this.getListenerRegistry().registerHandler(new ListenerHandler<WidgetKeyListener>(WidgetKeyListener.class)
		{
			@Override
			public void handle(WidgetKeyListener listener, WidgetEvent event)
			{
				if (event instanceof WidgetKeyTypeEvent)
					listener.onKeyTyped((WidgetKeyTypeEvent) event);
			}
		});

		this.getListenerRegistry().registerHandler(new ListenerHandler<WidgetMouseMotionListener>(WidgetMouseMotionListener.class)
		{
			@Override
			public void handle(WidgetMouseMotionListener listener, WidgetEvent event)
			{

				if (event instanceof WidgetMouseMoveEvent)
					listener.onMouseMoved((WidgetMouseMoveEvent) event);

				else if (event instanceof WidgetMouseDragEvent)
					listener.onMouseDragged((WidgetMouseDragEvent) event);
			}
		});

		this.addListener(new WidgetMouseAdapter()
		{
			@Override
			public void onMousePressed(WidgetMousePressEvent event)
			{
				if (event.isLeftButton())
					pressed = true;
			}
			
			@Override
			public void onMouseReleased(WidgetMouseReleaseEvent event)
			{
				if (event.isLeftButton())
					pressed = false;
			}
		});
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
	 * 
	 * @param enabled - the component status to set.
	 * @see {@link #isEnabled()} for further informations about this.
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;

		this.getListenerRegistry().invokeListeners(WidgetChangeListener.class, enabled ? new WidgetEnableEvent(this) : new WidgetDisableEvent(this));
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		long clickTime = System.currentTimeMillis();

		if (!isEnabled())
			return;

		List<WidgetInteractive> componentsAt = getComponentsAt(mouseX, mouseY);

		if (!componentsAt.isEmpty())
		{
			WidgetMouseClickEvent event = new WidgetMouseClickEvent(componentsAt.get(0), mouseX, mouseY, mouseButton);

			for (WidgetInteractive component : componentsAt)
			{
				synchronized (component.pressEvents)
				{
					component.pressEvents.add(new WidgetMousePressEvent(event.getComponent(), mouseX, mouseY, mouseButton, clickTime));
				}

				component.getListenerRegistry().invokeListeners(WidgetMouseListener.class, event);
			}

			checkClick(componentsAt, new WidgetMouseClickOutsideEvent(event.getComponent(), mouseX, mouseY, mouseButton));

			return;
		}

		this.getListenerRegistry().invokeListeners(WidgetMouseListener.class, new WidgetMouseClickOutsideEvent(null, mouseX, mouseY, mouseButton));
	}

	private void checkClick(List<WidgetInteractive> clickedComponents, WidgetMouseClickOutsideEvent event)
	{
		if (!clickedComponents.contains(this))
			this.getListenerRegistry().invokeListeners(WidgetMouseListener.class, event);

		Set<Widget> children = getChildren();

		synchronized (children)
		{
			for (Widget component : children)
			{
				if (component instanceof WidgetInteractive)
					((WidgetInteractive) component).checkClick(clickedComponents, event);
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton)
	{
		if (!isEnabled())
			return;

		WidgetMouseReleaseEvent event = new WidgetMouseReleaseEvent(this, mouseX, mouseY, mouseButton);

		Set<WidgetMouseEvent> pressEvents = registry.getPressEvents();

		synchronized (pressEvents)
		{
			Iterator<WidgetMouseEvent> it = pressEvents.iterator();

			if (it.hasNext())
			{
				WidgetMouseEvent next = it.next();

				if (next.isButton(mouseButton))
				{
					it.remove();

					performAction(MOUSE_RELEASED, event);
				}
			}
		}

		Set<Widget> children = getChildren();

		synchronized (children)
		{
			for (Widget component : children)
				component.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode)
	{
		if (!isEnabled())
			return;

		WidgetKeyEvent event = new WidgetKeyEvent(this, typedChar, keyCode);

		Set<Widget> children = getChildren();

		synchronized (children)
		{
			for (Widget component : children)
				component.keyTyped(typedChar, keyCode);
		}

		performAction(KEY_TYPED, event);
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
	public WidgetInteractive getComponentAt(int x, int y)
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
					WidgetInteractive componentAt = ((WidgetInteractive) next).getComponentAt(x, y);

					if (componentAt != null)
						component = componentAt;
				}
			}
		}

		return component == null && getHitbox().isPointInside(x, y) ? this : component;
	}

	/**
	 * Get the complete list of components at the given screen position.
	 * <p>
	 * This method will consider only WidgetInteractive or its sub-typed.
	 * <p>
	 * The first element of the list is the top component on the screen.
	 *
	 * @param x - the x coordinate on the screen.
	 * @param y - the y coordinate on the screen.
	 * 
	 * @return an ordered Set of found components, or an empty Set.
	 */
	public List<WidgetInteractive> getComponentsAt(int x, int y)
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
					widgets.addAll(((WidgetInteractive) next).getComponentsAt(x, y));
			}
		}

		if (getHitbox().isPointInside(x, y))
			widgets.add(this);

		return widgets;
	}

	@Override
	public void onUpdate()
	{
		int mouseX = WidgetUtil.getMouseX();
		int mouseY = WidgetUtil.getMouseY();

		Set<WidgetMouseEvent> pressEvents = registry.getPressEvents();

		synchronized (pressEvents)
		{
			Iterator<WidgetMouseEvent> it = pressEvents.iterator();

			while (it.hasNext())
			{
				WidgetMouseEvent event = it.next();
				event.setMouseX(mouseX);
				event.setMouseY(mouseY);
				performAction(MOUSE_PRESSED, event);
			}
		}

		boolean inside = getHitbox().isPointInside(mouseX, mouseY);

		if (!inside && entered)
		{
			entered = false;
			performAction(MOUSE_EXITED, new WidgetMouseEvent(this, mouseX, mouseY, -1));
		}
		else if (inside && !entered)
		{
			entered = true;
			performAction(MOUSE_ENTERED, new WidgetMouseEvent(this, mouseX, mouseY, -1));
		}

		Set<Widget> children = getChildren();

		synchronized (children)
		{
			for (Widget component : children)
				component.onUpdate();
		}
	}

	/**
	 * Check if the component is currently pressed with the mouse left-button.
	 * 
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
	 * 
	 * @param mouseX int
	 * @param mouseY int
	 * @return true if the component is hovered.
	 */
	public boolean isHovered(int mouseX, int mouseY)
	{
		return getHitbox().isPointInside(mouseX, mouseY);
	}
}
