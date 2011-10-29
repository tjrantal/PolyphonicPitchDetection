package Analysis;
import ui.*;
public class Analysis{
	/*Implement analysis here*/
	public double[] amplitudes;
	public double maximum;
	public double whitenedMaximum;
	public Klapuri klapuri;
	public Analysis(short[] dataIn,PolyphonicPitchDetection mainProgram){
		Complex[] x = new Complex[dataIn.length];
        for (int i = 0; i < dataIn.length; ++i) {
            x[i] = new Complex((double) dataIn[i], 0);
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
	

}

