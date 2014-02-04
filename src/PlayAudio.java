import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlayAudio extends Thread {
	String filename;
	PlaySound playSound;
	
	public PlayAudio() {}
	
	public PlayAudio(String file) {
		this.filename = file;
	}
	
	@Override
	public void run() {
		// opens the inputStream
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		// initializes the playSound Object
		playSound = new PlaySound(new BufferedInputStream(inputStream));

		// plays the sound
		try {
			playSound.play();
		} catch (PlayWaveException e) {
			e.printStackTrace();
			return;
		}
	}
}
