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
package org.rapla.components.treetable;

import javax.swing.CellEditor;
import javax.swing.JComponent;
import javax.swing.JTree;

public interface TreeTableEditor extends CellEditor {
    JComponent getEditorComponent(JTree tree,Object value,boolean isSelected,int row);
    int getGap(JTree tree, Object value, boolean isSelected, int row);
}

