/**
 * 
 */
package app;

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
		
		contentPane = new JPanel();
		this.setContentPane(contentPane);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		
	}
	
}
