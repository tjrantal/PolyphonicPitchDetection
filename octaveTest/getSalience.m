function salience = getSalience(whitened,constants)
	for i= 1:length(constants.f0index)
		findices = constants.f0index(i)*([1:20]);
		summa = 0;
		for (int j = 1;j <constants.harmonics;++j){
			if (constants.f0index[i]*j <constants.freq.length){
				summa +=(constants.samplingRate*constants.freq[constants.f0index[i]*j]+alpha)/(j*constants.samplingRate*constants.freq[constants.f0index[i]*j]+beta)*whitened[constants.f0index[i]*j];
			end
		end
		salience[constants.f0index[i]] = summa;
		if (salience[constants.f0index[i]] > salmax){
			index= constants.f0index[i];
			salmax = salience[constants.f0index[i]];
		end
	end
endfunction;
