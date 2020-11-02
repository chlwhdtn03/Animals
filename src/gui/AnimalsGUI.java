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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import animals.Animals;
import data.Player;
import server.AnimalServer;
import util.Log;

public class AnimalsGUI extends JFrame {
	
	public JTextArea textarea;
	public JScrollPane scrollbar;
	public JList<String> playerlist;
	public JScrollPane playerlist_scrollbar;
	
	private Font font = new Font("맑은 고딕", Font.PLAIN, 14);
	
	private List<String> addresslist = new ArrayList<String>();
	private int address_count = 0;
	
	public DefaultListModel<String> playerlist_model = new DefaultListModel<String>();
	
	public AnimalsGUI() {
		super("애니멀즈 Build. " + Animals.build);
		
		setLayout(new BorderLayout());		
		
		textarea = new JTextArea();
		scrollbar = new JScrollPane(textarea);
		
		playerlist = new JList<String>(playerlist_model);
		playerlist_scrollbar = new JScrollPane(playerlist);
		
		textarea.setBorder(null);
		scrollbar.setBorder(null);
		
		playerlist.setBorder(null);
		playerlist_scrollbar.setBorder(getTitleBorder());
		
		textarea.setFont(font);
		textarea.setEditable(false);
		textarea.setLineWrap(true);
		textarea.setOpaque(true);
		
		playerlist.setFont(font);
		
		AnimalsScrollbarUI scroll_ui = new AnimalsScrollbarUI();
		
		scrollbar.getVerticalScrollBar().setUI(scroll_ui);
		playerlist_scrollbar.getVerticalScrollBar().setUI(scroll_ui);
		
		
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
		
		add(playerlist_scrollbar, BorderLayout.EAST);
		
		setSize(700,300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void refreshPlayerList() {
		playerlist_model.clear();
		for(Player p : Animals.onlinePlayers) {
			playerlist_model.addElement(p.getName());
		}
	}
	
	private TitledBorder getTitleBorder() {
		TitledBorder result = new TitledBorder("접속자");
		result.setTitleFont(font);
		return result;
	}

}
