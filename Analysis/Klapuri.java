package Analysis;
import java.util.*;
import ui.*;
public class Klapuri{
	public double[] whitened;
	public Vector<Double> f0s;
	PolyphonicPitchDetection mainProgram;
	int harmonics = 20;
	int ymparoivia = 1;
	double alpha = 52.0; //Hz
	double beta = 320.0; //Hz
	double dee = 0.89;
	public Klapuri(double[] data, double max,PolyphonicPitchDetection mainProgram){
		//whitened = (double[]) data.clone();
		this.mainProgram = mainProgram;
		/*Whiten the data*/
		whitened = whiten(data,mainProgram);
		f0s = detectF0s(whitened,mainProgram);
	}
	
	Vector<Double> detectF0s(double[] whitened, PolyphonicPitchDetection mainProgram){
		Vector<Double> F0s = new Vector<Double>();
		Vector<Double> S = new Vector<Double>();  
		S.add(0.0);
		//Begin extracting F0s
		double smax=0;
		int sijainti =0;
		int detectedF0s = 0;
		//F0 detection
		double[] resultsk = new double [mainProgram.freq.length];
		double[] salience;
		double summa;
		while (S.lastElement() >= smax){
			//Calculating the salience function (the hard way...)
			salience = new double [mainProgram.freq.length];
			double salmax = 0;
			
			for (int i= 0;i<mainProgram.f0index.length;++i){
				summa = 0;
				for (int j = 1;j <harmonics;++j){
					if (mainProgram.f0index[i]*j <mainProgram.freq.length){
						summa +=(mainProgram.samplingRate*mainProgram.freq[mainProgram.f0index[i]*j]+alpha)/(j*mainProgram.samplingRate*mainProgram.freq[mainProgram.f0index[i]*j]+beta)*whitened[mainProgram.f0index[i]*j];
					}
				}
				salience[mainProgram.f0index[i]] = summa;
				if (salience[mainProgram.f0index[i]] > salmax){
					sijainti= mainProgram.f0index[i];
					salmax = salience[mainProgram.f0index[i]];
				}
			}
			
			//Salience calculated
			++detectedF0s;
			F0s.add(mainProgram.freq[sijainti]); //First F0
			
			//Frequency cancellation
			for (int j = 1; j<=harmonics;++j){
				if (sijainti*j+1 <whitened.length){
					for (int i = -1;i <= 1;++i){
						resultsk[sijainti*j+i] = resultsk[sijainti*j+i]+(mainProgram.samplingRate*mainProgram.freq[sijainti*j+i]+alpha)/(j*mainProgram.samplingRate*mainProgram.freq[sijainti*j+i]+beta)*whitened[sijainti*j+i];
						if (whitened[sijainti*j+i]-resultsk[sijainti*j+i] > 0){
							whitened[sijainti*j+i]= whitened[sijainti*j+i]-resultsk[sijainti*j+i]*dee;
						}else{
							whitened[sijainti*j+i]=0;
						}
					}
				}
			}
			//requency cancellation done
			//Polyphony estimation
			if (S.size() < detectedF0s){
				S.add(0.0);
			}
			summa = 0;
			for (int i = 0; i< resultsk.length;++i){
				summa += resultsk[i];
			}
			S.set(S.size()-1,summa/Math.pow(detectedF0s,0.7));
			if (S.lastElement() > smax){
				smax = S.lastElement();
			}
			//Polyphony estimated
		}
		//The last F0 is extra...
		System.out.println("Remove extra");
		if (F0s.size() > 1){
			F0s.remove(F0s.size()-1);
		}
		return F0s;
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
		/*Add the higher frequencies wihtout whitening*/
		for (int i = gammab.size();i<whitened.length;++i){
			whitened[i] = dataIn[i];
		}
		return whitened;
	}
}
