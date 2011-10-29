package Capture;

import ui.*; /*Import ui*/
import Analysis.*; /*Import analysis*/
import DrawImage.*; /*Import DrawImage*/
import java.io.*;		/*ByteArrayStream*/
import javax.sound.sampled.*; /*Sound capture*/

public class Capture implements Runnable{
	/*Implement analysis here*/
	
	AudioFormat aFormat;
	TargetDataLine line;
	DataLine.Info info;
	PolyphonicPitchDetection mainProgram;
	int bitDepth;
	int bitSelection;
	int stereo;
	/*Constructor*/
	public Capture(int bitDepthIn, PolyphonicPitchDetection mainProgramIn){
		bitDepth = bitDepthIn;
		bitSelection = bitDepth/8;
		mainProgram = mainProgramIn;
		stereo = 1; /*Capture mono*/
	}
	
	public void run() {
				aFormat = new AudioFormat(mainProgram.samplingRate,bitDepth,stereo,true,false);			
				info = new DataLine.Info(TargetDataLine.class, aFormat);
			System.out.println(info);
			try{
				line = (TargetDataLine) AudioSystem.getLine(info);
					line.open(aFormat,line.getBufferSize());
					line.start();		//Start capturing
					int bufferSize = mainProgram.fftWindow*bitSelection*stereo;
					byte buffer[] = new byte[bufferSize];
					while (mainProgram.continueCapturing) {
						int count = line.read(buffer, 0, buffer.length);
						if (count > 0) {
							if (bitSelection ==1){
								mainProgram.rawFigure.drawImage(buffer,mainProgram.imWidth,mainProgram.imHeight);
								/*Add pitch detection here for 8 bit*/							
							}
							if (bitSelection ==2){
								short[] data = byteArrayToShortArray(buffer);
								mainProgram.rawFigure.drawImage(data,mainProgram.imWidth,mainProgram.imHeight);
								/*Add pitch detection here for 16 bit*/
								Analysis analysis = new Analysis(data,mainProgram);	//FFT + klapuri analysis
								mainProgram.fftFigure.drawImage(analysis.amplitudes,mainProgram.imWidth,mainProgram.imHeight,analysis.maximum);
								mainProgram.whitenedFftFigure.drawImage(analysis.klapuri.whitened,mainProgram.imWidth,mainProgram.imHeight,analysis.whitenedMaximum);
							}
								//mainProgram.rawFigure.paintImmediately(0,0,mainProgram.imWidth,mainProgram.imHeight);							
							//mainProgram.rawFigure.repaint();
						}
						
			  		}
					line.stop();
					line.flush();
					line.close();
				} catch  (Exception err){	System.err.println("Error: " + err.getMessage());}
		}
		
		public static short[] byteArrayToShortArray(byte[] arrayIn){
			short[] shortArray = new short[arrayIn.length/2];
			for (int i = 0;i<shortArray.length;++i){
				shortArray[i] = (short) (((((int) arrayIn[2*i+1]) & 0XFF)<< 8) | (((int) arrayIn[2*i]) & 0XFF));
				//shortArray[i] = (short) (((int) arrayIn[2*i+1])<< 8 | ((int) arrayIn[2*i]));
			}
			return shortArray;
		}

}

