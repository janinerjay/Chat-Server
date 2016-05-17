/**
 * This class represents a single client. This will only work if a server
 * is running.
 * Last Edited: 03/17/16
 * @author Ian Jacobs, Melkis Espinal, Ally Colisto, and Janine Jay
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class Client extends JPanel {
	//instance variables
	private JButton b1, emojiButton;
	private static JTextField textField;
	private static JTextPane textArea;
	private static BufferedReader in;
	private static PrintWriter out;
	private static Socket socket = null;
	private static JFrame frame;
	private static HashMap<String,java.net.URL> emojiMap;

	/**
	 * Constructor
	 */
	public Client(){
		super(new GridBagLayout());

		emojiButton = new JButton("Emoji List");

		emojiMap = new HashMap<String,java.net.URL>();
		this.initializeEmojiMap();

		//create options from the drop down color selectors
		String[] colors1 = {"Red", "Blue", "Green", "Black", "White", "Yellow", "Orange", "Pink", "Purple"};

		//create the combo boxes for selecting colors
		//color one s for the backgrounds
		final JComboBox<String> color1 = new JComboBox<String>(colors1);
		color1.setSelectedIndex(3);
		color1.setToolTipText("Choose Background Color");

		//create the combo boxed for selecting colors
		//color2 is used for the text color
		final JComboBox<String> color2 = new JComboBox<String>(colors1);
		color2.setSelectedIndex(2);
		color2.setToolTipText("Choose Text Color");

		//create the area to display chat
		textArea = new JTextPane();
		textArea.setEditable(false);
		textArea.setFont(new Font("Chalkboard", Font.ITALIC, 16));
		textArea.setBackground(Color.black);
		textArea.setForeground(Color.green);
		textArea.setVisible(true);

		//scroll pane used to scroll through chat
		JScrollPane scrollPane = new JScrollPane(textArea);

		//using grid-bag are organizational outline
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		//pad borders around the text boxes
		c.insets = new Insets(5, 5, 5, 5);
		//create the scroll pane and attach to the text-area
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		add(scrollPane, c);

		//create test field where text is entered
		textField = new JTextField(10);
		textField.setBackground(Color.black);
		textField.setForeground(Color.green);
		textField.setHorizontalAlignment(JTextField.CENTER);

		//listener
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				out.println(textField.getText());

				if(textField.getText().equals("ADIOS")){
					try {
						if(socket != null )socket.close();
						if(out != null) out.close();
						if(in != null) in.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.exit(0);//close the client
				}
				textField.setText("");
			}
		});

		c.fill = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_END; //bottom of space
		c.insets = new Insets(10,0,0,0);  //top padding
		add(textField, c);

		//Exit button
		b1 = new JButton("Exit");
		b1.setVerticalTextPosition(AbstractButton.CENTER);
		b1.setHorizontalTextPosition(AbstractButton.RIGHT);
		b1.setVerticalAlignment(1);
		b1.setMnemonic(KeyEvent.VK_M);
		b1.setToolTipText("Button closes this client");

		//action listener that when clicked the exit
		//button will close the client
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if(JOptionPane.showConfirmDialog(frame, 
						"Are you sure to close this client?", "Terminate Client.", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					if(out != null){
						out.println("ADIOS");//tell the server you quit the application
					}
					try {
						if(socket != null )socket.close();
						if(out != null) out.close();
						if(in != null) in.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.exit(0);
				}
			}
		});

		//create action listener for the combo box
		//that selects the color of the background
		color1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String color = color1.getSelectedItem().toString();

				switch(color){
				case "Blue" : textArea.setBackground(Color.BLUE); textField.setBackground(Color.BLUE); break;
				case "Black" : textArea.setBackground(Color.BLACK); textField.setBackground(Color.BLACK); break;
				case "Red" : textArea.setBackground(Color.RED); textField.setBackground(Color.RED); break;
				case "Green" : textArea.setBackground(Color.GREEN); textField.setBackground(Color.GREEN); break;
				case "White" : textArea.setBackground(Color.WHITE); textField.setBackground(Color.WHITE); break;
				case "Yellow" : textArea.setBackground(Color.YELLOW); textField.setBackground(Color.YELLOW); break;
				case "Orange" : textArea.setBackground(Color.ORANGE); textField.setBackground(Color.ORANGE); break;
				case "Pink" : textArea.setBackground(Color.PINK); textField.setBackground(Color.PINK); break;
				case "Purple" : textArea.setBackground(Color.MAGENTA); textField.setBackground(Color.MAGENTA); break;
				}
			}
		});

		//action listener mapped to combo box color2
		//it is used to select the color of the text or
		//the  foreground.
		color2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String color = color2.getSelectedItem().toString();

				switch(color){
				case "Blue" : textArea.setForeground(Color.BLUE);  textField.setForeground(Color.BLUE); break;
				case "Black" : textArea.setForeground(Color.BLACK); textField.setForeground(Color.BLACK); break;
				case "Red" : textArea.setForeground(Color.RED); textField.setForeground(Color.RED); break;
				case "Green" : 	textArea.setForeground(Color.GREEN); textField.setForeground(Color.GREEN); break;
				case "White" : textArea.setForeground(Color.WHITE); 	textField.setForeground(Color.WHITE); break;
				case "Yellow" : textArea.setForeground(Color.YELLOW); 	textField.setForeground(Color.YELLOW); break;
				case "Orange" : textArea.setForeground(Color.ORANGE);  textField.setForeground(Color.ORANGE); break;
				case "Pink" : textArea.setForeground(Color.PINK); textField.setForeground(Color.PINK); break;
				case "Purple" : textArea.setForeground(Color.MAGENTA); textField.setForeground(Color.MAGENTA); break;
				}
			}
		});

		emojiButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,showEmojis(), "Emoji List",
						JOptionPane.INFORMATION_MESSAGE);

				textField.requestFocus();
			}
		});

		//add to panel
		add(b1);
		add(emojiButton);
		add(color1);
		add(color2);
	}

	/**
	 * This method shows the list of emojis so you know what to type.
	 */
	public JPanel showEmojis(){
		Set<String> keys = emojiMap.keySet();
		String[] keysArray = new String[keys.size()];
		keys.toArray(keysArray);
		JPanel panel = new JPanel(new GridLayout(10,2));
		
		for(String s: keysArray){
			JLabel label = new JLabel(s);
			JPanel p = new JPanel(new GridLayout(1,2));
			ImageIcon imageIcon = new ImageIcon(
					new ImageIcon(emojiMap.get(s)).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
			p.add(label);
			p.add(new JLabel(imageIcon));
			panel.add(p);
		}
		return panel;
		}

		/**
		 * Shows a dialog and waits for the user to input a name.
		 * @return: name gotten from user.
		 */
		public static String getUserName() {
			return JOptionPane.showInputDialog(frame, "Enter your name:","Get User Name",
					JOptionPane.PLAIN_MESSAGE);
		}

		/**
		 * Shows a dialog and waits for the user to input the Server's IP.
		 * @return: the Server's IP Address.
		 */
		public static String getServerIPAddress() {
			return JOptionPane.showInputDialog(frame,"Server's IP Address:","Get Server's IP",
					JOptionPane.QUESTION_MESSAGE);
		}

		/**
		 * Method to connect to the server.
		 */
		private static void connectToServer(){
			// Make connection and initialize streams
			String serverAddress = getServerIPAddress();
			try {
				socket = new Socket(serverAddress, Server.PORT);
			}
			catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame,"Connection refused. Server must be off.", "Socket Error",
						JOptionPane.ERROR_MESSAGE);
			}

			if(socket != null){//if you got the socket
				try {
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);
				} catch (IOException e) {e.printStackTrace();}
			}

			// Process all messages from server, using our protocol.
			while (!socket.isClosed()) {
				String line = null;
				try {
					if(in != null){
						line = in.readLine();

						if (line.startsWith("GETNAME")) {
							out.println(getUserName());//get the user name from user
							textField.requestFocus();//so you can type without having to click it
						} 
						else if (line.startsWith("MESSAGE")) {
							//"appends" the new message to the end of the text area.
							try {
								Document doc = textArea.getDocument();
								doc.insertString(doc.getLength(), line.substring(8) + "\n", null);
							} catch(BadLocationException exc) {exc.printStackTrace();}
							textArea.setCaretPosition(textArea.getDocument().getLength());
							textArea.revalidate();
							showEmoji(line); //checks if there is an emoji sign and show it/them.
						}
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Main.
		 */
		public static void main(String[] args) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Client.createAndShowGUI();
				}
			});
			Client.connectToServer();//connect to server*/
		}

		/**
		 * Initialize the Frame and its components
		 */
		private static void createAndShowGUI() {
			//Create and set up the window.
			Client newContentPane = new Client();
			newContentPane.setOpaque(true);
			frame = new JFrame("iJam Client");
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setContentPane(newContentPane);
			frame.pack();
			frame.setVisible(true);
			frame.setSize(500, 700);
			JLabel emptyLabel = new JLabel("PORT 1518");
			emptyLabel.setPreferredSize(new Dimension(500, 700));
			frame.getContentPane().add(emptyLabel);	

			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					if(JOptionPane.showConfirmDialog(frame, 
							"Are you sure to close this client?", "Terminate Client.", 
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
						if(out != null){
							out.println("ADIOS");//inform the server this client is leaving.
						}
						try {
							if(socket != null )socket.close();
							if(out != null) out.close();
							if(in != null) in.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						System.exit(0);//end the client.
					}
				}
			});
		}

		/**
		 * Method that searches through the message and finds any thing in the form of
		 * (emoji) where emoji is the name of the emoji and () is the way that the client
		 * knows that this is an emoji.
		 * @param line: message
		 */
		public static void showEmoji(String line){
			String message = line.substring(8);//remove the "MESSAGE part"

			//stores the list of emojis in the message sent
			ArrayList<String> emojiList = new ArrayList<String>();
			while(message.indexOf('(') > -1 && message.indexOf(')') > -1){
				int start = message.indexOf('(');
				int end = message.indexOf(')');
				String key = message.substring(start, end+1);
				emojiList.add(key);
				message = message.substring(end+1);
			}

			//displays the emojis in the text area only if they exist in the hash map
			for(String s: emojiList){
				if(emojiMap.containsKey(s)){
					textArea.setSelectionStart(textArea.getText().length());
					textArea.setSelectionEnd(textArea.getText().length());
					//inserts a new instance of the JLabel containing the emoji
					textArea.insertComponent(new JLabel(new ImageIcon(emojiMap.get(s))));
					try {
						Document doc = textArea.getDocument();
						doc.insertString(doc.getLength(),"\n", null);
					} catch(BadLocationException exc) {exc.printStackTrace();}
					//takes the text area to the end of the scrolling pane
					textArea.setCaretPosition(textArea.getDocument().getLength());
					textArea.revalidate();
				}
			}
		}
		
		/**
		 * Initializes the hash map with its corresponding names and URLs path 
		 * to get the emoji.
		 */
		public void initializeEmojiMap(){
			java.net.URL allyURL = this.getClass().getResource("images/ally.png");
			emojiMap.put("(ally)", allyURL);

			java.net.URL computerURL = this.getClass().getResource("images/computer.png");
			emojiMap.put("(computer)", computerURL);

			java.net.URL coolURL = this.getClass().getResource("images/cool.png");
			emojiMap.put("(cool)", coolURL);

			java.net.URL duncanURL = this.getClass().getResource("images/duncan.png");
			emojiMap.put("(duncan)", duncanURL);

			java.net.URL happyURL = this.getClass().getResource("images/happy.png");
			emojiMap.put("(happy)", happyURL);

			java.net.URL happytongueURL = this.getClass().getResource("images/happytongue.png");
			emojiMap.put("(happytongue)", happytongueURL);

			java.net.URL ianURL = this.getClass().getResource("images/ian.png");
			emojiMap.put("(ian)", ianURL);

			java.net.URL janineURL = this.getClass().getResource("images/janine.png");
			emojiMap.put("(janine)", janineURL);

			java.net.URL melkisURL = this.getClass().getResource("images/melkis.png");
			emojiMap.put("(melkis)", melkisURL);

			java.net.URL networkingURL = this.getClass().getResource("images/networking.png");
			emojiMap.put("(networking)", networkingURL);

			java.net.URL sadURL = this.getClass().getResource("images/sad.png");
			emojiMap.put("(sad)", sadURL);

			java.net.URL serverClientURL = this.getClass().getResource("images/serverClient.png");
			emojiMap.put("(serverclient)", serverClientURL);

			java.net.URL sickURL = this.getClass().getResource("images/sick.png");
			emojiMap.put("(sick)", sickURL);

			java.net.URL terminalURL = this.getClass().getResource("images/terminal.png");
			emojiMap.put("(terminal)", terminalURL);
			
			java.net.URL rubyURL = this.getClass().getResource("images/ruby.png");
			emojiMap.put("(ruby)", rubyURL);
			
			java.net.URL blakeURL = this.getClass().getResource("images/blake.png");
			emojiMap.put("(blake)", blakeURL);
			
			java.net.URL hoffmanURL = this.getClass().getResource("images/hoffman.png");
			emojiMap.put("(hoffman)", hoffmanURL);
			
			java.net.URL duncanBikeURL = this.getClass().getResource("images/duncanbike.png");
			emojiMap.put("(duncanbike)", duncanBikeURL);
			
			java.net.URL awesomeURL = this.getClass().getResource("images/awesome.png");
			emojiMap.put("(awesome)", awesomeURL);
			
			java.net.URL duncanRunURL = this.getClass().getResource("images/duncanrun.png");
			emojiMap.put("(duncanrun)", duncanRunURL);
			
			java.net.URL duncanWindowsURL = this.getClass().getResource("images/duncanwindows.png");
			emojiMap.put("(duncanwindows)", duncanWindowsURL);
			
			java.net.URL gradeURL = this.getClass().getResource("images/grade.png");
			emojiMap.put("(grade)", gradeURL);
			
			java.net.URL ianCarURL = this.getClass().getResource("images/iancar.png");
			emojiMap.put("(iancar)", ianCarURL);
			
			java.net.URL javaLogoURL = this.getClass().getResource("images/javalogo.png");
			emojiMap.put("(javalogo)", javaLogoURL);
			
			java.net.URL nerdURL = this.getClass().getResource("images/nerd.png");
			emojiMap.put("(nerd)", nerdURL);
			
			java.net.URL packetSniffURL = this.getClass().getResource("images/packetsniff.png");
			emojiMap.put("(packetsniff)", packetSniffURL);
			
			java.net.URL redbullURL = this.getClass().getResource("images/redbull.png");
			emojiMap.put("(redbull)", redbullURL);
			
			java.net.URL robotURL = this.getClass().getResource("images/robot.png");
			emojiMap.put("(robot)", robotURL);
			
			java.net.URL sleepURL = this.getClass().getResource("images/sleep.png");
			emojiMap.put("(sleep)", sleepURL);
			
			java.net.URL windowDevilURL = this.getClass().getResource("images/windowdevil.png");
			emojiMap.put("(windowdevil)", windowDevilURL);
		}
	}