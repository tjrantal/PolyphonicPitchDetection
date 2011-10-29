package Analysis;
import java.util.*;
import ui.*;
public class Klapuri{
	public double[] whitened;
	public Vector<Integer> f0indices;
	PolyphonicPitchDetection mainProgram;
	public Klapuri(double[] data, double max,PolyphonicPitchDetection mainProgram){
		//whitened = (double[]) data.clone();
		this.mainProgram = mainProgram;
		/*Whiten the data*/
		whitened = whiten(data,mainProgram);
		f0indices = detectF0s(whitened);
	}
	
	Vector<Integer> detectF0s(double[] whitened){
		Vector<Integer> f0indices = new Vector<Integer>();
		return f0indices;
	}
	
	double[] whiten(double[] dataIn,PolyphonicPitchDetection mainProgram){
		double[] whitened = new double[dataIn.length];
			
		/*Calculate signal energies in filter windows??*/
		Vector<Double> Hb = new Vector<Double>();
		Vector<Integer> indeksit = new Vector<Integer>();
		Vector<Double> gammab = new Vector<Double>();
		Vector<Double> stdb = new Vector<Double>();
		
		int kk;
		for (int i = 1;i<31;++i){
			Hb.clear();
			indeksit.clear();
			kk=0;
			while (mainProgram.freq[kk] <= mainProgram.cb[i+1]){
				if (mainProgram.freq[kk] >= mainProgram.cb[i-1]){
					indeksit.add(kk);
					Hb.add(1-Math.abs(mainProgram.cb[i]-mainProgram.freq[indeksit.lastElement()])/(mainProgram.cb[i+1]-mainProgram.cb[i-1]));
				}
				++kk;
			}
			double summa = 0;
			for (int j = 0;j< Hb.size();++j){
				summa += Hb.get(j)*Math.pow(dataIn[indeksit.get(j)],2.0);
			}
			stdb.add(Math.sqrt(1/((double)mainProgram.fftWindow)*summa));
			gammab.add(Math.pow(stdb.lastElement(),0.33-1.0));
		}

		//Interpolate gammab...
		kk=0;
		while (mainProgram.freq[kk] < mainProgram.cb[1]){	//Search for the first frequency..
			++kk;
		}
		double whitemax=0;
		for (int i = 0;i<gammab.size()-1;++i){
			whitened[kk] = gammab.get(i)*dataIn[kk];
			while (mainProgram.freq[kk] < mainProgram.cb[i+2]){
				if (mainProgram.freq[kk] > mainProgram.cb[i+1]){
					whitened[kk] = ((gammab.get(i+1)-gammab.get(i))*(mainProgram.freq[kk]-mainProgram.cb[i+1])/(mainProgram.cb[i+2]-mainProgram.cb[i+1])+gammab.get(i))*dataIn[kk];
				}
				++kk;
			}
			++kk;
		}
		//Pre whitening done
	
		return whitened;
	}
}
