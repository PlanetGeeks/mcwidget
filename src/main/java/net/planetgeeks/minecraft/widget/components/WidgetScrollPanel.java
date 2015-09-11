package net.planetgeeks.minecraft.widget.components;

import static net.planetgeeks.minecraft.widget.components.WidgetScrollPanel.WidgetScrollBarPolicy.SCROLLBAR_AS_NEEDED;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.events.WidgetActionEvent;
import net.planetgeeks.minecraft.widget.events.WidgetActionEvent.WidgetActionListener;
import net.planetgeeks.minecraft.widget.layout.Orientation;
import net.planetgeeks.minecraft.widget.layout.WidgetGroupLayout;

public class WidgetScrollPanel extends WidgetPanel
{
	@Getter
	private WidgetViewport viewport;
	@Getter
	@Setter
	@NonNull
	private WidgetScrollBarPolicy verticalScrollBarPolicy = SCROLLBAR_AS_NEEDED;
	@Getter
	private WidgetScrollBar verticalScrollBar;
	@Getter
	@Setter
	@NonNull
	private WidgetScrollBarPolicy horizontalScrollBarPolicy = SCROLLBAR_AS_NEEDED;
	@Getter
	private WidgetScrollBar horizontalScrollBar;

	public WidgetScrollPanel()
	{
		setViewport(new WidgetViewport());
		verticalScrollBar = createVerticalScrollBar();
		horizontalScrollBar = createHorizontalScrollBar();
		setLayout(new WidgetScrollPanelLayout());
	}

	protected WidgetScrollBar createHorizontalScrollBar()
	{
		return new WidgetScrollPanelBar(Orientation.HORIZONTAL);
	}

	protected WidgetScrollBar createVerticalScrollBar()
	{
		return new WidgetScrollPanelBar(Orientation.VERTICAL);
	}

	public void setViewport(@NonNull WidgetViewport viewport)
	{
		this.viewport = viewport;
		this.viewport.getEventBus().register(new WidgetActionListener()
		{
			@Override
			public void onAction(WidgetActionEvent event)
			{
				if (event.getActionID() == WidgetViewport.VIEWPORT_CHANGE_ACTION)
				{
					horizontalScrollBar.setTotalValue(viewport.getViewSize().getWidth());
					horizontalScrollBar.setReadableValue(viewport.getWidth());
					horizontalScrollBar.setValue(viewport.getViewPosition().getX());

					verticalScrollBar.setTotalValue(viewport.getViewSize().getHeight());
					verticalScrollBar.setReadableValue(viewport.getHeight());
					verticalScrollBar.setValue(viewport.getViewPosition().getY());
				}
			}
		});
		layout();
	}

	public static enum WidgetScrollBarPolicy
	{
		SCROLLBAR_AS_NEEDED, SCROLLBAR_ALWAYS, SCROLLBAR_NEVER;
	}

	protected class WidgetScrollPanelBar extends WidgetScrollBar
	{
		public WidgetScrollPanelBar(@NonNull final Orientation orientation)
		{
			super(orientation);

			if (orientation == Orientation.HORIZONTAL)
			{
				setMaximumHeight(10);
				setMinimumHeight(10);
			}
			else
			{
				setMaximumWidth(10);
				setMinimumWidth(10);
			}

			getEventBus().register(new WidgetActionListener()
			{
				@Override
				public void onAction(WidgetActionEvent event)
				{
					if (event.getActionID() == WidgetScrollBar.VALUE_CHANGE_ACTION)
					{
						float scrolledAmount = getScrolledAmount();

						if (orientation == Orientation.VERTICAL)
						{
							float viewY = -(scrolledAmount * (viewport.getViewSize().getHeight() - viewport.getHeight()) / 1.0F);
							viewport.getViewPosition().setY((int) viewY);
						}
						else
						{
							float viewX = -(scrolledAmount * (viewport.getViewSize().getWidth() - viewport.getWidth()) / 1.0F);
							viewport.getViewPosition().setX((int) viewX);
						}
					}
				}
			});
		}
	}

	protected class WidgetScrollPanelLayout extends WidgetGroupLayout
	{
		public WidgetScrollPanelLayout()
		{
			setHorizontalGroup(createHorizontalGroup());
			setVerticalGroup(createVerticalGroup());
		}

		protected Group<?> createHorizontalGroup()
		{
			return createSequentialGroup().addGroup(createParallelGroup().addComponent(viewport).addComponent(horizontalScrollBar)).addComponent(verticalScrollBar);
		}

		protected Group<?> createVerticalGroup()
		{
			return createParallelGroup().addGroup(createSequentialGroup().addComponent(viewport).addComponent(horizontalScrollBar)).addGroup(createSequentialGroup().addComponent(verticalScrollBar).addFixedGap(10));
		}
	}
}
