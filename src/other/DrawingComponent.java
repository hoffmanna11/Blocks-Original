package other;
import java.awt.Component;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import main.Main;
import main_units.Block;
import main_units.Cell;
import main_units.Player;

@SuppressWarnings("serial")
public class DrawingComponent extends Component {
	/* Drawing grid stuff */
	public static Graphics g;
	public static DrawingComponent d;
	/* frames */
	static javax.swing.JFrame textFrame;
	static javax.swing.JFrame frame;
	static javax.swing.JFrame consoleFrame;
	/* appearance */
	static int scalingFactor = 40;
	static int borderWidth = 1;
	/* */
	public static DecimalFormat intdf = new DecimalFormat("#");
	/* These make it so there's a little whitespace surrounding the grid */
	static int xWhiteSpaceOffset = 25;
	static int yWhiteSpaceOffset = 50;
	/* buttons */
	public static JButton trainingB;
	public static JButton delayB;
	public static JButton startB; public static JTextField startT;
	public static JButton stopB; // public static JButton pauseB;
	public static JButton basicWeightsB;
	public static JButton percentileB;
	public static JButton resetCyclesB;
	public static JTextField topPercentileT;
	public static int numberOfBoxes = 8;
	/* Milliseconds to sleep between activating a button press action while holding a button down */
	public static int buttonSleepAmount = 500;
	/* Rate at which the sleep decreases while holding */
	public static double buttonSleepDecreaseRate = .75;
	public static boolean mousePressed = false;

	public static void drawDirectional(Cell c){
		String dir = c.getLocalHighestWeightDir();
		if(dir.equals("-")){
			// Then there is no weight to use
			return;
		}
		g.setColor(Color.BLACK);
		int x = c.getLoc().getX();
		int y = c.getLoc().getY();
		int x1, y1, xGridBorderOffset = 0, yGridBorderOffset = 0;
		xGridBorderOffset = borderWidth * (x+1);
		yGridBorderOffset = borderWidth * (y+1);

		x1 = scalingFactor * x + xGridBorderOffset + (scalingFactor/2) + xWhiteSpaceOffset - 5 + 3;
		y1 = yWhiteSpaceOffset + yGridBorderOffset + scalingFactor * y + (scalingFactor/2) + 3;

		// Main.holdup(50); //!TEMP//

		if(Main.useBasicWeights){
			g.drawString(dir.substring(0,1), x1, y1);
		}else{
			if(c.getLocalHighestWeight() > Main.topPercentile){
				g.drawString(dir.substring(0,1), x1, y1);
			}else{
				g.drawString("-", x1, y1);
			}
		}
	}

	public static void redrawCell(Cell c){
		if(c.hasPlayer()){
			// draw player
			drawPlayer(c.getLoc().getX(), c.getLoc().getY(), Color.RED);
		}else if(c.hasBlock()){
			if(Main.blocks.get(c.getBlockIndex()).getLength() == 1){
				drawBlock(c.getLoc().getX(), c.getLoc().getY(), Main.blocks.get(c.getBlockIndex()).getLength(), Color.CYAN);
			}else{
				drawBlock(c.getLoc().getX(), c.getLoc().getY(), Main.blocks.get(c.getBlockIndex()).getLength(), Color.BLUE);
			}
		}else{
			drawPlayer(c.getLoc().getX(), c.getLoc().getY(), Color.WHITE);
		}
	}

	/* draws in player cell */
	public static void drawPlayer(int x, int y, Color color){
		g.setColor(color);
		int x1, y1, y2, xGridBorderOffset = 0, yGridBorderOffset = 0;
		xGridBorderOffset = borderWidth * (x+1);
		yGridBorderOffset = borderWidth * (y+1);
		y1 = yWhiteSpaceOffset + yGridBorderOffset + scalingFactor * y;
		y2 = yWhiteSpaceOffset + yGridBorderOffset + scalingFactor * y + scalingFactor - 1;
		for(int i=0; i<scalingFactor; i++){
			x1 = scalingFactor * x + xGridBorderOffset + i + xWhiteSpaceOffset;
			g.drawLine(x1,y1,x1,y2);
		}
		if(null != Main.cells){
			drawDirectional(Main.cells.getCell(x, y));
		}
	}

	public static void drawBlock(int x, int y, int length, Color color){
		g.setColor(color);
		int x1, y1, y2, xGridBorderOffset = 0, yGridBorderOffset = 0;

		if(length == 1){
			xGridBorderOffset = borderWidth * (x+1);
			yGridBorderOffset = borderWidth * (y+1);
			y2 = yWhiteSpaceOffset + yGridBorderOffset + scalingFactor * y + scalingFactor - 1;
			for(int i=0; i<scalingFactor; i++){
				// The xWhiteSpaceOffset and yWhiteSpaceOffset makes it so there's 
				// a little whitespace surrounding the grid
				x1 = scalingFactor * x + xGridBorderOffset + i + xWhiteSpaceOffset;
				y1 = yWhiteSpaceOffset + yGridBorderOffset + scalingFactor * y;
				g.drawLine(x1,y1,x1,y2);
			}
		}else{
			g.setColor(Color.BLUE);
			for(int l=0; l<length; l++){
				x += l;
				xGridBorderOffset = borderWidth * (x+1);
				yGridBorderOffset = borderWidth * (y+1);
				y2 = yWhiteSpaceOffset + yGridBorderOffset + scalingFactor * y + scalingFactor - 1;
				for(int i=0; i<scalingFactor; i++){
					// The xWhiteSpaceOffset and yWhiteSpaceOffset makes it so there's 
					// a little whitespace surrounding the grid
					x1 = scalingFactor * x + xGridBorderOffset + i + xWhiteSpaceOffset;
					y1 = yWhiteSpaceOffset + yGridBorderOffset + scalingFactor * y;
					g.drawLine(x1,y1,x1,y2);
				}
			}
		}
		if(null != Main.cells){
			drawDirectional(Main.cells.getCell(x, y));
		}
	}

	/* don't really use this anymore since drawInit makes an artificial grid by coloring just the cells, but this can be used if a different color is desired */
	public static void drawGridBorder(Graphics g, int frameWidth, int frameHeight, int scalingFactor){
		g.setColor(Color.LIGHT_GRAY);
		int x1, y1, x2, y2;
		int xx1, yy1, xx2, yy2;
		for(int i=0; i<frameWidth+1; i++){
			for(int j=0; j<borderWidth; j++){
				// Draw horizontal grid lines
				x1 = xWhiteSpaceOffset;
				y1 = (i * scalingFactor) + (borderWidth * i) + j + yWhiteSpaceOffset;
				x2 = (frameWidth * scalingFactor) + ((frameWidth) * borderWidth) + xWhiteSpaceOffset;
				y2 = y1;
				g.drawLine(x1,y1,x2,y2);

				// Draw vertical grid lines
				xx1 = (i * scalingFactor) + (borderWidth * i) + j + xWhiteSpaceOffset;
				yy1 = yWhiteSpaceOffset;
				xx2 = xx1;
				yy2 = (frameHeight * scalingFactor) + ((frameHeight+1) * borderWidth) + yWhiteSpaceOffset - 1;
				g.drawLine(xx1,yy1,xx2,yy2);
			}
		}
	}

	/* for every cell, colors in the cell white to get rid of the default gray color */
	public static void drawInit(){
		for(int i=0; i<Main.gridWidth; i++){
			for(int j=0; j<Main.gridHeight; j++){
				DrawingComponent.drawPlayer(i, j, Color.WHITE);
			}
		}
	}

	public static void initFrame(){
		if(frame != null){
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
		if(textFrame != null){
			textFrame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
		if(consoleFrame != null){
			textFrame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
		frame = new javax.swing.JFrame();
		textFrame = new javax.swing.JFrame();
		consoleFrame = new javax.swing.JFrame();
		int width = (Main.gridWidth * scalingFactor) + (borderWidth * (Main.gridWidth+1)) + 50;
		int height = (Main.gridHeight * scalingFactor) + (borderWidth * (Main.gridHeight+1)) + 75;
		frame.setSize(width, height);
		textFrame.setSize(230,30 * numberOfBoxes);
		consoleFrame.setSize(500,500);
		frame.setVisible(true);
		d = new DrawingComponent();

		textFrame.setLayout(new GridLayout(numberOfBoxes,3));

		Box delay = Box.createHorizontalBox(); //FIX//
		//JPanel delay = new JPanel();
		JLabel delayL = new JLabel("Delay:   ");
		JButton delayPlusB = new JButton("+");
		JButton delayMinusB = new JButton("-");
		delayMinusB.setMargin(new Insets(0, 5, 4, 5));
		delayPlusB.setMargin(new Insets(0, 5, 4, 5));
		JTextField delayT = new JTextField(5);
		delayT.setHorizontalAlignment(JTextField.CENTER);
		delayT.setText(Integer.toString(Main.delay));
		delayB = new JButton("Change");
		delay.add(delayL); delay.add(delayMinusB); delay.add(delayPlusB); delay.add(delayT); delay.add(delayB);
		textFrame.add(delay);

		Box training = Box.createHorizontalBox(); //FIX//
		//JPanel training = new JPanel();
		//training.setLayout(new BoxLayout(training, BoxLayout.LINE_AXIS));
		JLabel trainingL1 = new JLabel("Training:            ");
		JTextField trainingT = new JTextField("ON");
		trainingT.setEditable(false);
		trainingT.setHorizontalAlignment(JTextField.CENTER);
		trainingB = new JButton("Change");
		//trainingB.setAlignmentX(Component.RIGHT_ALIGNMENT);
		training.add(trainingL1); training.add(trainingT); training.add(trainingB);
		textFrame.add(training);

		Box percentile = Box.createHorizontalBox(); //FIX//
		//JPanel percentile = new JPanel();
		JLabel percentileL = new JLabel("Percentile:");
		JTextField percentileT = new JTextField(5);
		percentileT.setHorizontalAlignment(JTextField.CENTER);
		percentileT.setText(Integer.toString((int)(Main.percentile * 100)));
		JButton percentilePlusB = new JButton("+");
		JButton percentileMinusB = new JButton("-");
		percentileMinusB.setMargin(new Insets(0, 5, 4, 5));
		percentilePlusB.setMargin(new Insets(0, 5, 4, 5));
		percentileB = new JButton("Change");
		percentile.add(percentileL); percentile.add(percentileMinusB); percentile.add(percentilePlusB); percentile.add(percentileT); percentile.add(percentileB);
		textFrame.add(percentile);

		Box resetCycles = Box.createHorizontalBox(); //FIX//
		//JPanel resetCycles = new JPanel();
		JLabel resetCyclesL = new JLabel("Reset Cycles:   ");
		JTextField resetCyclesT = new JTextField();
		resetCyclesT.setText(Integer.toString(Main.resetCycles));
		resetCyclesT.setHorizontalAlignment(JTextField.CENTER);
		resetCyclesB = new JButton("Change");
		resetCycles.add(resetCyclesL); resetCycles.add(resetCyclesT); resetCycles.add(resetCyclesB);
		textFrame.add(resetCycles);

		Box basicWeights = Box.createHorizontalBox();
		JLabel basicWeightsL1 = new JLabel("Basic Weights:");
		JTextField basicWeightsT;
		if(Main.useBasicWeights == true){
			basicWeightsT = new JTextField("ON");
		}else{
			basicWeightsT = new JTextField("OFF");
		}
		basicWeightsT.setEditable(false);
		basicWeightsT.setHorizontalAlignment(JTextField.CENTER);
		basicWeightsB = new JButton("Change");
		basicWeights.add(basicWeightsL1); basicWeights.add(basicWeightsT); basicWeights.add(basicWeightsB);
		textFrame.add(basicWeights);

		Box topPercentile = Box.createHorizontalBox();
		JLabel topPercentileL1 = new JLabel("Top Percentile:");
		topPercentileT = new JTextField(Double.toString(Main.topPercentile));
		topPercentileT.setEditable(false);
		topPercentileT.setHorizontalAlignment(JTextField.CENTER);
		topPercentile.add(topPercentileL1); topPercentile.add(topPercentileT);
		textFrame.add(topPercentile);

		Box start = Box.createHorizontalBox(); //FIX//
		//JPanel start = new JPanel();
		JLabel startL = new JLabel("Cycles:               ");
		startT = new JTextField();
		startT.setText(Integer.toString(Main.cycles));
		startT.setHorizontalAlignment(JTextField.CENTER);
		start.add(startL); start.add(startT);
		textFrame.add(start);

		Box startStopPause = Box.createHorizontalBox();
		startB = new JButton("  Start ");
		stopB = new JButton("  Stop ");
		JButton resetB = new JButton("  Reset ");
		//pauseB = new JButton("  Pause ");
		startStopPause.add(startB); startStopPause.add(stopB); startStopPause.add(resetB);
		textFrame.add(startStopPause);
		
		JTextArea consoleT = new JTextArea();
		consoleFrame.add(consoleT);
		
		//consoleFrame.setLocation(width );

		textFrame.setLocation(width, 0);
		textFrame.setVisible(true);

		textFrame.pack();

		percentileMinusB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				mousePressed = true;
				if(Main.percentile > .95){
					Main.percentile = .95;
					percentileT.setText(intdf.format((Main.percentile) * 100));
					Cell.setTopPercentile();
					resetWeights();
					Printing.printCells();
				}else if((Main.percentile - .05) >= 0){
					Main.percentile -= .05;
					percentileT.setText(intdf.format((Main.percentile) * 100));
					Cell.setTopPercentile();
					resetWeights();
					Printing.printCells();
				}else if(Main.percentile == 0){
					// do nothing
				}else{
					Main.percentile = 0;
					percentileT.setText(intdf.format((Main.percentile) * 100));
					Cell.setTopPercentile();
					resetWeights();
					Printing.printCells();
				}
			}
		});

		percentilePlusB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				
				if((Main.percentile + .05) <= .99){
					Main.percentile += .05;
					percentileT.setText(intdf.format((Main.percentile) * 100));
					Cell.setTopPercentile();
					resetWeights();
					Printing.printCells();
				}else if(Main.percentile == .99){
					// do nothing
				}else{
					Main.percentile = .99;
					percentileT.setText(intdf.format((Main.percentile) * 100));
					Cell.setTopPercentile();
					resetWeights();
					Printing.printCells();
				}
			}
		});

		delayPlusB.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e){
				mousePressed = true;
				new Thread() {
					public void run() {
						int variableSleepAmount = buttonSleepAmount;
						while (mousePressed) {
							if(Main.delay >= 150){
								Main.delay += 50;
							}else{
								if(Main.delay < 25){
									Main.delay += 5;
								}else{
									Main.delay += 25;
								}
							}
							delayT.setText(Integer.toString(Main.delay));
							if(variableSleepAmount >= 100){
								variableSleepAmount *= buttonSleepDecreaseRate;
							}
							try {
								Thread.sleep(variableSleepAmount);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						// Once the button is no longer held, kill thread
						Thread.currentThread().interrupt();
					}
				}.start();
			}
			public void mouseReleased(MouseEvent e) {
				mousePressed = false;
			}
		});

		delayMinusB.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e){
				mousePressed = true;
				new Thread() {
					public void run() {
						int variableSleepAmount = buttonSleepAmount;
						while (mousePressed) {
							if(Main.delay >= 200){
								Main.delay -= 50;
							}else{
								if(Main.delay <= 25){
									if((Main.delay - 5) < 0){
										Main.delay = 0;
									}else{
										Main.delay -= 5;
									}
								}else{
									Main.delay -= 25;
								}
							}
							delayT.setText(Integer.toString(Main.delay));
							if(variableSleepAmount >= 100){
								variableSleepAmount *= buttonSleepDecreaseRate;
							}
							try {
								Thread.sleep(variableSleepAmount);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}.start();
			}

			public void mouseReleased(MouseEvent e) {
				mousePressed = false;
			}
		});

		basicWeightsB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set training to opposite value
				Main.useBasicWeights = !Main.useBasicWeights;
				if(Main.useBasicWeights == true){
					basicWeightsT.setText("ON");
				}else{
					basicWeightsT.setText("OFF");
				}
				// Redraw all of the weights to only draw the basic weights
				resetWeights();
			}
		});

		textFrame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				e.getWindow().dispose();
			}
		});
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.out.println("Closed");
				e.getWindow().dispose();
			}
		});

		trainingB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set training to opposite value
				Main.training = !(Main.training);
				if(Main.training == false){
					trainingT.setText("OFF");
				}else{
					trainingT.setText("ON");
				}
			}
		});

		delayB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set delay to value in textbox
				Main.delay = Integer.parseInt(delayT.getText());
			}
		});

		percentileB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set delay to value in textbox
				double percentileInput = Double.parseDouble(percentileT.getText());
				if(percentileInput <= 99 && percentileInput >= 0){
					Main.percentile = (Double.parseDouble(percentileT.getText()) / 100);
					Cell.setTopPercentile();
					resetWeights();
					Printing.printCells();
				}
			}
		});

		startB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Main.stopped){
					Main.stopped = false;
					startB.setText("Pause");
					stopB.setEnabled(true);
					Main.startThread();
					return;
				}

				if(Main.paused || Main.stopped){
					Main.paused = false;
					Main.stopped = false;
					stopB.setEnabled(true);
					startB.setText("Pause");
					stopB.setEnabled(true);
				}else{
					// The button is currently set to be a pause button
					Main.paused = true;
					startB.setText("Start");
					// Redraw everything to avoid the black lines
					redrawAllCells();
				}

			}
		});

		stopB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set delay to value in textbox
				//pauseB.setEnabled(false);
				Main.stopped = true;
				Main.paused = false;
				startB.setText("Start");
				Main.resetUnitLocations();
				stopB.setEnabled(false);
				g.clearRect(46, 35, 300, 15);
				g.setColor(Color.black);
				g.drawString("Cycle: " + 0, 10, 45);
				// Redraw everything to avoid the black blocks that appear sometimes when the delay
				// is set very low
				redrawAllCells();
			}
		});

		resetB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				StringBuilder cmd = new StringBuilder();
				cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
				for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
					cmd.append(jvmArg + " ");
				}
				cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
				cmd.append(Main.class.getName()).append(" ");

				/*
        for (String arg : args) {
            cmd.append(arg).append(" ");
        }
				 */

				try {
					Runtime.getRuntime().exec(cmd.toString());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
				/*
				Main.killThread = true;
				Thread newThread = new Thread(new Runnable() {
					public void run() {
						//Main.main(null);
					}
				});
				 */
				//newThread.start();
				//Thread.currentThread().interrupt();
			}
		});

		/*
		pauseB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set delay to value in textbox
				Main.paused = true;
			}
		});
		 */

		resetCyclesB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set resetCycles to value in textbox
				Main.resetCycles = Integer.parseInt(resetCyclesT.getText());
			}
		});

		resetCyclesB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set resetCycles to value in textbox
				Main.resetCycles = Integer.parseInt(resetCyclesT.getText());
			}
		});

		//label, textfield, button

		g = frame.getGraphics();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		textFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Main.holdup(50); // If this isn't included, the next method fails to run
	}

	public static void redrawCycles(int i, int cycles){
		g.clearRect(46, 35, 300, 15);
		g.setColor(Color.black);
		g.drawString("Cycle: " + i + " (of " + cycles + ") [ " + (int)((double)100*i/cycles) + "% ]", 10,45);
	}

	public static void resetWeights(){
		for(int i=0; i<Main.cells.size(); i++){
			for(int j=0; j<Main.cells.getRow(0).size(); j++){
				redrawCell(Main.cells.getCell(i, j));
				drawDirectional(Main.cells.getCell(i, j));
			}
		}
	}

	public void drawPlayers(List<Player> ps){
		for(int i=0; i<ps.size(); i++){
			drawPlayer(ps.get(i).getLoc().getX(),ps.get(i).getLoc().getY(),Color.RED);
		}
	}

	public void drawBlocks(List<Block> bs){
		for(int i=0; i<bs.size(); i++){
			drawBlock(bs.get(i).getLoc().getX(), bs.get(i).getLoc().getY(), bs.get(i).getLength(), Color.CYAN);
		}
	}

	public static void redrawAllCells(){
		for(int i=0; i<Main.cells.size(); i++){
			for(int j=0; j<Main.cells.getRow(0).size(); j++){
				redrawCell(Main.cells.getCell(i,j));
			}
		}
	}
}