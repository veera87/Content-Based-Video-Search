import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

/**
 * 
 * @author Prashant
 *	Stope the video from playing
 */
public class QueryStopButtonEvent extends MouseAdapter {
	JPanel panel;
	
	public QueryStopButtonEvent(JPanel panel) {
		this.panel = panel;
	}
	public QueryStopButtonEvent() {}

	@SuppressWarnings("deprecation")
	@Override
	public synchronized void mouseClicked(MouseEvent arg0) {
		if(null != UI.queryAudioThread) {
			UI.queryAudioThread.stop();
			UI.queryVideoThread.stop();
			UI.queryVideoPaused = false;
			this.panel.removeAll();
			this.panel.repaint();
		}		
	}
}
