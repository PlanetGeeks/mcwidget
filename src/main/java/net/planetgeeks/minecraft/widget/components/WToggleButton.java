package net.planetgeeks.minecraft.widget.components;

import lombok.Getter;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;

public class WToggleButton extends WidgetInteractive
{
	@Getter @Setter
    private boolean selected = false;
	
	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		
	}
}
