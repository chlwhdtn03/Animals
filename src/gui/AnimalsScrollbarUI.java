package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class AnimalsScrollbarUI extends BasicScrollBarUI {
	
	private JButton createZeroButton() {
		JButton jbutton = new JButton();
		jbutton.setPreferredSize(new Dimension(0, 0));
		jbutton.setMinimumSize(new Dimension(0, 0));
		jbutton.setMaximumSize(new Dimension(0, 0));
		return jbutton;
	}
	
	@Override
	protected JButton createDecreaseButton(int orientation) {
		return createZeroButton();
	}
	
	@Override
	protected JButton createIncreaseButton(int orientation) {
		return createZeroButton();
	}
	
	@Override
	protected void configureScrollBarColors() {
		this.thumbColor = new Color(200, 200, 200, 50);
		this.minimumThumbSize = new Dimension(0, 50);
		this.maximumThumbSize = new Dimension(0, 50);
		this.thumbDarkShadowColor = new Color(200, 200, 200);
	}

}
