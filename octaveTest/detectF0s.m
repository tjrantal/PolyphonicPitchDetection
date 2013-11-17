function F0s = detectF0s(whitened, constants)
	Vector<Double> F0s = new Vector<Double>();
	Vector<Double> S = new Vector<Double>();  
	S.add(0.0);
	%Begin extracting F0s
	double smax=0;
	int index =0;
	int detectedF0s = 0;
	%F0 detection
	resultsk = zeros(1,length(constants.freq));
	double[] salience;
	double summa;
	while (S.lastElement() >= smax){
		%Calculating the salience function (the hard way...)
		salience = getSalience(whitened,constants);
		[salmax index] = max(salience);
		
		%Salience calculated
		++detectedF0s;
		F0s.add(constants.freq[index]); %First F0
		
		%Frequency cancellation
		for (int j = 1; j<=constants.harmonics;++j){
			if (index*j+1 <whitened.length){
				for (int i = -1;i <= 1;++i){
					resultsk[index*j+i] = resultsk[index*j+i]+(constants.samplingRate*constants.freq[index*j+i]+alpha)/(j*constants.samplingRate*constants.freq[index*j+i]+beta)*whitened[index*j+i];
					if (whitened[index*j+i]-resultsk[index*j+i] > 0){
						whitened[index*j+i]= whitened[index*j+i]-resultsk[index*j+i]*dee;
					}else{
						whitened[index*j+i]=0;
					}
				}
			}
		}
		%requency cancellation done
		%Polyphony estimation
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
		%Polyphony estimated
	}
	%The last F0 is extra...
	%System.out.println("Remove extra");
	if (F0s.size() > 1){
		F0s.remove(F0s.size()-1);
	}
	return F0s;
endfunction;
