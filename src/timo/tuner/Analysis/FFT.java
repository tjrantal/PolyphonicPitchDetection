/*Copied from http://introcs.cs.princeton.edu/java/97data/FFT.java.html 15.07.2011
linked from http://introcs.cs.princeton.edu/java/97data/
Modified minimally by Timo Rantalainen 2011
*/

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
	Also, The licensing might not apply to the work I've simply copied from the
	web page indicated on the top of this file.

    Copyright (C) 2011 Timo Rantalainen
*/


/*************************************************************************
 *  Compilation:  javac FFT.java
 *  Execution:    java FFT N
 *  Dependencies: Complex.java
 *
 *  Compute the FFT and inverse FFT of a length N complex sequence.
 *  Bare bones implementation that runs in O(N log N) time. Our goal
 *  is to optimize the clarity of the code, rather than performance.
 *
 *  Limitations
 *  -----------
 *   -  assumes N is a power of 2
 *
 *   -  not the most memory efficient algorithm (because it uses
 *      an object type for representing complex numbers and because
 *      it re-allocates memory for the subarray, instead of doing
 *      in-place or reusing a single temporary array)
 *
 *************************************************************************/



 package timo.tuner.Analysis;

public class FFT {

    // compute the FFT of x[], assuming its length is a power of 2
    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) { throw new RuntimeException("N is not a power of 2"); }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }


	/*Reconstruct the original signal from N first coefficients*/
	public static double[] reconstruct(Complex[] y,int N){
		int length = y.length;
		double[] reconstructed = new double[length];
		double t;
		for (int i = 0; i < length; ++i){	//reconstruct the signal
			reconstructed[i] = y[0].re()/((double)length);
			t = ((double)i)/(length);
			for (int j = 1; j<N; ++j){
				reconstructed[i] += 2.0*y[j].re()/((double)length)*Math.cos(((double)j)*2*Math.PI*t)-2.0*y[j].im()/((double)length)*Math.sin(((double)j)*2*Math.PI*t);
			}
		}
		return reconstructed;
	}

	/*Calculate the amplitudes*/
	public static double[] calculateAmplitudes(Complex[] y){
		int N = y.length/2+1;
		double[] amplitudes = new double[N];
		amplitudes[0] = y[0].abs()/((double)y.length);
		for (int i = 1; i < N; i++) {
            	amplitudes[i] = 2.0*y[i].abs()/((double)y.length);
        	}
        	amplitudes[N-1] = amplitudes[N-1]/2.0;
		return amplitudes;
	}
}
