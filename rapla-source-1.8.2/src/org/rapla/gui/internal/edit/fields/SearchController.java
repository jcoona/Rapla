package org.rapla.gui.internal.edit.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.entities.dynamictype.DynamicTypeAnnotations;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.gui.RaplaGUIComponent;
import org.rapla.gui.internal.FilterEditButton;
import org.rapla.gui.internal.edit.ClassifiableFilterEdit;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;

public class SearchController extends RaplaGUIComponent{
/**
 * Search Controller acts as a controller mechanism that makes JComponents in ResourceSelection cooperate with one another.
 * 
 * In our specific enhancement, this will combine the efforts of the resource search bar and 
 * the resource combo box.
 * @author Connor Jackson, Adam Claxton
 */

//private SearchTextField searchTextField; //text field in ResourceSelection
private ResourceDropBox dropbox; //resource drop box in ResourceSelection
private FilterEditButton filterEditButton; //filter edit button in ResourceSelection
private ClassifiableFilterEdit filterEdit; //ClassificationFilterEdit in ResourceSelection

private List<DynamicType> resourceList; //resource data structure 
private SearchResource[] resourceObjects; //resource structure with DynamicType and actual name as values
boolean isPopupOpen;

/**
 * Main constructor for the class. Will connect to the main software model.
 * @param model - The software model.
 */
public SearchController(CalendarSelectionModel model, RaplaContext context){
	super(context);
	isPopupOpen = false;
  }
/**
 * This method is executed when the user performs a search.
 * It will take search from the textfield in ResourceSelection and simulate a filtering, based
 * on which resource was selected in the combobox in ResourceSelection.
 * @param textfield
 * @throws RaplaException
 */
@SuppressWarnings("rawtypes")
public void pressedEnter(SearchTextField textfield) throws RaplaException
{
  //FIRST, lets connect the text in the field to the selected resource.
  String searchText = textfield.getValue();
  String searchChoice = (String) dropbox.getSelectedItem();
  
  //print search text to the console as a test
  //System.out.println(searchText + " " + searchChoice);
  
  //Select which resource we are searching by, based on the dropbox value.
  filterEdit.reset();
  //if we don't search all, do a one resource search
  Boolean[] isChecked = new Boolean[filterEdit.getCheckBoxes().length];
  if (!searchChoice.equals("Search All")){
	  isChecked = filterEdit.updateCheckboxes(searchChoice);
  }
  //otherwise, leave all checkboxes marked off. Reset was just called, so all the checkboxes are checked off at the moment
  else{
	  for (int i=0; i<isChecked.length; i++)
		  isChecked[i] = true;
  }
  filterEdit.fireFilterChanged();
  
  //Select all applicable attributes in the attribute selector.
  JComboBox[] attributeSelectors = filterEdit.getAttributeSelectors();
  generateResourceList();
  createRules(searchChoice, isChecked, attributeSelectors);
  
  //Now, add the searchtext to all the generated JTextFields.
  filterEdit.setRules(isChecked, searchText);
  
  //And make ClassifiableFilterEdit pop open.
  //if (isPopupOpen == false)
	  //isPopupOpen = makeFilterPopup(); No longer needed!
}
/**
 * This method creates Resource objects based on what resources are currently present in
 * ClassifiableFilterEdit's checkbox areas. It will create objects that contain two keys:
 * a) The actual name
 * b) Internal DynamicType
 * @throws RaplaException
 */
public void generateResourceObjects() throws RaplaException{
	    JCheckBox[] checkBoxes = filterEdit.getCheckBoxes();
	    
	    SearchResource[] resources = new SearchResource[checkBoxes.length];
	    
	    for (int i=0;i<checkBoxes.length;i++){
	    	JCheckBox currentBox = checkBoxes[i];
	    	String resourceName = currentBox.getText();
	    	resources[i] = new SearchResource(resourceName, resourceList.get(i).getKey());
	    }
	    this.resourceObjects = resources;
}
/**
 * This method gets the actual name of resources Rapla contains. This helps to build the Resource selector in ResourceSelection menu.
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
/**
 * This method simulates selecting all the attributes for a resource, based on the one selected in the ResourceSelection combobox.
 * @param searchChoice
 * @param isChecked
 * @param attributeSelectors
 */
@SuppressWarnings("rawtypes")
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
 * When you make a search the instance of ClassifiableFilterEdit will popup.
 */
public boolean makeFilterPopup(){
	   filterEditButton.popup();
	   return true;
   }
/**
 * This method gets all resource types from the Rapla query.
 * @throws RaplaException
 */
public void generateResourceList() throws RaplaException{
 	  List<DynamicType> resourceList = new ArrayList<DynamicType>();
 	  resourceList.addAll(Arrays.asList(getQuery().getDynamicTypes(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_RESOURCE)));
 	  resourceList.addAll(Arrays.asList(getQuery().getDynamicTypes(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_PERSON)));
 	  this.resourceList = resourceList;
   }
/**
 * Connects a filterEditButton and its ClassifiableFilterEdit to this controller.
 * @param filterButton
 */
public void addFilter(FilterEditButton filterButton){
	  filterEdit = filterButton.getFilterUI();
	  filterEditButton = filterButton;
  }
/**
 * Adds a resource combo box to this controller.
 * @param dropbox
 */
public void addResourceDropBox(ResourceDropBox dropbox){
	  this.dropbox = dropbox;
}
/**
 * Getter.
 * @return
 */
public ClassifiableFilterEdit getFilter(){
	return filterEdit;
}
}
