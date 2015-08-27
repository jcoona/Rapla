/*--------------------------------------------------------------------------*
 | Copyright (C) 2014 Christopher Kohlhaas, Bettina Lademann                |
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
package org.rapla.gui;
import org.rapla.framework.RaplaException;
import org.rapla.gui.toolkit.MenuInterface;

public interface MenuFactory
{
    public MenuInterface addObjectMenu(MenuInterface menu, MenuContext context, String afterId) throws RaplaException;
    public void addReservationWizards(MenuInterface menu, MenuContext context,String afterId) throws RaplaException;
}









