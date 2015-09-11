package net.planetgeeks.minecraft.widget.components;

import static net.planetgeeks.minecraft.widget.layout.Alignment.CENTER;
import static net.planetgeeks.minecraft.widget.layout.Alignment.LEADING;
import static net.planetgeeks.minecraft.widget.layout.Alignment.TRAILING;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Alignment;
import net.planetgeeks.minecraft.widget.layout.Gap;
import net.planetgeeks.minecraft.widget.layout.Insets;
import net.planetgeeks.minecraft.widget.layout.WidgetLayout;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.Color;
import net.planetgeeks.minecraft.widget.util.TextContent;

/**
 * An area that can contain a text string or an image icon, or both.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetLabel extends WidgetInteractive implements TextContent
{
	@Getter
	private String text;

	@Getter
	@Setter
	@NonNull
	private Color foregroundColor = Color.WHITE;

	@Getter
	@Setter
	@NonNull
	private Color backgroundColor = Color.BLACK;

	@Getter
	@Setter
	private boolean opaque = false;

	@Getter
	private Insets insets = new Insets(3, 2, 3, 2);

	@Getter
	@NonNull
	private Alignment horizontalAlignment = LEADING;

	@Getter
	@NonNull
	private Alignment verticalAlignment = CENTER;

	@Getter
	@NonNull
	private Alignment textHorizontalPosition = TRAILING;

	@Getter
	@NonNull
	private Alignment textVerticalPosition = CENTER;

	private Gap textIconGap = Gap.fixedGap(2);

	@Getter
	private WidgetImageIcon icon = null;

	public WidgetLabel()
	{
		this("");
	}

	public WidgetLabel(String text)
	{
		setText(text);
		setLayout(new WidgetLabelLayout());
	}

	public WidgetLabel(WidgetImageIcon icon)
	{
		this("", icon);
	}

	public WidgetLabel(String text, WidgetImageIcon icon)
	{
		this(text);
		setIcon(icon);
	}

	@Override
	public void setText(String text)
	{
		this.text = text == null ? "null" : text;
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.push();
		if (isOpaque())
			drawBackground(mouseX, mouseY, partialTicks, renderer);
		drawForeground(mouseX, mouseY, partialTicks, renderer);
		renderer.pop();
	}

	protected void drawBackground(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.setColor(backgroundColor);
		renderer.drawFilledRect(0, 0, getWidth(), getHeight());
	}

	protected void drawForeground(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		WidgetLabelLayout layout = getLabelLayout();

		if (layout != null)
		{
			renderer.setColor(foregroundColor);
			renderer.drawString(text, layout.getTextX(), layout.getTextY());
		}
	}

	public void setIcon(@NonNull WidgetImageIcon icon)
	{
		if (this.icon != null)
			remove(this.icon);

		this.icon = icon;

		if (this.icon != null)
			add(this.icon);

		layout();
	}

	public void setTextHorizontalPosition(@NonNull Alignment alignment)
	{
		this.textHorizontalPosition = alignment;
		layout();
	}

	public void setTextVerticalPosition(@NonNull Alignment alignment)
	{
		this.textVerticalPosition = alignment;
		layout();
	}

	public void setHorizontalAlignment(@NonNull Alignment alignment)
	{
		this.horizontalAlignment = alignment;
		layout();
	}

	public void setVerticalAlignment(@NonNull Alignment alignment)
	{
		this.verticalAlignment = alignment;
		layout();
	}

	public void setIconTextGap(int gap)
	{
		this.textIconGap = Gap.fixedGap(gap);
		layout();
	}

	public int getIconTextGap()
	{
		return textIconGap.getSize();
	}

	@Override
	public void setLayout(WidgetLayout layout)
	{
		if (layout == null || !(layout instanceof WidgetLabelLayout))
			throw new IllegalArgumentException("Invalid layout! It must be an instance of WidgetLabelLayout.");

		super.setLayout(layout);
	}

	public WidgetLabelLayout getLabelLayout()
	{
		return getLayout() == null ? null : (WidgetLabelLayout) getLayout();
	}

	public class WidgetLabelLayout extends WidgetLayout
	{
		@Override
		public void dispose()
		{
			if (icon != null)
			{
				icon.setX(getIconX());
				icon.setY(getIconY());
			}
		}

		public int getContentWidth()
		{
			int iconWidth = getIconWidth();
			int textWidth = getTextWidth();

			switch (getTextHorizontalPosition())
			{
				case LEADING:
				case TRAILING:
					return iconWidth + textWidth + getIconTextGap();
				default:
					return iconWidth < textWidth ? textWidth : iconWidth;
			}
		}

		public int getContentHeight()
		{
			int iconHeight = getIconHeight();
			int textHeight = getTextHeight();

			switch (getTextHorizontalPosition())
			{
				case CENTER:
					switch(getTextVerticalPosition())
					{
						case LEADING:
						case TRAILING:
							return iconHeight + textHeight + getIconTextGap();
						default:
					}	
				default:
					return iconHeight < textHeight ? textHeight : iconHeight;
			}
		}

		public int getContentX()
		{
			switch (getHorizontalAlignment())
			{
				case LEADING:
					return getInsets().getLeft();
				case TRAILING:
					return getWidth() - getInsets().getRight() - getContentWidth();
				default:
					return getWidth() / 2 - getContentWidth() / 2;
			}
		}

		public int getContentY()
		{
			switch (getVerticalAlignment())
			{
				case LEADING:
					return getInsets().getTop();
				case TRAILING:
					return getHeight() - getInsets().getBottom() - getContentHeight();
				default:
					return getHeight() / 2 - getContentHeight() / 2;
			}
		}

		public int getTextX()
		{
			switch (getTextHorizontalPosition())
			{
				case LEADING:
					return getContentX();
				case TRAILING:
					return getContentX() + getContentWidth() - getTextWidth();
				default:
					return getContentX() + getContentWidth() / 2 - getTextWidth() / 2;
			}
		}

		public int getTextY()
		{
			switch (getTextVerticalPosition())
			{
				case LEADING:
					return getContentY();
				case TRAILING:
					return getContentY() + getContentHeight() - getTextHeight();
				default:
					return getContentY() + getContentHeight() / 2 - getTextHeight() / 2;
			}
		}

		public int getIconX()
		{
			switch (getTextHorizontalPosition())
			{
				case LEADING:
					return getContentX() + getContentWidth() - getIconWidth();
				case TRAILING:
					return getContentX();
				default:
					return getContentX() + getContentWidth() / 2 - getIconWidth() / 2;
			}
		}

		public int getIconY()
		{
			switch (getTextVerticalPosition())
			{
				case LEADING:
					return getContentY() + getContentHeight() - getIconHeight();
				case TRAILING:
					return getContentY();
				default:
					return getContentY() + getContentHeight() / 2 - getIconHeight() / 2;
			}
		}

		public int getTextWidth()
		{
			return getRenderer().getStringWidth(getText());
		}

		public int getTextHeight()
		{
			return getRenderer().getStringHeight();
		}

		public int getIconWidth()
		{
			return icon == null ? 0 : icon.getWidth();
		}

		public int getIconHeight()
		{
			return icon == null ? 0 : icon.getHeight();
		}
	}

	class WidgetLabelInsets extends Insets
	{
		public Insets setLeft(int gap)
		{
			super.setLeft(gap);
			layout();
			return this;
		}

		public Insets setRight(int gap)
		{
			super.setRight(gap);
			layout();
			return this;
		}

		public Insets setTop(int gap)
		{
			super.setTop(gap);
			layout();
			return this;
		}

		public Insets setBottom(int gap)
		{
			super.setBottom(gap);
			layout();
			return this;
		}
	}
}
