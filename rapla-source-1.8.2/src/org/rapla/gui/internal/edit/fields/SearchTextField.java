package org.rapla.gui.internal.edit.fields;

import java.awt.Color;
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

import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
/**
 * This will generate a SearchTextField.
 * 
 * TextFields are just fields that <i>contain</i> JTextFields. Confusing, but it acts as a container class.
 * 
 * This specific extension will work specifically to search through the Rapla calendar for events based on SearchCriteria.
 * @author Connor Jackson, Adam Claxton
 *
 */
public class SearchTextField extends TextField implements ActionListener,FocusListener,KeyListener, MultiEditField, SetGetField<String>
{
/**
 * We supplied all the same member variables at its super.
 */
	JTextComponent field;
    JComponent colorPanel;
    JScrollPane scrollPane;
    JButton colorChooserBtn ;
    JPanel color;
    Object oldValue;
    Color currentColor;
    SearchController controller; //has a controller that it connects to
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
    	//controller.addSearchTextField(this);
    	listener=listen;
    }
/**
 * This method is invoked when a key is released.
 * 
 * More specifically, it will react greatly when a user types on the "enter" key on the keyboard.
 * Then, a search will be performed.
 */
    public void keyReleased(KeyEvent evt){
    	//Pressing enter has the same effect as clicking the search button
    	if((int)evt.getKeyChar()==10)	//10 is the character code for 'enter'. This is a bit of a workaround instead of adding an ActionListener, because rapla's textfield does not extend JTextField.
    	{
    		try {
    			controller.pressedEnter(this);
    		} catch (RaplaException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
}
