import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class PlayButtonEvent extends MouseAdapter {
	JPanel panel;
	
	public PlayButtonEvent() {}
	public PlayButtonEvent(JPanel panel) {
		this.panel = panel;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void mouseClicked(MouseEvent arg0) {
		this.panel.repaint();
		//Playing the video after stopping or for the first time
		if(!UI.videoPaused) {
			UI.audioThread = new PlayAudio(UI.audioFileName);
			UI.videoThread = new PlayRGBVideo(UI.videoFileName, this.panel);
			
			try {
				UI.videoThread.join();
				UI.audioThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			UI.videoThread.start();
			UI.audioThread.start();
		}
		//Resuming the video after pausing
		else {
			UI.videoThread.resume();
			UI.audioThread.resume();
			UI.videoPaused = false;
		}
	}
}
