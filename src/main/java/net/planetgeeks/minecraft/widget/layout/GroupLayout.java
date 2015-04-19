package net.planetgeeks.minecraft.widget.layout;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.events.WidgetEvent;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.listeners.WidgetChangeListener;

public class GroupLayout implements WidgetChangeListener
{
	public ParallelGroup createParallelGroup(@NonNull Alignment alignment)
	{
		return new ParallelGroup(alignment);
	}
	
	public SequentialGroup createSequentialGroup(@NonNull Alignment alignment)
	{
		return new SequentialGroup(alignment);
	}
	
	public abstract class Group
	{
		@Getter
		private Alignment alignment;
		private Set<Object> elements = new LinkedHashSet<>();

		public Group(@NonNull Alignment alignment)
		{
			this.alignment = alignment;
		}
		
		private boolean add(Object element)
		{
			synchronized (elements)
			{
				return elements.add(element);
			}
		}

		public void addComponent(Widget component)
		{
			if (!add(component))
				throw new IllegalArgumentException("Component already added to the layout group!");
		}

		public void addGap(int size)
		{
			add(new Gap(size));
		}

		public void addGroup(Group group)
		{
			if(!add(group))
				throw new IllegalArgumentException("Group already added to the layout group!");
		}	
	}
	
	public class SequentialGroup extends Group
	{

		public SequentialGroup(Alignment alignment)
		{
			super(alignment);
		}
		
	}
	
	public class ParallelGroup extends Group
	{

		public ParallelGroup(Alignment alignment)
		{
			super(alignment);
		}
		
	}

	@Override
	public void onResize(WidgetResizeEvent component)
	{
		
	}

	@Override
	public void onEnabled(WidgetEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisabled(WidgetEvent event)
	{
		// TODO Auto-generated method stub
		
	}
}
