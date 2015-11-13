package org.rapla.gui.internal.edit.fields;

import org.rapla.framework.RaplaContext;
/**
 * Here is an enhancement for faster searching methods. This class
 * generates a text field for the user to type in search criteria.
 * The length is set to 15 so that it will fit smoothly with the pre-existing
 * filter button.
 * @author Jackson, Claxton
 *
 */
public class SearchTextField extends TextField
{

	public SearchTextField(RaplaContext context) 
    {
        this( context,"", 1, 15);
    }
    
    public SearchTextField(RaplaContext context,String fieldName) 
    {
        this( context,fieldName, 1, 15);
    }
        
    public SearchTextField(RaplaContext sm,String fieldName, int rows, int columns) 
    {
    	super(sm, fieldName, rows, columns);
    }
	
}
