package net.planetgeeks.minecraft.widget.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

/**
 * TODO: It works but it needs a better level of abstraction.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class GroupLayout extends Layout
{
	@Getter
	@Setter
	private Group horizontalGroup;
	@Getter
	@Setter
	private Group verticalGroup;

	public ParallelGroup createParallelGroup(@NonNull Alignment alignment)
	{
		return new ParallelGroup(alignment);
	}

	public SequentialGroup createSequentialGroup()
	{
		return new SequentialGroup();
	}

	public Set<Widget> getAddedComponents()
	{
		Set<Widget> components = new HashSet<>();

		if (horizontalGroup != null)
			components.addAll(horizontalGroup.getAddedComponents());

		if (verticalGroup != null)
			components.addAll(verticalGroup.getAddedComponents());

		return components;
	}

	public abstract class Group
	{
		@Getter(AccessLevel.PROTECTED)
		@Setter(AccessLevel.PROTECTED)
		private Group parent = null;
		@Getter(AccessLevel.PROTECTED)
		private LinkedHashSet<Object> elements = new LinkedHashSet<>();

		private boolean add(@NonNull Object element)
		{
			synchronized (elements)
			{
				boolean added = elements.add(element);
				if (added)
					invalidate();
				return added;
			}
		}

		public Group addComponent(@NonNull Widget component)
		{
			synchronized (elements)
			{
				if (elements.contains(component))
					throw new IllegalArgumentException("Component already added to the group!");
				else
				{
					List<Widget> components = getAddedComponents();

					for (Widget widget : components)
					{
						if (widget == component)
							throw new IllegalArgumentException("Component already added to another group!");
					}

					if(add(component))
						linkComponent(component);
				}
			}

			return this;
		}

		public Group addComponent(@NonNull Widget component, int min, int size, int max)
		{
			// TODO component.setMaximum(...) component.setMinimum(...)
			// automatically estimate if it's an horizontal group.
			return this;
		}

		public Group addGap(int size)
		{
			add(new Gap(size));

			return this;
		}

		public Group addGap(int min, int size, int max)
		{
			Gap gap = new Gap(size);
			gap.setMinimumSize(min);
			gap.setMaximumSize(max);

			add(gap);
			return this;
		}

		protected Group addGroup(@NonNull Group group)
		{
			synchronized (elements)
			{
				if (elements.contains(group))
					throw new IllegalArgumentException("Group already added to this group!");
				else if (group.getParent() != null)
					throw new IllegalArgumentException("Group already added to another group!");
				else
				{
					List<Widget> components = group.getAddedComponents();

					iteration: for (Object element : elements)
					{
						elementCheck: if (element instanceof Widget)
						{
							if (!components.contains(element))
								continue iteration;
						}
						else if (element instanceof Group)
						{
							List<Widget> elementComponents = ((Group) element).getAddedComponents();

							for (Widget elementComponent : elementComponents)
							{
								if (components.contains(elementComponent))
									break elementCheck;
							}

							continue iteration;
						}
						else if (element instanceof Gap)
						{
							continue iteration;
						}

						throw new IllegalArgumentException("Component already added to another group!");
					}

					if (add(group))
						group.setParent(this);
				}
			}

			return this;
		}

		public boolean hasComponent(@NonNull Widget widget)
		{
			return getAddedComponents().contains(widget);
		}

		public List<Widget> getAddedComponents()
		{
			List<Widget> components = new ArrayList<>();

			synchronized (elements)
			{
				for (Object element : elements)
				{
					if (element instanceof Widget)
						components.add((Widget) element);
					else if (element instanceof Group)
						components.addAll(((Group) element).getAddedComponents());
				}
			}

			return components;
		}

		/**
		 * Collocate elements on the given direction.
		 * 
		 * @param horizontal - true to consider width, false to consider height.
		 * @param offset - the starting position.
		 * @return the collocated size.
		 */
		protected abstract int collocate(boolean horizontal, int offset);

		public Group getRoot()
		{
			return parent == null ? this : parent.getRoot();
		}

		protected boolean isResizable(@NonNull Widget component, boolean horizontal)
		{
			return horizontal ? component.getMinimumSize().getWidth() != component.getMaximumSize().getWidth() : component.getMinimumSize().getHeight() != component.getMaximumSize().getHeight();
		}

		protected boolean isResizable(@NonNull Gap gap)
		{
			return gap.getMinimumSize() != gap.getMaximumSize();
		}

		/**
		 * Attempt to resize this group to the given size.
		 *
		 * @param horizontal - true to consider width, false to consider height.
		 * @param size - the preferred size.
		 * @return the effective size set.
		 */
		protected abstract int attemptResize(boolean horizontal, int size);

		/**
		 * Attempt to resize the given component to the given size.
		 * 
		 * @param component - the component to resize.
		 * @param horizontal - true to consider width, false to consider height.
		 * @param size - the preferred size.
		 * @return the effective size set.
		 */
		protected int attemptResize(@NonNull Widget component, boolean horizontal, int size)
		{
			if (horizontal)
				component.setWidth(size);
			else
				component.setHeight(size);

			return horizontal ? component.getWidth() : component.getHeight();
		}

		/**
		 * Attempt to resize the given gap to the given size.
		 * 
		 * @param gap - the gap to resize.
		 * @param size - the preferred size.
		 * @return the effective size set.
		 */
		protected int attemptResize(@NonNull Gap gap, int size)
		{
			gap.setSize(size);
			return gap.getSize();
		}

		/**
		 * Calculate group size.
		 * 
		 * @return the group size.
		 */
		protected abstract int getSize(boolean horizontal);
	}

	public class SequentialGroup extends Group
	{
		@Override
		protected SequentialGroup addGroup(Group group)
		{
			if (!(group instanceof ParallelGroup))
				throw new IllegalArgumentException("Sequential group accepts only Parallel groups!");

			return this.addGroup((ParallelGroup) group);
		}

		public SequentialGroup addGroup(ParallelGroup group)
		{
			return (SequentialGroup) super.addGroup(group);
		}

		@Override
		public SequentialGroup addGap(int size)
		{
			return (SequentialGroup) super.addGap(size);
		}

		@Override
		public SequentialGroup addGap(int min, int size, int max)
		{
			return (SequentialGroup) super.addGap(min, size, max);
		}

		@Override
		public SequentialGroup addComponent(Widget component)
		{
			return (SequentialGroup) super.addComponent(component);
		}

		@Override
		protected int collocate(boolean horizontal, int offset)
		{
			synchronized (getElements())
			{
				for (Object element : getElements())
				{
					if (element instanceof Gap)
					{
						Gap gap = (Gap) element;
						offset += gap.getSize();
					}
					else if (element instanceof Widget)
					{
						Widget widget = (Widget) element;
						if (horizontal)
							widget.setX(offset);
						else
							widget.setY(offset);
						offset += horizontal ? widget.getWidth() : widget.getHeight();
					}
					else if (element instanceof Group)
					{
						Group group = (Group) element;
						offset = group.collocate(horizontal, offset);
					}
				}
			}

			return offset;
		}

		@Override
		protected int attemptResize(boolean horizontal, int size)
		{
			int filledSize = 0;
			Set<Object> filledElements = new HashSet<>();

			synchronized (getElements())
			{
				int latestSize = 0;

				while (filledElements.size() < getElements().size() && filledSize < size)
				{
					int nextSize = (size - filledSize) / (getElements().size() - filledElements.size());

					if (nextSize < 1)
						nextSize = 1;

					for (Object element : getElements())
					{
						if (filledSize >= size)
							break;		

						if (filledElements.contains(element))
							continue;

						int nextFill = 0;

						if (element instanceof Gap)
						{
							Gap gap = (Gap) element;
							filledSize += (nextFill = attemptResize(gap, nextSize + latestSize)) - latestSize;
						}
						else if (element instanceof Widget)
						{
							Widget widget = (Widget) element;
							filledSize += (nextFill = attemptResize(widget, horizontal, nextSize + latestSize)) - latestSize;
						}
						else if (element instanceof Group)
						{
							Group group = (Group) element;
							filledSize += (nextFill = group.attemptResize(horizontal, nextSize + latestSize)) - latestSize;
						}

						if (nextFill != nextSize + latestSize)
							filledElements.add(element);
					}

					latestSize += nextSize;
				}
			}

			return filledSize > size ? size : filledSize;
		}

		/**
		 * The size of a sequential group is equals to the sum of the sizes of
		 * its elements.
		 * 
		 * @param horizontal - true to consider width, false to consider height.
		 * @return the calculated size.
		 */
		@Override
		protected int getSize(boolean horizontal)
		{
			int size = 0;

			synchronized (getElements())
			{
				for (Object element : getElements())
				{
					if (element instanceof Gap)
					{
						size += ((Gap) element).getSize();
					}
					else if (element instanceof Widget)
					{
						Widget widget = (Widget) element;
						size += horizontal ? widget.getWidth() : widget.getHeight();
					}
					else if (element instanceof Group)
					{
						size += ((Group) element).getSize(horizontal);
					}
				}
			}
			return size;
		}
	}

	public class ParallelGroup extends Group
	{
		@Getter
		private Alignment alignment;
		private Map<Object, Alignment> alignMap = new HashMap<>();

		public ParallelGroup(@NonNull Alignment alignment)
		{
			this.alignment = alignment;
		}

		@Override
		public ParallelGroup addComponent(Widget component)
		{
			return this.addComponent(component, Alignment.LEADING);
		}

		public ParallelGroup addComponent(Widget component, @NonNull Alignment alignment)
		{
			super.addComponent(component);

			synchronized (alignMap)
			{
				alignMap.put(component, alignment);
			}

			return this;
		}

		@Override
		protected ParallelGroup addGroup(Group group)
		{
			if (!(group instanceof SequentialGroup))
				throw new IllegalArgumentException("Parallel group accepts only Sequential groups!");

			return this.addGroup((SequentialGroup) group);
		}

		public ParallelGroup addGroup(SequentialGroup group)
		{
			return this.addGroup(group, Alignment.LEADING);
		}

		public ParallelGroup addGroup(SequentialGroup group, @NonNull Alignment alignment)
		{
			super.addGroup(group);

			synchronized (alignMap)
			{
				alignMap.put(group, alignment);
			}

			return this;
		}

		@Override
		public ParallelGroup addGap(int size)
		{
			return (ParallelGroup) super.addGap(size);
		}

		@Override
		public ParallelGroup addGap(int min, int size, int max)
		{
			return (ParallelGroup) super.addGap(min, size, max);
		}

		@Override
		protected int collocate(boolean horizontal, int offset)
		{
			int groupSize = getSize(horizontal);

			synchronized (getElements())
			{
				for (Object element : getElements())
				{
					Alignment alignment = null;

					synchronized (alignMap)
					{
						alignment = alignMap.get(element);
					}

					if (alignment == null)
						alignment = Alignment.LEADING;

					if (element instanceof Widget)
					{
						Widget widget = (Widget) element;
						switch (alignment)
						{
							case LEADING:
								if (horizontal)
									widget.setX(offset);
								else
									widget.setY(offset);
								break;
							case CENTER:
								if (horizontal)
									widget.setX(offset + groupSize / 2 - widget.getWidth() / 2);
								else
									widget.setY(offset + groupSize / 2 - widget.getHeight() / 2);
								break;
							case TRAILING:
								if (horizontal)
									widget.setX(offset + groupSize - widget.getWidth());
								else
									widget.setY(offset + groupSize - widget.getHeight());
								break;
						}
					}
					else if (element instanceof Group)
					{
						Group group = (Group) element;
						int size = 0;
						switch (alignment)
						{
							case LEADING:
								group.collocate(horizontal, offset);
								break;
							case CENTER:
								size = group.getSize(horizontal);
								group.collocate(horizontal, offset + groupSize / 2 - size / 2);
								break;
							case TRAILING:
								size = group.getSize(horizontal);
								group.collocate(horizontal, offset + groupSize - size);
								break;
						}
					}
				}
			}

			return offset + groupSize;
		}

		@Override
		protected int attemptResize(boolean horizontal, int size)
		{
			int filledSize = 0;

			synchronized (getElements())
			{
				for (Object element : getElements())
				{
					int nextFill = 0;

					if (element instanceof Gap)
					{
						Gap gap = (Gap) element;
						nextFill = attemptResize(gap, size);
					}
					else if (element instanceof Widget)
					{
						Widget widget = (Widget) element;
						nextFill = attemptResize(widget, horizontal, size);
					}
					else if (element instanceof Group)
					{
						Group group = (Group) element;
						nextFill = group.attemptResize(horizontal, size);
					}

					filledSize = nextFill > filledSize ? nextFill : filledSize;
				}
			}

			return filledSize;
		}

		/**
		 * The size of a parallel group is equals to the size of the bigger
		 * element into the group.
		 * 
		 * @param horizontal - true to consider width, false to consider height.
		 * @return the calculated size.
		 */
		@Override
		protected int getSize(boolean horizontal)
		{
			int size = 0;

			synchronized (getElements())
			{
				for (Object element : getElements())
				{
					int nextSize = 0;

					if (element instanceof Gap)
					{
						nextSize = ((Gap) element).getSize();
					}
					else if (element instanceof Widget)
					{
						Widget widget = (Widget) element;
						nextSize = horizontal ? widget.getWidth() : widget.getHeight();
					}
					else if (element instanceof Group)
					{
						nextSize = ((Group) element).getSize(horizontal);
					}

					size = nextSize > size ? nextSize : size;
				}
			}

			return size;
		}
	}

	@Override
	public void validate()
	{
		try
		{
			if (horizontalGroup == null)
				throw new InvalidGroupLayout("Horizontal group is null!");

			if (verticalGroup == null)
				throw new InvalidGroupLayout("Vertical group is null!");

			List<Widget> horizontalComponents = horizontalGroup.getAddedComponents();

			List<Widget> verticalComponents = verticalGroup.getAddedComponents();

			if (!verticalComponents.containsAll(horizontalComponents) || verticalComponents.size() != horizontalComponents.size())
				throw new InvalidGroupLayout(String.format("Missing components in %s layout!", verticalComponents.size() < horizontalComponents.size() ? "vertical" : "horizontal"));

			super.validate();
		}
		catch (RuntimeException e)
		{
			invalidate();
			throw e;
		}
	}

	private class InvalidGroupLayout extends IllegalStateException
	{
		private static final long serialVersionUID = 1L;

		public InvalidGroupLayout(String message)
		{
			super(String.format("Invalid group layout! %s", message));
		}
	}

	@Override
	public void onComponentResized(WidgetResizeEvent event)
	{
		if (!isValid())
			validate();

		horizontalGroup.attemptResize(true, getLinkedComponent().getWidth());
		horizontalGroup.collocate(true, 0);

		verticalGroup.attemptResize(false, getLinkedComponent().getHeight());
		verticalGroup.collocate(false, 0);
	}

	private void linkComponent(Widget component)
	{
		if (getLinkedComponent() != null)
			getLinkedComponent().add(component);
	}

	private void unlinkComponent(Widget component)
	{
		if (getLinkedComponent() != null)
			getLinkedComponent().remove(component);
	}

	@Override
	public Layout link(Widget component)
	{
		super.link(component);
		for (Widget componentToLink : getAddedComponents())
			linkComponent(componentToLink);
		return this;
	}

	@Override
	public Layout unlink()
	{
		for (Widget componentToUnlink : getAddedComponents())
			unlinkComponent(componentToUnlink);
		super.unlink();
		return this;
	}
}
