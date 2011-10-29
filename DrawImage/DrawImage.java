/*
	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

	N.B.  the above text was copied from http://www.gnu.org/licenses/gpl.html
	unmodified. I have not attached a copy of the GNU license to the source...

    Copyright (C) 2011 Timo Rantalainen
*/

package DrawImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;
import java.awt.image.*;
import java.awt.image.WritableRaster;

public class DrawImage extends JPanel{

	public BufferedImage preparationBuffer;
	public Image imageToDraw;
	double width;
	double height;
	Vector<Double> F0s;
	public DrawImage(){
		F0s = new Vector<Double>();
		//setBackground(new Color(0, 0, 0));
	}

	/*Double trace*/
	public void drawImage(double[] traceIn,int widthIn, int heightIn,double maximum) { 
		int[] image = new int[widthIn*heightIn];
      int pixel;
      int min =0;
      int max = 1;
      int x;
      int y;
      for (int i = 0; i<widthIn*heightIn;++i){
      	image[i] = 255<<24 | 0 <<16| 0 <<8| 0; 
      }
      for (int i = 0; i < traceIn.length;++i) {
         x = (int) Math.floor(((double)  i/(double) traceIn.length)*widthIn);
         y = heightIn - (int) Math.floor((traceIn[i]/maximum)*heightIn);
			if (x == widthIn) x = widthIn-1;
			if (x < 0) x = 0;
			if (y == heightIn) y = heightIn-1;
			if (y < 0) y = 0;
			image[x+y*widthIn]= 255<<24 | 255 <<16| 255 <<8| 255; 
		}
      imageToDraw = createImage(new MemoryImageSource(widthIn,heightIn,image,0,widthIn));
      //imageToDraw= imageToDraw.getScaledInstance(500, -1, Image.SCALE_SMOOTH);
		repaint();
	}
	
	/*Double trace with F0s*/
	public void drawImage(double[] traceIn,int widthIn, int heightIn,double maximum, Vector<Double> F0s) { 
		int[] image = new int[widthIn*heightIn];
      int pixel;
      int min =0;
      int max = 1;
      int x;
      int y;
      for (int i = 0; i<widthIn*heightIn;++i){
      	image[i] = 255<<24 | 0 <<16| 0 <<8| 0; 
      }
      for (int i = 0; i < traceIn.length;++i) {
         x = (int) Math.floor(((double)  i/(double) traceIn.length)*widthIn);
         y = heightIn - (int) Math.floor((traceIn[i]/maximum)*heightIn);
			if (x == widthIn) x = widthIn-1;
			if (x < 0) x = 0;
			if (y == heightIn) y = heightIn-1;
			if (y < 0) y = 0;
			image[x+y*widthIn]= 255<<24 | 255 <<16| 255 <<8| 255; 
		}
      imageToDraw = createImage(new MemoryImageSource(widthIn,heightIn,image,0,widthIn));
      this.F0s = F0s;
      //imageToDraw= imageToDraw.getScaledInstance(500, -1, Image.SCALE_SMOOTH);
		repaint();
	}

	/*Short trace*/
	public void drawImage(short[] traceIn,int widthIn, int heightIn) { 
		int[] image = new int[widthIn*heightIn];
      int pixel;
      int min =0;
      int max = 1;
      int x;
      int y;
      for (int i = 0; i<widthIn*heightIn;++i){
      	image[i] = 255<<24 | 0 <<16| 0 <<8| 0; 
      }
      for (int i = 0; i < traceIn.length;++i) {
         x = (int) Math.floor(((double)  i/(double) traceIn.length)*widthIn);
         y = (int) Math.floor((((double)  traceIn[i]/Math.pow(2.0,16.0))+0.5)*heightIn);
			if (x == widthIn) x = widthIn-1;
			if (x < 0) x = 0;
			if (y == heightIn) y = heightIn-1;
			if (y < 0) y = 0;
			image[x+y*widthIn]= 255<<24 | 255 <<16| 255 <<8| 255; 
		}
      imageToDraw = createImage(new MemoryImageSource(widthIn,heightIn,image,0,widthIn));
      //imageToDraw= imageToDraw.getScaledInstance(500, -1, Image.SCALE_SMOOTH);
		repaint();
	}
	
	/*Byte trace*/
	public void drawImage(byte[] traceIn,int widthIn, int heightIn) { 
		int[] image = new int[widthIn*heightIn];
      int pixel;
      int min =0;
      int max = 1;
      int x;
      int y;
      for (int i = 0; i<widthIn*heightIn;++i){
      	image[i] = 255<<24 | 0 <<16| 0 <<8| 0; 
      }
		for (int i = 0; i < traceIn.length;++i) {
         x = (int) Math.floor(((double)  i/(double) traceIn.length)*widthIn);
         y = (int) Math.floor((((double)  traceIn[i]/Math.pow(2.0,8.0))+0.5)*heightIn);
			if (x == widthIn) x = widthIn-1;
			if (x < 0) x = 0;
			if (y == heightIn) y = heightIn-1;
			if (y < 0) y = 0;
			image[x+y*widthIn]= 255<<24 | 255 <<16| 255 <<8| 255; 
		}
      imageToDraw = createImage(new MemoryImageSource(widthIn,heightIn,image,0,widthIn));
      //imageToDraw= imageToDraw.getScaledInstance(500, -1, Image.SCALE_SMOOTH);
		repaint();
	}
	
	public void paint(Graphics g) {
		g.drawImage(imageToDraw,0,0,null);
		if (F0s.size() > 0){
			System.out.println("F0"+F0s.get(0));
		   for (int i = 0; i<F0s.size();++i){
		   	g.drawString(Double.toString(F0s.get(i)),25,25*i);
		   }
		   F0s = new Vector<Double>();
      }
	}

} 
	
