clear all;
close all;
clc;

constants = struct();
constants.samplingRate = 44100;	%Hz
tempTime = [0:(constants.samplingRate-1)]/constants.samplingRate; 
test= zeros(size(tempTime,1),size(tempTime,2));
for i = 1:20
	test = test+(1/(2+i))*(sin(2*pi*82.4*i*tempTime) ...
	+sin(2*pi*123.5*i*tempTime) ...
	+sin(2*pi*164.8*i*tempTime));
end
test = test';
wavwrite(test,constants.samplingRate,16,'testSound.wav');
constants.desiredPitchDetectionSampleLength = 0.1; %Seconds
constants.fftWindow = 2^nextpow2(constants.samplingRate*constants.desiredPitchDetectionSampleLength);
constants.actualSL = constants.fftWindow/constants.samplingRate;
constants = createConstants(constants);
constants.fh = figure('position',[10 10 1000 500]);
constants.sp(1) = subplot(3,1,1);
plot(test);
%keyboard;
hold on;
constants.overlayH = plot(1:constants.fftWindow,test(1:constants.fftWindow),'r');
constants.sp(2) = subplot(3,1,2);
constants.freqVisualizationIndices = find(constants.freq <=10000);
constants.fftH = plot(constants.freq(constants.freqVisualizationIndices),zeros(1,length(constants.freqVisualizationIndices)));
constants.sp(3) = subplot(3,1,3);
constants.whitenedH = plot(constants.freq(constants.freqVisualizationIndices),zeros(1,length(constants.freqVisualizationIndices)));
hold on;
constants.detectedH = plot(constants.freq(constants.freqVisualizationIndices),zeros(1,length(constants.freqVisualizationIndices)),'r');
for i = 1:constants.fftWindow/2:length(test)-constants.fftWindow;
	constants.epoch = i:i+constants.fftWindow-1;
	detectedF0s = polyphonicPitchDetect(test(constants.epoch),constants);
	printF0s = "";
	for j = 1:length(detectedF0s)
		printF0s = [printF0s ' F' num2str(j-1) ' ' num2str(detectedF0s(j))];
	end
	disp(printF0s);
end
