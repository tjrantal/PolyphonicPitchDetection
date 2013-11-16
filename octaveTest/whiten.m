
%Signal whitening approach from Kalpuri 2006 page 2, heading 2.1
function whitened = whiten(dataIn,constants)
		whitened = zeros(1,length(dataIn));
		%Calculate standard deviations and bandwise compression coefficients within frequencybands
		stdb = zeros(1,size(constants.Hb,2));
		gammab = zeros(1,size(constants.Hb,2));
		for i = 1:size(constants.Hb,2)
			stdb(i) = sqrt(sum(constants.Hb(:,i)'.*(dataIn.^2))/length(dataIn));
			gammab(i) = stdb(i)^(0.33-1);
		end
		%Interpolate gammab
		gamma = zeros(1,constants.fftWindow);
		%Set the compression coefficients for prior to below the first centre band and above the final
		gamma(1:find(constants.freq >= constants.cb(2),1,'first')) = gammab(1);
		gamma(find(constants.freq >= constants.cb(31),1,'first'):length(gamma)) = gammab(length(gammab));
		for i = 1:(length(gammab)-1)
			initF	= find(constants.freq >= constants.cb(i+1),1,'first');
			endF	= find(constants.freq >= constants.cb(i+2),1,'first');
			gamma(initF:endF) = linspace(gammab(i),gammab(i+1),endF-initF+1);
		end
		%Whiten the signal
		whitened = gamma.*dataIn;
		
		figure
		subplot(3,1,1)
		plot(constants.freq(find(constants.freq >= constants.cb(1),1,'first'):find(constants.freq >= constants.cb(32),1,'first')),whitened(find(constants.freq >= constants.cb(1),1,'first'):find(constants.freq >= constants.cb(32),1,'first')));
		subplot(3,1,2)
		plot(constants.freq(find(constants.freq >= constants.cb(1),1,'first'):find(constants.freq >= constants.cb(32),1,'first')),gamma(find(constants.freq >= constants.cb(1),1,'first'):find(constants.freq >= constants.cb(32),1,'first')));
		subplot(3,1,3)
		plot(constants.freq(find(constants.freq >= constants.cb(1),1,'first'):find(constants.freq >= constants.cb(32),1,'first')),dataIn(find(constants.freq >= constants.cb(1),1,'first'):find(constants.freq >= constants.cb(32),1,'first')));
		figure
		plot(dataIn./max(dataIn));
		hold on;
		plot(whitened./max(whitened),'r');
		keyboard
endfunction
