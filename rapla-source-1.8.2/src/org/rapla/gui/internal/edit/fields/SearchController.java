/// Homework 9: Rythminator Part 3
/// CSE 1102: Object Oriented Design and Programming
/// Connor Jackson
/// TA: Aditya Dhakal
/// Creation Date: April 30th, 2014

package org.rapla.gui.internal.edit.fields;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import org.rapla.entities.RaplaObject;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.gui.internal.edit.ClassifiableFilterEdit;

public class SearchController{
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
private ClassifiableFilterEdit _filterEdit;

Collection<RaplaObject> selectedObjects;

/**
 * Main constructor for the class. Will connect to the main software model.
 * @param model - The software model.
 */
  public SearchController(CalendarSelectionModel model){
    this._model = model;
    this.selectedObjects = model.getSelectedObjects();
  }

/**
 * When a button is pressed, we want to transfer its related text to perform a filter action.
 * @param button - the button that was clicked
 */
  public void buttonPressed(SearchButton button)
  {
    String searchText = button.getSearchText();
    System.out.println(searchText); 
    //for now, just printing to console to ensure that collecting text works
    //it does!
    //Now connect to the software! (:
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
  
  public void addClassifiableFilterEdit(ClassifiableFilterEdit filterEdit){
	  _filterEdit = filterEdit;
  }

}
