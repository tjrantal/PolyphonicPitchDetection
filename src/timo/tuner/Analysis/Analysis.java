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

package timo.tuner.Analysis;
import timo.tuner.ui.*;
public class Analysis{
	/*Implement analysis here*/
	public double[] amplitudes;
	public double[] hanData;
	public double maximum;
	public double whitenedMaximum;
	public Klapuri klapuri;
	public Analysis(short[] dataIn,PolyphonicPitchDetection mainProgram){
		/*Apply Hann windowing function*/
		hanData = hannWindow(dataIn);
		/*Append zeros*/
		Complex[] x = new Complex[hanData.length*2];
	        for (int i = 0; i < hanData.length*2.0; ++i) {
	        		if (i >= hanData.length){
	        			x[i] = new Complex(0.0,0.0);	//Zero padding
	        		}else{
	            	x[i] = new Complex(hanData[i], 0);
	            }
	        }
	        Complex[] y = FFT.fft(x); //Calculate FFT
		 amplitudes = FFT.calculateAmplitudes(y);
		 maximum = Functions.max(amplitudes);
		 klapuri = new Klapuri(amplitudes,maximum,mainProgram);	//Klapuri picth detection
		 whitenedMaximum = Functions.max(klapuri.whitened);
		 if (whitenedMaximum == 0){
		 	whitenedMaximum = 1.0;
		 }
	}

	/*Hann window function taken from http://en.wikipedia.org/wiki/Window_function
		w(n) = 0.5(1-cos(2pin/(N-1))
	*/
	double[] hannWindow(short[] dataIn){
		double[] hanData = new double[dataIn.length];
		double N = (double) dataIn.length;
		for (int i = 0;i<dataIn.length;++i){
			hanData[i] = ((double) dataIn[i])*0.5*(1-Math.cos(2.0*Math.PI*(double) i/(N-1)));
		}
		return hanData;
	}
}

