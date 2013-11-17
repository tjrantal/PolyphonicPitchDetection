function constants = createConstants(constantsIn)
	if nargin > 0
		constants = constantsIn;
	else
		constants = struct();
		constants.fftWindow = 2^12;		%Data points
		constants.samplingRate = 44100; %Hz
	end
	%Frequencies, signal will be zero padded to twice its length
	constants.freq = zeros(1,constants.fftWindow);
	b = 1:constants.fftWindow;
	constants.freq= (b-1)*constants.samplingRate/constants.fftWindow;
	%Create constants required for polyphonicPitchDetect
	%CB filtebank
	constants.cb = zeros(1,32);
	%CB filterbank has always the same values
	b = 1:32;
	constants.cb = 229.0*(10.0.^(b/21.4)-1.0); %frequency division
	%Pre-calculate the frequency bank
	%Signal whitening approach from Kalpuri 2006 page 2, heading 2.1
	%Create the bandpass filterbank, 30 triangular power response filters
	constants.Hb = zeros(constants.fftWindow,length(constants.cb)-2);
	for i=2:(length(constants.cb)-1) %Consider the centrebands, triangle from (i-1:i+1)
		kk=find(constants.freq >=constants.cb(i-1),1,'first'); %The lower border
		while (constants.freq(kk) <= constants.cb(i+1))	%loop till the higher border
			if constants.freq(kk) <= constants.cb(i)
				constants.Hb(kk,i-1) = (1-abs(constants.cb(i)-constants.freq(kk))/(constants.cb(i)-constants.cb(i-1)));
			else	%Descending limb
				constants.Hb(kk,i-1) = (1-abs(constants.cb(i)-constants.freq(kk))/(constants.cb(i+1)-constants.cb(i)));
			end
			kk = kk+1;
		end
	end
	

	constants.f0index = find(constants.freq <= 1500 & constants.freq >=60);
	constants.f0cands = constants.freq(constants.f0index);
	constants.harmonics = 20;
	constants.alpha = 52.0; %Hz
	constants.beta = 320.0; %Hz
	constants.dee = 0.89;
	
endfunction
