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
package org.rapla.plugin.tableview.internal.server;

import org.rapla.framework.Configuration;
import org.rapla.framework.Container;
import org.rapla.framework.PluginDescriptor;
import org.rapla.plugin.tableview.TableViewExtensionPoints;
import org.rapla.plugin.tableview.internal.AppointmentCounter;
import org.rapla.plugin.tableview.internal.AppointmentEndDate;
import org.rapla.plugin.tableview.internal.AppointmentNameColumn;
import org.rapla.plugin.tableview.internal.AppointmentStartDate;
import org.rapla.plugin.tableview.internal.EventCounter;
import org.rapla.plugin.tableview.internal.PersonColumn;
import org.rapla.plugin.tableview.internal.ReservationLastChangedColumn;
import org.rapla.plugin.tableview.internal.ReservationNameColumn;
import org.rapla.plugin.tableview.internal.ReservationStartColumn;
import org.rapla.plugin.tableview.internal.ResourceColumn;
import org.rapla.plugin.tableview.internal.TableViewPlugin;
import org.rapla.server.RaplaServerExtensionPoints;
import org.rapla.server.ServerServiceContainer;

public class TableViewServerPlugin  implements PluginDescriptor<ServerServiceContainer>
{

    public void provideServices(final ServerServiceContainer container, Configuration config)
    {
        if ( !config.getAttributeAsBoolean("enabled", TableViewPlugin.ENABLE_BY_DEFAULT) )
        	return;

        container.addContainerProvidedComponent( RaplaServerExtensionPoints.HTML_CALENDAR_VIEW_EXTENSION,ReservationHTMLTableViewFactory.class);
        container.addContainerProvidedComponent( RaplaServerExtensionPoints.HTML_CALENDAR_VIEW_EXTENSION,AppointmentHTMLTableViewFactory.class);
        
        
		addReservationTableColumns(container);
        addAppointmentTableColumns(container);

        //Summary rows
        container.addContainerProvidedComponent(TableViewExtensionPoints.RESERVATION_TABLE_SUMMARY, EventCounter.class);
		container.addContainerProvidedComponent(TableViewExtensionPoints.APPOINTMENT_TABLE_SUMMARY, AppointmentCounter.class);
        
        
    }

	protected void addAppointmentTableColumns(final Container container) {
		container.addContainerProvidedComponent(TableViewExtensionPoints.APPOINTMENT_TABLE_COLUMN, AppointmentNameColumn.class);
        container.addContainerProvidedComponent(TableViewExtensionPoints.APPOINTMENT_TABLE_COLUMN, AppointmentStartDate.class);
        container.addContainerProvidedComponent(TableViewExtensionPoints.APPOINTMENT_TABLE_COLUMN, AppointmentEndDate.class);
        container.addContainerProvidedComponent(TableViewExtensionPoints.APPOINTMENT_TABLE_COLUMN, ResourceColumn.class);
        container.addContainerProvidedComponent(TableViewExtensionPoints.APPOINTMENT_TABLE_COLUMN, PersonColumn.class);
	}

	protected void addReservationTableColumns(final Container container) {
		container.addContainerProvidedComponent(TableViewExtensionPoints.RESERVATION_TABLE_COLUMN, ReservationNameColumn.class);
        container.addContainerProvidedComponent(TableViewExtensionPoints.RESERVATION_TABLE_COLUMN, ReservationStartColumn.class);
        container.addContainerProvidedComponent(TableViewExtensionPoints.RESERVATION_TABLE_COLUMN, ReservationLastChangedColumn.class); 
	}
}

