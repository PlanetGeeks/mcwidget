package net.planetgeeks.minecraft.widget.components;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.events.WidgetActionEvent;
import net.planetgeeks.minecraft.widget.events.WidgetActionEvent.WidgetActionListener;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent.WidgetMousePressListener;
import net.planetgeeks.minecraft.widget.events.WidgetMouseReleaseEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseReleaseEvent.WidgetMouseReleaseListener;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.layout.Gap;
import net.planetgeeks.minecraft.widget.layout.WidgetLayout;
import net.planetgeeks.minecraft.widget.render.NinePatch;
import net.planetgeeks.minecraft.widget.render.TextureRegion;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public class WidgetCheckBox extends WidgetInteractive
{
	private static final byte CHECK_ACTION = 0, UNCHECK_ACTION = 1;
	private static final byte BOX = 0, RELEASED = 1, CHECKED = 2, PRESSED = 3;
	private static final TextureRegion TEXTURE = new TextureRegion(WidgetUtil.guiTexture("components.png"), 0, 0, 36, 9);

	@Getter
	private boolean checked;
	@Getter
	private Box box;
	@Getter
	private WidgetLabel label;
	private Gap boxTextGap = Gap.fixedGap(3);

	public WidgetCheckBox()
	{
		this("");
	}

	public WidgetCheckBox(String text)
	{
		add(box = new Box(this));
		add(label = new WidgetLabel(text));
	    setText(text);	
	}

	public void setText(String text)
	{
		label.setText(text);
	}

	public String getText()
	{
		return label.getText();
	}
	
	public void setBoxTextGap(int gap)
	{
		boxTextGap = Gap.fixedGap(gap);
		layout();
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
		getEventBus().post(new WidgetActionEvent(this, this.checked ? CHECK_ACTION : UNCHECK_ACTION));
	}

	public class Box extends WidgetInteractive
	{
		private NinePatch[] ninePatches;
		private WidgetCheckBox checkBox;

		public Box(WidgetCheckBox checkBox)
		{
			this.setSize(new Dimension(9, 9));
			this.checkBox = checkBox;
			final WidgetBoxHandler boxHandler = new WidgetBoxHandler(this);
			getEventBus().register(boxHandler);
			this.checkBox.getEventBus().register(new WidgetActionListener()
			{
				@Override
				public void onAction(WidgetActionEvent event)
				{
					boxHandler.playSoundLater();
				}
			});

			class InnerNinePatch extends NinePatch.Dynamic
			{
				public InnerNinePatch(WidgetInteractive component, TextureRegion texture, int left, int top, int right, int bottom)
				{
					super(component, texture, left, top, right, bottom);
				}

				@Override
				public void onResize(WidgetResizeEvent event)
				{
					setSize(getComponent().getWidth() - 2, getComponent().getHeight() - 2);
				}
			}

			ninePatches = new NinePatch[4];
			ninePatches[BOX] = new NinePatch.Dynamic(this, TEXTURE.split(0, 0, 9, 9), 1, 1, 1, 1);
			ninePatches[CHECKED] = new InnerNinePatch(this, TEXTURE.split(10, 1, 7, 7), 1, 1, 1, 1);
			ninePatches[PRESSED] = new InnerNinePatch(this, TEXTURE.split(19, 1, 7, 7), 1, 1, 1, 1);
			ninePatches[RELEASED] = new InnerNinePatch(this, TEXTURE.split(28, 1, 7, 7), 1, 1, 1, 1);
			setMinimumSize(new Dimension(2, 2));
		}

		protected class WidgetBoxHandler implements WidgetMousePressListener, WidgetMouseReleaseListener
		{
			private final Box box;
			private boolean playSound = false;

			public WidgetBoxHandler(Box box)
			{
				this.box = box;
			}

			@Override
			public void onMousePressed(WidgetMousePressEvent event)
			{
				if (event.isLeftButton() && (event.getComponent() == box || isParentOf(event.getComponent())))
					setChecked(!isChecked());
			}

			@Override
			public void onMouseReleased(WidgetMouseReleaseEvent event)
			{
				if (event.isLeftButton() && playSound)
				{
					WidgetButton.playPressSound(WidgetUtil.getSoundHandler());
					playSound = false;
				}
			}

			public void playSoundLater()
			{
				playSound = true;
			}
		}

		@Override
		protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
		{
			ninePatches[BOX].draw(mouseX, mouseY, partialTicks, renderer);

			renderer.push();
			renderer.translate(1, 1);

			if (isPressed())
				ninePatches[isChecked() ? RELEASED : PRESSED].draw(mouseX, mouseY, partialTicks, renderer);

			else if (isChecked())
				ninePatches[CHECKED].draw(mouseX, mouseY, partialTicks, renderer);

			renderer.pop();
		}
	}
	
	protected class WidgetCheckBoxLayout extends WidgetLayout
	{
		@Override
		public void dispose()
		{
			
		}
	}
}
