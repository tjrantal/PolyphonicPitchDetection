package Analysis;
import java.util.*;
import ui.*;
public class Klapuri{
	public double[] whitened;
	PolyphonicPitchDetection mainProgram;
	public Klapuri(double[] data, double max,PolyphonicPitchDetection mainProgram){
		//whitened = (double[]) data.clone();
		this.mainProgram = mainProgram;
		whitened = whiten(data,mainProgram);
	}
	
	double[] whiten(double[] dataIn,PolyphonicPitchDetection mainProgram){
		double[] whitened = new double[dataIn.length];
			
			/*Calculate signal energies in filter windows??*/
			Vector<Double> Hb = new Vector<Double>();
			Vector<Integer> indeksit = new Vector<Integer>();
			
			for (int i = 1;i<31;++i){
				Hb.clear();
				indeksit.clear();
				kk=0;
				while (mainProgram.freq[kk] <= mainProgram.cb[i+1]){
					if (mainProgram.freq[kk] >= mainProgram.cb[i-1]){
						indeksit.add(kk);
						Hb.push_back(1-Math.abs(mainProgram.cb[i]-mainProgram.freq[indeksit.back()])/(mainProgram.cb[i+1]-mainProgram.cb[i-1]));
					}
					kk++;
				}
				summa = 0;
				for (int j = 0;j< Hb.size();j++){
					summa = summa+Hb[j]*yK[indeksit[j]]*yK[indeksit[j]];
				}
				stdb.push_back(sqrt(1/(double )ikkuna*summa));
				gammab.push_back(pow(stdb.back(),0.33-1.0));
			}

			//Interpolate gammab...
			kk=0;
			while (freq[kk] < cb[1]){	//Search for the first frequency..
				kk++;
			}
			double whitemax=0;
			for (int i = 0;i<gammab.size()-1;i++){
				whitenedk[kk] = gammab[i]*yK[kk];
				while (freq[kk] < cb[i+2]){
					if (freq[kk] > cb[i+1]){
						whitenedk[kk] = ((gammab[i+1]-gammab[i])*(freq[kk]-cb[i+1])/(cb[i+2]-cb[i+1])+gammab[i])*yK[kk];
					}
					kk++;
				}
				kk++;
			}
			origwhite = whitenedk;
			//Pre whitening done
		
		
		return whitened;
	}
}
