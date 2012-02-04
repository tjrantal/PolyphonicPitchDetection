/*
	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

	N.B.  the above text was copied from http://www.gnu.org/licenses/gpl.html
	unmodified. I have not attached a copy of the GNU license to the source...

    Copyright (C) 2011-2012 Timo Rantalainen
*/

/*
Written by Timo Rantalainen tjrantal@gmail.com 2010 (C++ version) - 2012 (Java version)
Based on Anssi Klapuri's (list of publications http://www.cs.tut.fi/~klap/iiro/ and http://www.elec.qmul.ac.uk/people/anssik/publications.htm) congress publication
Klapuri, A., " Multiple fundamental frequency estimation by summing harmonic amplitudes," 7th International Conference on Music Information Retrieval (ISMIR-06), Victoria, Canada, Oct. 2006. 
http://www.cs.tut.fi/sgn/arg/klap/klap2006ismir.pdf
 and doctoral thesis:
Klapuri, A. " Signal processing methods for the automatic transcription of music," Ph.D. thesis, Tampere University of Technology, Finland, April 2004.
 http://www.cs.tut.fi/sgn/arg/klap/phd/klap_phd.pdf  

Contributions from other people taken from the internet (in addition to Java-tutorials for GUI, sound capture etc.)
FFT-transform					

Required class files (in addition to this one..).
ReadStratecFile.java		//Stratec pQCT file reader
DrawImage.java				//Visualize image in a panel
SelectROI.java
AnalyzeRoi.java			//Analysis calculations

JAVA compilation:
javac -cp '.:' ui/PolyphonicPitchDetection.java \
Capture/Capture.java \
DrawImage/DrawImage.java \
Analysis/Analysis.java \
Analysis/Complex.java \
Analysis/FFT.java \
Analysis/Functions.java \
Analysis/Klapuri.java  
JAR building:
jar cfe PolyphonicPitchDetection.jar ui.PolyphonicPitchDetection ui DrawImage Analysis Capture

*/
package ui;
import javax.swing.*;		//GUI commands swing
import java.awt.event.*; 	//Events & Actionlistener
import java.io.*;				//File IO
import java.lang.Math;
import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.Vector;
import java.util.Enumeration;
import java.io.*;
import javax.sound.sampled.*;
import java.awt.font.*;
import java.text.*;
import java.awt.image.*;
import java.awt.image.DataBuffer;

//import Analysis.*;	//Polyphonic analysis
import Capture.*;	//Sound capture
import DrawImage.*;		//Drawing images

public class PolyphonicPitchDetection extends JPanel implements ActionListener {	
	JButton beginPitchDetection;
	JButton endPitchDetection;
	public DrawImage fftFigure;
	public DrawImage rawFigure;
	public DrawImage whitenedFftFigure;
	public int fftWindow = 4096;	/*FFT window width ~0.1 s -> Max ~600 bpm*/
	public float samplingRate = 44100;
	public static int imWidth =800;
	public static int imHeight =250;
	public boolean continueCapturing;
	public static int w;
	public static int h; 
	static int traces = 2;		/*how many traces are we plotting...*/
	public double[] cb;			/*Klapuri whitening ranges*/
	public double[] freq;		/*FFT fequency bins*/
	public double[] f0cands;	/*Klapuri F0 candidates*/
	public int[] f0index;		/*Klapuri F0 candidate indices*/
	public PolyphonicPitchDetection(){ /*Constructor*/

		JPanel buttons = new JPanel(); /*Panel for start and stop*/
		/*Begin button*/
		beginPitchDetection= new JButton("Begin pitch detection");
		beginPitchDetection.setMnemonic(KeyEvent.VK_B);
		beginPitchDetection.setActionCommand("beginPitchDetection");
		beginPitchDetection.addActionListener(this);
		beginPitchDetection.setToolTipText("Press to Begin pitch detection");
		
		/*End button*/
		buttons.add(beginPitchDetection);
		endPitchDetection= new JButton("End pitch detection");
		endPitchDetection.setMnemonic(KeyEvent.VK_E);
		endPitchDetection.setActionCommand("endPitchDetection");
		endPitchDetection.addActionListener(this);
		endPitchDetection.setToolTipText("Press to End pitch detection");
		endPitchDetection.setEnabled(false);
		buttons.add(endPitchDetection);
		add(buttons);
		

		/*Figure for captured sound*/
		rawFigure = new DrawImage();
		rawFigure.setBackground(new Color(0, 0, 0));
		rawFigure.setPreferredSize(new Dimension(imWidth,imHeight));
		rawFigure.setOpaque(true);
		add(rawFigure);
		/*Figure for fft*/
		/*
		fftFigure = new DrawImage();
		fftFigure.setBackground(new Color(0, 0, 0));
		fftFigure.setPreferredSize(new Dimension(imWidth,imHeight));
		fftFigure.setOpaque(true);
		add(fftFigure);
		*/
		/*Figure for whitened fft*/
		whitenedFftFigure = new DrawImage();
		whitenedFftFigure.setBackground(new Color(0, 0, 0));
		whitenedFftFigure.setPreferredSize(new Dimension(imWidth,imHeight));
		whitenedFftFigure.setOpaque(true);
		add(whitenedFftFigure);
		}

	public void actionPerformed(ActionEvent e) {
		if ("beginPitchDetection".equals(e.getActionCommand())) {
			endPitchDetection.setEnabled(true);
			beginPitchDetection.setEnabled(false);	
			/*Create constant arrays for Klapuri*/
			cb = new double[32];
			/*CB filterbank always the same values, could be included from somewhere...*/
			for (int b = 0;b<32;++b){
		 		cb[b] = 229.0*(Math.pow(10.0,(((double) (b+1))/21.4))-1.0); //frequency division
			}
			/*Frequencies, always the same after capture init...
			captured signal will be zero padded to twice its length
			*/
			freq = new double[(int) Math.floor((double) fftWindow)];
			for (int b = 0;b<Math.floor((double) fftWindow);b++){
				freq[b] = (double) b*(double)samplingRate/(double) fftWindow;
			}
			int temp = 0;
			int kk=0;
			while (freq[kk] <= 1500.0){
				if (freq[kk] >=60.0){
					++temp;
				}
				kk++;
			}
			f0cands = new double[temp];
			f0index = new int[temp];
			kk=0;
			temp = 0;
			while (freq[kk] <= 1500.0){
				if (freq[kk] >=60.0){
					f0cands[temp] = freq[kk];
					f0index[temp] = kk;
					++temp;				
				}
				kk++;
			}
			
			continueCapturing = true;
			/*Start capturing and analysis thread*/
			//Capture capture = new Capture(samplingRate,8,this);
			Capture capture = new Capture(16,this);
			Thread captureThread = new Thread(capture,"captureThread");
			captureThread.start();	
		}	
		if ("endPitchDetection".equals(e.getActionCommand())){
			continueCapturing = false;
			endPitchDetection.setEnabled(false);
			beginPitchDetection.setEnabled(true);
		}		
	}

	public static void initAndShowGUI(){
		JFrame f = new JFrame("Polyphonic Pitch Detection");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JComponent newContentPane = new PolyphonicPitchDetection();
		newContentPane.setOpaque(true); //content panes must be opaque
		f.setContentPane(newContentPane);
		f.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		if (screenSize.width < imWidth+40){w = screenSize.width-40;}else{w=imWidth+40;}
		if (screenSize.height < imHeight*traces+100){h = screenSize.height-40;}else{h=imHeight*traces+100;}
		f.setLocation(20, 20);
		//f.setLocation(screenSize.width/2 - w/2, screenSize.height/2 - h/2);
		f.setSize(w, h);
		f.setVisible(true);
	}
	
	
	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				initAndShowGUI();
			}
		}
		);
	}
}


