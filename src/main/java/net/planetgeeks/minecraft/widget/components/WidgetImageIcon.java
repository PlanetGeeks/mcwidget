package net.planetgeeks.minecraft.widget.components;

import static net.planetgeeks.minecraft.widget.layout.Alignment.CENTER;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Alignment;
import net.planetgeeks.minecraft.widget.render.TextureRegion;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;

public class WidgetImageIcon extends WidgetInteractive
{
	@Getter
	private TextureRegion imageTexture;

	@Getter
	@Setter
	@NonNull
	private Alignment horizontalAlignment = CENTER;

	@Getter
	@Setter
	@NonNull
	private Alignment verticalAlignment = CENTER;

	public WidgetImageIcon()
	{
		this(null);
	}
	
	public WidgetImageIcon(TextureRegion texture)
	{
		setImageTexture(texture);
	}

	public void setImageTexture(TextureRegion texture)
	{
		this.imageTexture = texture;
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.drawTexture(imageTexture, getImageX(), getImageY());
	}

	protected int getImageX()
	{
		switch (getHorizontalAlignment())
		{
			case CENTER:
				return getWidth() / 2 - (imageTexture == null ? 0 : imageTexture.getWidth() / 2);
			case TRAILING:
				return getWidth() - (imageTexture == null ? 0 : imageTexture.getWidth());
			default:
				return 0;
		}
	}

	protected int getImageY()
	{
		switch (getHorizontalAlignment())
		{
			case CENTER:
				return getHeight() / 2 - (imageTexture == null ? 0 : imageTexture.getHeight() / 2);
			case TRAILING:
				return getHeight() - (imageTexture == null ? 0 : imageTexture.getHeight());
			default:
				return 0;
		}
	}
}
