package net.planetgeeks.minecraft.widget.components;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.planetgeeks.minecraft.widget.events.WidgetActionEvent;
import net.planetgeeks.minecraft.widget.events.WidgetDisableEvent;
import net.planetgeeks.minecraft.widget.events.WidgetDisableEvent.WidgetDisableListener;
import net.planetgeeks.minecraft.widget.events.WidgetEnableEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEnableEvent.WidgetEnableListener;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEnterEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEnterEvent.WidgetMouseEnterListener;
import net.planetgeeks.minecraft.widget.events.WidgetMouseExitEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseExitEvent.WidgetMouseExitListener;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent.WidgetMousePressListener;
import net.planetgeeks.minecraft.widget.events.WidgetUpdateEvent;
import net.planetgeeks.minecraft.widget.events.WidgetUpdateEvent.WidgetUpdateListener;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Alignment;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.layout.WidgetGroupLayout;
import net.planetgeeks.minecraft.widget.render.NinePatch;
import net.planetgeeks.minecraft.widget.render.TextureRegion;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.Color;
import net.planetgeeks.minecraft.widget.util.TextContent;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public class WidgetButton extends WidgetInteractive
		implements TextContent
{
	private static final byte DISABLED = 0, ENABLED = 1, HOVERED = 2;
	private static final byte PRESS_ACTION = 0;
	private static final TextureRegion TEXTURE = new TextureRegion(WidgetUtil.guiTexture("components.png"), 0, 16, 200, 60);

	@Getter
	private WidgetFixedLabel label;
	@Getter
	@Setter
	@NonNull
	private Color hoverForegroundColor = new Color(16777120);
	@Getter
	@Setter
	@NonNull
	private Color disabledForegroundColor = new Color(10526880);
	@Getter
	@Setter
	@NonNull
	private Color foregroundColor = Color.WHITE;
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	private NinePatch[] ninePatches;
	@Setter
	private boolean repeatActionEvent = false;

	public WidgetButton(int width, int height)
	{
		this(0, 0, width, height, "");
	}

	public WidgetButton(int width, int height, String text)
	{
		this(0, 0, width, height, text);
	}

	public WidgetButton(int xPosition, int yPosition, int width, int height, String text)
	{
		super(xPosition, yPosition, width, height);
		getEventBus().register(new WidgetButtonHandler(this));
		label = new WidgetFixedLabel(getWidth());
		label.setText(text);
		label.setHorizontalAlignment(Alignment.CENTER);
		init();
	}

	protected void init()
	{
		WidgetGroupLayout layout = new WidgetGroupLayout();
		layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(label));
		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup().addComponent(label, Alignment.CENTER)));
		setLayout(layout);
		layout.dispose();
		ninePatches = new NinePatch[3];
		ninePatches[DISABLED] = new NinePatch.Dynamic(this, TEXTURE.split(0, 0, 200, 20), 2, 2, 2, 3);
		ninePatches[ENABLED] = new NinePatch.Dynamic(this, TEXTURE.split(0, 20, 200, 20), 2, 2, 2, 3);
		ninePatches[HOVERED] = new NinePatch.Dynamic(this, TEXTURE.split(0, 40, 200, 20), 2, 2, 2, 3);
		setMinimumSize(new Dimension(4, 5));
	}
	
	public boolean isRepeatingActionEvent()
	{
		return repeatActionEvent;
	}

	public static void playPressSound(SoundHandler soundHandlerIn)
	{
		soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		NinePatch ninePatch = ninePatches[isEnabled() ? (isHovered(mouseX, mouseY) ? HOVERED : ENABLED) : DISABLED];

		ninePatch.draw(mouseX, mouseY, partialTicks, renderer);
	}

	@Override
	public void setText(String text)
	{
		label.setText(text);
	}

	@Override
	public String getText()
	{
		return label.getText();
	}

	protected class WidgetButtonHandler
			implements WidgetMousePressListener, WidgetMouseEnterListener,
			WidgetMouseExitListener, WidgetEnableListener,
			WidgetDisableListener, WidgetUpdateListener
	{
		private final WidgetButton button;
		private long latestActionEvent = 0L;
		private boolean firstRepeat = false;

		public WidgetButtonHandler(WidgetButton button)
		{
			this.button = button;
		}

		@Override
		public void onMousePressed(WidgetMousePressEvent event)
		{
			if (isEnabled() && event.isLeftButton() && (event.getComponent() == button || isParentOf(event.getComponent())))
			{
				firstRepeat = true;
				latestActionEvent = System.currentTimeMillis();
				playPressSound(WidgetUtil.getSoundHandler());
				fireAction();
			}
		}

		@Override
		public void onMouseEntered(WidgetMouseEnterEvent event)
		{
			if (isEnabled())
				setHoverColor();
		}

		@Override
		public void onMouseExited(WidgetMouseExitEvent event)
		{
			if (isEnabled())
				setDefaultColor();
		}

		@Override
		public void onEnabled(WidgetEnableEvent event)
		{
			if (isHovered(WidgetUtil.getMouseX(), WidgetUtil.getMouseY()))
				setHoverColor();
			else
				setDefaultColor();
		}

		@Override
		public void onDisabled(WidgetDisableEvent event)
		{
			setDisabledColor();
		}

		private void setHoverColor()
		{
			label.setForegroundColor(getHoverForegroundColor());
		}

		private void setDefaultColor()
		{
			label.setForegroundColor(getForegroundColor());
		}

		private void setDisabledColor()
		{
			label.setForegroundColor(getDisabledForegroundColor());
		}
		
		@Override
		public void onComponentUpdated(WidgetUpdateEvent event)
		{
			if(isPressed())
			{
				if(System.currentTimeMillis() - latestActionEvent > (firstRepeat ? 500L : 30L))
				{
					latestActionEvent = System.currentTimeMillis();
					firstRepeat = false;
					fireAction();
				}
			}
		}
		
		private void fireAction()
		{
			getEventBus().post(new WidgetActionEvent(button, PRESS_ACTION));
		}
	}
}
