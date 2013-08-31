/*   
 * This file is part of LAIS (LaSEEB Agent Interaction Simulator).
 * 
 * LAIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LAIS is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LAIS.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.laseeb.LAIS.utils;

import java.util.Collection;

import javax.swing.AbstractListModel;

/**
 * Implementation of a {@link javax.swing.AbstractListModel} using an array as
 * the backbone for a {@link javax.swing.JList}. It's possible to add an array
 * or collection, erasing previously existing data in the list.
 * 
 * @author Nuno Fachada
 *
 */
@SuppressWarnings("serial")
public class ArrayListModel extends AbstractListModel {
	
	/* The array holding stuff for the list. */
	private Object objectArray[];
	
	/**
	 * Creates a new model which will be populated with data from the given
	 * collection.
	 * 
	 * @param collection Data for the list.
	 */
	public ArrayListModel(Collection<?> collection) {
		setCollection(collection);
	}
	
	/**
	 * Creates a new model which will be populated with data from the given array.
	 * 
	 * @param array Data for the list.
	 */
	public ArrayListModel(Object array[]) {
		setArray(array);
	}
	
	/**
	 * Creates an empty model.
	 */
	public ArrayListModel() {
		objectArray = null;
	}

	/**
	 * Sets the list contents with data from the given array. Erases previously
	 * existing data.
	 * 
	 * @param array Data for the list.
	 */
	public void setArray(Object array[]) {
		if (objectArray != null)
			fireIntervalRemoved(this, 0, objectArray.length - 1);
		objectArray = array;
		fireIntervalAdded(this, 0, objectArray.length - 1);		
	}
	
	/**
	 * Sets the list contents with data from the given collection. Erases previously
	 * existing data.
	 * 
	 * @param collection Data for the list.
	 */
	public void setCollection(Collection<?> collection) {
		if (objectArray != null) {
			fireIntervalRemoved(this, 0, objectArray.length - 1);
		}
		if (collection.isEmpty()) {
			objectArray = null;
		} else {
			objectArray = new Object[collection.size()];
			collection.toArray(objectArray);
			fireIntervalAdded(this, 0, objectArray.length - 1);
		}
	}

	/**
	 * @see javax.swing.AbstractListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		if (objectArray == null)
			return null;
		if (index >= objectArray.length)
			return null;
		return objectArray[index];
	}

	/**
	 * @see javax.swing.AbstractListModel#getSize()
	 */
	public int getSize() {
		if (objectArray == null)
			return 0;
		else
			return objectArray.length;
	}

}
