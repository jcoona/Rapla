/// Homework 9: Rythminator Part 3
/// CSE 1102: Object Oriented Design and Programming
/// Connor Jackson
/// TA: Aditya Dhakal
/// Creation Date: April 30th, 2014

package org.rapla.gui.internal.edit.fields;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.rapla.entities.RaplaObject;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.entities.dynamictype.DynamicTypeAnnotations;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.gui.RaplaGUIComponent;
import org.rapla.gui.internal.FilterEditButton;
import org.rapla.gui.internal.edit.ClassifiableFilterEdit;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;

public class SearchController extends RaplaGUIComponent{
/**
 * Search Controller acts as a controller mechanism that makes a text field and button cooperate.
 * In our specific enhancement, this will combine the efforts of the resource search bar and 
 * the resource search button.
 * 
 * This class is basically the "middle man" between the two; it allows for the search bar
 * to pass through text to the search button, and will allow the search button to execute a filter.
 * 
 * @author Connor Jackson, Adam Claxton
 */
private CalendarSelectionModel _model;
private SearchTextField _searchTextField;
private SearchButton _searchButton;
private FilterEditButton _filterButton;
private ClassifiableFilterEdit _filterEdit;

Collection<RaplaObject> selectedObjects;

/**
 * Main constructor for the class. Will connect to the main software model.
 * @param model - The software model.
 */
  public SearchController(CalendarSelectionModel model, RaplaContext context){
	super(context);
    this._model = model;
  }

/**
 * When a button is pressed, we want to transfer its related text to perform a filter action.
 * @param button - the button that was clicked
 * @throws RaplaException 
 */
  public void buttonPressed(SearchButton button) throws RaplaException
  {
    String searchText = button.getSearchText();
    //print search text to the console
    System.out.println(searchText);
    
    //generate a list of all resource types; this includes "resource" and "person."
    List<DynamicType> resourceList = new ArrayList<DynamicType>();
    resourceList.addAll( Arrays.asList( getQuery().getDynamicTypes( DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_RESOURCE )));
    resourceList.addAll( Arrays.asList( getQuery().getDynamicTypes( DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_PERSON )));
    
  }
  
/**
 * When text is entered, we want to connect that subsequent string of characters to the search button,
 * that will be pressed when search criteria is finished being typed.
 * @param textField - the field we are typing into.
 */
  public void textEntered(SearchTextField textField){
	  String enteredText = textField.getValue();
	  _searchButton.setSearchText(enteredText);
  }
  
/**
 * Associates a text field with the controller.
 * @param textfield
 */
  public void addSearchTextField(SearchTextField textfield){
	  _searchTextField = textfield;
  }

/**
 * Associates a search button with the controller.
 * @param button
 */
  public void addSearchButton(SearchButton button){
	  _searchButton = button;
  }
  
  public void addFilterButton(FilterEditButton filterButton){
	  _filterButton = filterButton;
  }

}
