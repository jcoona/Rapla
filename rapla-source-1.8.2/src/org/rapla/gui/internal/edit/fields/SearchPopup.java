package org.rapla.gui.internal.edit.fields;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class SearchPopup extends JFrame implements KeyListener, WindowListener {

	private static final long serialVersionUID = 1L;
	
	public SearchPopup(String header){
		super(header);
		addWindowListener(this);
		addKeyListener(this);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setFocusable(true);
		setSize(400, 600);
		setLocationRelativeTo(null); //sets popup to show in the middle of the screen
	}
	
	public void fullscreen(){
		maximize();
		setUndecorated(true);
	}
	
	public void maximize(){
		setSize(-1, -1);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	@Override
	public void keyPressed(KeyEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

}
