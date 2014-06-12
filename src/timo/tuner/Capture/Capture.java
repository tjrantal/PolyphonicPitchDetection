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

package timo.tuner.Capture;

import timo.tuner.ui.*; /*Import ui*/
import timo.tuner.Analysis.*; /*Import analysis*/
import timo.tuner.DrawImage.*; /*Import DrawImage*/
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
	public Capture(int bitDepthIn, PolyphonicPitchDetection mainProgram){
		bitDepth = bitDepthIn;
		bitSelection = bitDepth/8;
		this.mainProgram = mainProgram;
		mainProgram.rawFigure.f0s = null;
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
					int testC = 0;
					while (mainProgram.continueCapturing) {
						int count = line.read(buffer, 0, buffer.length); /*Blocking call to read*/
						//System.out.println("Got data "+count);
						if (count > 0) {
							if (bitSelection ==1){
								mainProgram.rawFigure.drawImage(buffer,mainProgram.imWidth,mainProgram.imHeight);
								/*Add pitch detection here for 8 bit, not implemented...*/
							}
							if (bitSelection ==2){
							   	short[] data = byteArrayToShortArray(buffer);
								/*Build test signal*/
							   	if (true){
							   	   for (int i = 0;i<data.length;++i){
							   	      data[i] = 0;
						   			for (int h =0;h<20;++h){
						   				data[i] += (short) (1.0/(2.0+((double)h))*
						   				(
						   				Math.sin(2.0*Math.PI*((double)(i+testC))/mainProgram.samplingRate*82.4)
						   				)
						   				*Math.pow(2.0,12.0)
						   				);

						   				data[i] += (short) (1.0/(2.0+((double)h))*
						   				(
						   				Math.sin(2.0*Math.PI*((double)(i+testC))/mainProgram.samplingRate*123.5)
						   				)
						   				*Math.pow(2.0,12.0)
						   				);

   						   				data[i] += (short) (1.0/(2.0+((double)h))*
						   				(
						   				Math.sin(2.0*Math.PI*((double)(i+testC))/mainProgram.samplingRate*164.8)
						   				)
						   				*Math.pow(2.0,12.0)
						   				);
						   			}
							   	   	//System.out.print(data[i]+" ");
							   	   }
							   	 	++testC;
							   	}
								//mainProgram.rawFigure.drawImage(data,mainProgram.imWidth,mainProgram.imHeight);
								/*Add pitch detection here for 16 bit*/
								Analysis analysis = new Analysis(data,mainProgram);	//FFT + klapuri analysis
								mainProgram.rawFigure.drawImage(analysis.hanData,mainProgram.imWidth,mainProgram.imHeight);
								//mainProgram.rawFigure.drawImage(data,mainProgram.imWidth,mainProgram.imHeight);

								System.out.println("Updating figure "+data.length);
								/*
								mainProgram.fftFigure.drawImage(analysis.amplitudes,mainProgram.imWidth,mainProgram.imHeight,analysis.maximum);
								*/
								mainProgram.whitenedFftFigure.drawImage(analysis.klapuri.whitened,mainProgram.imWidth,mainProgram.imHeight,analysis.whitenedMaximum,analysis.klapuri.f0s);
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
			}
			return shortArray;
		}

}

