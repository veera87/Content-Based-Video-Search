import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class QueryPlayButtonEvent extends MouseAdapter {
	JPanel panel;
	
	public QueryPlayButtonEvent() {}
	public QueryPlayButtonEvent(JPanel panel) {
		this.panel = panel;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void mouseClicked(MouseEvent arg0) {
		this.panel.repaint();
		//Playing the video after stopping or for the first time
		if(!UI.queryVideoPaused) {
			UI.queryVideoThread = new PlayRGBQueryVideo(UI.queryVideoFileName, this.panel);
			UI.queryAudioThread = new PlayAudio(UI.queryAudioFileName);
			
			try {
				UI.queryVideoThread.join();
				UI.queryAudioThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			UI.queryVideoThread.start();
			UI.queryAudioThread.start();
		}
		//Resuming the video after pausing
		else {
			UI.queryVideoThread.resume();
			UI.queryAudioThread.resume();
			UI.queryVideoPaused = false;
		}
	}
}
