package org.rapla.gui.internal.edit.fields;
/**
 * This is a structure that will contain a resources actual name and its type.
 * It's important to access the real name, because in the Rapla software its "actual name" is saved as hash code we cannot decrypt.
 * But, we also need to access its type (resource1, resource2...) to interact with the rest of the other modules that exist.
 * @author Jackson
 *
 */
public class SearchResource {
	
	private String actualName;
	private String dynamicType;
	/**
	 * Constructor.
	 * @param actualName
	 * @param dynamicType
	 */
	public SearchResource(String actualName, String dynamicType){
		this.actualName = actualName;
		this.dynamicType = dynamicType;
	}
	/**
	 * Getter.
	 * @return
	 */
	public String getActualName(){
		return actualName;
	}
	/**
	 * Getter.
	 * @return
	 */
	public String getDynamicType(){
		return dynamicType;
	}
}
