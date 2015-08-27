/*--------------------------------------------------------------------------*
 | Copyright (C) 2014 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/
package org.rapla.entities.dynamictype.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import org.rapla.entities.Entity;
import org.rapla.entities.EntityNotFoundException;
import org.rapla.entities.RaplaType;
import org.rapla.entities.ReadOnlyException;
import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.Classification;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.entities.dynamictype.DynamicTypeAnnotations;
import org.rapla.entities.dynamictype.internal.ParsedText.EvalContext;
import org.rapla.entities.storage.CannotExistWithoutTypeException;
import org.rapla.entities.storage.DynamicTypeDependant;
import org.rapla.entities.storage.EntityReferencer;
import org.rapla.entities.storage.EntityResolver;
import org.rapla.entities.storage.UnresolvableReferenceExcpetion;

/** Use the method <code>newClassification()</code> of class <code>DynamicType</code> to
 *  create a classification. Once created it is not possible to change the
 *  type of a classifiction. But you can replace the classification of an
 *  object implementing <code>Classifiable</code> with a new one.
 *  @see DynamicType
 *  @see org.rapla.entities.dynamictype.Classifiable
 */
public class ClassificationImpl implements Classification,DynamicTypeDependant, EntityReferencer {

	private String typeId;
	private String type;
	private Map<String,List<String>> data = new LinkedHashMap<String,List<String>>();
	private transient boolean readOnly = false;

	private transient TextCache name;
	private transient EntityResolver resolver;
    
    /** stores the nonreference values like integers,boolean and string.*/
    //HashMap<String,Object> attributeValueMap = new HashMap<String,Object>(1);
    /** stores the references to the dynamictype and the reference values */
    //transient ReferenceHandler referenceHandler = new ReferenceHandler(data);

    class TextCache
    {
        String nameString;
        ParsedText lastParsedAnnotation;
        public String getName(Locale locale, String keyNameFormat) {
    		DynamicTypeImpl type = (DynamicTypeImpl)getType();
    		ParsedText parsedAnnotation = type.getParsedAnnotation( keyNameFormat );
            if ( parsedAnnotation == null) {
                return type.toString();
            }

            if (nameString != null)
            {
                if (parsedAnnotation.equals(lastParsedAnnotation))
                    return nameString;
            }
            lastParsedAnnotation =  parsedAnnotation;
            nameString = format(locale, keyNameFormat);
            return nameString;
    	}
    }
    
    public ClassificationImpl()
    {
    	
    }

    ClassificationImpl(DynamicTypeImpl dynamicType) {
        typeId = dynamicType.getId();
        type = dynamicType.getKey();
    }

    public void setResolver( EntityResolver resolver)
    {
        this.resolver = resolver;
    }

    public void setReadOnly() {
        this.readOnly = true;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void checkWritable() {
        if ( readOnly )
            throw new ReadOnlyException( this );
    }

    @Override
    public Iterable<ReferenceInfo> getReferenceInfo() {
        List<ReferenceInfo> result = new ArrayList<ReferenceInfo>();
        String parentId = getParentId();
        result.add( new ReferenceInfo(parentId, DynamicType.class) );
        DynamicTypeImpl type = getType();
        for ( Map.Entry<String,List<String>> entry:data.entrySet())
        {
            String key = entry.getKey();
            Attribute attribute = type.getAttribute(key);
            RaplaType refType = attribute.getRefType();
            if ( attribute == null || refType == null)
            {
                continue;
            }
            List<String> values = entry.getValue();
            if  (values != null ) 
            {
                @SuppressWarnings("unchecked")
                Class<? extends Entity> class1 = refType.getTypeClass();
                for ( String value:values)
                {
                    result.add(new ReferenceInfo(value, class1) );
                }
            }
        }
        return result;
    }

    
    
	private String getParentId() {
		if  (typeId != null)
			return typeId;
		if (type == null)
		{
			throw new UnresolvableReferenceExcpetion( "type and parentId are both not set");
		}
		DynamicType dynamicType = resolver.getDynamicType( type);
		if ( dynamicType == null)
		{
			throw new UnresolvableReferenceExcpetion( type);
		}
		typeId = dynamicType.getId();
		return typeId;
			
	}

    public DynamicTypeImpl getType() {
    	if ( resolver == null)
    	{
    		throw new IllegalStateException("Resolver not set on classification  ");
    	}
        String parentId = getParentId();
		DynamicTypeImpl type = (DynamicTypeImpl) resolver.tryResolve( parentId, DynamicType.class);
        if ( type == null)
        {
        	throw new UnresolvableReferenceExcpetion(DynamicType.class +":" + parentId + " " +data);
        }
    	return type;
    }

    
    public String getName(Locale locale) {
    	// display name = Title of event
        if ( name == null)
        {
            name = new TextCache();
        }
        return name.getName(locale,  DynamicTypeAnnotations.KEY_NAME_FORMAT);
    }
    
    public String format( Locale locale, String annotationName)
    {
        DynamicTypeImpl type = (DynamicTypeImpl)getType();
        ParsedText parsedAnnotation = type.getParsedAnnotation( annotationName );
        if ( parsedAnnotation == null)
        {
            return "";
        }
        EvalContext evalContext = new EvalContext(locale, 0, annotationName, this );
        String nameString = parsedAnnotation.formatName(evalContext).trim();
        return nameString;
    }

//    public String getNamePlaning(Locale locale) {
//        if ( namePlaning == null)
//        {
//            namePlaning = new TextCache();
//        }
//        return namePlaning.getName(locale,  DynamicTypeAnnotations.KEY_NAME_FORMAT_PLANNING);
//    }
	

    public String getValueAsString(Attribute attribute,Locale locale)
    {
    	Collection values =  getValues(attribute);
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for ( Object value: values)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                buf.append(", ");
            }
        	buf.append( ((AttributeImpl)attribute).getValueAsString( locale, value));
        }
        String result = buf.toString();
        return result;
    }
    
    public Attribute getAttribute(String key) {
        return getType().getAttribute(key);
    }

    public Attribute[] getAttributes() {
        return getType().getAttributes();
    }

    public boolean needsChange(DynamicType newType) {
    	if ( !hasType (newType )) {
            return false;
    	}
        DynamicTypeImpl type = getType();
		if ( !newType.getKey().equals( type.getKey()))
        	return true;
		
        for (String key:data.keySet()) {
        	Attribute attribute = getType().getAttribute(key);
        	if ( attribute == null)
        	{
        	    return true;
        	}
            String attributeId = attribute.getId();
			if (type.hasAttributeChanged( (DynamicTypeImpl)newType , attributeId))
            	return true;
        }
        return false;
    }

    boolean hasType(DynamicType type) {
        return getType().equals( type);
    }

    public void commitChange(DynamicType type) {
    	if ( !hasType (type )) {
            return;
        }
        
        Collection<String> removedKeys = new ArrayList<String>();
        Map<Attribute,Attribute> attributeMapping = new HashMap<Attribute,Attribute>();
        for  (String key:data.keySet()) {
        	Attribute attribute = getType().getAttribute(key);
        	Attribute attribute2 = type.getAttribute(key);
        	// key now longer availabe so remove it
            if ( attribute2 == null)
            {
                removedKeys.add( key );
            }
			if ( attribute == null)
			{
        		continue;
			}
			String attId = attribute.getId();
			Attribute newAtt = findAttributeById(type, attId);
			if ( newAtt != null)
			{
				attributeMapping.put(attribute, newAtt);
			}
        }
        for (Attribute attribute: attributeMapping.keySet()) 
        {
			Collection<Object> convertedValues = new ArrayList<Object>();
			Collection<?> valueCollection = getValues( attribute);
            Attribute newAttribute = attributeMapping.get( attribute);
			for (Object oldValue: valueCollection)
			{
    			Object newValue = newAttribute.convertValue(oldValue);
    			if ( newValue != null)
    			{
    				convertedValues.add( newValue);
    			}
			}
			setValues(newAttribute, convertedValues);
        }
       
        for (String key:removedKeys)
        {
        	data.remove( key );
        }
        this.type = type.getKey();
        name = null;
    }

    /** find the attribute of the given type that matches the id */
    private Attribute findAttributeById(DynamicType type,String id) {
        Attribute[] typeAttributes = type.getAttributes();
        for (int i=0; i<typeAttributes.length; i++) {
            String key2 = typeAttributes[i].getId();
			if (key2.equals(id)) {
                return typeAttributes[i];
            }
        }
        return null;
    }


    public void setValue(String key,Object value) {
    	Attribute attribute = getAttribute( key );
    	if ( attribute == null ) {
    		throw new NoSuchElementException("No attribute found for key " + key);
    	}

    	setValue( attribute,value);
    }

    public Object getValue(String key) {
    	Attribute attribute = getAttribute( key );
    	if ( attribute == null ) {
    		throw new NoSuchElementException("No attribute found for key " + key);
    	}

    	return getValue(getAttribute(key));
    }

    public void setValue(Attribute attribute,Object value) {
    	checkWritable();
    	if ( value != null && !(value instanceof Collection<?>))
    	{
    		value = Collections.singleton( value);
    	}
    	setValues(attribute, (Collection<?>) value);
    }

    
    public <T> void setValues(Attribute attribute,Collection<T> values) {
        checkWritable();
        String attributeKey = attribute.getKey();
		if ( values == null || values.isEmpty())
        {
			data.remove(attributeKey);
			name = null;
        	return;
        }
		ArrayList<String> newValues = new ArrayList<String>();
		for (Object value:values)
		{
		    String stringValue = toSafeString(attribute, value);
			if ( stringValue != null)
			{
				newValues.add(stringValue);
			}
        }
		data.put(attributeKey,newValues);
        //isNameUpToDate = false;
        name = null;
    }

    private String toSafeString(Attribute attribute, Object value) throws IllegalArgumentException
    {
        final AttributeImpl attributeImpl = (AttributeImpl)attribute;
        String stringValue = attributeImpl.toStringValue(value);
        return stringValue;
    }

    public <T> void addValue(Attribute attribute,T value) {
    	checkWritable();
    	String attributeKey = attribute.getKey();
    	String stringValue = toSafeString(attribute, value);
        if ( stringValue == null)
        {
        	return;
        }
    	List<String> l = data.get(attributeKey);
    	if ( l == null) 
    	{
    		l = new ArrayList<String>();
    		data.put(attributeKey, l);
    	}
    	l.add(stringValue);
    }
    
    public Collection<Object> getValues(Attribute attribute) {
    	if ( attribute == null ) {
    		throw new NullPointerException("Attribute can't be null");
    	}
    	String attributeKey = attribute.getKey();
    	// first lookup in attribute map
        List<String> list = data.get(attributeKey);
        if ( list == null || list.size() == 0)
        {
        	return Collections.emptyList();
        }
        List<Object> result = new ArrayList<Object>();
        for (String value:list)
        {
        	Object obj;
			try {
				obj = ((AttributeImpl)attribute).fromString(resolver,value);
				result.add( obj);
			} catch (EntityNotFoundException e) {
			}
        }
        return result;
    }
    
    public Object getValue(Attribute attribute) {
    	if ( attribute == null ) {
    		throw new NullPointerException("Attribute can't be null");
    	}
    	String attributeKey = attribute.getKey();
        // first lookup in attribute map
        List<String> o = data.get(attributeKey);
        if ( o == null  || o.size() == 0)
        {
        	return null;
        }
        String stringRep = o.get(0);
        Object fromString;
		try {
			fromString = ((AttributeImpl)attribute).fromString(resolver, stringRep);
			return fromString;
		} catch (EntityNotFoundException e) {
			throw new IllegalStateException(e.getMessage());
		}
    }

	public ClassificationImpl clone() {
        ClassificationImpl clone = new ClassificationImpl((DynamicTypeImpl)getType());
        //clone.referenceHandler = (ReferenceHandler) referenceHandler.clone((Map<String, List<String>>) ((HashMap<String, List<String>>)data).clone());
        //clone.attributeValueMap = (HashMap<String,Object>) attributeValueMap.clone();
        for ( Map.Entry<String,List<String>> entry: data.entrySet())
        {
        	String key = entry.getKey();
			List<String> value = new ArrayList<String>(entry.getValue());
			clone.data.put(key, value);
        }
        clone.resolver = resolver;
        clone.typeId = getParentId();
        clone.type = type;
        clone.name = null;
        clone.readOnly = false;// clones are always writable
        return clone;
    }

     public String toString() {
         try
         {
             StringBuilder builder = new StringBuilder();
             boolean first = true;
             builder.append("{");
             for ( Attribute attribute:getAttributes())
             {
                 if ( !first)
                 {
                     builder.append(", ");
                 }
                 else
                 {
                     first = false;
                 }
                 String key = attribute.getKey();
                 String valueAsString = getValueAsString(attribute, null);
                 builder.append(key);
                 builder.append(':');
                 builder.append(valueAsString);
             }
             builder.append("}");
             return builder.toString();
         } catch (Exception ex)
         {
             return data.toString();
         }
     }

    public void commitRemove(DynamicType type) throws CannotExistWithoutTypeException 
    {
        throw new CannotExistWithoutTypeException();
    }

}
