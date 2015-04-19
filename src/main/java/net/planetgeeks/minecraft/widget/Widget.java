package net.planetgeeks.minecraft.widget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.planetgeeks.minecraft.widget.events.WidgetEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetHideEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetMoveEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetShowEvent;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.layout.Layout;
import net.planetgeeks.minecraft.widget.listeners.WidgetComponentListener;
import net.planetgeeks.minecraft.widget.listeners.WidgetListener;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.Drawable;
import net.planetgeeks.minecraft.widget.util.Point;
import net.planetgeeks.minecraft.widget.util.Visible;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public abstract class Widget extends Gui implements Drawable, Visible
{
	public Minecraft mc;
	private Dimension size = new Dimension();
	private Point position = new Point();
	@Getter
	private Dimension minimumSize = new Dimension();
	@Getter
	private Dimension maximumSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	@Getter
	private LinkedHashSet<Widget> children = new LinkedHashSet<>();
	private boolean visible = true;
	private Widget parent;
	@Getter
	private WidgetRenderer renderer;
	@Getter
	@Setter
	private Layout layout = null;
	@Getter(AccessLevel.PROTECTED)
	private ListenerRegistry listenerRegistry = new ListenerRegistry();

	/**
	 * Creates a new instance of Widget.
	 * 
	 * @param xPosition - the x coordinate.
	 * @param yPosition - the y coordinate.
	 * @param width - the width of the component.
	 * @param height - the height of the component.
	 */
	public Widget(int xPosition, int yPosition, int width, int height)
	{
		setPosition(new Point(width, height));
		setSize(new Dimension(width, height));
		this.mc = Minecraft.getMinecraft();
		this.renderer = new WidgetRenderer(this);
		this.listenerRegistry.registerHandler(new ListenerHandler<WidgetComponentListener>(WidgetComponentListener.class)
		{
			@Override
			public void handle(WidgetComponentListener listener, WidgetEvent event)
			{
				if (event instanceof WidgetShowEvent)
					listener.onComponentShown((WidgetShowEvent) event);
				
				else if (event instanceof WidgetHideEvent)
					listener.onComponentHidden((WidgetHideEvent) event);
				
				else if (event instanceof WidgetMoveEvent)
					listener.onComponentMoved((WidgetMoveEvent) event);
				
				else if (event instanceof WidgetResizeEvent	)
					listener.onComponentResized((WidgetResizeEvent) event);
			}
		});
	}

	/**
	 * Creates a new instance of Widget with position at {x:0,y:0}
	 * <p>
	 * Wrapper constructor of {@link #Widget(int, int, int, int)}
	 * 
	 * @param width - the width of the component.
	 * @param height - the height of the component.
	 */
	public Widget(int width, int height)
	{
		this(0, 0, width, height);
	}

	/**
	 * Add the given component to the child list.
	 * <p>
	 * The given component mustn't already have a parent.
	 * <p>
	 * The added component will be drawn after the parent component.
	 * 
	 * @param childComponent - the component to add.
	 * @return true if the component has been added.
	 */
	public boolean add(@NonNull Widget childComponent)
	{
		if (childComponent.getParent() != null)
			throw new IllegalArgumentException("The given component already has a parent!");

		if (childComponent.isParentOf(this))
			throw new IllegalArgumentException("The given component is parent of this!");

		synchronized (children)
		{
			if (children.add(childComponent))
			{
				childComponent.parent = this;
				return true;
			}

			return false;
		}
	}

	/**
	 * Remove the given component from the child list.
	 * 
	 * @param childComponent - the component to remove.
	 * @return true if the component has been removed.
	 */
	public boolean remove(@NonNull Widget childComponent)
	{
		synchronized (children)
		{
			if (children.remove(childComponent))
			{
				childComponent.parent = null;
				return true;
			}

			return false;
		}
	}

	public boolean addListener(WidgetListener listener)
	{
		if (listener == null)
		{
			WidgetUtil.warn("Attempting to register a null listener. Registration aborted!");
			return false;
		}

		if (!listenerRegistry.addListener(listener))
		{
			WidgetUtil.warn("Unsupported listener! It has not been registered!");
			return false;
		}

		return true;
	}

	public boolean removeListener(WidgetComponentListener listener)
	{
		return listener == null ? false : listenerRegistry.removeListener(listener);
	}

	public boolean hasListener(WidgetComponentListener listener)
	{
		return listener == null ? false : listenerRegistry.hasListener(listener);
	}

	/**
	 * Set component position.
	 * 
	 * @param position - the position to set, relative to parent position, or to
	 *            the screen origin.
	 */
	public void setPosition(@NonNull Point position)
	{
		Point latest = this.position.clone();

		this.position.moveTo(position);

		listenerRegistry.invokeListeners(WidgetComponentListener.class, new WidgetMoveEvent(this, latest));
	}

	public Point getPosition()
	{
		return this.position;
	}

	/**
	 * Set x coordinate of the component position.
	 * 
	 * @param the x coordinate to set, relative to parent position, or to the
	 *            screen origin.
	 * @see {@link #getX()} for further information.
	 */
	public void setX(int xPosition)
	{
		setPosition(new Point(xPosition, position.getY()));
	}

	/**
	 * Get x coordinate of the component position.
	 * <p>
	 * The returned value is relative to parent position. If the component
	 * doesn't have a parent the returned value is relative to the screen
	 * origin.
	 * 
	 * @return the x coordinate relative to parent position, or to the screen
	 *         origin.
	 */
	public int getX()
	{
		return getPosition().getX();
	}

	/**
	 * Set y coordinate of the component position.
	 * 
	 * @param the y coordinate to set, relative to parent position, or to the
	 *            screen origin.
	 * @see {@link #getY()} for further information.
	 */
	public void setY(int yPosition)
	{
		setPosition(new Point(getPosition().getX(), yPosition));
	}

	/**
	 * Get y coordinate of the component position.
	 * <p>
	 * The returned value is relative to parent position. If the component
	 * doesn't have a parent the returned value is relative to the screen
	 * origin.
	 * 
	 * @return the y coordinate relative to parent position, or to the screen
	 *         origin.
	 */
	public int getY()
	{
		return getPosition().getY();
	}

	/**
	 * Set maximum size and resize the component if it's needed.
	 * 
	 * @param maximumSize - to set.
	 */
	public void setMaximumSize(@NonNull Dimension maximumSize)
	{
		this.maximumSize = maximumSize;
		setSize(getSize());
	}

	/**
	 * Set minimum size and resize the component if it's needed.
	 * 
	 * @param minimumSize - to set.
	 */
	public void setMinimumSize(@NonNull Dimension minimumSize)
	{
		this.minimumSize = minimumSize;
		setSize(getSize());
	}

	/**
	 * Set components's size.
	 * 
	 * @param size - the size to set.
	 */
	public void setSize(@NonNull Dimension size)
	{
		Dimension latest = getSize().clone();

		getSize().setWidth(size.getWidth());

		if (getSize().compareWidth(minimumSize) < 0)
			getSize().setWidth(getMinimumSize().getWidth());

		if (getSize().compareWidth(maximumSize) > 0)
			getSize().setWidth(getMaximumSize().getWidth());

		getSize().setHeight(size.getHeight());

		if (getSize().compareHeight(minimumSize) < 0)
			getSize().setHeight(getMinimumSize().getHeight());

		if (getSize().compareHeight(maximumSize) > 0)
			getSize().setHeight(getMaximumSize().getHeight());

		listenerRegistry.invokeListeners(WidgetComponentListener.class, new WidgetResizeEvent(this, latest));
	}

	/**
	 * Get component size.
	 * 
	 * @return a dimension object that represents component size.
	 */
	public Dimension getSize()
	{
		return size;
	}

	/**
	 * Set component's width.
	 * <p>
	 * Wrapper method of {@link #setSize(Dimension)}.
	 * 
	 * @param width - the width to set.
	 */
	public void setWidth(int width)
	{
		setSize(new Dimension(width, getSize().getHeight()));
	}

	/**
	 * Set component's height.
	 * <p>
	 * Wrapper method of {@link #setSize(Dimension)}.
	 * 
	 * @param height - the height to set.
	 */
	public void setHeight(int height)
	{
		setSize(new Dimension(getSize().getWidth(), height));
	}

	public int getWidth()
	{
		return getSize().getWidth();
	}

	public int getHeight()
	{
		return getSize().getHeight();
	}

	/**
	 * Called by the parent component to update the widget.
	 */
	public void onUpdate()
	{
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		if (!isVisible())
			return;

		drawComponent(mouseX, mouseY, partialTicks, renderer);

		synchronized (children)
		{
			Iterator<Widget> it = children.iterator();
			while (it.hasNext())
			{
				Widget widget = it.next();
				widget.draw(mouseX, mouseY, partialTicks, widget.getRenderer());
			}
		}
	}

	/**
	 * Draw the component. Child components will be drawn after this.
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @param partialTicks
	 */
	protected abstract void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer);

	public void mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
	}

	public void mouseReleased(int mouseX, int mouseY, int mouseButton)
	{
	}

	public void keyTyped(char typedChar, int keyCode)
	{
	}

	/**
	 * Check if this component is a parent of the given one.
	 * 
	 * @param widget - the component expected to be child of this one.
	 * @return true if this component is a direct parent of the given component
	 *         or a parent of the given component's parent.
	 */
	public boolean isParentOf(Widget widget)
	{
		if (widget == null || widget.getParent() == null)
			return false;

		Widget parent = widget.getParent();

		if (parent == this)
			return true;

		return isParentOf(parent);
	}

	/**
	 * Return true if the component is visible.
	 * <p>
	 * An non-visible component will not be draw and will not react to
	 * mouse/keyboard events.
	 * 
	 * @return the visibility set .
	 */
	@Override
	public boolean isVisible()
	{
		return parent != null ? parent.isVisible() : visible;
	}

	/**
	 * Set the component visibility.
	 * 
	 * @see {@link #isVisible()} for further informations about visibility.
	 * @param visible - the component's visibility to set.
	 */
	@Override
	public void setVisible(boolean visible)
	{
		this.visible = visible;

		listenerRegistry.invokeListeners(WidgetComponentListener.class, visible ? new WidgetShowEvent(this) : new WidgetHideEvent(this));
	}

	/**
	 * Get the parent component.
	 * 
	 * @return the parent of this component, or null if there's no parent.
	 */
	public Widget getParent()
	{
		return this.parent;
	}

	public void setZLevel(float zLevel)
	{
		this.zLevel = zLevel;
	}

	public float getZLevel()
	{
		return this.zLevel;
	}

	public int getXOnScreen()
	{
		return this.parent != null ? parent.getXOnScreen() + getX() : getY();
	}

	public int getYOnScreen()
	{
		return this.parent != null ? parent.getYOnScreen() + getX() : getY();
	}

	@Override
	public void drawGradientRect(int x1, int y1, int x2, int y2, int startColor, int endColor)
	{
		super.drawGradientRect(x1, y1, x2, y2, startColor, endColor);
	}

	@Override
	public void drawHorizontalLine(int x1, int x2, int y, int color)
	{
		super.drawHorizontalLine(x1, x2, y, color);
	}

	@Override
	public void drawVerticalLine(int y1, int y2, int x, int color)
	{
		super.drawVerticalLine(x, y1, y2, color);
	}

	protected class ListenerRegistry
	{
		private HashMap<Class<? extends WidgetListener>, ListenerHandler<?>> handlers = new HashMap<>();

		public <T extends WidgetListener> void registerHandler(@NonNull ListenerHandler<T> handler)
		{
			synchronized (handlers)
			{
				handlers.put(handler.getType(), handler);
			}
		}

		public boolean addListener(@NonNull WidgetListener listener)
		{
			boolean added = false;

			synchronized (handlers)
			{
				for (Map.Entry<Class<? extends WidgetListener>, ListenerHandler<?>> entry : handlers.entrySet())
				{
					if (entry.getKey().isInstance(listener))
						added = entry.getValue().addListener(listener) ? true : added;
				}
			}

			return true;
		}

		public boolean removeListener(@NonNull Object listener)
		{
			boolean removed = false;

			synchronized (handlers)
			{
				for (Map.Entry<Class<? extends WidgetListener>, ListenerHandler<?>> entry : handlers.entrySet())
				{
					if (entry.getKey().isInstance(listener))
						removed = entry.getValue().removeListener(listener) ? true : removed;
				}
			}

			return removed;
		}

		public boolean hasListener(@NonNull Object listener)
		{
			synchronized (handlers)
			{
				for (Map.Entry<Class<? extends WidgetListener>, ListenerHandler<?>> entry : handlers.entrySet())
				{
					if (entry.getKey().isInstance(listener) && entry.getValue().hasListener(listener))
						return true;
				}
			}

			return false;
		}

		public void invokeListeners(@NonNull Class<? extends WidgetListener> type, WidgetEvent event)
		{
			synchronized (handlers)
			{
				ListenerHandler<?> handler = handlers.get(type);

				if (handler != null)
					handler.invokeListeners(event);
			}
		}
	}

	protected static abstract class ListenerHandler<T extends WidgetListener>
	{
		@Getter
		private Set<T> listeners = new LinkedHashSet<>();
		@Getter
		private Class<T> type;

		public ListenerHandler(@NonNull Class<T> type)
		{
			this.type = type;
		}

		public void invokeListeners(WidgetEvent event)
		{
			synchronized (listeners)
			{
				for (T listener : listeners)
					handle(listener, event);
			}
		}

		public abstract void handle(T listener, WidgetEvent event);
 
		public boolean addListener(@NonNull WidgetListener listener)
		{
			if (!type.isInstance(listener))
				return false;

			synchronized (listeners)
			{
				return listeners.add(type.cast(listener));
			}
		}

		public boolean removeListener(@NonNull Object listener)
		{
			synchronized (listeners)
			{
				return listeners.remove(listener);
			}
		}

		public boolean hasListener(@NonNull Object listener)
		{
			synchronized (listeners)
			{
				return listeners.contains(listener);
			}
		}
	}
}
