package org.rapla.gui.internal.edit.fields;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.gui.toolkit.RaplaButton;

/**
 * This event will be displayable on the user interface, based on its attribute criteria, such as:
 * Title of event
 * People going to event
 * Location of event
 * ...etc
 * @author clj13001
 *
 */
public class DisplayableEvent extends JButton {

	List<DynamicType> attributes;
	String title;
	
	public DisplayableEvent(String title){
		super(title);
		Dimension size = getPreferredSize();
		setSize(size);
		//setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
}
