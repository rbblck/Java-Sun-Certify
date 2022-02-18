package suncertify.presentation.gui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * This class extends <code>AbstarctAction</code> and is used to close the 
 * program completely.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class CloseProgramAction extends AbstractAction {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 2545585382482720890L;

    /**
     * The default constructor stores the name / value pairs for display of any 
     * button, menu item or tool bar button that may use it.
     */
    public CloseProgramAction() {
        this.putValue(Action.NAME, "Exit");
        this.putValue(Action.SHORT_DESCRIPTION, "Close program.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
    
}
