function Y=kspluck(f,length, fs, excitation, B, A, p)
%Karplus-Strong model with additional paremeters
%   ks(f,length) 
%   f=frequency
%   length=time (seconds)
%   fs = sampling freqency
%   excitation=string excitation signal
%   B = numerator coefficients of loop filter
%   A = denominator coefficients of loop filter
%   p = pluck position along waveguide (0 < p < 1 - fraction of waveguide length)

N=fix(fs/f);

%modify length of excitation signal to match desired duration
if(max(size(excitation)) <= length*fs)
  X = [excitation zeros(1,length*fs - max(size(excitation)))];
else
  X = excitation(1:length*fs) ;
end

hnum = B;
hden = A;

%lagrange interpolation filter to account for fractional delay
% (keeps the string in tune with the desired frequency)
l=lagrange(3,f/fs);

%     Hd(z)L(z)z^-N               
b1=[zeros(1,N) conv(l, hden)];

%                Hd(z) - Hl(z)L(z)z^-N
a1=[hden zeros(1,N-max(size(hden))) -1*conv(hnum,l)];

%pluck location:
p = round(p*N);

P = filter([1 zeros(1,p-1) -1], 1, X);

%no initial conditions
y=filter(b1,a1,P);

Y = y;

