function salience = getSalience(whitened,constants)
	salience = zeros(1,length(constants.f0cands));
	for i= 1:length(constants.f0cands)
		findices = constants.f0candsFreqBins(i).binIndices;
		j=1:length(findices);
		salience(i) = sum((constants.samplingRate*constants.freq(findices(j))+constants.alpha)./(j.*constants.samplingRate.*constants.freq(findices(j))+constants.beta).*whitened(findices(j)));
	end
endfunction;
