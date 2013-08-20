/**
 * 
 */
package app;

import javax.swing.SwingWorker;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import framework.Clue;
import framework.EntityRecogniser;

/**
 * @author Ben Griffiths
 *
 */
public class EntityRecogniserTask extends SwingWorker<Void, Void> {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Clue clue;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private EntityRecogniser entityRecogniser;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private DisplayPanel displayPanel;

	/* This constructor will be used to instantiate a task that recognises entities in a clue */
	public EntityRecogniserTask(Clue clue, EntityRecogniser entityRecogniser, DisplayPanel displayPanel) {
		super(); // call SwingWorker Default constructor
		this.setClue(clue);
		this.setEntityRecogniser(entityRecogniser);
		this.setDisplayPanel(displayPanel);
	}
    
	/*
     * Main task. Executed in background thread. 
     */
    @Override
    public Void doInBackground() {
        
        int progress = 0;
        //Initialize progress property of SwingWorker
        setProgress(0);

    int combinedLengthOfQueries = this.getEntityRecogniser().getClueFragments().size();
    int taskLength = 100 / combinedLengthOfQueries;

        while (progress < 100) {
            
	/* extract entities for next clue fragment */
	
            setProgress(progress + taskLength); // one query has been completed
        }
        return null;
    }

    /*
     * Executed on EDT
     */
    @Override
    public void done() {
        this.getDisplayPanel().getMessageArea().append("Entity Recognition complete!\n");
    }
}
