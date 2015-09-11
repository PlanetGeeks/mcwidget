package net.planetgeeks.minecraft.widget.components;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.render.NinePatch;
import net.planetgeeks.minecraft.widget.render.TextureRegion;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.Color;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public class WidgetPanel extends WidgetInteractive
{
	@Getter @Setter @NonNull
	private Color background = Color.GRAY;

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.setColor(getBackground());
		renderer.drawFilledRect(0, 0, getWidth(), getHeight());
	}

	public static class WidgetGenericPanel extends WidgetPanel
	{
		private static final TextureRegion GENERIC_TEXTURE = new TextureRegion(WidgetUtil.guiTexture("generic.png"), 0, 0, 127, 49);
		private NinePatch ninePatch = new NinePatch.Dynamic(this, GENERIC_TEXTURE, 5, 5, 5, 5);

		public WidgetGenericPanel()
		{
			setMinimumSize(new Dimension(ninePatch.getLeft() + ninePatch.getRight(), ninePatch.getTop() + ninePatch.getBottom()));
		}
		
		@Override
		protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
		{
			ninePatch.draw(mouseX, mouseY, partialTicks, renderer);
		}
	}
}
