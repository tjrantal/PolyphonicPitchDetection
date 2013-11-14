function e = getexcitesignal(B, A, W, f, fs)
%Takes a sound sample and puts it through an inverse filter
%to find the corresponding excitation signal
%
%B = numerator coeeficients of loop filter 
%A = denominator coeeficients of loop filter 
%W = sound sample to filter
%f = fundamental frequency of W
%fs = sampling frequency
%
%NOTE: the excitation signal returned will be the same length as the input
%      signal.  Since it dies away so quickly it makes sense to only save
%      the first 300-500 ms.

%generate excitation signal by inverse filtering of recorded guitar note

N=fix(fs/f);


%inverse filter: A(z) = 1 - H(z)z^-N 

%H(z) is IIR, so break up into numerator and denominator:
hnum = B;
hden = A;

%A(z) = (Hd(z) - Hn(z)z^-N)/Hd(z)
n = [hden zeros(1, N - length(hden)) -hnum];
d = hden;


e = filter(n, d, W);
