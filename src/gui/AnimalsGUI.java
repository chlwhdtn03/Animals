package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.sun.org.apache.bcel.internal.generic.NEW;

import animals.Animals;
import data.Chat;
import data.Player;
import packet.AnimalsPacket;
import server.AnimalServer;
import server.ConnectionListener;
import util.Log;

@SuppressWarnings("serial")
public class AnimalsGUI extends JFrame {
	
	public JTextArea textarea;
	public JTextField chat_field;
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
		
		JPanel console_Panel = new JPanel(new BorderLayout());
		
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
		AnimalsScrollbarUI scroll_ui2 = new AnimalsScrollbarUI();
		
		scrollbar.getVerticalScrollBar().setUI(scroll_ui);
		playerlist_scrollbar.getVerticalScrollBar().setUI(scroll_ui2);
		
		chat_field = new JTextField();
		chat_field.setOpaque(true);
		chat_field.setBorder(null);
		chat_field.setBackground(SystemColor.lightGray);
		chat_field.setFont(font);
		
		chat_field.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = chat_field.getText();
				if(msg.trim().isEmpty())
					return;
				
				chat_field.setText("");
				
				ConnectionListener.sendAll(new AnimalsPacket("notice", new Chat(msg)));
				Log.info("[방장] " + msg);
				msg = null;
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
				
				if(e.isLoopback()) // 루프백 주소 필요 X 
					continue;
				if(e.isVirtual()) // 가상환경 주소 필요 X
					continue;
				
				a = e.getInetAddresses();
				for (; a.hasMoreElements();) {
					addr = a.nextElement();
					if(addr.isLinkLocalAddress())
						continue;
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
		add(playerlist_scrollbar, BorderLayout.EAST);
		
		add(connectDescription, BorderLayout.NORTH);
		
		console_Panel.add(scrollbar, BorderLayout.CENTER);
		console_Panel.add(chat_field, BorderLayout.SOUTH);
		
		add(console_Panel, BorderLayout.CENTER);
		
		
		
		
		setSize(700,300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		playerlist_scrollbar.setPreferredSize(playerlist_scrollbar.getSize());
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
