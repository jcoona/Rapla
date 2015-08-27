package org.rapla.gui.toolkit;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.rapla.framework.Disposable;

/** Disposes an object on window close. Must be added as a WindowListener
 to the target window*/
final public class DisposingTool extends WindowAdapter {
    Disposable m_objectToDispose;
    public DisposingTool(Disposable objectToDispose) {
        m_objectToDispose = objectToDispose;
    }
    public void windowClosed(WindowEvent e) {
        m_objectToDispose.dispose();
    }
}
