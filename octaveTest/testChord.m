%	This program is free software: you can redistribute it and/or modify
%    it under the terms of the GNU General Public License as published by
%    the Free Software Foundation, either version 3 of the License, or
%    (at your option) any later version.
%
%    This program is distributed in the hope that it will be useful,
%    but WITHOUT ANY WARRANTY; without even the implied warranty of
%    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
%    GNU General Public License for more details.
%
%    You should have received a copy of the GNU General Public License
%    along with this program.  If not, see <http://www.gnu.org/licenses/>.
%
%	N.B.  the above text was copied from http://www.gnu.org/licenses/gpl.html
%	unmodified. I have not attached a copy of the GNU license to the source...
%
%    Copyright (C) 2013 Timo Rantalainen
%
%Modified from mary.m downloaded from http://www.ee.columbia.edu/~ronw/dsp/

close all;
clear all;
clc;
addpath('sandersAndWeissGuitaSynthesization');

notes;   %load note frequencies
%getexcitesignal;
e = wavread('sandersAndWeissGuitaSynthesization/excite-picked-nodamp.wav');
e = e';

fs = 44100;

%loop filter:
Bpoles = [0.8995 0.1087];
Apoles = [1 0.0136];

o = 2;   %octave
nd = .3; %note duration
p = .9;  %pluck position

%testNote = kspluck(E(o), nd, fs, e, B, A, p);
note1 = kspluck(E(o), 3*nd, fs, e, Bpoles, Apoles, p);	%82.410Hz
note2 = kspluck(B(o+1), 3*nd, fs, e, Bpoles, Apoles, p);	%123.48
note3 = kspluck(E(o+1), 3*nd, fs, e, Bpoles, Apoles, p);	%164.82

%Add delays...
delayPadding = zeros(1,int32(fs*0.1));
note1 = [note1 delayPadding delayPadding];
note2 = [delayPadding note2 delayPadding];
note3 = [delayPadding delayPadding note3];

L = mean([note1; note2; note3]);

figure
plot(L)
wavwrite(L',fs,16,'chord.wav');
