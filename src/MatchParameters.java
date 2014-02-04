

public class MatchParameters {
	Integer h[];
	Integer y[];
	Double motion[];
	
	public MatchParameters() {
		int i = 0;
		
		h = new Integer[Constants.H_QUANTIZATION_FACTOR];
		y = new Integer[Constants.Y_QUANTIZATION_FACTOR];
		motion = new Double[Constants.NO_OF_MOTION_VECTORS];
		
		for(i = 0; i < Constants.H_QUANTIZATION_FACTOR; i++) {
			h[i] = 0;
		}
		
		for(i = 0; i < Constants.Y_QUANTIZATION_FACTOR; i++) {
			y[i] = 0;
		}
		
		for(i = 0; i < Constants.NO_OF_MOTION_VECTORS; i++) {
			motion[i] = 0.0;
		}		
	}
}
