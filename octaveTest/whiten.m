function whitened = whiten(dataIn,constants)
		whitened = zeros(1,length(dataIn));
		
		%Calculate signal energies in filter windows??
		gammab = [];
		stdb = [];
		for i=2:(length(constants.cb)-1)
			Hb=[];
			indexes=[];
			kk=1;
			while (constants.freq(kk) <= constants.cb(i+1))
				if (constants.freq(kk) >= constants.cb(i-1))
					indexes = [indexes kk];
					Hb = [Hb (1-abs(constants.cb(i)-constants.freq(kk))/(constants.cb(i+1)-constants.cb(i-1)))];
				end
				kk = kk+1;
			end
			summa = 0;
			for j=1:length(Hb)
				summa = summa + Hb(j)*(dataIn(indexes(j))^2.0);
			end
			stdb = [stdb sqrt(1/(constants.fftWindow)*summa)];
			gammab = [gammab stdb(length(stdb))^(0.33-1.0)];
		end
		figure
		subplot(3,1,1)
		plot(stdb)
		subplot(3,1,2)
		plot(gammab)
		subplot(3,1,3)
		plot(constants.freq(find(constants.freq >= constants.cb(1),1,'first'):find(constants.freq >= constants.cb(32),1,'first')),dataIn(find(constants.freq >= constants.cb(1),1,'first'):find(constants.freq >= constants.cb(32),1,'first')));
		%keyboard
		%Interpolate gammab
		kk = find(constants.freq >= constants.cb(1),1,'first');
		double whitemax=0;
		testInterp = [];
		for i=1:(length(gammab)-1)
			whitened(kk) = gammab(i)*dataIn(kk);
			testInterp(kk) = gammab(i);
			while constants.freq(kk) < constants.cb(i+2)
				if constants.freq(kk) > constants.cb(i+1)
					whitened(kk) = ((gammab(i+1)-gammab(i))*(constants.freq(kk)-constants.cb(i+1))/(constants.cb(i+2)-constants.cb(i+1))+gammab(i))*dataIn(kk);
					testInterp(kk) =((gammab(i+1)-gammab(i))*(constants.freq(kk)-constants.cb(i+1))/(constants.cb(i+2)-constants.cb(i+1))+gammab(i));
				end
				kk=kk+1;
			end
			kk=kk+1;
		end
		figure
		plot(testInterp)
		keyboard;
		%Pre whitening done
		%Add the higher frequencies wihtout whitening
		for i = (length(gammab)+1):length(whitened)
			whitened(i) = dataIn(i);
		end
endfunction
