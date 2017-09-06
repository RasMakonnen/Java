package test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.*;

public class gaussianfilter {
	


	 public gaussianfilter( String filename ){
		   
			try {
			     System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
			         System.out.println(filename);
			     Mat source = Imgcodecs.imread(filename,
			        		 Imgcodecs.CV_LOAD_IMAGE_COLOR);
			         
			         Mat destination = new Mat(source.rows(),source.cols(),source.type());
			         Imgproc.GaussianBlur(source, destination,new Size(15,15), 0);
			         Imgcodecs.imwrite(filename, destination);
			      
			      } catch (Exception e) {
			         //System.out.println("Error: " + e.getMessage());
			      }
			   }
			

}
