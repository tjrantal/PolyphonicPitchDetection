function pitches = polyphonicPitchDetect(signalIn,constants)
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
	fftAmp = abs(fftSignal(1:(constants.fftWindow+1))); %Ignore the second half of the fft
	whitened = whiten(fftAmp,constants);
	%Implement estimating polyphony and frequency cancellation here
	pitches = [];
	detectedF0s = 0;
	detectedFreqs = zeros(size(whitened,1),size(whitened,2));
	smax = 0;
	S = [];
	S(1) = 0;
	%Loop while smax is increasing
	while S(length(S)) >=smax
		salience = getSalience(whitened,constants);
		[salmax index] = max(salience);
		[whitened detectedFreqs] = cancelPitch(whitened,detectedFreqs,constants,index);
		%estimate smax here
		detectedF0s = detectedF0s+1;
		sumDetected = sum(detectedFreqs);
		S = [S sumDetected/(detectedF0s^0.7)];
		if S(length(S)) > smax
			smax = S(length(S));
			pitches(detectedF0s ) = constants.f0cands(index);
		end
	end
	%Polyphony and pitches estimated
	set(constants.overlayH,'xdata',constants.epoch,'ydata',hannWindowed);
	set(constants.fftH,'ydata',fftAmp(constants.freqVisualizationIndices));
	set(constants.whitenedH,'ydata',whitened(constants.freqVisualizationIndices));
	set(constants.detectedH,'ydata',detectedFreqs(constants.freqVisualizationIndices));
	drawnow();
	figure,plot(fftAmp(1:1024))
	figure,plot(whitened(1:1024))
endfunction
