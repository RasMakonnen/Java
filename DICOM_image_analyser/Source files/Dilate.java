package test;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Dilate {
	
	public Dilate(  String filename ) {
		   
	      try{	
	         System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	         Mat source = Imgcodecs.imread(filename,  Imgcodecs.CV_LOAD_IMAGE_COLOR);
	         Mat destination = new Mat(source.rows(),source.cols(),source.type());
	         
	         source = Imgcodecs.imread(filename,  Imgcodecs.CV_LOAD_IMAGE_COLOR);
	         int dilation_size = 3;
	         destination = source;
	         
	         Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
	         Imgproc.dilate(source, destination, element1);
	         Imgcodecs.imwrite(filename, destination);

	         
	      }catch (Exception e) {
	         System.out.println("error: " + e.getMessage());
	      } 
	   }

}
