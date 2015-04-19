package net.planetgeeks.minecraft.widget.components;

import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.render.NinePatch;
import net.planetgeeks.minecraft.widget.render.TextureRegion;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public class WidgetPanel extends WidgetInteractive
{
	public WidgetPanel(int xPosition, int yPosition, int width, int height)
	{
		super(xPosition, yPosition, width, height);
	}

	public WidgetPanel(int width, int height)
	{
		super(width, height);
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
	}

	public static class WidgetGenericPanel extends WidgetPanel
	{
		private static final TextureRegion GENERIC_TEXTURE = new TextureRegion(WidgetUtil.guiTexture("generic.png"), 0, 0, 127, 49);
		private NinePatch ninePatch = new NinePatch.Dynamic(this, GENERIC_TEXTURE, 5, 5, 5, 5);

		public WidgetGenericPanel(int xPosition, int yPosition, int width, int height)
		{
			super(xPosition, yPosition, width, height);

			setMinimumSize(new Dimension(ninePatch.getLeft() + ninePatch.getRight(), ninePatch.getTop() + ninePatch.getBottom()));
		}

		public WidgetGenericPanel(int width, int height)
		{
			this(0, 0, width, height);
		}

		@Override
		protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
		{
			ninePatch.draw(mouseX, mouseY, partialTicks, renderer);
		}
	}
}
