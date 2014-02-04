import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


@SuppressWarnings("serial")
public class UI extends JFrame {
	static String[] videoFileNames = new String[Constants.NO_OF_FILES];
	static String[] videoFileValues = new String[Constants.NO_OF_FILES];
	static String[] audioFileValues = new String[Constants.NO_OF_FILES];
	static float errorList[][] = new float[Constants.NO_OF_FILES][];
	static int[] bestMatchArray = new int[Constants.NO_OF_FILES]; 
	
	public static float[] errorPercentageArray;
	public static int bestMatchIndex;
	
	public Container contentPane;
	public static JTextField textField;
	public JPanel origVideoPanel;
	public JPanel queryVideoPanel;
	public JButton queryVideoPlay;
	public JButton queryVideoPause;
	public JButton queryVideoStop;
	public JButton origVideoPlay;
	public JButton origVideoPause;
	public JButton origVideoStop;
	public static boolean videoPaused;
	public static boolean queryVideoPaused;
	public static PlayRGBVideo videoThread;
	public static PlayAudio audioThread;
	public static PlayRGBQueryVideo queryVideoThread;
	public static PlayAudio queryAudioThread;
	private JScrollPane scrollPane;
	@SuppressWarnings("rawtypes")
	public static DefaultListModel model;
	public static JSlider slider;
	
	public static QueryPlayButtonEvent queryPlay;
	public static QueryPauseButtonEvent queryPause;
	public static QueryStopButtonEvent queryStop;
	
	public static PlayButtonEvent play;
	public static PauseButtonEvent pause;
	public static StopButtonEvent stop;
	
	public static String queryAudioFileName;
	public static String queryVideoFileName;
	public static String audioFileName;
	public static String videoFileName;
	
	public static JPanel histogramPanel;
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final UI frame = new UI();
		frame.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access"})
	public UI() {
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(150, 5, 1024, 700);
		this.contentPane = new JPanel();
		((JComponent) this.contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		
		this.histogramPanel = new JPanel();
		
		UI.textField = new JTextField();
		UI.textField.setBounds(191, 44, 257, 20);
		this.contentPane.add(UI.textField);
		UI.textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Browse");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Constants.BASE_PATH));
		        int res = fc.showOpenDialog(null);
		        try {
		            if (res == JFileChooser.APPROVE_OPTION) {
		                File file = fc.getSelectedFile();
		                String queryVideo = file.getAbsolutePath();
		                UI.textField.setText(queryVideo);
		                String audioName = queryVideo.substring(queryVideo.indexOf("query"),queryVideo.indexOf("query")+6);
		                String queryAudio = Constants.BASE_PATH+audioName+".wav";
		                
		                UI.queryAudioFileName = queryAudio;
		                UI.queryVideoFileName = queryVideo;
		                
		                if(UI.queryStop != null)
		                UI.queryStop.mouseClicked(null);
		                
		                if(UI.queryPlay == null) {
		                	UI.queryPlay = new QueryPlayButtonEvent(queryVideoPanel);
			                UI.queryPause = new QueryPauseButtonEvent();
			                UI.queryStop = new QueryStopButtonEvent(queryVideoPanel);
			                
			                queryVideoPlay.addMouseListener(UI.queryPlay);
							queryVideoPause.addMouseListener(UI.queryPause);
							queryVideoStop.addMouseListener(UI.queryStop);
		                }
		            }
		        } 
		        catch (Exception iOException) {
		        	iOException.printStackTrace();
		        }
			}
		});
		btnNewButton.setBounds(96, 43, 85, 23);
		this.contentPane.add(btnNewButton);
		
		this.origVideoPanel = new JPanel();
		this.origVideoPanel.setBounds(604, 330, 352, 288);
		this.contentPane.add(this.origVideoPanel);
		
		this.queryVideoPanel = new JPanel();
		this.queryVideoPanel.setBounds(96, 330, 352, 288);
		this.contentPane.add(this.queryVideoPanel);
		
		JLabel lblQuery = new JLabel("Query");
		lblQuery.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblQuery.setBounds(96, 305, 46, 14);
		this.contentPane.add(lblQuery);
		
		JLabel lblVideo = new JLabel("Video");
		lblVideo.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblVideo.setBounds(604, 305, 46, 14);
		this.contentPane.add(lblVideo);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(604, 46, 352, 95);
		contentPane.add(scrollPane);
		
		model = new DefaultListModel();
		final JList list = new JList(model);
		list.setVisibleRowCount(5);
		scrollPane.setViewportView(list);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if(!list.getValueIsAdjusting()) {
					if(list.isSelectedIndex(list.getSelectedIndex())) {
						UI.slider.setValue(0);
						UI.histogramPanel.removeAll();
						UI.histogramPanel.repaint();
						UI.audioFileName = audioFileValues[list.getSelectedIndex()];
						UI.videoFileName = videoFileValues[list.getSelectedIndex()];
						
						errorPercentageArray = errorList[list.getSelectedIndex()];
						bestMatchIndex = bestMatchArray[list.getSelectedIndex()];
						
						if(UI.stop != null) {
			                UI.stop.mouseClicked(null);
						}
			                
						if (UI.play == null) {
							UI.play = new PlayButtonEvent(origVideoPanel);
							UI.pause = new PauseButtonEvent();
							UI.stop = new StopButtonEvent(origVideoPanel);

							origVideoPlay.addMouseListener(UI.play);
							origVideoPause.addMouseListener(UI.pause);
							origVideoStop.addMouseListener(UI.stop);
						}
					}
				}
			}
		});
		
		this.queryVideoPlay = new JButton("Play");
		this.queryVideoPlay.setBounds(96, 640, 89, 23);
		this.contentPane.add(this.queryVideoPlay);
		
		this.queryVideoPause = new JButton("Pause");
		this.queryVideoPause.setBounds(228, 640, 89, 23);
		this.contentPane.add(this.queryVideoPause);
		
		this.queryVideoStop = new JButton("Stop");
		this.queryVideoStop.setBounds(359, 640, 89, 23);
		this.contentPane.add(this.queryVideoStop);
		
		this.origVideoPlay = new JButton("Play");
		this.origVideoPlay.setBounds(604, 640, 89, 23);
		this.contentPane.add(this.origVideoPlay);
		
		this.origVideoPause = new JButton("Pause");
		this.origVideoPause.setBounds(736, 640, 89, 23);
		this.contentPane.add(this.origVideoPause);
		
		this.origVideoStop = new JButton("Stop");
		this.origVideoStop.setBounds(867, 640, 89, 23);
		this.contentPane.add(this.origVideoStop);
		
		slider = new JSlider();
		slider.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				paintHistogram(UI.slider.getValue());
			}
		});
		slider.addMouseListener(new SliderMotionListener());
		slider.setPaintTicks(true);
		slider.setBorder(null);
		slider.setBounds(604, 273, 352, 31);
		slider.setValue(0);
		slider.setMajorTickSpacing(10);
		contentPane.add(slider);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(null != UI.textField && !"".equals(UI.textField.getText())) {
					OnlineProcess op = new OnlineProcess(UI.textField.getText());
					op.run();
				}
			}
		});
		btnSearch.setBounds(96, 96, 85, 23);
		contentPane.add(btnSearch);
		
		histogramPanel.setBounds(610, 180, 352, 71);
		contentPane.add(histogramPanel);
	}
	
	public static void paintHistogram(int index) {
		 if(index >= UI.errorPercentageArray.length)
			 return;
		 int buffer = 13, maxValue = 100;
		 int xOffset = 0, yOffset = 0, xPos = 0, yPos = 0;
		 int width = UI.histogramPanel.getWidth() - 1;
		 int height = UI.histogramPanel.getHeight() - 1;
		 
		 Graphics g = UI.histogramPanel.getGraphics();
		 g.clearRect(0, 0, width, height);
		 Graphics2D g2d = (Graphics2D) g.create();
		 g2d.setColor(Color.DARK_GRAY);
		 g2d.drawRect(xOffset, yOffset, width - 13, height);
		 
		 int barWidth = Math.max(5, (int) Math.floor((float) width / (float) (2*buffer+1)));
		 //System.out.println("width = " + width + "; height=" + height + "; size = " + UI.errorPercentageArray.length + "; barWidth = " + barWidth);
		 
		 int start = index - buffer;
		 if(start < 0)
			 start = 0;
		 int end = index + buffer;
		 if(end > UI.errorPercentageArray.length)
			 end = UI.errorPercentageArray.length;
		 
		 for(int i = start; i < end; i++) {
			     int barHeight = Math.round(((float) (100 - UI.errorPercentageArray[i]) / (float) maxValue) * height);
			     g2d.setColor(new Color(100, 100, 100));
			     yPos = height + yOffset - barHeight;
			     Rectangle2D bar = new Rectangle.Float(xPos, yPos, barWidth, barHeight);
			    
			     if(i == index)
			    	 g2d.setColor(Color.GREEN);
			     else {
			    	 if(i == bestMatchIndex) 
			    		 g2d.setColor(Color.RED);
			    	 else
			    		 g2d.setColor(Color.BLUE);
			     }
			     g2d.fill(bar);
			     g2d.setColor(Color.DARK_GRAY);
			     g2d.draw(bar);
			     xPos += barWidth;
			 }
		
		g2d.dispose();
   }
}
