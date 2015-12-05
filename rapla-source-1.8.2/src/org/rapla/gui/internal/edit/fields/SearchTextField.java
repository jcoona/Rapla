package org.rapla.gui.internal.edit.fields;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
/**
 * Here is an enhancement for faster searching methods. This class
 * generates a text field for the user to type in search criteria.
 * @author Connor Jackson, Adam Claxton
 *
 */
public class SearchTextField extends TextField implements ActionListener,FocusListener,KeyListener, MultiEditField, SetGetField<String>
{

	JTextComponent field;
    JComponent colorPanel;
    JScrollPane scrollPane;
    JButton colorChooserBtn ;
    JPanel color;
    Object oldValue;
    Color currentColor;
    SearchController controller;
    ChangeListener listener;
/**
 * Constructor. Will contain a controller that connects it to a search button.	
 * @param context
 * @param controller
 */
	public SearchTextField(RaplaContext context, ChangeListener listener, SearchController controller) 
    {
        this( context,"", 1, 15, listener, controller);
        
    }
 /**
  * Another constructor.  
  * @param context
  * @param fieldName
  * @param controller
  */
    public SearchTextField(RaplaContext context,String fieldName, ChangeListener listen, SearchController controller) 
    {
        this( context,fieldName, 1, 15, listen, controller);
    }
/**
 * And... another constructor.        
 * @param sm
 * @param fieldName
 * @param rows
 * @param columns
 * @param controller
 */
    public SearchTextField(RaplaContext sm,String fieldName, int rows, int columns, ChangeListener listen, SearchController controller) 
    {
    	super(sm, fieldName, rows, columns);
    	this.controller = controller;
    	controller.addSearchTextField(this);
    	listener=listen;
    }
/**
 * This method is invoked when a key is released, when typing search criteria
 * into a text field. This is the method that Rapla has been designed to work with in previous
 * functions with a text field, so we chose to leave it that way here.
 * 
 * This will tell the controller to perform the text entered function, which will pass the input to the search button.
 */
    public void keyReleased(KeyEvent evt){
    	//Pressing enter has the same effect as clicking the search button
    	if((int)evt.getKeyChar()==10)	//10 is the character code for 'enter'. This is a bit of a workaround instead of adding an ActionListener, because rapla's textfield does not extend JTextField.
    	{
    		try {
    			controller.buttonPressed(controller.getSearchButton());
    		} catch (RaplaException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	//System.out.println(currentString); //for testing
    	controller.textEntered(this);
    }
public ClassificationFilter[] getFilters() 
{
	return controller.getFilters();
}
}
