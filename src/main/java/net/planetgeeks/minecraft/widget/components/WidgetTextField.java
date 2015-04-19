package net.planetgeeks.minecraft.widget.components;

import java.util.regex.Pattern;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.util.ChatAllowedCharacters;
import net.planetgeeks.minecraft.widget.adapters.WidgetMouseAdapter;
import net.planetgeeks.minecraft.widget.events.WidgetEvent;
import net.planetgeeks.minecraft.widget.events.WidgetKeyEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.interactive.WidgetFocusable;
import net.planetgeeks.minecraft.widget.listeners.WidgetChangeListener;
import net.planetgeeks.minecraft.widget.listeners.WidgetKeyListener;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.Drawable;
import net.planetgeeks.minecraft.widget.util.TextContent;
import net.planetgeeks.minecraft.widget.util.Visible;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

import org.lwjgl.input.Keyboard;

public class WidgetTextField extends WidgetFocusable implements TextContent
{
	@Getter
	@Setter
	private int borderColor = 0xffffff;
	@Getter
	@Setter
	private int backgroundColor = 0x000000;
	@Getter
	@Setter
	private int foregroundColor = 0xffffff;
	@Getter
	private WidgetFixedLabel label;
	private Cursor cursor = new Cursor();
	@Getter
	private int maxStringWidth = Integer.MAX_VALUE;
	@Getter
	@Setter
	@NonNull
	private Pattern textPattern = Pattern.compile(".*", Pattern.DOTALL);

	public WidgetTextField(int width, int height)
	{
		this(0, 0, width, height);
	}

	public WidgetTextField(int width, int height, String text)
	{
		this(0, 0, width, height, text);
	}

	public WidgetTextField(int xPosition, int yPosition, int width, int height)
	{
		this(xPosition, yPosition, width, height, "");
	}

	public WidgetTextField(int xPosition, int yPosition, int width, int height, String text)
	{
		super(xPosition, yPosition, width, height);
		addListener(new ActionAdapter());
		add(label = new WidgetFixedLabel(getWidth()));
		setHeight(getHeight());
		setText(text);
		Keyboard.enableRepeatEvents(true);
	}

	class ActionAdapter extends WidgetMouseAdapter implements WidgetKeyListener, WidgetChangeListener
	{
		private long latestClick = 0L;

		@Override
		public void onMousePressed(WidgetMousePressEvent event)
		{
			if (event.isLeftButton() && event.getComponent() == label)
			{
				latestClick = System.currentTimeMillis();
				String fixedText = label.getRenderedText();

				for (int bound = fixedText.length(); bound > 0; bound--)
				{
					if (event.getMouseX() - getXOnScreen() > label.getTextGap().getLeftGap() + getRenderer().getStringWidth(fixedText.substring(0, bound)))
					{
						cursor.set(label.getOffset() + bound);
						cursor.resetSelect();
						return;
					}
				}

				cursor.set(label.getOffset());
				cursor.resetSelect();
			}
		}

		@Override
		public void onMousePressed(WidgetMouseEvent event) //REPLACE WITH onComponentUpdate and check if it's pressed.
		{
			if (event.isLeftButton() && event.getComponent() == label)
			{
				String fixedText = label.getRenderedText();
				boolean isClickOld = System.currentTimeMillis() - latestClick > 400;

				for (int bound = fixedText.length(); bound > 0; bound--)
				{
					if (event.getMouseX() - getXOnScreen() > label.getTextGap().getLeftGap() + getRenderer().getStringWidth(fixedText.substring(0, bound)))
					{
						if (!isClickOld || cursor.getIndex() < label.getOffset() + fixedText.length())
						{
							if (!cursor.isSelected())
							{
								cursor.setSelect(cursor.getIndex());
							}

							cursor.set(label.getOffset() + bound);
						}
						else
						{
							cursor.move(1);
						}
						return;
					}
				}

				if (!isClickOld || cursor.getIndex() < label.getOffset())
				{
					if (!cursor.isSelected())
					{
						cursor.setSelect(cursor.getIndex());
					}

					cursor.set(label.getOffset());
				}
				else
				{
					cursor.move(-1);
				}
			}
		}

		@Override
		public void onKeyTyped(WidgetKeyEvent event)
		{
			if (!isEnabled() || !isFocused())
				return;

			switch (event.getKeyCode())
			{
				case Keyboard.KEY_LEFT:
					if (WidgetKeyEvent.isCtrlKeyDown())
					{
						if (WidgetKeyEvent.isShiftKeyDown())
						{
							selectAllBeforeCursor();
						}
						else
						{
							cursor.set(0);
							cursor.resetSelect();
						}
					}
					else if (WidgetKeyEvent.isShiftKeyDown())
					{
						selectFromCursor(-1);
					}
					else
					{
						cursor.move(-1);
						cursor.resetSelect();
					}
					break;
				case Keyboard.KEY_RIGHT:
					if (WidgetKeyEvent.isCtrlKeyDown())
					{
						if (WidgetKeyEvent.isShiftKeyDown())
						{
							selectAllAfterCursor();
						}
						else
						{
							cursor.setMax();
							cursor.resetSelect();
						}
					}
					else if (WidgetKeyEvent.isShiftKeyDown())
					{
						selectFromCursor(1);
					}
					else
					{
						cursor.move(1);
						cursor.resetSelect();
					}
					break;
				case Keyboard.KEY_BACK:
					if (cursor.isSelected())
					{
						removeSelection();
					}
					else
					{
						removeFromCursor(WidgetKeyEvent.isCtrlKeyDown() ? -getTextBeforeCursor().length() : -1);
					}
					break;
				case Keyboard.KEY_END:
					if (WidgetKeyEvent.isShiftKeyDown())
					{
						selectAllAfterCursor();
					}
					else
					{
						cursor.setMax();
						cursor.resetSelect();
					}
					break;
				case Keyboard.KEY_HOME:
					if (WidgetKeyEvent.isShiftKeyDown())
					{
						selectAllBeforeCursor();
					}
					else
					{
						cursor.set(0);
						cursor.resetSelect();
					}
					break;
				case Keyboard.KEY_DELETE:
					if (cursor.isSelected())
					{
						removeSelection();
					}
					else
					{
						removeFromCursor(WidgetKeyEvent.isCtrlKeyDown() ? getTextAfterCursor().length() : 1);
					}
					break;
				default:
					if (WidgetKeyEvent.isCtrlKeyDown())
					{
						if (event.isKey(Keyboard.KEY_C) && cursor.isSelected())
						{
							WidgetUtil.setStringToClipboard(getSelectedText());
						}
						else if (event.isKey(Keyboard.KEY_V))
						{
							String message = WidgetUtil.getStringFromClipboard();

							if (message != null && !message.isEmpty())
								insert(message);
						}
						else if (event.isKey(Keyboard.KEY_X) && cursor.isSelected())
						{
							WidgetUtil.setStringToClipboard(getSelectedText());
							removeSelection();
						}
						break;
					}
					else
					{
						insert(String.valueOf(event.getTypedChar()));
					}
					break;
			}
		}

		@Override
		public void onEnabled(WidgetEvent event){}

		@Override
		public void onDisabled(WidgetEvent event){}

		@Override
		public void onResize(WidgetResizeEvent component)
		{
			if (label != null)
			{
				label.setWidth(getWidth());
				label.setY(getHeight() / 2 - label.getHeight() / 2);
			}
		}
	}

	public String correctInsert(String textToInsert)
	{
		if (textToInsert == null)
			return null;

		textToInsert = ChatAllowedCharacters.filterAllowedCharacters(textToInsert);

		int cursorIndex = cursor.isSelected() ? (cursor.getSelectIndex() < cursor.getIndex() ? cursor.getSelectIndex() : cursor.getIndex()) : cursor.getIndex();

		StringBuilder builder = new StringBuilder(getText());

		if (cursor.isSelected())
		{
			if (cursor.getSelectIndex() > cursor.getIndex())
				builder.delete(cursor.getIndex(), cursor.getSelectIndex());

			else
				builder.delete(cursor.getSelectIndex(), cursor.getIndex());
		}

		if (textToInsert.isEmpty() || builder.length() == getMaxStringWidth())
			return null;

		if (builder.length() + textToInsert.length() > getMaxStringWidth())
		{
			textToInsert = textToInsert.substring(0, getMaxStringWidth() - builder.length());
		}

		builder.insert(cursorIndex, textToInsert);

		return textPattern.matcher(builder.toString()).matches() ? builder.toString() : null;
	}

	/**
	 * Remove an amount of characters starting from cursor's position.
	 * 
	 * @param amount - a negative amount to remove characters before cursor, a
	 *            positive one to remove characters after cursor.
	 * 
	 * @return the amount of removed characters.
	 */
	protected int removeFromCursor(int amount)
	{
		String text = getText();

		if (text.isEmpty() || amount == 0)
			return 0;

		StringBuilder builder = new StringBuilder(getText());

		if (amount < 0)
		{
			amount = Math.abs(amount);

			if (cursor.getIndex() - amount < 0)
				amount = cursor.getIndex();

			builder.delete(cursor.getIndex() - amount, cursor.getIndex());
		}
		else
		{
			if (cursor.getIndex() + amount > cursor.getMax())
				amount = cursor.getMax() - cursor.getIndex();

			builder.delete(cursor.getIndex(), cursor.getIndex() + amount);
		}

		label.setText(builder.toString());
		cursor.move(-amount);
		if (cursor.getIndex() > 0 && cursor.getCursorX() <= label.getX() + label.getTextGap().getLeftGap())
			cursor.move(cursor.move(-8));
		return amount;
	}

	/**
	 * Remove selected text.
	 */
	protected void removeSelection()
	{
		if (cursor.isSelected())
		{
			int latest = cursor.getIndex();
			int latestSelect = cursor.getSelectIndex();
			removeFromCursor(latestSelect - latest);
			cursor.set(latest > latestSelect ? latestSelect : latest);
			cursor.resetSelect();
		}
	}

	public void selectFromCursor(int amount)
	{
		if (amount != 0)
		{
			if (!cursor.isSelected())
			{
				cursor.setSelect(cursor.getIndex());
			}

			cursor.move(amount);
		}
	}

	public void selectAllBeforeCursor()
	{
		if (!cursor.isSelected())
		{
			cursor.setSelect(cursor.getIndex());
		}

		cursor.set(0);
	}

	public void selectAllAfterCursor()
	{
		if (!cursor.isSelected())
		{
			cursor.setSelect(cursor.getIndex());
		}

		cursor.setMax();
	}

	public void selectAll()
	{
		cursor.setSelect(0);
		cursor.setMax();
	}

	private void adjustTextOffset()
	{
		if (cursor.getCursorX() + cursor.getCursorWidth() < label.getX() + label.getTextGap().getLeftGap())
		{
			label.setOffset(cursor.getIndex());
		}
		else
		{
			while (cursor.getCursorX() + cursor.getCursorWidth() > label.getX() + label.getWidth() - label.getTextGap().getRightGap())
			{
				if (label.setOffset(label.getOffset() + 1) == 0)
					break;
			}
		}
	}

	public String getTextBeforeCursor()
	{
		return getText().substring(0, cursor.getIndex());
	}

	public String getTextAfterCursor()
	{
		return getText().substring(cursor.getIndex(), cursor.getMax());
	}

	public String getSelectedText()
	{
		return !cursor.isSelected() ? "" : (cursor.getIndex() < cursor.getSelectIndex() ? getText().substring(cursor.getIndex(), cursor.getSelectIndex()) : getText().substring(cursor.getSelectIndex(), cursor.getIndex()));
	}

	/**
	 * Add text by inserting it after cursor or replacing current selection.
	 * 
	 * @param textToInsert - text to insert.
	 * 
	 * @return cursor effective movement.
	 */
	public int insert(String textToInsert)
	{
		String textCorrected = correctInsert(textToInsert);

		if (textCorrected != null)
		{
			if (cursor.isSelected())
				removeSelection();

			int movement = textCorrected.length() - getText().length();
			label.setText(textCorrected);
			cursor.move(movement);
			return movement;
		}
		else
			return 0;
	}

	public Cursor getCursor()
	{
		return cursor;
	}
	
	@Override
	public void setText(@NonNull String text)
	{
		selectAll();
		insert(text);
	}
	
	@Override
	public String getText()
	{
		return label.getText();
	}

	public void setMaxStringWidth(int maxWidth)
	{
		this.maxStringWidth = maxWidth < 0 ? 0 : maxWidth;
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.push();
		drawBackground(mouseX, mouseY, partialTicks, renderer);
		drawBorder(mouseX, mouseY, partialTicks, renderer);
		cursor.draw(mouseX, mouseY, partialTicks, renderer);
		renderer.pop();
	}

	protected void drawBackground(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.setColor(backgroundColor);
		renderer.drawRect(0, 0, getWidth(), getHeight());
	}

	protected void drawBorder(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.setColor(borderColor);
		renderer.drawHorizontalLine(0, 0, getWidth());
		renderer.drawHorizontalLine(0, getHeight() - 1, getWidth());
		renderer.drawVerticalLine(0, 0, getHeight());
		renderer.drawVerticalLine(getWidth() - 1, 0, getHeight());
	}

	public class Cursor implements Drawable, Visible
	{
		@Getter
		private int index = 0;
		@Getter
		private int selectIndex = -1;
		private long latestBlink = 0L;
		@Getter
		@Setter
		private long blinkDelay = 500;
		@Getter
		@Setter
		private int selectColor = 0x91A6C1;
		@Getter
		@Setter
		private int cursorColor = 0xffffff;

		/**
		 * Set cursor position.
		 * 
		 * @param nextIndex - the index to set.
		 * 
		 * @return the effective movement amount.
		 */
		public int set(int nextIndex)
		{
			int latest = this.index;
			this.index = nextIndex < 0 ? 0 : (nextIndex > getText().length() ? getText().length() : nextIndex);
			int movement = Math.abs(latest - this.index);
			if (movement > 0)
			{
				adjustTextOffset();
				resetBlink();
			}

			if (this.selectIndex == this.index)
				resetSelect();

			return movement;
		}

		public void setSelect(int selectIndex)
		{
			this.selectIndex = selectIndex > getText().length() ? getText().length() : selectIndex;
		}

		public boolean isSelected()
		{
			return selectIndex >= 0;
		}

		public void resetSelect()
		{
			setSelect(-1);
		}

		/**
		 * Move cursor of the given amount.
		 * 
		 * @param amount - a negative amount to move the cursor backward, a
		 *            positive one to move the cursor forward.
		 * 
		 * @return the effective movement amount.
		 */
		public int move(int amount)
		{
			return set(this.index + amount);
		}

		public void setMax()
		{
			set(getText().length());
		}

		public int getMax()
		{
			return getText().length();
		}

		public int getSelectX()
		{
			if (!isSelected())
				return 0;

			int offset = label.getOffset();

			if (offset > selectIndex)
			{
				return label.getTextGap().getLeftGap();
			}

			String fixedText = label.getRenderedText();

			if (offset + fixedText.length() < selectIndex)
				return label.getTextGap().getLeftGap() + getRenderer().getStringWidth(fixedText);
			else
				return label.getTextGap().getLeftGap() + getRenderer().getStringWidth(fixedText.substring(0, selectIndex - offset));
		}

		public int getSelectY()
		{
			return label.getY();
		}

		public int getSelectHeight()
		{
			return label.getHeight() + 1;
		}

		public int getCursorX()
		{
			int offset = label.getOffset();
			int x = getRenderer().getStringWidth(index < offset ? getText().substring(index, offset) : getText().substring(offset, index));
			return (index < offset ? -x : x) + label.getTextGap().getLeftGap();
		}

		public int getCursorY()
		{
			return getType() == CursorType.VERTICAL ? getHeight() / 2 - getCursorHeight() / 2 : label.getY() + label.getHeight();
		}

		public int getCursorWidth()
		{
			return getType() == CursorType.HORIZONTAL ? 4 : 1;
		}

		public int getCursorHeight()
		{
			return getType() == CursorType.HORIZONTAL ? 1 : label.getHeight() + 2;
		}

		@Override
		public void draw(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
		{
			if (isSelected())
				drawSelection(mouseX, mouseY, partialTicks, renderer);

			if (isVisible())
			{
				int x = getCursorX();
				int y = getCursorY();

				renderer.push();
				renderer.setColor(getCursorColor());

				if (getType() == CursorType.HORIZONTAL)
					renderer.drawHorizontalLine(x, y, getCursorWidth());
				else
					renderer.drawVerticalLine(x, y, getCursorHeight());

				renderer.pop();
			}
		}

		private void drawSelection(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
		{
			int cursorX = getCursorX();
			int selectX = getSelectX();
			int selectY = getSelectY();
			int selectHeight = getSelectHeight();

			renderer.push();
			renderer.setColor(getSelectColor());

			if (cursorX > selectX)
				renderer.drawRect(selectX, selectY, cursorX - selectX, selectHeight);
			else
				renderer.drawRect(cursorX, selectY, selectX - cursorX, selectHeight);

			renderer.pop();
		}

		public CursorType getType()
		{
			return index == getMax() ? CursorType.HORIZONTAL : CursorType.VERTICAL;
		}

		@Override
		public boolean isVisible()
		{
			if (!isFocused())
				return false;

			if (System.currentTimeMillis() - latestBlink > 1000)
			{
				latestBlink = System.currentTimeMillis();
				return true;
			}

			return System.currentTimeMillis() - latestBlink <= 500;
		}

		public void resetBlink()
		{
			this.latestBlink = System.currentTimeMillis();
		}

		@Override
		public void setVisible(boolean visible)
		{
			setFocused(visible);
		}
	}

	public enum CursorType
	{
		VERTICAL, HORIZONTAL;
	}
}
