/*Copied from http://introcs.cs.princeton.edu/java/97data/FFT.java.html 15.07.2011
linked from http://introcs.cs.princeton.edu/java/97data/
Modified for reconstructing the signal from N first coefficients by
Timo Rantalainen 2011
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
 
public class FFT_fit {

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


    // compute the inverse FFT of x[], assuming its length is a power of 2
    public static Complex[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].times(1.0 / N);
        }

        return y;

    }

    // display an array of Complex numbers to standard output
    public static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println();
    }
	
	public static void show(double[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println();
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
		int N = y.length;
		double[] amplitudes = new double[N];
		amplitudes[0] = y[0].abs()/((double)N);
		for (int i = 1; i < N; i++) {
            amplitudes[i] = y[i].abs()/((double)N)*2;
        }
		return amplitudes;
	}
	
    public static void main(String[] args) { 
        int N = Integer.parseInt(args[0]);
        Complex[] x = new Complex[N];

        // original data
        for (int i = 0; i < N; i++) {
            //x[i] = new Complex(0.5*Math.sin(((double)i)*5*2*Math.PI/((double)N))+2, 0);
			x[i] = new Complex(Math.sin(((double)i)*2*Math.PI/((double)N))+1, 0);
        }
        show(x, "x");

		
        // FFT of original data
        Complex[] y = fft(x);
		/*
        show(y, "y = fft(x)");
		*/
		
		//Show the amplitudes
		double[] amplitudes = calculateAmplitudes(y);
		show(amplitudes,"amplitudes");
		
		//Reconstruct according to N first coefficients
		int N2 = 8;
		double[] reconstructed = reconstruct(y,N2);
		show(reconstructed,"Reconstructed "+N2);

    }

}
