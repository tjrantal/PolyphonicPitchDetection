function [whitened detectedFreqs] = cancelPitch(whitened,detectedFreqs,constants,index)
	%Frequency cancellation
	binsToCancel = constants.f0candsFreqBins(index).binIndices;
	for j = 1:length(binsToCancel)
		%cancel adjacent bins as well
		for k =-1:1
			detectedFreqs(binsToCancel(j)+k) = detectedFreqs(binsToCancel(j)+k)+((constants.samplingRate*constants.freq(binsToCancel(j)+k)+constants.alpha)/(j*constants.samplingRate*constants.freq(binsToCancel(j)+k)+constants.beta))*whitened(binsToCancel(j)+k);
			subtract = detectedFreqs(binsToCancel(j)+k)*constants.dee;
			whitened(binsToCancel(j)+k)=max([0 whitened(binsToCancel(j)+k)-subtract]);
		end
	end
endfunction
