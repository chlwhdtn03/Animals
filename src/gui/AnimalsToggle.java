package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import data.PointerBoolean;

@SuppressWarnings("serial")
public class AnimalsToggle extends JToggleButton {
	
	PointerBoolean target;
	
	public AnimalsToggle(PointerBoolean target) {
		this.target = target;
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				target.setValue(!target.isValue());
			}
		});
		setFocusable(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		if(target.isValue()) {
			g.setColor(Color.GREEN);
			g.fillRect(getWidth()/2, 0, getWidth(), getHeight());
		} else {
			g.setColor(Color.red);
			g.fillRect(0, 0, getWidth()/2, getHeight());
		}
	}
	
	
}
