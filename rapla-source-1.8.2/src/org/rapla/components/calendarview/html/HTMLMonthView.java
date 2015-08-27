/*--------------------------------------------------------------------------*
 | Copyright (C) 2006  Christopher Kohlhaas                                 |
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

package org.rapla.components.calendarview.html;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.rapla.components.calendarview.Block;
import org.rapla.components.calendarview.Builder;
import org.rapla.components.util.DateTools;

public class HTMLMonthView extends AbstractHTMLView {
    public final static int ROWS = 6; //without the header row
    public final static int COLUMNS = 7;
    HTMLSmallDaySlot[] slots;
    
    public Collection<Block> getBlocks() {
        ArrayList<Block> list = new ArrayList<Block>();
        for (int i=0;i<slots.length;i++) {
            list.addAll(slots[i]);
        }
        return Collections.unmodifiableCollection( list );
    }
    
    protected boolean isEmpty(int column) {
        for ( int i=column;i < slots.length;i+=7 ) {
            if (!slots[i].isEmpty() ) {
                return false;
            }
        }
        return true;
    }
    
    public void rebuild() {
        //      we need to clone the calendar, because we modify the calendar object int the getExclude() method 
        Calendar counter = (Calendar) blockCalendar.clone(); 
        
        // calculate the blocks
        Iterator<Builder> it= builders.iterator();
        final Date startDate = getStartDate();
		while (it.hasNext()) {
           Builder b= it.next();
           b.prepareBuild(startDate,getEndDate());
        }
        slots = new HTMLSmallDaySlot[ daysInMonth ];
        for (int i=0;i<slots.length;i++) {
            slots[i] = new HTMLSmallDaySlot(String.valueOf( i + 1));
        }
        
        it= builders.iterator();
        while (it.hasNext()) {
           Builder b= it.next();
           if (b.isEnabled()) { b.build(this); }
        }
        int lastRow = 0;
        HTMLSmallDaySlot[][] table = new HTMLSmallDaySlot[ROWS][COLUMNS];
        counter.setTime(startDate);
        int firstDayOfWeek = getFirstWeekday();
		if ( counter.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek)
        {
			counter.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
			if ( counter.getTime().after( startDate))
			{
				counter.add(Calendar.DATE, -7);
			}
        }
		Date time = counter.getTime();
		int offset = (int) DateTools.countDays(counter.getTime(),startDate);
        // add headers
     
	    counter.setTime(startDate);
        for (int i=0; i<daysInMonth; i++) {
            int column = (offset + i) % 7;
            int row = (counter.get(Calendar.DATE) + 6 - column ) /  7;
            table[row][column] = slots[i];
            lastRow = row;
            slots[i].sort();
            counter.add(Calendar.DATE,1);
        }
        
        StringBuffer result = new StringBuffer();
        
		// Rapla 1.4: Show month and year in monthview
		SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMMM yyyy", locale);
		monthYearFormat.setTimeZone( getTimeZone() );
		result.append("<h2 class=\"title\">" + monthYearFormat.format(startDate) + "</h2>\n");
        
        result.append("<table class=\"month_table\">\n");
        result.append("<tr>\n");

        counter.setTime( time );
        for (int i=0;i<COLUMNS;i++) {
            if (isExcluded(i)) {
            	counter.add(Calendar.DATE, 1);
            	continue;
            }

            int weekday = counter.get(Calendar.DAY_OF_WEEK);
        	if ( counter.getTime().equals( startDate))
        	{
        		offset = i;
        	}
            result.append("<td class=\"month_header\" width=\"14%\">");
            result.append("<nobr>");
            String name = getWeekdayName(weekday);
            result.append(name);
            result.append("</nobr>");
            result.append("</td>");
            counter.add(Calendar.DATE, 1);
        }
        result.append("\n</tr>");
        
        for (int row=0; row<=lastRow; row++) {
            boolean excludeRow = true;
            // calculate if we can exclude the row
            for (int column = 0; column<COLUMNS; column ++) {
                if ( table[row][column] != null && !isExcluded( column )) {
                    excludeRow = false;
                }
            }
            if ( excludeRow )
                continue;
            result.append("<tr>\n");
            for (int column = 0; column<COLUMNS; column ++) {
                if ( isExcluded( column )) {
                    continue;
                }
                HTMLSmallDaySlot slot = table[row][column];
                if ( slot == null ) {
                    result.append("<td class=\"month_cell\" height=\"40\"></td>\n");
                } else {
                    result.append("<td class=\"month_cell\" valign=\"top\" height=\"40\">\n");
                    slot.paint( result );
                    result.append("</td>\n");
                }
            }
            result.append("</tr>\n");
        }
        result.append("</table>");
        m_html = result.toString();
    }

    public void addBlock(Block block,int col,int slot) {
        checkBlock( block );
        blockCalendar.setTime(block.getStart());
        int day = blockCalendar.get(Calendar.DATE);
        slots[day-1].putBlock( block );
    }
   

}
