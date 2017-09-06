package test;

import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferByte;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.imageio.plugins.dcm.DicomStreamMetaData;
import org.dcm4che2.io.DicomOutputStream;

public class Kernelfilter {
	public Kernelfilter(String path, String output) {

		File sourceFile = new File(path);
		
		try {
			   Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
			   ImageReader reader = (ImageReader)iter.next();
			   DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
			   ImageInputStream iis = ImageIO.createImageInputStream(sourceFile);

			   reader.setInput(iis, false);
			   Raster raster = reader.readRaster(0, param);     //read pixel array
			   
			   if (raster == null) {
				      System.out.println("Error: couldn't read Dicom image!");
				      return;
				   }
				   iis.close();
				   
				float[] emboss = new float[] { -2,0,0,   0,1,0,   0,0,2 };
				Kernel kernel = new Kernel(3, 3, emboss);     // kernel matrix
				ConvolveOp op = new ConvolveOp(kernel);       // implement a convolution 
				Raster newRaster = op.filter(raster, null);   // filtration of image
				DataBufferByte buffer = (DataBufferByte) newRaster.getDataBuffer();  // extract the array element from the raster DataBuffer
				byte[] pixelData = buffer.getData();            
				DicomStreamMetaData dsmd = (DicomStreamMetaData) reader.getStreamMetadata();
				DicomObject dicom = dsmd.getDicomObject();        // Create a Dicom object without pixel data
				dicom.putBytes(Tag.PixelData, dicom.vrOf(Tag.PixelData), pixelData);  // qdd to the Dicom object the pixel data extracted from the DataBuffer
				File f = new File(output);   
				FileOutputStream fos = new FileOutputStream(f);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				DicomOutputStream dos = new DicomOutputStream(bos);      
				dos.writeDicomFile(dicom);
				dos.close();

	   }
		
	   catch(Exception e) {
		      System.out.println("Error: couldn't read dicom image! "+ e.getMessage());
		      return;
		   }
		
		
	}	

}
