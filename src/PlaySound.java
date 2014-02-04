import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlaySound {

	private BufferedInputStream waveStream;

	//private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
	public static final int EXTERNAL_BUFFER_SIZE = 3528; // 128Kb
	
	AudioInputStream audioInputStream; 
	long frameLength;
	SourceDataLine dataLine;

	/**
	 * CONSTRUCTOR
	 */
	public PlaySound(BufferedInputStream waveStream) {
		this.waveStream = waveStream;
	}

	public void play() throws PlayWaveException {

		try {
			audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);
		} catch (UnsupportedAudioFileException e1) {
			throw new PlayWaveException(e1);
		} catch (IOException e1) {
			throw new PlayWaveException(e1);
		}
		
		// Obtain the information about the AudioInputStream
		AudioFormat audioFormat = audioInputStream.getFormat();
		Info info = new Info(SourceDataLine.class, audioFormat);

		// opens the audio channel
		dataLine = null;
		try {
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open(audioFormat, PlaySound.EXTERNAL_BUFFER_SIZE);
		} catch (LineUnavailableException e1) {
			throw new PlayWaveException(e1);
		}

		// Starts the music :P
		dataLine.start();

		int readBytes = 0;
		byte[] audioBuffer = new byte[PlaySound.EXTERNAL_BUFFER_SIZE];
		audioInputStream.mark(Integer.MAX_VALUE);
		frameLength = audioInputStream.getFrameLength();
		try {
			while (readBytes != -1) {
				readBytes = audioInputStream.read(audioBuffer, 0,
						audioBuffer.length);
				
				if (readBytes >= 0) {
					dataLine.write(audioBuffer, 0, readBytes);
				}
			}
			//System.out.println(audioInputStream.getFrameLength() + "; Total: " + total);
		} catch (IOException e1) {
			throw new PlayWaveException(e1);
		} finally {
			// plays what's left and and closes the audioChannel
			//dataLine.drain();
			dataLine.close();
		}

	}
}
