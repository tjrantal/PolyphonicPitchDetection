function pitch = polyphonicPitchDetect(signalIn,constants)
	%Apply hann windowing
	hannWindowed = signalIn.*hanning(length(signalIn));
	%Double the signal length by appending zeroes
	if size(hannWindowed,1) > size(hannWindowed,2)
		hannWindowed = hannWindowed';
	end
	appended = [hannWindowed zeros(1,length(hannWindowed))];
	fftSignal = fft(appended);
	fftSignal= fftSignal./(length(fftSignal)/2+1);
    fftSignal(1) = fftSignal(1)/2;
	fftAmp = abs(fftSignal(1:constants.fftWindow)); %Ignore the second half of the fft
	whitened = whiten(fftAmp,constants);
	set(constants.overlayH,'xdata',constants.epoch,'ydata',signalIn);
	set(constants.fftH,'ydata',fftAmp(constants.freqVisualizationIndices));
	set(constants.whitenedH,'ydata',whitened(constants.freqVisualizationIndices)); 
	drawnow();
endfunction
