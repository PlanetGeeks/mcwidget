package net.planetgeeks.minecraft.widget.components;

import lombok.Getter;
import lombok.NonNull;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.render.TextureRegion;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;

public class WidgetImage extends WidgetInteractive
{
	@Getter
	private TextureRegion imageTexture;

	public WidgetImage(@NonNull TextureRegion texture)
	{
		this(0, 0, texture);
	}

	public WidgetImage(int xPosition, int yPosition, TextureRegion texture)
	{
		super(xPosition, yPosition, texture.getRegionWidth(), texture.getRegionHeight());
		setImageTexture(texture);
	}
	
	public void setImageTexture(@NonNull TextureRegion texture)
	{
		this.imageTexture = texture;
		this.setSize(texture.getSize().clone());
		this.setMinimumAndMaximumSize(texture.getSize().clone(), texture.getSize().clone());
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.drawTexture(imageTexture, 0, 0);
	}
}
