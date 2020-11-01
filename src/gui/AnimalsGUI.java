package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

import animals.Animals;
import util.Log;

public class AnimalsGUI extends JFrame {
	
	public JTextArea textarea;
	public JScrollPane scrollbar;
	
	private List<String> addresslist = new ArrayList<String>();
	private int address_count = 0;
	
	public AnimalsGUI() {
		super("애니멀즈 Build. " + Animals.build);
		
		setLayout(new BorderLayout());		
		
		textarea = new JTextArea();
		scrollbar = new JScrollPane(textarea);
		
		textarea.setBorder(null);
		scrollbar.setBorder(null);
		
		textarea.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		textarea.setEditable(false);
		textarea.setLineWrap(true);
		textarea.setOpaque(true);
		
		scrollbar.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			
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
		});
		
		
		JLabel connectDescription = new JLabel("이게 뭔 컴퓨터죠?");
		try {
			connectDescription = new JLabel("주소창에 '" + InetAddress.getLocalHost().getHostAddress() + "' 를 입력하세요");
			Log.info("사용 가능한 IP를 확인합니다.");
			Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
			
			NetworkInterface e;
			Enumeration<InetAddress> a;
			InetAddress addr;
			for (; n.hasMoreElements();) {
				e = n.nextElement();
				a = e.getInetAddresses();
				for (; a.hasMoreElements();) {
					addr = a.nextElement();
					Log.info(e.getDisplayName() + " - " + addr.getHostAddress());		
					addresslist.add(addr.getHostAddress());
				}
			}
			
			connectDescription.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		} catch (UnknownHostException | SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connectDescription.setHorizontalAlignment(SwingUtilities.CENTER);
		
		connectDescription.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(address_count >= addresslist.size())
					address_count = 0;
				((JLabel) e.getSource()).setText("주소창에 '" + addresslist.get(address_count++) + "' 를 입력하세요");
			}
		});
		

		add(connectDescription, BorderLayout.NORTH);
		add(scrollbar, BorderLayout.CENTER);
		
		setSize(700,300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

}
