package net.planetgeeks.minecraft.widget;

import java.util.Iterator;
import java.util.LinkedHashSet;

import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.planetgeeks.minecraft.widget.events.WidgetHideEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMoveEvent;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetShowEvent;
import net.planetgeeks.minecraft.widget.events.WidgetUpdateEvent;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.layout.WidgetLayout;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.render.shape.Rectangle;
import net.planetgeeks.minecraft.widget.util.Drawable;
import net.planetgeeks.minecraft.widget.util.Point;
import net.planetgeeks.minecraft.widget.util.Visible;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * Represents an object that is renderered onto the screen.
 * <p>
 * The component can be moved, resized, shown and hidden. It can also have
 * children components that will be renderered on the component itself.
 * 
 * <p>
 * <b>Complete list of supported events.</b>
 * <ul>
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetUpdateEvent WidgetUpdateEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetShowEvent WidgetShowEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetHideEvent WidgetHideEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetResizeEvent WidgetResizeEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetMoveEvent WidgetMoveEvent}
 * </ul>
 * 
 * @author Vincenzo Fortunato (Flood)
 */
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
	private WidgetLayout layout = null;
	private final EventBus eventBus = createEventBus();

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
		setPosition(new Point(xPosition, yPosition));
		setSize(new Dimension(width, height));
		this.mc = Minecraft.getMinecraft();
		this.renderer = new WidgetRenderer(this);
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
	 * Set layout.
	 * 
	 * @param layout - the layout to set.
	 */
	public void setLayout(WidgetLayout layout)
	{
		if(this.layout != null)
			this.layout.unlink();
		
		this.layout = layout.link(this);
	}
	
	protected EventBus createEventBus()
	{
		return new EventBus(new SubscriberExceptionHandler()
		{
			@Override
			public void handleException(Throwable exception, SubscriberExceptionContext context)
			{
				exception.printStackTrace();
			}
		});
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
			if(childComponent.getParent() != this)
				throw new IllegalArgumentException("The given component already has a parent!");
			else
				return true;

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
		this.eventBus.post(new WidgetMoveEvent(this, latest));
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
		setMinimumAndMaximumSize(this.minimumSize, maximumSize);
	}

	/**
	 * Set minimum size and resize the component if it's needed.
	 * 
	 * @param minimumSize - to set.
	 */
	public void setMinimumSize(@NonNull Dimension minimumSize)
	{
		setMinimumAndMaximumSize(minimumSize, this.maximumSize);
	}

	/**
	 * Wrapper method of {@link #setMinimumSize(Dimension)}.
	 * 
	 * @param minimumWidth - to set.
	 */
	public void setMinimumWidth(int minimumWidth)
	{
		setMinimumSize(new Dimension(minimumWidth, this.minimumSize.getHeight()));
	}
	
	/**
	 * Wrapper method of {@link #setMinimumSize(Dimension)}.
	 * 
	 * @param minimumHeight - to set.
	 */
	public void setMinimumHeight(int minimumHeight)
	{
		setMinimumSize(new Dimension(this.minimumSize.getWidth(), minimumHeight));
	}
	
	/**
	 * Wrapper method of {@link #setMaximumSize(Dimension)}.
	 * 
	 * @param maximumWidth - to set.
	 */
	public void setMaximumWidth(int maximumWidth)
	{
		setMaximumSize(new Dimension(maximumWidth, this.maximumSize.getHeight()));
	}
	
	/**
	 * Wrapper method of {@link #setMaximumSize(Dimension)}.
	 * 
	 * @param maximumHeight - to set.
	 */
	public void setMaximumHeight(int maximumHeight)
	{
		setMaximumSize(new Dimension(this.maximumSize.getWidth(), maximumHeight));
	}
	
	/**
	 * Set minimum and maximum sizes and resize the component if it's needed.
	 * 
	 * @param minimumSize - to set.
	 * @param maximumSize - to set.
	 */
	public void setMinimumAndMaximumSize(Dimension minimumSize, Dimension maximumSize)
	{
		this.minimumSize = minimumSize;
		this.maximumSize = maximumSize;
		setSize(getSize());
	}
	
	/**
	 * Set components's size.
	 * 
	 * @param size - the size to set.
	 * @return effective size set.
	 */
	public Dimension setSize(@NonNull Dimension size)
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

		this.eventBus.post(new WidgetResizeEvent(this, latest));
		return getSize();
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
	 * @return effective width set.
	 */
	public int setWidth(int width)
	{
		return setSize(new Dimension(width, getSize().getHeight())).getWidth();
	}

	/**
	 * Set component's height.
	 * <p>
	 * Wrapper method of {@link #setSize(Dimension)}.
	 * 
	 * @param height - the height to set.
	 * @return effective height set.
	 */
	public int setHeight(int height)
	{
		return setSize(new Dimension(getSize().getWidth(), height)).getHeight();
	}

	/**
	 * Get component's width.
	 * 
	 * @return the component width.
	 */
	public int getWidth()
	{
		return getSize().getWidth();
	}

	/**
	 * Get component's height.
	 * 
	 * @return the component height.
	 */
	public int getHeight()
	{
		return getSize().getHeight();
	}

	/**
	 * Called by the parent component to update the widget.
	 */
	protected void onUpdate()
	{
		synchronized (children)
		{
			for (Widget component : children)
				component.onUpdate();
		}

		getEventBus().post(new WidgetUpdateEvent(this));
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
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
		synchronized (children)
		{
			for (Widget component : children)
				component.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	public void mouseReleased(int mouseX, int mouseY, int mouseButton)
	{
		synchronized (children)
		{
			for (Widget component : children)
				component.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}

	public void keyTyped(char typedChar, int keyCode)
	{
		synchronized (children)
		{
			for (Widget component : children)
				component.keyTyped(typedChar, keyCode);
		}
	}

	/**
	 * Called when an LWJGL mouse input event occurs (e.g. mouse move, wheel, button press ...)
	 */
	protected void handleMouseInput()
	{
		synchronized(children)
		{
			for(Widget component : children)
				component.handleMouseInput();
		}
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
	 * @return the visibility set.
	 */
	@Override
	public boolean isVisible()
	{
		return (parent != null ? parent.isVisible() : true) && visible;
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
		this.eventBus.post(visible ? new WidgetShowEvent(this) : new WidgetHideEvent(this));
	}
	
	/**
	 * The component visible area.
	 * <p>
	 * The position of the area is relative to the screen origin.
	 * 
	 * @return the visible area, or null if the component area is not visible.
	 */
	public Rectangle getVisibleArea()
	{
		if(getWidth() == 0 || getHeight() == 0)
			return null;
		
		Rectangle componentArea = new Rectangle(getXOnScreen(), getYOnScreen(), getWidth(), getHeight());
		
		return parent != null ? componentArea.intersect(parent.getVisibleArea()) : componentArea;
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

	/**
	 * The Widget personal EventBus.
	 * <p>
	 * This is used by the Widget to call events located into
	 * net.planetgeeks.minecraft.widget.events package. Can be also used to
	 * register event handler. The event handler will receive events triggered
	 * by this Widget only.
	 * 
	 * @return the Widget personal EventBus.
	 */
	public EventBus getEventBus()
	{
		return eventBus;
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
		return (this.parent != null ? parent.getXOnScreen() : 0) + getX();
	}

	public int getYOnScreen()
	{
		return (this.parent != null ? parent.getYOnScreen() : 0) + getY();
	}

	@Override
	public void drawGradientRect(int x1, int y1, int x2, int y2, int startColor, int endColor)
	{
		super.drawGradientRect(x1, y1, x2, y2, startColor, endColor);
	}
}