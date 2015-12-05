/// Homework 9: Rythminator Part 3
/// CSE 1102: Object Oriented Design and Programming
/// Connor Jackson
/// TA: Aditya Dhakal
/// Creation Date: April 30th, 2014

package org.rapla.gui.internal.edit.fields;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rapla.entities.RaplaObject;
import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.entities.dynamictype.DynamicTypeAnnotations;
import org.rapla.entities.dynamictype.internal.AttributeImpl;
import org.rapla.entities.dynamictype.internal.ClassificationFilterImpl;
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
private ArrayList<ClassificationFilter> filters;

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
    
    List<Attribute> raplaAttributes = generateAttributeList();
    List<DynamicType> reservationList = generateReservationList();
    
    
    //makes pop-up in a really strange way; in construction //we don't need this right now
    /*
    JFrame resultFrame = new JFrame("Search Results");
    resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    resultFrame.getContentPane().add(button,BorderLayout.CENTER);
    resultFrame.pack();
    resultFrame.setVisible(true);
    */
    updateFilters(reservationList, raplaAttributes);
    performSearch();
  }
  
  private void performSearch() 
  {
	  //ClassifiableFilterEdit seems to do this by called stateChanged on its ChangeListener
	  //So I guess we'll try the same
	  ChangeListener listener = _searchTextField.listener;
      ChangeEvent evt = new ChangeEvent(this);
      listener.stateChanged(evt);
  }

/**
   * This bulldozes the list of filters every time the search is called and makes a new one
   * Could be more efficient, but it works. Maybe.
   */
  private void updateFilters(List<DynamicType> reservationList, List<Attribute> raplaAttributes) 
  {
	  filters = new ArrayList<ClassificationFilter>();
	  for(Attribute a: raplaAttributes)
	  {
		  if(a != null)
		  {
			  ClassificationFilter filter = reservationList.get(0).newClassificationFilter();	//I don't fully understand what types are in rapla, but debugging indicates the list contains only one type at this point, so I will use it to create the filter.
			  //Our filter needs rules to be added to it.
			  //rapla uses a two dimensional "conditions" array for this
			  Object[][] conditions = new Object[1][2];
			  conditions[0][0]="contains"; //first element is operator
			  conditions[0][1]=_searchButton.getSearchText(); //second element is the string
			  filter.setRule(0, a, conditions); //First parameter is index; this is for multiple rules within one filter. We don't use that.
			  filters.add(filter);
		  }
	  }
	  return;
  }

/**
   * Generates a list of all the attributes in the Rapla console.
   * @return
   * @throws RaplaException
   */
  public List<Attribute> generateAttributeList() throws RaplaException{
	//generate a list of all resource & attribute types; this includes "resource" and "person."
	    List<DynamicType> resourceList = new ArrayList<DynamicType>();
	    List<Attribute> raplaAttributes = new ArrayList<Attribute>();
	    
	    resourceList.addAll(Arrays.asList(getQuery().getDynamicTypes(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_RESOURCE)));
	    resourceList.addAll(Arrays.asList(getQuery().getDynamicTypes(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_PERSON)));
	    
	    int resourceListLength = resourceList.size();
	    
	    for (int i=0; i<resourceListLength; i++){//iterate through all resources in rapla
	    	DynamicType currentResource = resourceList.get(i);
	    	Attribute[] attributeArray = currentResource.getAttributes();
	    	for (int j=0; j<attributeArray.length; j++){//add all those attributes to the attributes array
	    		Attribute currentAttribute = attributeArray[j];
	    		raplaAttributes.add(currentAttribute);
	    	}
	    }
	    
	    return raplaAttributes;
  }
  /**
   * This method will retrieve all events currently existing in the Rapla console.
   * @return
   * @throws RaplaException
   */
  public List<DynamicType> generateReservationList() throws RaplaException{
	List<DynamicType> reservationList = new ArrayList<DynamicType>();
	//not quite... this gets event TYPES, not the events themselves
	reservationList.addAll(Arrays.asList(getQuery().getDynamicTypes(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_RESERVATION)));
	return reservationList;
  }
  
/**
 * When text is entered, we want to connect that subsequent string of characters to the search button,
 * that will be pressed when search criteria is finished being typed.
 * @param textField - the field we are typing into.
 */
  public void textEntered(SearchTextField textField){
	  String enteredText = textField.getValue();
	  getSearchButton().setSearchText(enteredText);
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
	  setSearchButton(button);
  }
  
  public void addFilterButton(FilterEditButton filterButton){
	  _filterButton = filterButton;
  }

public SearchButton getSearchButton() {
	return _searchButton;
}

public void setSearchButton(SearchButton _searchButton) {
	this._searchButton = _searchButton;
}

public ClassificationFilter[] getFilters() 
{
	Object[] array = filters.toArray();
	ClassificationFilter[] f = new ClassificationFilter[array.length];
	for(int i=0; i<array.length; i++)	
	{
		f[i]=(ClassificationFilter)array[i];	//casting the full object[] to a filter[] doesn't work, so I guess we'll do it item by item
	}
	return f; 
}

}
