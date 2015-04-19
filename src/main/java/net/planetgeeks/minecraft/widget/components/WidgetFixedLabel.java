package net.planetgeeks.minecraft.widget.components;

import lombok.Getter;
import lombok.NonNull;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.interactive.WidgetStatusAdapter;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;

public class WidgetFixedLabel extends WidgetLabel
{
	@Getter
	private int offset = 0;
	private String fixedText = "";
	
	public WidgetFixedLabel(int width)
	{
		this(0, 0, width, "");
	}

	public WidgetFixedLabel(int xPosition, int yPosition, int width)
	{
		this(xPosition, yPosition, width, "");
	}
	
	public WidgetFixedLabel(int xPosition, int yPosition, int width, String text)
	{
		super(text, xPosition, yPosition);
		addListener(new WidgetStatusAdapter()
		{
			@Override
			public void onResize(WidgetResizeEvent event)
			{
				if(getText() != null)
					updateFixedText();
			}
		});
		setWidth(width);
	}

	public void setText(@NonNull String text)
	{
		int fixedWidth = getWidth();
		super.setText(text);
		this.setWidth(fixedWidth);
	}

	private void updateFixedText()
	{
		if (offset > getText().length())
			setOffset(getOffset());
		fixedText = getFixedText(getText(), getRenderer());
	}

	public int setOffset(int offset)
	{
		int latest = this.offset;
		this.offset = offset < 0 ? 0 : (offset > getText().length() ? getText().length() : offset);
		updateFixedText();
		return Math.abs(latest - this.offset);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.setColor(getForegroundColor());
		renderer.drawString(fixedText, getTextX(), getTextY());
	}

	@Override
	public String getRenderedText()
	{
		return fixedText;
	}

	private String getFixedText(@NonNull String text, @NonNull WidgetRenderer renderer)
	{
		return getTruncatedText(getOffsetText(text, renderer), renderer);
	}

	private String getTruncatedText(@NonNull String text, @NonNull WidgetRenderer renderer)
	{
		if (!text.equals("") && renderer.getStringWidth(text) > getWidth() - getTextGap().getRightGap() * 2)
		{
			return getTruncatedText(text.substring(0, text.length() - 1), renderer);
		}

		return text;
	}
	
	private String getOffsetText(@NonNull String text, @NonNull WidgetRenderer renderer)
	{
		return text.substring(getOffset());
	}
}
