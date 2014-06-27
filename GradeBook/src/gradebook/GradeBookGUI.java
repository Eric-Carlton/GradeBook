package gradebook;

import gradebookdata.User;

import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.JFrame;

public class GradeBookGUI extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7101904035096706463L;
	LoginPage login;
	Dashboard dashboard;
	CardLayout layout;
	Container cards;
	
	public GradeBookGUI()
	{
		cards = getContentPane();
		layout = new CardLayout();
		cards.setLayout(layout);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("GradeBook Assignment Management System");

		login = new LoginPage();
		dashboard = new Dashboard(this);

		cards.add(login, "Login");
		cards.add(dashboard, "Dashboard");
		pack();
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		GradeBookGUI gui = new GradeBookGUI();
		gui.setVisible(true);
	}
	
	public void showDashboardWithUser(User user){
		CardLayout cardLayout = (CardLayout) cards.getLayout();
		dashboard.setUser(user);
		cardLayout.show(cards, "Dashboard");
	}

}
