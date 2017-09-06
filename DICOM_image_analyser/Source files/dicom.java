package test;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;

import com.sun.media.jai.codecimpl.JPEGCodec;
import com.sun.media.jai.codecimpl.JPEGImageEncoder;

public class dicom {
	
	public static class DicomToJpeg {
		   public static void main(String[] args) {
			 
			   File myDicomFile = new File("c:/dicomImage.dcm");
			   BufferedImage myJpegImage = null;
			   Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
			   ImageReader reader = (ImageReader) iter.next();
			   DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
			   try {
				   ImageInputStream iis = ImageIO.createImageInputStream(myDicomFile);
				   reader.setInput(iis, false);   
				   myJpegImage = reader.read(0, param);   
				   iis.close();
		    
			   if (myJpegImage == null) {
				      System.out.println("\nError: couldn't read dicom image!");
				      return;
				   }
			   
			   File myJpegFile = new File("c:/jpegImage.jpg");   
			   OutputStream output = new BufferedOutputStream(new FileOutputStream(myJpegFile));
			   JPEGImageEncoder encoder = (JPEGImageEncoder) JPEGCodec.createImageEncoder("img.jpg", output, null);
			   encoder.encode(myJpegImage);
			   output.close();
			   
		   } 
		   catch(IOException e) {
		      System.out.println("\nError: couldn't read dicom image!"+ e.getMessage());
		      return;
		   }

		}

}
}
