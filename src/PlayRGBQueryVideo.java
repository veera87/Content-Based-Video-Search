import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PlayRGBQueryVideo extends Thread{
	String file;
	JPanel contentPane;
	public PlayRGBQueryVideo() {}
	
	public PlayRGBQueryVideo(String file, JPanel contentPane) {
		this.file = file;
		this.contentPane = contentPane;
	}
	
	@Override
	public void run() {
		this.contentPane.removeAll();
		BufferedImage img = new BufferedImage(Constants.WIDTH, Constants.HEIGHT, BufferedImage.TYPE_INT_RGB);

		InputStream is;
		
		JLabel label;
		try {
			File file = new File(this.file);
			is = new FileInputStream(file);

			long len = file.length();
			int frameLength = Constants.WIDTH*Constants.HEIGHT * 3;
			byte[] bytes = new byte[frameLength];
			int totalRead = 0;

			while(totalRead < len) {
				long st = System.currentTimeMillis();
				int numRead = 0;
				int offset = 0;
				while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
					totalRead += numRead;
				}

				int ind = 0;		
				for (int y = 0; y < Constants.HEIGHT; y++) {
					for (int x = 0; x < Constants.WIDTH; x++) {
						int r = bytes[ind];
						int g = bytes[ind + Constants.HEIGHT * Constants.WIDTH];
						int b = bytes[ind + Constants.HEIGHT * Constants.WIDTH * 2];
						
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						
						img.setRGB(x, y, pix);
						ind++;
					}
				}
		
				label = new JLabel(new ImageIcon(img));
				
				this.contentPane.add( label, BorderLayout.CENTER );
				this.contentPane.repaint();
				this.contentPane.updateUI();
				long et = System.currentTimeMillis();
				Thread.sleep(40-(et-st) < 0 ? 0 : 40-(et-st));
			}
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
