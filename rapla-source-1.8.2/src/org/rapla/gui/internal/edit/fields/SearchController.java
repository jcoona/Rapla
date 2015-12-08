package org.rapla.gui.internal.edit.fields;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
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
private SearchButton _searchButton; //not needed
private ResourceDropBox _dropbox;
private RaplaContext _context;
private ClassifiableFilterEdit _filterEdit;
private ArrayList<ClassificationFilter> filters;

private String currentResourceTitle; //dropbox option we want to search for
private List<DynamicType> resourceList; //resource data structure based on what rapla designers made
private SearchResource[] resourceObjects; //resource structure with dynamictype and actual name as values
private String currentSearch; //what we are searching for at the moment

/**
 * Main constructor for the class. Will connect to the main software model.
 * @param model - The software model.
 */
  public SearchController(CalendarSelectionModel model, RaplaContext context){
	super(context);
    this._model = model;
  }
  /**
   * This creates an array of resources, where it has two values:
   * a) its dynamic type value. (resource1, resource2...)
   * b) its actual name (location, goals...)
   * @throws RaplaException
   */
  public void generateResourceObjects() throws RaplaException{
	    JCheckBox[] checkBoxes = _filterEdit.getCheckBoxes();
	    
	    SearchResource[] resources = new SearchResource[checkBoxes.length];
	    
	    for (int i=0;i<checkBoxes.length;i++){
	    	JCheckBox currentBox = checkBoxes[i];
	    	String resourceName = currentBox.getText();
	    	resources[i] = new SearchResource(resourceName, resourceList.get(i).getKey());
	    }
	    this.resourceObjects = resources;
}
  
  /**
   * This gets the actual names of all resource instances. 
   * @return
   * @throws RaplaException
   */
   public String[] getResourceNames() throws RaplaException{
 	  generateResourceList();
 	  generateResourceObjects();
 	  String[] actualNames = new String[resourceList.size()];
 		for (int i=0; i<resourceList.size(); i++){
 			SearchResource currentResource = resourceObjects[i];
 			actualNames[i] = currentResource.getActualName();
 		}
 		return actualNames;
   }
   
   public void createRules(String searchChoice, Boolean[] isChecked, JComboBox[] attributeSelectors){
	   for (int i = 0; i<resourceList.size(); i++){
		   if (isChecked[i].equals(true)){
			   JComboBox currentBox = attributeSelectors[i];
			   DynamicType currentResource = resourceList.get(i);
			   Attribute[] attributeArray = currentResource.getAttributes();
			   
		    	for (int j=0; j<attributeArray.length; j++){//add all those attributes to the attributes array
		    		currentBox.setSelectedIndex(j);
		    	}
		   }
	   }
   }
   
   /**
    * Method is called when user presses enter when typing in search criteria in the search bar.
    * This will go through and search through Rapla.
    * @param textfield
    * @throws RaplaException
    */
   public void pressedEnter(SearchTextField textfield) throws RaplaException
   {
	 //FIRST, lets connect the text in the field to the selected resource.
     String searchText = textfield.getValue();
     String searchChoice = (String) _dropbox.getSelectedItem();
     //print search text to the console
     System.out.println(searchText + " " + searchChoice);
     
     //Edit checkbox based on what resource we typed in.
     Boolean[] isChecked = _filterEdit.updateCheckboxes(searchChoice);
     
     //Edit the comboboxes
     JComboBox[] attributeSelectors = _filterEdit.getAttributeSelectors();
     generateResourceList();
     createRules(searchChoice, isChecked, attributeSelectors);
     //_filterEdit.setRules(isChecked, searchText);
     
     //attributeSelectors[0].setSelectedIndex(0);
     //Attribute test =  (Attribute)attributeSelectors[0].getSelectedItem();
     //_filterEdit).autoRuleRow(test);
     //test = (String) attributeSelectors[0].getItemAt(0);
     
     //Lets get all the rapla attributes and all the events
     //List<Attribute> raplaAttributes = generateAttributeList();
     //List<DynamicType> reservationList = generateReservationList();
     
     //Then make a popup come up.
     //SearchPopup searchResults = new SearchPopup("Search Results");
     //searchResults.setVisible(true);
     
     //Get all the search results
     //List<DisplayableEvent> searchHits = getSearchResults();
     
     //Add the search results to the window
     //for (int i=0; i<searchHits.size(); i++){//iterates through whole collection of events
    	 //DisplayableEvent currentHit = searchHits.get(i);
    	 //String[] attributes = currentHit.getAttributes();
    	 //int width = 0;
    	 
    	 //for (int j = 0; j<currentHit.getLength(); j++){
    		 //String currentString = attributes[j];
    		 //EventInfoBox eventBox = new EventInfoBox(currentString);
    		 //eventBox.setLocation(30*(j+1)+(10*j)+width, 30*(i+1)+(10*i));
    		 //width += eventBox.getWidth();
    	 	 //searchResults.add(eventBox);
    	 //}
     //}
     
     //updateFilters(reservationList, raplaAttributes);
     //performSearch();
   }
   /**
    * Will get search results. At the moment Just tweaking it to make it display something.
    * @return
    */
   public List<DisplayableEvent> getSearchResults(){
	   List<DisplayableEvent> eventHits = new ArrayList<DisplayableEvent>();
	   eventHits.add(new DisplayableEvent(new String[] {"Connor's Party", "Connor's House", "Connor Jackson", "Adam Claxton"}));
	   eventHits.add(new DisplayableEvent(new String[] {"Study Group Meeting", "ITE 138", "Connor Jackson", "Maegan Dyakiw"}));
	   eventHits.add(new DisplayableEvent(new String[] {"Vacation to Vermont", "Vermont", "John Costa"}));
	  
	   return eventHits;
   }
  
   /**
    * Generates a list of all resources in Rapla. This is the structure that Rapla has made.
    * @throws RaplaException
    */
   public void generateResourceList() throws RaplaException{
 	  List<DynamicType> resourceList = new ArrayList<DynamicType>();
 	  resourceList.addAll(Arrays.asList(getQuery().getDynamicTypes(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_RESOURCE)));
 	  resourceList.addAll(Arrays.asList(getQuery().getDynamicTypes(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_PERSON)));
 	  this.resourceList = resourceList;
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
    //performSearch();
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
	    generateResourceList();
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
   * Updates the chosen resource type based on when the user clicks on the combo box.
   * @param dropbox
   */
  public void menuOptionChosen(ResourceDropBox dropbox){
	  currentResourceTitle = dropbox.getName();
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
  
  public void addFilter(FilterEditButton filterButton){
	  _filterEdit = filterButton.getFilterUI();
  }

public SearchButton getSearchButton() {
	return _searchButton;
}

/**
 * Associates a drop box with the controller.
 * @param dropbox
 */
public void addResourceDropBox(ResourceDropBox dropbox){
	  this._dropbox = dropbox;
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
