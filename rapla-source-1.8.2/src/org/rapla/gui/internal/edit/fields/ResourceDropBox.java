package org.rapla.gui.internal.edit.fields;

import javax.swing.JComboBox;
/**
 * This class will populate a drop box of all Rapla resources in the resource menu.
 * It will allow the user to search based on a resource that they pick.
 * @author Jackson
 *
 */
@SuppressWarnings("rawtypes")
public class ResourceDropBox extends JComboBox{

	private static final long serialVersionUID = 1L;
	SearchController controller; //its controller
	String[] menuOptions; //the values in the drop box
	String currentSelection; //the currently selected value
/**
 * Constructor.
 * @param resourceNames
 * @param controller
 */
	@SuppressWarnings("unchecked")
	public ResourceDropBox(String[] resourceNames, SearchController controller){
		super(resourceNames);
		this.menuOptions = resourceNames;
		this.controller = controller;
		if (resourceNames.length >= 1)
				this.currentSelection = resourceNames[0];
		controller.addResourceDropBox(this);
	}
	
}
