function constants = createConstants(constantsIn)
	if nargin > 0
		constants = constantsIn;
	else
		constants = struct();
		constants.fftWindow = 2^12;		%Data points
		constants.samplingRate = 44100; %Hz
	end
	%Create constants required for polyphonicPitchDetect
	%CB filtebank
	constants.cb = zeros(1,32);
	%CB filterbank has always the same values
	b = 1:32;
	constants.cb = 229.0*(10.0.^(b/21.4)-1.0); %frequency division
	%Frequencies, always the same after capture init...
	%captured signal will be zero padded to twice its length
	constants.freq = zeros(1,constants.fftWindow);
	b = 1:constants.fftWindow;
	constants.freq= (b-1)*constants.samplingRate/constants.fftWindow;
	constants.f0index = find(constants.freq <= 1500 && constants.freq >=60);
	constants.f0cands = constants.freq(constants.f0index);
endfunction
