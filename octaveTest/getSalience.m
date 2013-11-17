function salience = getSalience(whitened,constants)
	salience = zeros(1,length(constants.freq));
	for i= 1:length(constants.f0index)
		findices = constants.f0index(i)*([1:constants.harmonics]);
		findices = findices(find(findices <= length(constants.freq)));
		j=1:length(findices);
		salience(constants.f0index(i)) = sum((constants.samplingRate*constants.freq(findices(j))+constants.alpha)./(j.*constants.samplingRate.*constants.freq(findices(j))+constants.beta).*whitened(findices(j)));
	end
endfunction;
