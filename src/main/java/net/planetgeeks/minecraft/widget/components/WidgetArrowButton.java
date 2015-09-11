package net.planetgeeks.minecraft.widget.components;

import lombok.Getter;
import lombok.NonNull;
import net.planetgeeks.minecraft.widget.layout.Alignment;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.layout.Direction;
import net.planetgeeks.minecraft.widget.layout.WidgetGroupLayout;
import net.planetgeeks.minecraft.widget.render.NinePatch;
import net.planetgeeks.minecraft.widget.render.TextureRegion;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.Point;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public class WidgetArrowButton extends WidgetButton
{
	private static final byte ENABLED = 0, HOVERED = 1, PRESSED = 2;
	private static final TextureRegion TEXTURE = new TextureRegion(WidgetUtil.guiTexture("components.png"), 0, 0, 42, 16);

	@Getter
	private Direction direction;
	private ArrowImage arrowImage = new ArrowImage(TEXTURE.split(36, 1, 5, 3));

	public WidgetArrowButton()
	{
		this(Direction.UP);
	}

	public WidgetArrowButton(@NonNull Direction direction)
	{
		init(direction);
	}

	protected void init(Direction direction)
	{
		setDirection(direction);
		WidgetGroupLayout layout = new WidgetGroupLayout();
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup().addComponent(arrowImage, Alignment.CENTER).addComponent(getLabel(), Alignment.TRAILING)));
		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup().addComponent(getLabel(), Alignment.CENTER).addComponent(arrowImage, Alignment.CENTER)));
		setLayout(layout);
		layout.dispose();
		NinePatch[] ninePatches = new NinePatch[3];
		ninePatches[ENABLED] = new NinePatch.Dynamic(this, TEXTURE.split(0, 9, 13, 7), 1, 1, 1, 1);
		ninePatches[HOVERED] = new NinePatch.Dynamic(this, TEXTURE.split(13, 9, 13, 7), 1, 1, 1, 1);
		ninePatches[PRESSED] = new NinePatch.Dynamic(this, TEXTURE.split(26, 9, 13, 7), 1, 1, 1, 1);
		setNinePatches(ninePatches);
		setMinimumSize(new Dimension(2, 2));
	}

	@Override
	protected void init()
	{
	}

	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		getNinePatches()[isPressed() ? PRESSED : (isHovered(mouseX, mouseY) ? HOVERED : ENABLED)].draw(mouseX, mouseY, partialTicks, renderer);
	}

	class ArrowImage extends WidgetImageIcon
	{
		public ArrowImage(TextureRegion texture)
		{
			super(texture);
		}
		
		@Override
		public void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
		{
			renderer.push();
			Point rotationOrigin = new Point(getWidth() / 2, getHeight() / 2);
			switch (direction)
		 	{
				case UP:
					renderer.rotate(180, rotationOrigin);
					break;
				case RIGHT:
					renderer.rotate(-Math.PI / 2, rotationOrigin);
					break;
				case LEFT:
					renderer.rotate(Math.PI / 2, rotationOrigin);
					break;
				default:
					break;
			}
			super.drawComponent(mouseX, mouseY, partialTicks, renderer);
			renderer.pop();
		}
	}
}