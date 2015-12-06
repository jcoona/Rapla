package org.rapla.gui.internal.edit.fields;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
/**
 * This class will take input text and make a box containing that text.
 * @author clj13001
 *
 */
public class EventInfoBox extends JButton {

	private String boxText;
	private int width;
	private int height;
	
	public EventInfoBox(String text){
		super(text);
		Dimension size = getPreferredSize();
		setSize(size);
		this.width = size.width;
		this.height = size.height;
		boxText = text;
	}
	
	public int getWidth(){
		return width;
	}
}
