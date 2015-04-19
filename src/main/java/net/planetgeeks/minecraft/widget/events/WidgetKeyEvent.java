package net.planetgeeks.minecraft.widget.events;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.Widget;

import org.lwjgl.input.Keyboard;

@Getter
public class WidgetKeyEvent extends WidgetEvent
{
    private char typedChar;
    private int keyCode;

    public WidgetKeyEvent(Widget component, char typedChar, int keyCode)
    {
    	super(component);
    	this.typedChar = typedChar;
    	this.keyCode = keyCode;
    }
    
    public boolean isKey(int expectedKeyCode)
    {
    	return this.keyCode == expectedKeyCode;
    }
    
    public boolean isChar(char expectedChar)
    {
    	return this.typedChar == expectedChar;
    }

    public static boolean isCtrlKeyDown()
    {
    	return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    }

    public static boolean isShiftKeyDown()
    {
    	return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }
    
    public static class WidgetKeyTypeEvent extends WidgetKeyEvent
    {
		public WidgetKeyTypeEvent(Widget component, char typedChar, int keyCode)
		{
			super(component, typedChar, keyCode);
		}
    }
}
