/**
 * 
 */
package app;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import framework.UserInterface;

/**
 * @author Ben Griffiths
 *
 */
@SuppressWarnings("serial")
public class GraphicalUserInterface extends JFrame implements UserInterface {
	private JPanel contentPane;
	@Override
	public void createAndShow() {
		this.setTitle("Semantic Crossword Solver");
		
		Dimension d = new Dimension(550, 700);
		
		contentPane = new JPanel();
		contentPane.setPreferredSize(d);
		this.setContentPane(contentPane);
		
		this.setPreferredSize(d);
		this.setMinimumSize(d);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
	}
	
}
