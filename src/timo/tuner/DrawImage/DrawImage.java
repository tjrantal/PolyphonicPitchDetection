/*
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

N.B. the above text was copied from http://www.gnu.org/licenses/gpl.html
unmodified. I have not attached a copy of the GNU license to the source...

Copyright (C) 2011-2014 Timo Rantalainen
*/

package timo.tuner.DrawImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;
import java.awt.BasicStroke;
import java.awt.image.*;
import java.awt.image.WritableRaster;
import java.text.DecimalFormat;	//For rounding text

public class DrawImage extends JPanel{

	public BufferedImage bufferedImage;	/**Draw on this bi off screen, render in paint*/
	int[] emptyImage;
	int colorIndex;
	int zero;
	int maxBitValue = 0;
	int[][] brushColor;
	Dimension imageSize;
	double width;
	double height;
	public String f0s;
	DecimalFormat dfo;
	private int[][] coordinates;
	private BasicStroke basicStroke;
	private Graphics2D g2;

	public DrawImage(Dimension imageSize, int maxBitValue){
		this(imageSize);
		this.maxBitValue = maxBitValue;
		zero = maxBitValue/2;
	}

	public DrawImage(Dimension imageSize){
		this.imageSize = imageSize;
		setPreferredSize(imageSize);
		dfo = new DecimalFormat("0.0");
		coordinates = null;
		bufferedImage = new BufferedImage(imageSize.width,imageSize.height,BufferedImage.TYPE_INT_ARGB);
		emptyImage = new int[imageSize.width*imageSize.height];
		/*Set pixels to fully opaque black*/
		for (int i = 0; i<emptyImage.length;++i){
			emptyImage[i] = ((0xff & 255) << 24) | ((0xff & 0) << 16) | ((0xff & 0) << 8) | ((0xff & 0) );
		}
		clearPlot();
		paintImageToDraw();
		colorIndex = 0;
		maxBitValue = (int) (Math.pow(2.0,16.0)-1);
		zero = (int) (((double)maxBitValue)/2.0);

		brushColor = new int[][]{{255,0,0},{0,255,0},{0,0,255},{255,0,255},{255,255,0},{0,255,255},{127,127,127},{127,127,255},{255,127,0}};
		basicStroke = new BasicStroke();
	}

	public void clearPlot(){
		/*Empty the image*/
		bufferedImage.setRGB(0,0,imageSize.width,imageSize.height,emptyImage,0,imageSize.width);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.setBackground(Color.BLACK);	/*Set the background color to black*/
		g2.dispose();
	}

	public void plotNumber(Vector<Double> F0s){
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.setColor(new Color(255,255,255));
		g2.setFont(new Font("Helvetica",Font.PLAIN,24));
		f0s = "";
		if (F0s.size() > 0){
			for (int i = 0; i<F0s.size();++i){
				f0s += "F"+i+" "+dfo.format(F0s.get(i))+" ";
			}
		}

		//g2.drawString(dfo.format(number),(int) (imageSize.width/2),(int) (imageSize.height/2));
		g2.drawString(f0s,25,25);
		g2.dispose();
		repaint();
	}

	/*Short trace*/
	public void plotTrace(short[] traceIn) {
	 	Integer[] tempTrace = new Integer[traceIn.length];
	 	for (int i = 0; i<traceIn.length;++i){
	 		tempTrace[i] = (int) traceIn[i];
	 	}
	 	plotTrace(tempTrace);
	}


	/*Int trace*/
	public void plotTrace(Integer[] traceIn) {
		Dimension size = imageSize;
	      int pixel;
	      int min =0;
	      int max = 1;
	      int x;
	      int y;
	      coordinates = new int[traceIn.length][2];
	      for (int i = 0; i < traceIn.length;++i) {
			x = (int) Math.floor(((double) i/(double) traceIn.length)*size.width);
			y = (int) Math.floor((((double) (-traceIn[i]+(zero)))/((double) maxBitValue))*size.height);
			coordinates[i][0] = checkVal(x,size.width);
			coordinates[i][1] = checkVal(y,size.height);
	      }
		plotCoordinates(coordinates);

	}

		/*Double trace with normalization*/
	public void plotTrace(double[] traceIn,double normalization,int length) {
	   	double[] temp = new double[length];
	   	for (int i =0;i<length;++i){
	   	 	temp[i]=traceIn[i];
	   	}
	   	plotTrace(temp,normalization);
	}

	/*Double trace with normalization*/
	public void plotTrace(double[] traceIn,double normalization) {
		Dimension size = imageSize;
	      int pixel;
	      int min =0;
	      int max = 1;
	      int x;
	      int y;
	      coordinates = new int[traceIn.length][2];
	      for (int i = 0; i < traceIn.length;++i) {
			x = (int) Math.floor(((double) i/(double) traceIn.length)*size.width);
			y = (int) Math.floor((( (normalization-traceIn[i]))/(normalization))*size.height);
			coordinates[i][0] = checkVal(x,size.width);
			coordinates[i][1] = checkVal(y,size.height);
	      }
		plotCoordinates(coordinates);

	}

	private void plotCoordinates(int[][] coordinates){
	      /**Plot the line*/
	      if (coordinates != null){
			/*Get the graphics2D to draw on the bufferedImage*/
			Graphics2D g2 = bufferedImage.createGraphics();
			g2.setColor(new Color(brushColor[colorIndex][0],brushColor[colorIndex][1],brushColor[colorIndex][2]));
			g2.setStroke(basicStroke);

			/**Go through the coordinates from previous to next, draw the connecting line*/
			for (int i = 0;i<coordinates.length-1;++i){
				g2.drawLine(coordinates[i][0],coordinates[i][1],coordinates[i+1][0],coordinates[i+1][1]);
			}
			g2.dispose();
		}
	}

	public void paintImageToDraw(){
		repaint();
	}

	private int checkVal(int val, int limit){
		if (val >= limit){val = limit-1;}
		if (val < 0){val = 0;}
		return val;
	}

	public void paint(Graphics g) {
		g.drawImage(bufferedImage,0,0,null);
	}

}
