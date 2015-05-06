package net.planetgeeks.minecraft.widget.components;

import static net.planetgeeks.minecraft.widget.layout.Direction.DOWN;
import static net.planetgeeks.minecraft.widget.layout.Direction.LEFT;
import static net.planetgeeks.minecraft.widget.layout.Direction.RIGHT;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.planetgeeks.minecraft.widget.layout.Alignment;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.layout.Direction;
import net.planetgeeks.minecraft.widget.layout.WidgetGroupLayout;
import net.planetgeeks.minecraft.widget.render.NinePatch;
import net.planetgeeks.minecraft.widget.render.TextureRegion;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public class WidgetArrowButton extends WidgetButton
{
	private static final byte ENABLED = 0, HOVERED = 1, PRESSED = 2;
	private static final TextureRegion TEXTURE = new TextureRegion(WidgetUtil.guiTexture("components.png"), 0, 0, 42, 16);

	@Getter
	private Direction direction;
	private WidgetImage arrowImage;

	public WidgetArrowButton(int width, int height, Direction direction)
	{
		this(0, 0, width, height, direction);
	}

	public WidgetArrowButton(int xPosition, int yPosition, int width, int height, @NonNull Direction direction)
	{
		super(xPosition, yPosition, width, height, "");
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
		if (direction == LEFT || direction == RIGHT)
		{
			//TODO CORRECT FOR NOW
			direction = Direction.UP;
		}

		this.direction = direction;

		TextureRegion texture = TEXTURE.split(36, direction == DOWN ? 1 : 5, 5, 3);

		if (this.arrowImage == null)
			this.arrowImage = new WidgetImage(texture);
		else
			this.arrowImage.setImageTexture(texture); // TODO ADD SUPPORT FOR
														// LEFT AND
		// RIGHT ARROWS.
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		getNinePatches()[isPressed() ? PRESSED : (isHovered(mouseX, mouseY) ? HOVERED : ENABLED)].draw(mouseX, mouseY, partialTicks, renderer);
	}
}