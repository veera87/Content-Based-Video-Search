import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 		
 * @author Prashant
 *	Self-explanatory method. Used to pause a video
 */
public class PauseButtonEvent extends MouseAdapter {
	
	public PauseButtonEvent() {}
	
	@SuppressWarnings("deprecation")
	@Override
	public synchronized void mouseClicked(MouseEvent arg0) {
		if(!UI.videoPaused) {
			UI.audioThread.suspend();
			UI.videoThread.suspend();
			UI.videoPaused = true;
		}
	}
}
