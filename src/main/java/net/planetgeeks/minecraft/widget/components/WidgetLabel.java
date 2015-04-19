package net.planetgeeks.minecraft.widget.components;

import static net.planetgeeks.minecraft.widget.layout.Alignment.CENTER;
import static net.planetgeeks.minecraft.widget.layout.Alignment.LEADING;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Alignment;
import net.planetgeeks.minecraft.widget.layout.SideGap;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.TextContent;

public class WidgetLabel extends WidgetInteractive implements TextContent
{
	@Getter
	private String text;
	@Getter
	@Setter
	private int foregroundColor = 0xffffff;
	@Getter
	@Setter
	private int backgroundColor = 0x000000;
	@Getter
	@Setter
	private boolean drawBackground = false;
	@Getter
	private SideGap textGap = new SideGap(3, 2, 3, 2);
	@Getter
	private Alignment horizontalAlignment = LEADING;
	@Getter
	private Alignment verticalAlignment = CENTER;
	
	public WidgetLabel(@NonNull String text, int xPosition, int yPosition)
	{
		super(xPosition, yPosition, 0, 0);
		
		setText(text);
	}
	
	public WidgetLabel(String text)
	{
		this(text, 0 , 0);
	}

	@Override
	public void setText(String text)
	{
		this.text = text == null ? "null" : text;
		this.setWidth(getRenderer().getStringWidth(text) + getTextGap().getLeftGap() + getTextGap().getRightGap());
		this.setHeight(getRenderer().getStringHeight());
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.push();
		drawBackground(mouseX, mouseY, partialTicks, renderer);
		drawForeground(mouseX, mouseY, partialTicks, renderer);
		renderer.pop();
	}

	protected void drawBackground(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		if(isBackgroundDrawn())
		{
			renderer.setColor(backgroundColor);
			renderer.drawRect(0, 0, getWidth(), getHeight());
		}
	}

	public void setHorizontalAlignment(@NonNull Alignment alignment)
	{
		this.horizontalAlignment = alignment;
	}

	public void setVerticalAlignment(@NonNull Alignment alignment)
	{
		this.verticalAlignment = alignment;
	}

	protected void drawForeground(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.setColor(foregroundColor);
		renderer.drawString(text, getTextX(), getTextY());
	}
	
	public int getTextX()
	{
		switch(getHorizontalAlignment())
		{
			case LEADING:
				return getTextGap().getLeftGap();
			case CENTER:
				return getWidth() / 2 - getRenderer().getStringWidth(getRenderedText()) / 2;
			case TRAILING:
				return getWidth() - getTextGap().getRightGap() - getRenderer().getStringWidth(getRenderedText());
		}
		
		return getTextGap().getLeftGap();
	}

	public int getTextY()
	{
        switch(getVerticalAlignment())
        {
        	case LEADING:
        		return getTextGap().getTopGap();
        	case CENTER:
        		return getHeight() / 2 - getRenderer().getStringHeight() / 2;
        	case TRAILING:
        		return getHeight() - getTextGap().getBottomGap() - getRenderer().getStringHeight();
        }
        
        return getTextGap().getTopGap();
	}
	
	protected String getRenderedText()
	{
		return getText();
	}

	/**
	 * Returns true when a box is drawn behind the label.
	 * <p>
	 * Default is false. 
	 * 
	
	 * @return true if the box is drawn. */
	public boolean isBackgroundDrawn()
	{
		return drawBackground;
	}

	public void enableBackgroundDraw(boolean draw)
	{
		this.drawBackground = draw;
	}
}
