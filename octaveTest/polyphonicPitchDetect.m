function pitch = polyphonicPitchDetect(signalIn,constants)
	%Apply hann windowing
	hannWindowed = signalIn.*hanning(length(signalIn));
	%Double the signal length by appending zeroes
	if size(hannWindowed,1) > size(hannWindowed,2)
		hannWindowed = hannWindowed';
	end
	appended = [hannWindowed zeros(1,length(hannWindowed))];
	set(constants.overlayH,'xdata',constants.epoch,'ydata',signalIn);
	set(constants.fftH,'ydata',appended);
	drawnow();
endfunction
