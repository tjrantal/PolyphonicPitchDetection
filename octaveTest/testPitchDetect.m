test = wavread('chord.wav');
constants = struct();
constants.samplingFreq = 44100;	%Hz
constants.desiredPitchDetectionSampleLength = 0.1; %Seconds
constants.fftWindow = 2^nextpow2(samplingFreq*desiredPitchDetectionSampleLength);
constants.actualSL = fftWindow/samplingFreq;
constants.fh = figure('position',[10 10 1000 500]);
constants.sp(1) = subplot(2,1,1)
plot(test);
hold on;
constants.overlayH = plot(1:fftWindow,test(1:fftWindow),'r');
constants.sp(2) = subplot(2,1,2);
constants.fftH = plot(zeros(1,fftWindow*2));%plot(freqs,zeros(1,fftWindow*2));
for i = 1:fftWindow/2:length(test)-fftWindow;
	constants.epoch = i:i+fftWindow-1;
	polyphonicPitchDetect(test(constants.epoch),constants);
	disp(num2str(i));
end
