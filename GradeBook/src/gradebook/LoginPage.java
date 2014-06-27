package gradebook;

import gradebookclient.Client;
import gradebookdata.Packet;
import gradebookdata.User;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginPage extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6324795034886765855L;
	private static final TableLayout manager = new TableLayout();
	BufferedImage bgImage;
	JLabel uNameLbl;
	JLabel pWordLbl;
	JLabel header;
	JLabel footer;
	JLabel right;
	JLabel aboveExit;
	JButton loginBtn;
	JButton createAcctBtn;
	JButton closeBtn;
	JTextField uNameTxt;
	JPasswordField pWordTxt;
	JLabel firstName,lastName,desiredUsername,pass,confirmPass;
	JTextField firstNameTxt, lastNameTxt,desiredUsernameTxt;
	JPasswordField passTxt, confirmPassTxt;

	public LoginPage(){
		super(manager);

		initializeComponents();
		addComponents();

		bgImage = null;
		try{
			bgImage = ImageIO.read(new File("resources/loginbg.jpg"));
		}
		catch (IOException e) {
			System.out.println("IOException");
		}

		repaint();

		//setBackground(Color.WHITE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == loginBtn)
		{
			tryLogin();
		}
		else if(e.getSource() == createAcctBtn){
			changeToCreateAcctScreen();
		}	
		else if(e.getSource() == closeBtn)
			System.exit(0);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(bgImage != null)
			g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
	}




	private void initializeComponents(){

		header = new JLabel("GradeBook", JLabel.CENTER);
		header.setPreferredSize(new Dimension(400, 275));
		header.setFont(new Font("Serif", Font.BOLD, 48));
		header.setForeground(Color.white);
		uNameLbl = new JLabel("Username");
		uNameLbl.setPreferredSize(new Dimension(100, 20));
		uNameLbl.setForeground(Color.white);
		uNameTxt = new JTextField();
		uNameTxt.setPreferredSize(new Dimension(100,20));
		pWordLbl = new JLabel("Password");
		pWordLbl.setPreferredSize(new Dimension(100,20));
		pWordLbl.setForeground(Color.white);
		pWordTxt = new JPasswordField();
		pWordTxt.setPreferredSize(new Dimension(100,20));
		loginBtn = new JButton("Login");
		loginBtn.setPreferredSize(new Dimension(100,20));
		loginBtn.addActionListener(this);
		createAcctBtn = new JButton("Create Account");
		createAcctBtn.setPreferredSize(new Dimension(150,20));
		createAcctBtn.addActionListener(this);
		aboveExit = new JLabel(" ");
		aboveExit.setPreferredSize(new Dimension(1, 50));
		closeBtn = new JButton("Exit");
		closeBtn.addActionListener(this);
		closeBtn.setPreferredSize(new Dimension(100,20));
		footer = new JLabel(" ", JLabel.CENTER);
		footer.setPreferredSize(new Dimension(400,275));
		footer.setFont(new Font("Serif", Font.BOLD, 24));
		right = new JLabel(" ");
		right.setPreferredSize(new Dimension (300,20));
	}

	private void addComponents(){
		add(header, "LR3W.");
		add(uNameLbl, "R");
		add(uNameTxt, "LR");
		add(right,".");
		add(pWordLbl, "R");
		add(pWordTxt,"LR.");
		add(loginBtn, "R");
		add(createAcctBtn, "LR.");
		add(aboveExit,"LR.");
		add(closeBtn, "3W.");
		add(footer,"LR");
	}

	private void tryLogin(){
		String username = uNameTxt.getText().trim();
		String password = new String(pWordTxt.getPassword()).trim();
		if(username.length() > 0 && password.length() > 0)
		{
			//create a client to log in
			try{
				Client login = new Client(new Packet(1, username + ";" + md5(password)));

				if (login.succeeded()) {
					//parse data into meaningful strings
					String[] temp = login.getResponse().getData().split(";");

					//wrong number of fields received
					if (temp.length != 3) {
						System.out.println("Error receiving information from server!");
						System.exit(-1);
					}
					//create a user based on good data
					User curUser = new User(temp[0], temp[1], Integer.parseInt(temp[2]));
					GradeBookGUI gui = (GradeBookGUI) SwingUtilities.getWindowAncestor(this);
					gui.showDashboardWithUser(curUser);
				}
				//login did not succeed
				else
				{
					//tell user that login unsuccessful
					//give option to create account
					int reply = JOptionPane.showConfirmDialog(this, "Those credentials don't match any records. Would you like to create an account?", "Login Error", JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.YES_OPTION)
						changeToCreateAcctScreen();
				}
				//destroy login
				login = null;
			}
			catch(Exception e){
				JOptionPane.showMessageDialog(this, "Cannot send information to server", "Server Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		//no text entered
		else
		{
			JOptionPane.showMessageDialog(this, "You must provide a username and password to log in", "Missing required input", JOptionPane.ERROR_MESSAGE);
		}

	}

	private void changeToCreateAcctScreen(){
		Object[] components = setupCreateAcctComponents();

		int result = JOptionPane.showConfirmDialog(null, components, "Create Account", JOptionPane.OK_CANCEL_OPTION);
		
		if(result == JOptionPane.OK_OPTION){
			String password = new String(passTxt.getPassword()).trim();
			String confirmPassword = new String(confirmPassTxt.getPassword()).trim();
			if(firstNameTxt.getText().trim().length() > 0 && lastNameTxt.getText().trim().length() > 0 && desiredUsernameTxt.getText().trim().length() > 0 && passTxt.getPassword().toString().trim().length() > 0){
				if(password.equals(confirmPassword)){
					Client created =createAccount(firstNameTxt.getText().trim(),lastNameTxt.getText().trim(),desiredUsernameTxt.getText().trim(), password);
					if(created.succeeded()){
						JOptionPane.showMessageDialog(this, "User your new username and password to log in!", "Account Creation Successful", JOptionPane.INFORMATION_MESSAGE);
					}
					else{
						JOptionPane.showMessageDialog(this, created.getResponse().getData(),"Error",JOptionPane.ERROR_MESSAGE);
					}
				}else{
					JOptionPane.showMessageDialog(this, "Passwords don't match!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}else{
				JOptionPane.showMessageDialog(this, "All fields are required to create an account", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private static String md5(String password) {
		String md5 = null;

		if (null == password)
			return null;

		try {

			// Create MessageDigest object for MD5
			MessageDigest digest = MessageDigest.getInstance("MD5");

			// Update input string in message digest
			digest.update(password.getBytes(), 0, password.length());

			// Converts message digest value in base 16 (hex)
			md5 = new BigInteger(1, digest.digest()).toString(16);

		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException - MD5");
			return null;
		}
		return md5;
	}

	private static Client createAccount(String first, String last, String user, String pass){
		try{
			Client create = new Client(new Packet(0, first + ";" + last + ";"
					+ user + ";" + md5(pass)));
			return create;
		}catch(Exception e){
			return null;
		}


	}
	
	private Object[] setupCreateAcctComponents(){
		firstName = new JLabel("First Name: ");
		lastName = new JLabel("Last Name: ");
		desiredUsername = new JLabel("Desired Username: ");
		pass = new JLabel("Password: ");
		confirmPass = new JLabel("Confirm Password: ");
		firstNameTxt = new JTextField();
		lastNameTxt = new JTextField();
		desiredUsernameTxt = new JTextField();
		passTxt = new JPasswordField();
		confirmPassTxt = new JPasswordField();
		Object[] components = new Object[10];
		components[0] = firstName;
		components[1] = firstNameTxt;
		components[2] = lastName;
		components[3] = lastNameTxt;
		components[4] = desiredUsername;
		components[5] = desiredUsernameTxt;
		components[6] = pass;
		components[7] = passTxt;
		components[8] = confirmPass;
		components[9] = confirmPassTxt;

		return components;
	}
}
