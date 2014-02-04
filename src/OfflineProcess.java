import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class OfflineProcess {
	
	public static void main(String[] args) {
		
		PrintWriter fileWriter = null;
		int i = 0;
		
		// Opening the Parameters file to write
		try {
			fileWriter = new PrintWriter(Constants.PARAMETERS_FILE, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// Reading all the Input files and extracting the HIntensity and writing to file
		for(i = 0; i < Constants.FILE_NAMES.length; i++) {
			ArrayList<MatchParameters> frameParametersList = new ArrayList<MatchParameters>();
			String filePath = Constants.BASE_PATH + Constants.FILE_NAMES[i] + Constants.VIDEO_FILE_EXTENSION;
			String fileName = Constants.FILE_NAMES[i];
			
			// Getting the hIntensity List
			Compute.getParametersList(frameParametersList, filePath);
			
			// Writing to file
			writeHIntensityToFile(frameParametersList, fileName, fileWriter);
		}
		
		// Closing the HIntensity file
		fileWriter.close();
	}
	
	static void writeHIntensityToFile(ArrayList<MatchParameters> frameParametersList, String fileName, PrintWriter fileWriter) {
		int i = 0, j = 0;
		
		// Writing the fileName and the number of lines as the 1st line
		fileWriter.println(fileName + "," + frameParametersList.size());
		
		// Writing the Parameter values - one line per frame (first 360-H and 256-Y)
		for(i = 0; i < frameParametersList.size(); i++) {
			
			// Writing the Hue first - 360 values
			for(j = 0; j < Constants.H_QUANTIZATION_FACTOR; j++) {
				fileWriter.print(frameParametersList.get(i).h[j] + ",");
			}
			
			//fileWriter.println("==");
			
			// Writing the Y values - 256 values
			for(j = 0; j < Constants.Y_QUANTIZATION_FACTOR; j++) {
				if(j < Constants.Y_QUANTIZATION_FACTOR - 1)
					fileWriter.print(frameParametersList.get(i).y[j] + ",");
				else
					fileWriter.print(frameParametersList.get(i).y[j]);
			}
			
			if(Constants.motion) {
				if(i < frameParametersList.size() - 1) {
					fileWriter.print(",");
					int size = frameParametersList.get(i).motion.length;
					for(j = 0; j < size ; j++) {
						fileWriter.print(frameParametersList.get(i).motion[j]);
						if(j != size-1)
							fileWriter.print(",");
					}
				}
			}
			fileWriter.println();
		}
	}
}
