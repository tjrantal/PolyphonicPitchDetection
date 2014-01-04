function [whitened detectedFreqs] = cancelPitch(whitened,detectedFreqs,constants,index)
	%Frequency cancellation
	binsToCancel = constants.f0candsFreqBins(index).binIndices;
	for j = 1:length(binsToCancel)
			detectedFreqs(binsToCancel(j)) = detectedFreqs(binsToCancel(j))+((constants.samplingRate*constants.freq(binsToCancel(j))+constants.alpha)/(j*constants.samplingRate*constants.freq(binsToCancel(j))+constants.beta))*whitened(binsToCancel(j));
			subtract = detectedFreqs(binsToCancel(j))*constants.dee;
			whitened(binsToCancel(j))=max([0 whitened(binsToCancel(j))-subtract]);
	end
endfunction
