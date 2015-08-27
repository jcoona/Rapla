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

import java.util.Stack;

import org.rapla.components.util.Assert;
import org.rapla.components.util.xml.RaplaSAXAttributes;
import org.rapla.components.util.xml.RaplaSAXParseException;
import org.rapla.entities.Annotatable;
import org.rapla.entities.Category;
import org.rapla.entities.IllegalAnnotationException;
import org.rapla.entities.MultiLanguageName;
import org.rapla.entities.internal.CategoryImpl;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;

public class CategoryReader extends RaplaXMLReader
{
    MultiLanguageName currentName = null;
    Annotatable currentAnnotatable = null;
    String currentLang = null;
    Stack<CategoryImpl> categoryStack = new Stack<CategoryImpl>();
    CategoryImpl superCategory;
    String annotationKey = null;
    CategoryImpl lastProcessedCategory = null;
    boolean readOnlyThisCategory;

    public CategoryReader( RaplaContext context ) throws RaplaException
    {
        super( context );
        superCategory = getSuperCategory();
        currentName = superCategory.getName();

    }

    public void setReadOnlyThisCategory( boolean enable )
    {
        readOnlyThisCategory = enable;
    }

    @Override
    public void processElement(
        String namespaceURI,
        String localName,
        RaplaSAXAttributes atts ) throws RaplaSAXParseException
    {
        if (localName.equals( "category" ) && namespaceURI.equals( RAPLA_NS ))
        {
            String key = atts.getValue( "key" );
            Assert.notNull( key );
            TimestampDates ts = readTimestamps( atts);
            CategoryImpl category = new CategoryImpl(ts.createTime, ts.changeTime );
            category.setKey( key );
            currentName = category.getName();
            currentAnnotatable = category;
            if (atts.getValue( "id" )!=null)
            {
                setId( category, atts );
            }
            else
            {
                setNewId( category );
            }

            
            if (!readOnlyThisCategory)
            {
                if ( !categoryStack.empty() )
                {
                    Category parent =  categoryStack.peek();
                    parent.addCategory( category);
                }
                else
                {
                    String parentId = atts.getValue( "parentid"); 
                    if (  parentId!= null)
                    {
                    	if (parentId.equals(Category.SUPER_CATEGORY_ID)) {
                    	    superCategory.addCategory( category);
                    	    category.putEntity("parent", superCategory);
                    	} else {
                    		String parentIdN = getId( Category.TYPE, parentId);
                    	    category.putId("parent", parentIdN);
                    	}
                    } 
                    else 
                    {
                        superCategory.addCategory( category);
                    }
                }
            }
            categoryStack.push( category );
           /*
            Category test = category;
            String output = "";
            while (test != null)
            {
                output = "/" + test.getKey() + output;
                test = test.getParent();
            }
            //            System.out.println("Storing category " + output );
             */
        }

        if (localName.equals( "name" ) && namespaceURI.equals( ANNOTATION_NS ))
        {
            startContent();
            currentLang = atts.getValue( "lang" );
            Assert.notNull( currentLang );
        }

        if (localName.equals( "annotation" ) && namespaceURI.equals( RAPLA_NS ))
        {
            annotationKey = atts.getValue( "key" );
            Assert.notNull( annotationKey, "key attribute cannot be null" );
            startContent();
        }
    }

    @Override
    public void processEnd( String namespaceURI, String localName )
        throws RaplaSAXParseException
    {
        if (localName.equals( "category" ))
        {
            // Test Namespace uris here for possible xerces bug
            if (namespaceURI.equals( "" ))
            {
                throw createSAXParseException( " category namespace empty. Possible Xerces Bug. Download a newer version of xerces." );
            }

            CategoryImpl category =  categoryStack.pop();
            setCurrentTranslations( category.getName());
            if (!readOnlyThisCategory)
            {
               add( category );
            }
            lastProcessedCategory = category;
        }
        else if (localName.equals( "name" ) && namespaceURI.equals( ANNOTATION_NS ))
        {
            String translation = readContent();
            currentName.setName( currentLang, translation );
        }
        else if (localName.equals( "annotation" ) && namespaceURI.equals( RAPLA_NS ))
        {
            try
            {
            	String annotationValue = readContent().trim();
                currentAnnotatable.setAnnotation( annotationKey,  annotationValue );
            }
            catch (IllegalAnnotationException ex)
            {
            }
        }
    }

    public CategoryImpl getCurrentCategory()
    {
        return lastProcessedCategory;
    }

}
