/*--------------------------------------------------------------------------*
 | Copyright (C) 2006 Gereon Fassbender, Christopher Kohlhaas               |
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
package org.rapla.components.calendarview;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;
public interface CalendarView
{
    public TimeZone getTimeZone();
    
    /** returns the first Date that will be displayed in the calendar */
    public Date getStartDate();
    /** returns the last Date that will be displayed in the calendar */
    public Date getEndDate();
    
    /** sets the calendarview to the selected date*/
    public void setToDate(Date weekDate);

    /** This method removes all existing blocks first. 
     * Then it calls the build method of all added builders, so that they can add blocks into the CalendarView again.
     * After all blocks are added the Calendarthat repaints the screen. 
     */
    public void rebuild();
    
    /** Adds a block. You can optionaly specify a slot, if the day-view supports multiple slots (like in the weekview).
     *  If the selected slot does not exist it will be created. This method is usually called by the builders.
    */
    public void addBlock(Block bl, int column,int slot);
    
    /** returns a collection of all the added blocks 
     * @see #addBlock*/
    Collection<Block> getBlocks();
}


