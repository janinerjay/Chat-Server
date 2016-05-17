/**
 * This class represents a multi-threaded Server using Swing as GUI. 
 * A lot of clients can connect to this server and have a different
 * thread assigned to them.
 * Last Edited: 03/17/16
 * @author Ian Jacobs, Melkis Espinal, Ally Colisto, and Janine Jay
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;

public class Server extends JPanel {
	//instance variables
	public static final int PORT = 1518;
	private JButton start;
	private static JTextArea log;
	private static ServerSocket serverSocket;
	private ArrayList<String> userNames;
	private ArrayList<PrintWriter> printWriters;
	private static Thread serverThread;
	private static boolean saveToLog = true;

	/**
	 * Constructor.
	 */
	public Server(){
		super(new GridBagLayout());
		//initialize
		userNames = new ArrayList<String>();
		printWriters = new ArrayList<PrintWriter>();

		//log text area configuration
		log = new JTextArea();
		log.setEditable(false);
		log.setFont(new Font("Chalkboard", Font.ITALIC, 16));
		log.setLineWrap(true);
		log.setWrapStyleWord(true);
		log.setBackground(Color.white);
		log.setForeground(Color.black);

		//scroll pane used to scroll through chat
		JScrollPane scrollPane = new JScrollPane(log);

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

		//start button configuration
		start = new JButton("Start");
		start.setVerticalTextPosition(AbstractButton.CENTER);
		start.setHorizontalTextPosition(AbstractButton.RIGHT);
		start.setVerticalAlignment(1);
		start.setMnemonic(KeyEvent.VK_M);
		start.setToolTipText("Button closes the server");
		start.setBounds(450,650, 10, 30);

		//button listener
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if(start.getText().equals("Start")){
					saveToLog = true;
					start.setText("Stop");//change to stop
					log.append("Server Started...\n");//log it
					//try to initialize the server socket
					try {serverSocket = new ServerSocket(PORT);} 
					catch (IOException e1) {
						log.append("Error: " + e1.getMessage() + "\n");
						e1.printStackTrace();
					}
					startServer();//start the server
				}
				else{
					start.setText("Start");//change to start
					log.append("Server Stopped...\n");//log it
					serverThread.stop();//stop the thread. Deprecated, but works.
					try {serverSocket.close();}//try to close the socket
					catch (IOException e1) {
						log.append("Error: " + e1.getMessage() + "\n");
						e1.printStackTrace();
					}
					if(JOptionPane.showConfirmDialog(null, 
							"Do you want to save a log file? This log \nwill erase.", "Export Log", 
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
						if(saveToLog){
							exportLog();
							saveToLog = false;
						}
					}
					log.setText("");//clear the log
					log.revalidate();
				}
			}
		});
		//add start button to panel.
		add(start);
	}

	/**
	 * 
	 */
	private static void exportLog(){
		try {
			PrintWriter out = new PrintWriter(new FileWriter("ServerLog.txt"));
			out.println(log.getText());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to start the server in a separate thread to avoid 
	 * UI blocking.
	 */
	private void startServer(){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try{
					while(!serverSocket.isClosed()){
						try {
							new ThreadClass(serverSocket.accept()).start();
						} 
						catch (IOException ioe) {
							log.append("Error: " + ioe.getMessage() + "\n");
							ioe.printStackTrace();
						}
					}
				} finally{
					try {serverSocket.close();} 
					catch (IOException ioe1) {
						log.append("Error: " + ioe1.getMessage() + "\n");
						ioe1.printStackTrace();
					}
				}
			}
		};
		serverThread = new Thread(r);
		serverThread.start();//start server
	}

	/**
	 * This method displays all user names in the log.
	 */
	private void displayUserNames(){
		synchronized(userNames){//has to be synchronized
			log.append("Current members: ");
			for(String name: userNames){
				log.append(name + ", ");
			}
			log.append("\n");
		}
	}

	/**
	 * Initialize the Frame and its components
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		final JFrame frame = new JFrame("iJam Server");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Server newContentPane = new Server();
		newContentPane.setOpaque(true); 
		frame.setContentPane(newContentPane);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(400, 500);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				log.append("Server Stopped...\n");//log it
				if(serverThread != null){
					serverThread.stop();//stop the thread. Deprecated, but works.
					try {serverSocket.close();}//try to close the socket
					catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				if(saveToLog){
					if(JOptionPane.showConfirmDialog(null, 
							"Do you want to save a log file? This log \nwill erase.", "Export Log", 
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
						exportLog();
					}
				}
				System.exit(0);//exit the server
			}

		});
	}

	/**
	 * Main
	 */
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/**
	 * Thread class for each incoming client.
	 */
	private class ThreadClass extends Thread{
		//instance variables
		private String name;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		/**
		 * Constructor.
		 * @param socket: socket from client.
		 */
		public ThreadClass(Socket socket){this.socket = socket;}

		@Override
		public void run(){
			try {
				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				//Ask for a name to the client. Keep asking until a valid one is input.
				while (!serverSocket.isClosed()) {
					out.println("GETNAME");
					name = in.readLine().toUpperCase();
					if (name == null) {return;}

					synchronized (userNames) {//must be locked
						if (!userNames.contains(name) && !name.isEmpty() && !name.toUpperCase().equals("SERVER")) {
							userNames.add(name);
							int last = userNames.size()-1;
							log.append(userNames.get(last) + " just joined.\n");
							displayUserNames();
							break;
						}
					}
				}
				//add the print writer to the array-list
				printWriters.add(out);

				//for each client, inform them that this client joined.
				for (PrintWriter writer : printWriters) {
					writer.println("MESSAGE " + "SERVER" + ": " + name + " joined.");
				}

				// Accept messages from this client and send to others.
				while (true) {
					String input = in.readLine();
					if (input == null) {return;}

					//for each client, send the message
					for (PrintWriter writer : printWriters) {
						writer.println("MESSAGE " + name + ": " + input);
					}

					//if one of the clients sends this word, their client GUI will
					//close and the server will inform everyone that they left and
					//remove them from the user names and print writers array-lists
					if(input.equals("ADIOS")){
						synchronized(userNames){
							int index = userNames.indexOf(name);
							userNames.remove(name);//remove it from user names
							printWriters.remove(index);//remove from print writers
							//inform others that this guy left.
							for (PrintWriter writer : printWriters) {
								writer.println("MESSAGE " + "SERVER: " + name + " left.");
							}
						}
						log.append(name + " left.\n");//log it
						displayUserNames();//display current members
					}
				}
			} catch (IOException e) {
				log.append("Error: " + e.getMessage() + "\n");
				e.printStackTrace();
			} finally {
				// This client is leaving so remove its name and its print
				// writer from the lists, and close its socket.
				if (name != null) {userNames.remove(name);}
				if (out != null) {printWriters.remove(out);}
				try {socket.close();} 
				catch (IOException e) {
					log.append("Error: " + e.getMessage() + "\n");
					e.printStackTrace();
				}
			}
		}
	}
}