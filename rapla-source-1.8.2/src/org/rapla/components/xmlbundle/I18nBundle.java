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
package org.rapla.components.xmlbundle;

import java.util.Locale;
import java.util.MissingResourceException;

import javax.swing.ImageIcon;

/**The interface provides access to a resourcebundle that
   can be defined in XML or as an java-object.
Example Usage:
<pre>
   I18nBundle i18n = serviceManager.lookup(I18nBundle.class);
   i18n.getString("yes"); // will get the translation for yes.
</pre>
*/

public interface I18nBundle {
    /** same as <code>format(key,new Object[] {obj1});</code>
        @see #format(String,Object[])
     */
    String format(String key,Object obj1) throws MissingResourceException;
    /** same as <code>format(key,new Object[] {obj1, obj2});</code>
        @see #format(String,Object[])
     */
    String format(String key,Object obj1,Object obj2) throws MissingResourceException;
    /** same as
        <code>
        (new MessageFormat(getString(key))).format(obj);
        </code>
        @see java.text.MessageFormat
    */
    String format(String key,Object... obj) throws MissingResourceException;

    /** returns the specified icon from the image-resource-file.
        @throws MissingResourceException if not found or can't be loaded.
     */
    ImageIcon getIcon(String key) throws MissingResourceException;

    /** returns the specified string from the selected resource-file.
     * Same as getString(key,getLocale())
     *  @throws MissingResourceException if not found or can't be loaded.
    */
    String getString(String key) throws MissingResourceException;
    
    /** returns the specified string from the selected resource-file for the specified locale
    @throws MissingResourceException if not found or can't be loaded.
     */
    String getString( String key, Locale locale);

    /** @return the selected language. */
    String getLang();

    /** @return the selected Locale. */
    Locale getLocale();
}
