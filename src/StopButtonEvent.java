import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

/**
 * 
 * @author Prashant
 *	Stope the video from playing
 */
public class StopButtonEvent extends MouseAdapter {
	JPanel panel;
	
	public StopButtonEvent(JPanel panel) {
		this.panel = panel;
	}
	public StopButtonEvent() {}

	@SuppressWarnings("deprecation")
	@Override
	public synchronized void mouseClicked(MouseEvent arg0) {
		if(null != UI.audioThread) {
			UI.audioThread.stop();
			UI.videoThread.stop();
			UI.videoPaused = false;
			this.panel.removeAll();
			this.panel.repaint();
			UI.slider.setValue(0);
		}		
	}
}
