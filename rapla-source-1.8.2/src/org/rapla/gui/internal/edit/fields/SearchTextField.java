package org.rapla.gui.internal.edit.fields;

import org.rapla.framework.RaplaContext;

public class SearchTextField extends TextField
{

	public SearchTextField(RaplaContext context) 
    {
        this( context,"", 1, SearchTextField.DEFAULT_LENGTH);
    }
    
    public SearchTextField(RaplaContext context,String fieldName) 
    {
        this( context,fieldName, 1, SearchTextField.DEFAULT_LENGTH);
    }
        
    public SearchTextField(RaplaContext sm,String fieldName, int rows, int columns) 
    {
    	super(sm, fieldName, rows, columns);
    }
	
}
