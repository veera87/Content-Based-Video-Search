import java.util.ArrayList;
import java.util.List;


public class Constants {
	static final String BASE_PATH = "C:\\Multimedia\\all_files\\";
	static final String PARAMETERS_FILE = "Parameters.txt";
	static final String VIDEO_FILE_EXTENSION = ".rgb";
	static final String AUDIO_FILE_EXTENSION = ".wav";
	static final String[] FILE_NAMES = { "soccer1", "soccer2", "soccer3", "soccer4",
											"talk1", "talk2", "talk3", "talk4",
											"wreck1", "wreck2", "wreck3", "wreck4" 
										};
	//static final String[] FILE_NAMES = { "soccer3", "soccer2", "soccer1", "soccer4"};
	//static final String[] FILE_NAMES = { "wreck2"};
	static final int H_QUANTIZATION_FACTOR = 360;
	static final int Y_QUANTIZATION_FACTOR = 256;
	static final int WIDTH = 352;
	static final int HEIGHT = 288;
	static final int MB_SIZE = 10;
	static final int P = 16;
	static final boolean motion = true;
	static final int NO_OF_FILES = 12;
	public static final int MAX_INT = Integer.MAX_VALUE;
	public static final int NO_OF_MOTION_VECTORS = 9;
	public static boolean initialized = false;

	public static List<SearchCoords> coordList = new ArrayList<SearchCoords>();
	
	/*
	 * Sorting flag values based on different parameters
	 * 1 - AVG(3*M + 2*H + 1*Y)
	 * 2 - M
	 * 3 - H
	 * 4 - Y
	 */
	public static int SORT_FLAG = 1; 
	
	
	public static int HISTOGRAM_HIEGHT = 100;
}
