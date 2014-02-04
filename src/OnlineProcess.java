import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class OnlineProcess extends Thread{
	
	public String file = "";

	OnlineProcess(String file) {
		this.file = file;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		ArrayList<MatchParameters> queryParametersList = new ArrayList<MatchParameters>();
		ArrayList<ArrayList<MatchParameters>> dbParametersList = new ArrayList<ArrayList<MatchParameters>>();
		ArrayList<ArrayList<ErrorData>> errorPercentageList = new ArrayList<ArrayList<ErrorData>>();
		ArrayList<ErrorData> rankList = new ArrayList<ErrorData>();
		
		// Reading the Parameters file and keeping in memory
		getHIntensitiesFromFile(dbParametersList, Constants.PARAMETERS_FILE);
		
		// Getting the hIntensity List of query video
		Compute.getParametersList(queryParametersList, file);
		
		// Compare query and db
		compare(dbParametersList, queryParametersList, errorPercentageList, rankList);
		
		/*for(int i = 0; i < errorPercentageList.size(); i++) { 
			for(int j = 0; j < errorPercentageList.get(i).size(); j++) {
				System.out.println("H: " + errorPercentageList.get(i).get(j).hError
									+ "\tY: " + errorPercentageList.get(i).get(j).yError
									+ "\tMotion: " + errorPercentageList.get(i).get(j).mError 
									+ "\tStartIndex: " + errorPercentageList.get(i).get(j).startIndex);
			}
			System.out.println("============================");
		}*/
		
		UI.model.removeAllElements();
		sort(rankList);
		for(int i = 0; i < Constants.FILE_NAMES.length; i++) {
			/*System.out.println("Video: " + Constants.FILE_NAMES[rankList.get(i).videoIndex]
					+ " \tMError: " + rankList.get(i).mError
					+ " \tHError: " + rankList.get(i).hError
					+ " \tYError: " + rankList.get(i).yError
					+ " \tavgError: " + rankList.get(i).averageError 
					+ " \tIndex: " + rankList.get(i).startIndex);*/
			//UI.videoFileNames[i] = Constants.FILE_NAMES[rankList.get(i).videoIndex] + "( starts at : "+ rankList.get(i).startIndex+" frame";
			UI.model.addElement(Constants.FILE_NAMES[rankList.get(i).videoIndex] + " - (Match : "+ String.format("%.2f",(100-rankList.get(i).averageError)) +"% --- starts at frame: "+ rankList.get(i).startIndex+" )");
			UI.videoFileValues[i] = Constants.BASE_PATH+Constants.FILE_NAMES[rankList.get(i).videoIndex]+Constants.VIDEO_FILE_EXTENSION;
			UI.audioFileValues[i] = Constants.BASE_PATH+Constants.FILE_NAMES[rankList.get(i).videoIndex]+Constants.AUDIO_FILE_EXTENSION;
			UI.bestMatchArray[i] = rankList.get(i).startIndex;
			
			int noOfFrames = errorPercentageList.get(rankList.get(i).videoIndex).size();
			int videoIndex = rankList.get(i).videoIndex;
			UI.errorList[i] = new float[noOfFrames];
			for(int j = 0; j < noOfFrames; j++) {
				UI.errorList[i][j] = errorPercentageList.get(videoIndex).get(j).averageError;
			}
		}
	}	
	
	static boolean compareLessThan(ErrorData ed1, ErrorData ed2) {
		float a = 0, b = 0;
		
		if(Constants.SORT_FLAG == 1) {
			a = ed1.averageError;
			b = ed2.averageError;
		} else if(Constants.SORT_FLAG == 2) {
			a = ed1.mError;
			b = ed2.mError;
		} else if(Constants.SORT_FLAG == 3) {
			a = ed1.hError;
			b = ed2.hError;
		} else if(Constants.SORT_FLAG == 4) {
			a = ed1.yError;
			b = ed2.yError;
		}
		if(a < b)
			return true;
		return false;
	}
	
	
	static void sort(ArrayList<ErrorData> rankList) {
		for(int i = 0; i < rankList.size(); i++) {
			for(int j = 0; j < rankList.size(); j++) {
				if(compareLessThan(rankList.get(i), rankList.get(j))) {
					ErrorData edTemp = new ErrorData(rankList.get(i).hError, 
							rankList.get(i).yError, rankList.get(i).mError, rankList.get(i).averageError, rankList.get(i).startIndex, rankList.get(i).videoIndex);
					rankList.get(i).Copy(rankList.get(j));
					rankList.get(j).Copy(edTemp);
				}
			}
		}
	}
	
	static void getHIntensitiesFromFile(ArrayList<ArrayList<MatchParameters>> dbParametersList, String filePath) {
		BufferedReader br;
		String line = "";
		int i = 0, j = 0; 
		
		try {		
			br = new BufferedReader(new FileReader(filePath));
			line = br.readLine();
			while(line != null) {
				String[] splitHeading = line.split(",");
				
				if(splitHeading.length == 2) {
					ArrayList<MatchParameters> videoParametersList = new ArrayList<MatchParameters>(); 
					int noOfFrames = Integer.parseInt(splitHeading[1]);
					for(i = 0; i < Integer.parseInt(splitHeading[1]); i++) {
						line = br.readLine();
						String[] splitData = line.split(",");

						MatchParameters matchParameters = new MatchParameters();
						
						// Getting the H values
						for(j = 0; j < Constants.H_QUANTIZATION_FACTOR; j++) {
							matchParameters.h[j] = Integer.parseInt(splitData[j]);
						}
						
						// Getting the Y values
						for(j = 0; j < Constants.Y_QUANTIZATION_FACTOR; j++) {
							matchParameters.y[j] = Integer.parseInt(splitData[Constants.H_QUANTIZATION_FACTOR + j]);
						}
						
						
						//Getting motion value
						if(Constants.motion) {
							if(i < noOfFrames - 1) {
								for(j = 0; j < Constants.NO_OF_MOTION_VECTORS; j++) {
									matchParameters.motion[j] = Double.parseDouble(splitData[Constants.H_QUANTIZATION_FACTOR+Constants.Y_QUANTIZATION_FACTOR+j]);
								}
							}
						}
						
						videoParametersList.add(matchParameters);
					}
					dbParametersList.add(videoParametersList);
				}
				
				line = br.readLine();
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	static void compare(ArrayList<ArrayList<MatchParameters>> dbParametersList, ArrayList<MatchParameters> queryParametersList, 
			ArrayList<ArrayList<ErrorData>> errorPercentageList, ArrayList<ErrorData> rankList) {
		
		int dbFilesIndex = 0, dbFramesIndex = 0, queryFramesIndex = 0, hIndex = 0, yIndex = 0, mIndex = 0;
		
		// Initilizing the rankList
		for(int i = 0; i < Constants.FILE_NAMES.length; i++) {
			ErrorData ed = new ErrorData();
			ed.mError = Integer.MAX_VALUE;
			ed.hError = Integer.MAX_VALUE;
			ed.yError = Integer.MAX_VALUE;
			ed.averageError = Integer.MAX_VALUE;
			rankList.add(ed);
		}
		
		// Iterating through the db video files
		for(dbFilesIndex = 0; dbFilesIndex < dbParametersList.size(); dbFilesIndex++) {
			
			ArrayList<ErrorData> fileErrorData = new ArrayList<ErrorData>(); 
			// Iterating through the frames of the db video file
			for(dbFramesIndex = 0; dbFramesIndex < dbParametersList.get(dbFilesIndex).size() - queryParametersList.size() + 1; dbFramesIndex++) {
				
				float hQueryWindowError = 0, yQueryWindowError = 0, mQueryWindowError = 0;
				ErrorData queryWindowErrorData = new ErrorData();
				
				// Iterating through the query frames
				for(queryFramesIndex = 0; queryFramesIndex < queryParametersList.size(); queryFramesIndex++) {
					
					float hFrameError = 0, yFrameError = 0, motionError = 0;
		
					// Iterating through the H parameter
					double original = 0, query = 0, error = 0, diff = 0;
					for(hIndex = 0; hIndex < Constants.H_QUANTIZATION_FACTOR; hIndex++) {
						original = dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).h[hIndex];
						query = queryParametersList.get(queryFramesIndex).h[hIndex];
						diff = Math.abs(original - query);
						
						// Avoiding divde by 0
						if(original == 0.0)
							original = 1.0;
						error = (diff / original);
						
						// Making the error 100% if its more than 100%
						if(error > 1.0)
							error = 1.0;
						
						hFrameError += error;
						
						// Comparing the corresponding frame in query with the DB
						//hFrameError += (Math.abs(dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).h[hIndex]
						//							- queryParametersList.get(queryFramesIndex).h[hIndex]) * 1.0 / (Constants.WIDTH * Constants.HEIGHT));
					}
					hFrameError = (hFrameError / Constants.H_QUANTIZATION_FACTOR);
					
					// Adding to the query window error
					hQueryWindowError += hFrameError;
					
					// Iterating through the Y parameter
					error = original = query = diff = 0;
					for(yIndex = 0; yIndex < Constants.Y_QUANTIZATION_FACTOR; yIndex++) {
						original = dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).y[yIndex];
						query = queryParametersList.get(queryFramesIndex).y[yIndex];
						diff = Math.abs(original - query);
						
						// Avoiding divde by 0
						if(original == 0.0)
							original = 1.0;
						error = (diff / original);
						
						// Making the error 100% if its more than 100%
						if(error > 1.0)
							error = 1.0;
						
						yFrameError += error;
						// Comparing the corresponding frame in query with the DB
						//yFrameError += (Math.abs(dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).y[yIndex]
							//	- queryParametersList.get(queryFramesIndex).y[yIndex]) * 1.0 / (Constants.WIDTH * Constants.HEIGHT));
					}
					yFrameError = (yFrameError / Constants.Y_QUANTIZATION_FACTOR);
					
					// Adding to the query window error
					yQueryWindowError += yFrameError;

					//Comparing motion error
					if(Constants.motion) {
						if((dbFramesIndex < dbParametersList.get(dbFilesIndex).size() - 1) && (queryFramesIndex < queryParametersList.size()  - 1)) {
							error = original = query = diff = 0;
							for(mIndex = 0; mIndex < Constants.NO_OF_MOTION_VECTORS; mIndex++) {	 
								// Comparing the corresponding frame in query with the DB
								original += dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).motion[mIndex];
								query += queryParametersList.get(queryFramesIndex).motion[mIndex];
								//motionError += (Math.abs(dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).motion[mIndex]
								//		- queryParametersList.get(queryFramesIndex).motion[mIndex]));
							}
							diff = Math.abs(original - query);
								
							// Avoiding divde by 0
							if(original == 0.0)
								original = 1.0;
							error = (diff / original);
							
							// Making the error 100% if its more than 100%
							if(error > 1.0)
								error = 1.0;
							
							motionError = (float)(error);
						}
						mQueryWindowError += (motionError);
					}
				}
				
				queryWindowErrorData.hError = (hQueryWindowError / queryParametersList.size()) * 100;
				queryWindowErrorData.yError = (yQueryWindowError / queryParametersList.size()) * 100;
				if(Constants.motion) {
					queryWindowErrorData.mError = (mQueryWindowError / (queryParametersList.size()-1)) * 100;
				}
				queryWindowErrorData.averageError = ((3 * queryWindowErrorData.mError) 
													+ (2 * queryWindowErrorData.hError) 
													+ (1 * queryWindowErrorData.yError)
													+ (1 * queryWindowErrorData.audioError)) / 6;
				queryWindowErrorData.startIndex = dbFramesIndex;
				queryWindowErrorData.videoIndex = dbFilesIndex;
				fileErrorData.add(queryWindowErrorData);
				
				/*if(Constants.motion) {
					if(rankList.get(dbFilesIndex).mError > queryWindowErrorData.mError) {
						rankList.get(dbFilesIndex).Copy(queryWindowErrorData);
					}
				}
				else {
					if(rankList.get(dbFilesIndex).hError > queryWindowErrorData.hError) {
						rankList.get(dbFilesIndex).Copy(queryWindowErrorData);
					}
				}*/
				if(compareLessThan(queryWindowErrorData, rankList.get(dbFilesIndex))) {
					rankList.get(dbFilesIndex).Copy(queryWindowErrorData);
				}
				
			}
			errorPercentageList.add(fileErrorData);
		}
	}
	
	public static float klDivergence(MatchParameters p1, MatchParameters p2, int flag) {
	      float klDiv = 0;

	      if( flag == 1) {
		      for (int i = 0; i < p1.h.length; ++i) {
		        if (p1.h[i] == 0) { continue; }
		        if (p2.h[i] == 0) { continue; } // Limin
	
		        klDiv += (p1.h[i]*1.0 / (Constants.HEIGHT * Constants.WIDTH)) * Math.log( (p1.h[i]*1.0 / (Constants.HEIGHT * Constants.WIDTH)) / (p2.h[i]*1.0 / (Constants.HEIGHT * Constants.WIDTH)) );
		      }
	      } else {
		      for (int i = 0; i < p1.y.length; ++i) {
		        if (p1.y[i] == 0) { continue; }
		        if (p2.y[i] == 0) { continue; } // Limin
	
		        klDiv += (p1.y[i] * 1.0 / (Constants.HEIGHT * Constants.WIDTH)) * Math.log( (p1.y[i] * 1.0 / (Constants.HEIGHT * Constants.WIDTH)) / (p2.y[i] * 1.0 / (Constants.HEIGHT * Constants.WIDTH)) );
	      }
	      }

	      return (float) (klDiv / Math.log(2)); // moved this division out of the loop -DM
	    }
}
