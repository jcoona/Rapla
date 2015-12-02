package org.rapla.gui.internal.edit.fields;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;

import org.rapla.gui.toolkit.RaplaButton;
/**
 * Search Button will be used along with the textfield. It will take text from the textfield, and
 * perform a search on it.
 * @author Connor Jackson, Adam Claxton
 */
public class SearchButton extends RaplaButton implements MouseListener{
	private static final long serialVersionUID = 1L;
	
	private SearchController controller;
	private String searchText = ""; //text we typed into text field
	private int style; //large, 
	
	//styles
	public static int SMALL= -1;
    public static int LARGE = 1;
    public static int DEFAULT = 0;
    
    private static Insets smallInsets = new Insets(0,0,0,0);
    private static Insets largeInsets = new Insets(5,10,5,10);
/**
 * Main constructor for the search button.
 * @param text - what the button will say.
 * @param style - size of button.
 * @param controller - controller that cooperates with text field.
 */
	public SearchButton(String text, int style, SearchController controller){
		super(text, style);
		this.controller = controller;
		controller.addSearchButton(this);
		this.addMouseListener(this);
	}
/**
 * Associates the button with text from a matching text field.
 * Please note, this is NOT the text ON the button. This is the text
 * typed into a search text field, and we click the button to search that
 * specific instance of string.	
 * @param text
 */
	public void setSearchText(String text){
			searchText = text;
	}
/**
 * Gets the text associated with a matching text field.	
 * @return Text field text.
 */
	public String getSearchText(){
		return searchText;
	}
/**
 * Left empty; needed for interface.
 */
	@Override
	public void mouseClicked(MouseEvent e) {}
/**
 * This method is invoked when the search button is pressed (when the user wants to perform a search, essentially.)
 * It will call on the controller instance, which performs the specific tasks of this button.
 */
	@Override
	public void mousePressed(MouseEvent e) {
		controller.buttonPressed(this);
	}
/**
 * Needed for interface. Left empty.
 */
	@Override
	public void mouseReleased(MouseEvent e) {}
/**
 * Needed for interface. Left empty.
 */
	@Override
	public void mouseEntered(MouseEvent e) {}
/**
 * Needed for interface. Left empty.
 */
	@Override
	public void mouseExited(MouseEvent e) {}
	
	

}
