/*--------------------------------------------------------------------------*
 | Copyright (C) 2014 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org .       |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/

package org.rapla.storage.xml;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.rapla.components.util.Assert;
import org.rapla.components.util.xml.RaplaSAXAttributes;
import org.rapla.components.util.xml.RaplaSAXParseException;
import org.rapla.entities.Annotatable;
import org.rapla.entities.Category;
import org.rapla.entities.IllegalAnnotationException;
import org.rapla.entities.MultiLanguageName;
import org.rapla.entities.domain.Permission;
import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.AttributeAnnotations;
import org.rapla.entities.dynamictype.AttributeType;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.entities.dynamictype.DynamicTypeAnnotations;
import org.rapla.entities.dynamictype.internal.AttributeImpl;
import org.rapla.entities.dynamictype.internal.DynamicTypeImpl;
import org.rapla.entities.internal.CategoryImpl;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;

public class DynamicTypeReader extends RaplaXMLReader
{
    DynamicTypeImpl dynamicType;
    MultiLanguageName currentName = null;
    String currentLang = null;
    String constraintKey = null;
    AttributeImpl attribute = null;
    String annotationKey = null;
    boolean isAttributeActive = false;
    boolean isDynamictypeActive = false;
    HashMap<String,String> typeAnnotations = new LinkedHashMap<String,String>();
    HashMap<String,String> attributeAnnotations = new LinkedHashMap<String,String>();
	private HashMap<String, Map<Attribute,String>> unresolvedDynamicTypeConstraints = new HashMap<String, Map<Attribute,String>>();
	private PermissionReader permissionHandler;
	
    public DynamicTypeReader( RaplaContext context ) throws RaplaException
    {
        super( context );
        unresolvedDynamicTypeConstraints.clear();
        permissionHandler = new PermissionReader( context );
        addChildHandler( permissionHandler);
    }

    @Override
    public void processElement(
        String namespaceURI,
        String localName,
        RaplaSAXAttributes atts ) throws RaplaSAXParseException
    {
        if (localName.equals( "element" ))
        {
            String qname = getString( atts, "name" );
            String name = qname.substring( qname.indexOf( ":" ) + 1 );
            Assert.notNull( name );
            //System.out.println("NAME: " + qname + " Level " + level + " Entry " + entryLevel);

            if (!isDynamictypeActive)
            {
                isDynamictypeActive = true;
                typeAnnotations.clear();
                TimestampDates ts = readTimestamps( atts);
                dynamicType = new DynamicTypeImpl(ts.createTime, ts.changeTime);
                 if (atts.getValue( "id" )!=null)
                {
                    setId( dynamicType, atts );
                }
                else
                {
                    setNewId( dynamicType );
                }

                currentName = dynamicType.getName();
                dynamicType.setKey( name );
                
                // because the dynamic types refered in the constraints could be loaded after their first reference we resolve all prior unresolved constraint bindings to that type  when the type is loaded
                Map<Attribute,String> constraintMap = unresolvedDynamicTypeConstraints.get( name );
                if ( constraintMap != null)
                {
                	for (Map.Entry<Attribute,String> entry: constraintMap.entrySet())
                	{
                		Attribute att = entry.getKey();
                		String constraintKey = entry.getValue();
                		// now set the unresolved constraint, we need to ignore readonly check, because the type may be already closed
                		((AttributeImpl)att).setContraintWithoutWritableCheck(constraintKey, dynamicType);
                	}
                }
                unresolvedDynamicTypeConstraints.remove( name);
            }
            else
            {
                isAttributeActive = true;
                attribute = new AttributeImpl();
                currentName = attribute.getName();
                attribute.setKey( name );
                Assert.notNull( name, "key attribute cannot be null" );
                if (atts.getValue("id") != null)
                {
                    setId( attribute, atts );
                }
                else
                {
                    setNewId( attribute );
                }
                attributeAnnotations.clear();
            }
        }

        if (localName.equals( "permission" ))
        {
            permissionHandler.setContainer( dynamicType );
            delegateElement(
                    permissionHandler,
                    namespaceURI,
                    localName,
                    atts );
            return;
            
        }
        if (localName.equals( "constraint" ) && namespaceURI.equals( RAPLA_NS ))
        {
            constraintKey = atts.getValue( "name" );
            startContent();
        }
        
        if (localName.equals( "default" ))
        {
            startContent();
        }

        // if no attribute type is set
        if (localName.equals( "data" ) && namespaceURI.equals( RELAXNG_NS ) && attribute.getType().equals(
            AttributeImpl.DEFAULT_TYPE ))
        {
            String typeName = atts.getValue( "type" );
            if (typeName == null)
                throw createSAXParseException( "element relax:data is requiered!" );
            AttributeType type = AttributeType.findForString( typeName );
            if (type == null)
            {
            	getLogger().error( "AttributeType '" + typeName + "' not found. Using string.");
            	type = AttributeType.STRING;
            }
            attribute.setType( type );
        }

        if (localName.equals( "annotation" ) && namespaceURI.equals( RAPLA_NS ))
        {
            annotationKey = atts.getValue( "key" );
            Assert.notNull( annotationKey, "key attribute cannot be null" );
            startContent();
        }

        if (localName.equals( "name" ) && namespaceURI.equals( ANNOTATION_NS ))
        {
            startContent();
            currentLang = atts.getValue( "lang" );
            Assert.notNull( currentLang );
        }
    }

    private void addAnnotations( Annotatable annotatable, Map<String,String> annotations )
    {
        for (Iterator<Map.Entry<String,String>> it = annotations.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<String,String> entry = it.next();
            String key =  entry.getKey();
            String annotation =  entry.getValue();
            try
            {
                annotatable.setAnnotation( key, annotation );
            }
            catch (IllegalAnnotationException e)
            {
                getLogger().error("Can't parse annotation " + e.getMessage(), e);
            	//throw createSAXParseException( e.getMessage() );
            }
        }

    }

    @Override
    public void processEnd( String namespaceURI, String localName )
    throws RaplaSAXParseException
    {
        if (localName.equals( "element" ))
        {
            if (!isAttributeActive)
            {
                addAnnotations( dynamicType, typeAnnotations );
                setCurrentTranslations(dynamicType.getName());
                dynamicType.setResolver( store);
                if ( isBefore1_2())
                {
                    addNewPermissions( dynamicType );
                    addAnnotationsToSpecialAttributes( dynamicType);
                }
                add( dynamicType );
                // We ensure the dynamic type is not modified anymore
                //dynamicType.setReadOnly(  );
                isDynamictypeActive = false;
            }
            else
            {
                addAnnotations( attribute, attributeAnnotations );
                //System.out.println("Adding attribute " + attribute + " to " + dynamicType);
                setCurrentTranslations(attribute.getName());
                dynamicType.addAttribute( attribute );
                add( attribute );
                isAttributeActive = false;
            }
        }
        else if (localName.equals( "annotation" ) && namespaceURI.equals( RAPLA_NS ))
        {
        	String annotationValue = readContent().trim();
            if (isAttributeActive)
            {
                attributeAnnotations.put( annotationKey, annotationValue );
            }
            else
            {
                typeAnnotations.put( annotationKey, annotationValue );
            }
        }
        else if (localName.equals( "optional" ) && namespaceURI.equals( RELAXNG_NS ))
        {
            attribute.setOptional( true );
        }
        else if (localName.equals( "name" ) && namespaceURI.equals( ANNOTATION_NS ))
        {
            Assert.notNull( currentName );
            currentName.setName( currentLang, readContent() );
        }
        else if (localName.equals( "constraint" ) && namespaceURI.equals( RAPLA_NS ))
        {
            String content = readContent().trim();
            Object constraint = null;
            if (attribute.getConstraintClass( constraintKey ) == Category.class)
            {
                @SuppressWarnings("deprecation")
                boolean idContent = org.rapla.storage.OldIdMapping.isTextId(Category.TYPE, content );
                if (idContent)
                {
                    String id = getId( Category.TYPE, content );
                    constraint = store.tryResolve( id, Category.class );
                    if ( constraint == null)
                    {
                        getLogger().error("Can't resolve root category for " + dynamicType.getKey() + "." + attribute.getKey() + " id is " + id + " (" + content + ")");
                    }
                }
                else
                {
                    constraint = getCategoryFromPath( content );
                }
            }
            else if (attribute.getConstraintClass( constraintKey ) == DynamicType.class)
            {
                @SuppressWarnings("deprecation")
                boolean idContent = org.rapla.storage.OldIdMapping.isTextId( DynamicType.TYPE, content );
                if (idContent)
                {
					constraint = content.trim();
                }
                else
                {
                	String elementKey = content;
                	// check if the dynamic type refers to itself
                	if (elementKey.equals( dynamicType.getKey()))
                	{
                		constraint = dynamicType;
                	}
                	else
                	{
	                	// this only works if the dynamic type is already loaded
	                    constraint = getDynamicType(elementKey);
	                    // so we cache the contraint attributes in a map to be filled when the types are loaded
	                    if (constraint == null)
	                    {
	                    	Map<Attribute,String> collection = unresolvedDynamicTypeConstraints.get( elementKey);
	                    	if ( collection == null)
	                    	{
	                    		collection = new HashMap<Attribute,String>();
	                    		unresolvedDynamicTypeConstraints.put( elementKey, collection);
	                    	}
	                    	collection.put( attribute, constraintKey);
	                    }
                	}
                }
            }
            else if (attribute.getConstraintClass( constraintKey ) == Integer.class)
            {
                constraint = parseLong( content );
            }
            else if (attribute.getConstraintClass( constraintKey ) == Boolean.class)
            {
                constraint = parseBoolean( content );
            }
            else
            {
                constraint = content;
            }
            attribute.setConstraint( constraintKey, constraint );
        }
        
        if (localName.equals( "default" ) && namespaceURI.equals( RAPLA_NS ))
        {
            String content = readContent().trim();
            final Object defaultValue;
            final AttributeType type = attribute.getType();
            if (type == AttributeType.CATEGORY)
            {
                @SuppressWarnings("deprecation")
                boolean idContent = org.rapla.storage.OldIdMapping.isTextId(Category.TYPE, content );
                if (idContent)
                {
                    defaultValue = resolve( Category.TYPE, content );
                }
                else
                {
                    defaultValue = getCategoryFromPath( content );
                }
            }
            else 
            {
                Object value;
                try 
                {
                    value = parseAttributeValue(attribute, content);
                } 
                catch (RaplaException e) 
                {
                    value = null;
                }
                defaultValue = value;
            }
            attribute.setDefaultValue(defaultValue );
        }
    }

    @SuppressWarnings("deprecation")
    private void addNewPermissions(DynamicTypeImpl dynamicType) throws RaplaSAXParseException {
        String classificationType = dynamicType.getAnnotation(DynamicTypeAnnotations.KEY_CLASSIFICATION_TYPE);
        if (classificationType.equals(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_RESOURCE) || classificationType.equals(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_PERSON)) {
            {
                Permission permission = dynamicType.newPermission();
                permission.setAccessLevel( Permission.READ_TYPE);
                dynamicType.addPermission( permission);
            }
            {
                Permission permission = dynamicType.newPermission();
                permission.setAccessLevel( Permission.ALLOCATE_CONFLICTS);
                dynamicType.addPermission( permission);
            }
            String registerer = Permission.GROUP_REGISTERER_KEY;
            addNewPermissionWithGroup(dynamicType, Permission.CREATE, registerer, false);
        } else if (classificationType.equals(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_RESERVATION)) {
            {
                Permission permission = dynamicType.newPermission();
                permission.setAccessLevel( Permission.READ_TYPE);
                dynamicType.addPermission( permission);
            }
            addNewPermissionWithGroup(dynamicType, Permission.READ, Permission.GROUP_CAN_READ_EVENTS_FROM_OTHERS, true);
            addNewPermissionWithGroup(dynamicType, Permission.CREATE, Permission.GROUP_CAN_CREATE_EVENTS, true);
        }
    }

    private void addNewPermissionWithGroup(DynamicTypeImpl dynamicType, Permission.AccessLevel acceassLevel, String groupKey, boolean create) throws RaplaSAXParseException {
        Category userGroups = getSuperCategory().getCategory(Permission.GROUP_CATEGORY_KEY);
        Category group = userGroups.getCategory(groupKey);
        if ( group == null)
        {   
            if ( !create )
            {
                return;
            }
            Date date = getReadTimestamp();
            CategoryImpl category = new CategoryImpl(date, date);
            setNewId( category );
            category.setKey( groupKey );
            category.getName().setName("en", groupKey);
            userGroups.addCategory(category);
            group = category;
        }
        Permission permission = dynamicType.newPermission();
        permission.setAccessLevel( acceassLevel);
        permission.setGroup( group);
        dynamicType.addPermission( permission);
    }

    
    private void addAnnotationsToSpecialAttributes(DynamicTypeImpl dynamicType) throws RaplaSAXParseException {
        try
        {
            for (Attribute att:dynamicType.getAttributeIterable())
            {
                String key = att.getKey();
                if (key.equals("color"))
                {
                    att.setAnnotation(AttributeAnnotations.KEY_COLOR, "true");
                }
                else if (key.equals("categorization"))
                {
                    att.setAnnotation(AttributeAnnotations.KEY_CATEGORIZATION, "true");
                }
            }
        }
        catch (IllegalAnnotationException ex)
        {
            throw createSAXParseException(ex.getMessage(), ex );
        }
    }

    
   
}
