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
	b = 1:(constants.fftWindow+1);
	constants.freq= (b-1)*(constants.samplingRate/2)/constants.fftWindow;
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
	

	%constants.f0index = find(constants.freq <= 1500 & constants.freq >=60);
	%constants.f0cands = constants.freq(constants.f0index);
	%Create actual candidate notes (http://www.phy.mtu.edu/~suits/NoteFreqCalcs.html)
	n = (1:(5*12))-1;	%Five octaves of candidate notes. Use half step to get out of tune freqs
	f0 = 55.0;			%Hz, A three octaves below A above the middle C
	a = 2.0^(1.0/12.0);
	constants.f0cands = f0*a.^n;
	%Pre-calculate frequency bins to include for a specific f0 candidate
	constants.harmonics = 20;
	halfBinWidth = ((constants.samplingRate/2)/constants.fftWindow)/2;
	for i = 1:length(constants.f0cands)
		binIndices = [];
		for h = 1:constants.harmonics
			testi = find(constants.freq > (constants.f0cands(i)*h-halfBinWidth) & constants.freq < (constants.f0cands(i)*h+halfBinWidth));
			binIndices = [binIndices testi];
		end
		constants.f0candsFreqBins(i).binInidices = binIndices;
	end
	
	constants.alpha = 52.0; %Hz
	constants.beta = 320.0; %Hz
	constants.dee = 0.89;
	
endfunction
