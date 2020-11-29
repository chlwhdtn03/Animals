package gui;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import data.PointerBoolean;

@SuppressWarnings("serial")
public class AnimalsLED extends JCheckBox {
	
	PointerBoolean target;
	
	public AnimalsLED(PointerBoolean target) {
		this.target = target;
		setBorderPainted(false);
		for(ActionListener actionListener : getActionListeners())
			removeActionListener(actionListener);
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
	
		g.setColor(getParent().getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if(target.isValue())
			g.setColor(Color.green);
		else
			g.setColor(Color.red);
		g.fillArc(0, 0, getHeight(), getHeight(), 0, 360);
	}
		
	

}
