package org.rapla.gui.internal.edit.fields;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;
/**
 * This class will populate a drop box of all Rapla resources in the resource menu.
 * It will allow the user to search based on a resource that they pick.
 * @author Jackson
 *
 */
public class ResourceDropBox extends JComboBox implements MouseListener{
	
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
		this.addMouseListener(this);
		controller.addResourceDropBox(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
/**
 * When you click on the combo box, we want to update the current selected value in the controller class.
 */
	@Override
	public void mousePressed(MouseEvent e) {
		controller.menuOptionChosen(this);
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
}
