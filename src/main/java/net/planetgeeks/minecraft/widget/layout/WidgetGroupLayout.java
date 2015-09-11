 package net.planetgeeks.minecraft.widget.layout;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.collect.Sets;

public class WidgetGroupLayout extends WidgetLayout
{
	@Getter
	@Setter
	private Group<?> horizontalGroup, verticalGroup;

	@Override
	public void dispose()
	{
		if (!isValid())
			validate();

		horizontalGroup.attemptResize(Orientation.HORIZONTAL, getLinkedComponent().getWidth());
		horizontalGroup.collocate(Orientation.HORIZONTAL, 0);

		verticalGroup.attemptResize(Orientation.VERTICAL, getLinkedComponent().getHeight());
		verticalGroup.collocate(Orientation.VERTICAL, 0);
	}
	
	/**
	 * Creates a new <code>ParallelGroup</code>.
	 * 
	 * @return a new instance of <code>ParallelGroup</code>
	 */
	public ParallelGroup createParallelGroup()
	{
		return new ParallelGroup();
	}

	/**
	 * Creates a new <code>SequentialGroup</code>.
	 * 
	 * @return a new instance of <code>SequentialGroup</code>
	 */
	public SequentialGroup createSequentialGroup()
	{
		return new SequentialGroup();
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

			Set<Widget> horizontalComponents = horizontalGroup.getAddedComponents(), verticalComponents = verticalGroup.getAddedComponents();

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
	public WidgetLayout link(Widget component)
	{
		super.link(component);
		for (Widget componentToLink : getAddedComponents())
			linkComponent(componentToLink);
		return this;
	}

	@Override
	public WidgetLayout unlink()
	{
		for (Widget componentToUnlink : getAddedComponents())
			unlinkComponent(componentToUnlink);
		super.unlink();
		return this;
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

	public abstract class Group<S extends Group<S>>
	{
		@Getter(AccessLevel.PROTECTED)
		@Setter(AccessLevel.PROTECTED)
		private Group<?> parent = null;
		@Getter(AccessLevel.PROTECTED)
		private Set<Element<?>> elements = new LinkedHashSet<>();

		protected S add(@NonNull Element<?> element)
		{
			synchronized (elements)
			{
				if (elements.add(element))
					invalidate();
			}

			return self();
		}

		/**
		 * Adds a component to the Group.
		 * 
		 * @param component - the component to add.
		 * @return the group (Method chaining).
		 */
		public S addComponent(Widget component)
		{
			return addComponent(new WidgetElement(component));
		}

		/**
		 * Adds a component to the Group.
		 * <p>
		 * The given minimum and maximum sizes will be used by the layout resize behavior.
		 * 
		 * @param component - the component to add.
		 * @param minimumSize - the minimum component size.
		 * @param maximumSize - the maximum component size.
		 * @return the group (Method chaining).
		 */
		public S addComponent(Widget component, int minimumSize, int maximumSize)
		{
			return addComponent(new WidgetElement(component, minimumSize, maximumSize));
		}

		/**
		 * Adds a fixed component to the Group. It will not be resized by the layout resize behavior.
		 * 
		 * @param component - the component to add.
		 * @param fixedSize - the component size.
		 * @return the group (Method chaining).
		 */
		public S addFixedComponent(Widget component, int fixedSize)
		{
			return addComponent(new WidgetElement(component, fixedSize, fixedSize));
		}

		protected S addComponent(@NonNull WidgetElement widgetElement)
		{
			synchronized (elements)
			{
				if (elements.contains(widgetElement))
					throw new IllegalArgumentException("Component already added to the group!");
				else
				{
					Set<Widget> components = getAddedComponents();

					if (components.contains(widgetElement.getElement()))
						throw new IllegalArgumentException("Component already added to a child group!");

					add(widgetElement);
					linkComponent(widgetElement.getElement());
				}
			}

			return self();
		}

		protected S addGroup(@NonNull GroupElement groupElement)
		{
			synchronized (elements)
			{
				if (elements.contains(groupElement))
					throw new IllegalArgumentException("Group already added to this group!");
				else if (groupElement.getElement().getParent() != null)
					throw new IllegalArgumentException("Group already added to another group! It has a parent!");
				else
				{
					Set<Widget> componentsToMerge = groupElement.getAddedComponents();
					Set<Widget> components = getAddedComponents();

					for (Widget component : componentsToMerge)
					{
						if (components.contains(component))
							throw new IllegalArgumentException("Duplicated components! Group contains components already added to the parent group!");
					}

					add(groupElement);
					groupElement.getElement().setParent(this);
				}
			}

			return self();
		}

		/**
		 * Return the complete Set of components added to this group, including
		 * components added to its children groups.
		 * 
		 * @return an Set of added components, or an empty one if the group
		 *         doesn't contain any component.
		 */
		public Set<Widget> getAddedComponents()
		{
			Set<Widget> components = new LinkedHashSet<>();

			synchronized (elements)
			{
				for (Element<?> element : elements)
					components.addAll(element.getAddedComponents());
			}

			return components;
		}

		/**
		 * Get the group at the top of the hierarchy.
		 * 
		 * @return the group that hasn't a parent into the hierarchy.
		 */
		public Group<?> getRoot()
		{
			return parent == null ? this : parent.getRoot();
		}

		/**
		 * Calculate group size.
		 * 
		 * @return the group size.
		 */
		protected abstract int getSize(Orientation orientation);

		/**
		 * Attempts to resize the group to the given size.
		 *
		 * @param horizontal - true to consider width, false to consider height.
		 * @param size - the preferred size.
		 * @return the effective size set.
		 */
		protected abstract int attemptResize(Orientation orientation, int size);

		/**
		 * Collocates the group at the given offset.
		 * 
		 * @param horizontal - true to consider width, false to consider height.
		 * @param offset - the position offset (Where the element will be collocated).
		 * @return the sum of the given offset and group's {@link #getSize(boolean)}.
		 */
		protected abstract int collocate(Orientation orientation, int offset);
		
		protected abstract S self();

		@EqualsAndHashCode
		protected abstract class Element<T>
		{
			@Getter
			private T element;

			public Element(@NonNull T element)
			{
				this.element = element;
			}

			/**
			 * Return the complete Set of components added to this element.
			 * 
			 * @return an Set of added components, or an empty one if the group
			 *         doesn't contain any component.
			 */
			protected abstract Set<Widget> getAddedComponents();
			
			/**
			 * Return the size of the element.
			 * 
			 * @param horizontal - true to consider width, false to consider height.
			 * @return - the calculated size.
			 */
			protected abstract int getSize(Orientation orientation);

			/**
			 * Attempt to resize the element to the given size.
			 * 
			 * @param horizontal - true to consider width, false to consider height.
			 * @param size - the preferred size.
			 * @return the effective size set.
			 */
			protected abstract int attemptResize(Orientation orientation, int size);
			
			/**
			 * Collocates element at the given offset.
			 * 
			 * @param horizontal - true to consider width, false to consider height.
			 * @param offset - the position offset (Where the element will be collocated).
			 * @return the sum of the given offset and element's {@link #getSize(boolean)}.
			 */
			protected abstract int collocate(Orientation orientation, int offset);
		}

		protected abstract class AlignableElement<T> extends Element<T>
		{
			@Getter
			private transient Alignment alignment;

			public AlignableElement(@NonNull T element, @NonNull Alignment alignment)
			{
				super(element);
				this.alignment = alignment;
			}
		}

		protected class WidgetElement extends AlignableElement<Widget>
		{
			private transient int minimumSize;
			private transient int maximumSize;

			public WidgetElement(Widget element)
			{
				this(element, -1, -1);
			}

			public WidgetElement(Widget element, Alignment alignment)
			{
				this(element, -1, -1, alignment);
			}

			public WidgetElement(Widget element, int minimumSize, int maximumSize)
			{
				this(element, minimumSize, maximumSize, Alignment.LEADING);
			}

			public WidgetElement(Widget element, int minimumSize, int maximumSize, Alignment alignment)
			{
				super(element, alignment);
				this.minimumSize = minimumSize;
				this.maximumSize = maximumSize;
				if (this.minimumSize > this.maximumSize)
					throw new IllegalArgumentException("Minimum size cannot be greater than maximum size!");
			}

			@Override
			protected Set<Widget> getAddedComponents()
			{
				return Sets.newHashSet(getElement());
			}

			@Override
			protected int attemptResize(@NonNull Orientation orientation, int size)
			{
				parseMinMax(orientation);
				return orientation == Orientation.HORIZONTAL ? getElement().setWidth(size) : getElement().setHeight(size);
			}

			private void parseMinMax(@NonNull Orientation orientation)
			{
				if (minimumSize >= 0 && maximumSize >= minimumSize)
				{
					Dimension minimum = getElement().getMinimumSize().clone();
					Dimension maximum = getElement().getMaximumSize().clone();

					if (orientation == Orientation.HORIZONTAL)
					{
						minimum.setWidth(minimumSize);
						maximum.setWidth(maximumSize);
					}
					else
					{
						minimum.setHeight(minimumSize);
						maximum.setHeight(maximumSize);
					}

					getElement().setMinimumAndMaximumSize(minimum, maximum);
				}
			}

			@Override
			protected int collocate(@NonNull Orientation orientation, int offset)
			{
				if(orientation == Orientation.HORIZONTAL)
				{
					getElement().setX(offset);
					return offset + getElement().getWidth();
				}
				else
				{
					getElement().setY(offset);
					return offset + getElement().getHeight();
				}
			}

			@Override
			protected int getSize(@NonNull Orientation orientation)
			{
				return orientation == Orientation.HORIZONTAL ? getElement().getWidth() : getElement().getHeight();
			}
		}

		protected class GroupElement extends AlignableElement<Group<?>>
		{
			public GroupElement(Group<?> group)
			{
				this(group, Alignment.LEADING);
			}

			public GroupElement(Group<?> group, Alignment alignment)
			{
				super(group, alignment);
			}

			@Override
			protected Set<Widget> getAddedComponents()
			{
				return getElement().getAddedComponents();
			}

			@Override
			protected int getSize(@NonNull Orientation orientation)
			{
				return getElement().getSize(orientation);
			}
			
			@Override
			protected int attemptResize(@NonNull Orientation orientation, int size)
			{
				return getElement().attemptResize(orientation, size);
			}

			@Override
			protected int collocate(@NonNull Orientation orientation, int offset)
			{
				return getElement().collocate(orientation, offset);
			}
		}
	}

	public class SequentialGroup extends Group<SequentialGroup>
	{
		/**
		 * Adds a child group.
		 * 
		 * @param group - the group to add.
		 * @return the group (Method chaining).
		 */
		public SequentialGroup addGroup(ParallelGroup group)
		{
			return super.addGroup(new GroupElement(group));
		}

		/**
		 * Adds a gap of the given size.
		 * 
		 * @param size - the gap size.
		 * @return the group (Method chaining).
		 */
		public SequentialGroup addGap(int size)
		{
			return add(new GapElement(size));
		}

		/**
		 * Adds a gap that can be resized according to the given minimum and maximum sizes.
		 * 
		 * @param minimum - the minimum gap size allowed.
		 * @param preferred - the preferred gap size.
		 * @param maximum - the maximum gap size allowed.
		 * @return the group (Method chaining).
		 */
		public SequentialGroup addGap(int minimum, int preferred, int maximum)
		{
			return add(new GapElement(minimum, preferred, maximum));
		}

		/**
		 * Adds a gap that cannot be resized.
		 * 
		 * @param size - the gap fixed size.
		 * @return the group (Method chaining).
		 */
		public SequentialGroup addFixedGap(int size)
		{
			return addGap(size, size, size);
		}
		
		/**
		 * The size of a sequential group is equals to the sum of the sizes of
		 * its elements.
		 * 
		 * @param horizontal - true to consider width, false to consider height.
		 * @return the calculated size.
		 */
		@Override
		protected int getSize(@NonNull Orientation orientation)
		{
			int size = 0;

			synchronized (getElements())
			{
				for (Element<?> element : getElements())
				{
					size += element.getSize(orientation);
				}
			}
			
			return size;
		}
		
		@Override
		protected int attemptResize(@NonNull Orientation orientation, int size)
		{
			int filledSize = 0;
			Set<Element<?>> filledElements = new HashSet<>();

			synchronized (getElements())
			{
				int latestSize = 0;

				while (filledElements.size() < getElements().size() && filledSize < size)
				{
					int nextSize = (size - filledSize) / (getElements().size() - filledElements.size());

					if (nextSize < 1)
						nextSize = 1;

					for (Element<?> element : getElements())
					{
						if (filledSize >= size)
							break;

						if (filledElements.contains(element))
							continue;

						int nextFill = 0;

						filledSize += (nextFill = element.attemptResize(orientation, nextSize + latestSize)) - latestSize;

						if (nextFill != nextSize + latestSize)
							filledElements.add(element);
					}

					latestSize += nextSize;

				}
			}

			return filledSize > size ? size : filledSize;
		}

		@Override
		protected int collocate(@NonNull Orientation orientation, int offset)
		{
			synchronized(getElements())
			{
			    for(Element<?> element : getElements())
			    {
			    	offset = element.collocate(orientation, offset);
			    }
			}
			
			return offset;
		}
		
		@Override
		protected SequentialGroup self()
		{
			return this;
		}

		/**
		 * Represents space between other elements in <code>SequentialGroup</code>s.
		 * It can be resized and can have a minimum and a maximum size.
		 * 
		 * @author Vincenzo Fortunato - (Flood)
		 */
		protected class GapElement extends Element<Gap>
		{
			public GapElement(Gap gap)
			{
				super(gap);
			}

			public GapElement(int size)
			{
				this(new Gap(size));
			}

			public GapElement(int min, int preferred, int max)
			{
				this(new Gap(min, preferred, max));
			}

			@Override
			protected Set<Widget> getAddedComponents()
			{
				return new HashSet<>();
			}

			@Override
			protected int attemptResize(@NonNull Orientation orientation, int size)
			{
				getElement().setSize(size);
				return getElement().getSize();
			}

			@Override
			protected int collocate(@NonNull Orientation orientation, int offset)
			{
				return offset + getElement().getSize();
			}

			@Override
			protected int getSize(@NonNull Orientation orientation)
			{
				return getElement().getSize();
			}
		}
	}

	public class ParallelGroup extends Group<ParallelGroup>
	{
		/**
		 * Adds a child group.
		 * 
		 * @param group - the group to add.
		 * @return the group (Method chaining).
		 */
		public ParallelGroup addGroup(SequentialGroup group)
		{
			return super.addGroup(new GroupElement(group));
		}

		/**
		 * Adds a child group with the given alignment.
		 * 
		 * @param group - the group to add.
		 * @param alignment - the group alignment.
		 * @return the group (Method chaining).
		 */
		public ParallelGroup addGroup(SequentialGroup group, Alignment alignment)
		{
			return super.addGroup(new GroupElement(group, alignment));
		}

		/**
		 * Add a component to the group with the given alignment.
		 * 
		 * @param component - the component to add.
		 * @param alignment - the component alignment.
		 * @return the group (Method chaining).
		 */
		public ParallelGroup addComponent(Widget component, Alignment alignment)
		{
			return addComponent(new WidgetElement(component, alignment));
		}
		
		/**
		 * Adds a component to the Group with the given alignment.
		 * <p>
		 * The given minimum and maximum sizes will be used by the layout resize behavior.
		 * 
		 * @param component - the component to add.
		 * @param minimumSize - the minimum component size.
		 * @param maximumSize - the maximum component size.
		 * @param alignment - the component alignment.
		 * @return the group (Method chaining).
		 */
		public ParallelGroup addComponent(Widget component, int minimumSize, int maximumSize, Alignment alignment)
		{
			return addComponent(new WidgetElement(component, minimumSize, maximumSize, alignment));
		}

		/**
		 * Adds a fixed component to this Group with the given alignment. It will not be resized by the layout resize behavior.
		 * 
		 * @param component - the component to add.
		 * @param fixedSize - the component size.
		 * @param alignment - the component alignment.
		 * @return the group (Method chaining).
		 */
		public ParallelGroup addFixedComponent(Widget component, int fixedSize, Alignment alignment)
		{
			return addComponent(component, fixedSize, fixedSize, alignment);
		}

		/**
		 * The size of a parallel group is equals to the size of the bigger
		 * element into the group.
		 * 
		 * @param horizontal - true to consider width, false to consider height.
		 * @return the calculated size.
		 */
		@Override
		protected int getSize(@NonNull Orientation orientation)
		{
			int size = 0;

			synchronized (getElements())
			{
				for (Element<?> element : getElements())
				{
					int nextSize = element.getSize(orientation);
					
					if(nextSize > size)
						size = nextSize;
				}
			}

			return size;
		}
		
		@Override
		protected int attemptResize(@NonNull Orientation orientation, int size)
		{
			int filledSize = 0;

			synchronized (getElements())
			{
				for (Element<?> element : getElements())
				{
					int nextFill = element.attemptResize(orientation, size);

					filledSize = nextFill > filledSize ? nextFill : filledSize;
				}
			}

			return filledSize;
		}

		@Override
		protected int collocate(@NonNull Orientation orientation, int offset)
		{
			int groupSize = getSize(orientation);
			
			synchronized(getElements())
			{
				for(Element<?> element : getElements())
				{
					if(element instanceof AlignableElement)
					{
						AlignableElement<?> alignable = (AlignableElement<?>) element;
						
						switch(alignable.getAlignment())
						{
							case LEADING:
								element.collocate(orientation, offset);
								break;
							case CENTER:
								element.collocate(orientation, offset + groupSize / 2 - element.getSize(orientation) / 2);
								break;
							case TRAILING:
								element.collocate(orientation, offset + groupSize - element.getSize(orientation));
								break;
						}
					}
				}
			}
			
			return offset + groupSize;
		}
		
		@Override
		protected ParallelGroup self()
		{
			return this;
		}
	}
}
