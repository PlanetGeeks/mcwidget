package net.planetgeeks.minecraft.widget.components;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.adapters.WidgetMouseAdapter;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.render.NinePatch;
import net.planetgeeks.minecraft.widget.render.TextureRegion;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public class WidgetCheckBox extends WidgetInteractive
{
	private static final byte BOX = 0, RELEASED = 1, CHECKED = 2, PRESSED = 3;
	private static final TextureRegion TEXTURE = new TextureRegion(WidgetUtil.guiTexture("components.png"), 0, 0, 36, 9);

	@Getter
	@Setter
	private boolean checked;
	@Getter
	private Box box;
	@Getter
	private WidgetLabel label;

	public WidgetCheckBox(@NonNull String text, int xPosition, int yPosition)
	{
		super(xPosition, yPosition, 0, 0);

		add(box = new Box());
		add(label = new WidgetLabel("", box.getWidth(), 0));
		setText(text);
	}

	public WidgetCheckBox(String text)
	{
		this(text, 0, 0);
	}

	public void setText(String text)
	{
		label.setText(text);
		setWidth(box.getWidth() + label.getWidth() + 3);
		setHeight(box.getHeight() > label.getHeight() ? box.getHeight() : label.getHeight());
	}

	public String getText()
	{
		return label.getText();
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
	}

	public class Box extends WidgetInteractive
	{
		private NinePatch[] ninePatches;

		public Box()
		{
			super(9, 9);

			this.addListener(new WidgetMouseAdapter()
			{
				@Override
				public void onMousePressed(WidgetMousePressEvent event)
				{
					if (event.isLeftButton())
						checked = !checked;
				}

				@Override
				public void onMouseReleased(WidgetMouseEvent event)
				{
					if (event.isLeftButton())
						WidgetButton.playPressSound(WidgetUtil.getSoundHandler());
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

		@Override
		protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
		{
            ninePatches[BOX].draw(mouseX, mouseY, partialTicks, renderer);
            
            renderer.push();
			renderer.translate(1, 1);

			if(isPressed())
				ninePatches[isChecked() ? RELEASED : PRESSED].draw(mouseX, mouseY, partialTicks, renderer);
			
			else if(isChecked())
				ninePatches[CHECKED].draw(mouseX, mouseY, partialTicks, renderer);
			
			renderer.pop();
		}
	}
}
