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
 * This will generate an event with text associated with each attribute type, so that it can be displayed.
 * @author clj13001
 *
 */
public class DisplayableEvent extends JLabel {

	private String[] attributes;
	private int length;
	
	public DisplayableEvent(String[] values){
		if (values.length > 0){
			this.length = values.length;
			this.attributes = values;
		}
	}
	
	public String[] getAttributes(){
		return attributes;
	}

	public int getLength(){
		return length;
	}
}
