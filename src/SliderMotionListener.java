import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class SliderMotionListener extends MouseAdapter {
	private int MAX_SKIP = 65536;
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(UI.play != null) {
			long pos = Constants.WIDTH * Constants.HEIGHT * 3 * UI.slider.getValue();
			int audioPos = PlaySound.EXTERNAL_BUFFER_SIZE * UI.slider.getValue();
			
			try {
				//Skipping video
				UI.videoThread.fileAccess.seek(pos);
				UI.videoThread.totalRead = pos;
				
				//Skipping audio
				UI.audioThread.playSound.audioInputStream.reset();
				UI.audioThread.playSound.dataLine.flush();
				while (audioPos > 0) {
					UI.audioThread.playSound.audioInputStream.skip(audioPos > MAX_SKIP ? MAX_SKIP : audioPos);
					audioPos -= MAX_SKIP;
				}		
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			UI.play.mouseClicked(null);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		UI.pause.mouseClicked(null);
	}
}
