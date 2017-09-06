package test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

public class Erode {
	
	public Erode(  String filename ) {
		   
	      try{	
	         System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	         Mat source = Imgcodecs.imread(filename,  Imgcodecs.CV_LOAD_IMAGE_COLOR);
	         Mat destination = new Mat(source.rows(),source.cols(),source.type());
	         
	         destination = source;

	         int erosion_size = 3;
	         int dilation_size = 5;
	         
	         Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
	         Imgproc.erode(source, destination, element);
	         Imgcodecs.imwrite(filename, destination);

	         
	      }catch (Exception e) {
	         System.out.println("error: " + e.getMessage());
	      } 
	   }
	}


