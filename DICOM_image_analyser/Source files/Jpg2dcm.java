package test;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.util.UIDUtils;

public class Jpg2dcm {
	
	public Jpg2dcm(String path, String outpath) {
	      // TODO Auto-generated method stub
		
		System.out.println("path : "+ path);
		System.out.println("outpath: "+outpath);
		File jpgSource = new File(path); 
		File dcmDestination = new File(outpath);
		
		try { 
			   BufferedImage jpegImage = ImageIO.read(jpgSource);
			   if (jpegImage == null)
			      throw new Exception("Invalid file."); 
			   int colorComponents = jpegImage.getColorModel().getNumColorComponents();
			   int bitsPerPixel = jpegImage.getColorModel().getPixelSize();
			   int bitsAllocated = (bitsPerPixel / colorComponents);
			   int samplesPerPixel = colorComponents;
			   DicomObject dicom = new BasicDicomObject();
			   dicom.putString(Tag.SpecificCharacterSet, VR.CS, "ISO_IR 100");
			   dicom.putString(Tag.PhotometricInterpretation, VR.CS, samplesPerPixel == 3 ? "YBR_FULL_422" : "MONOCHROME2");

			   dicom.putInt(Tag.SamplesPerPixel, VR.US, samplesPerPixel);         
			   dicom.putInt(Tag.Rows, VR.US, jpegImage.getHeight());
			   dicom.putInt(Tag.Columns, VR.US, jpegImage.getWidth());
			   dicom.putInt(Tag.BitsAllocated, VR.US, bitsAllocated);
			   dicom.putInt(Tag.BitsStored, VR.US, bitsAllocated);
			   dicom.putInt(Tag.HighBit, VR.US, bitsAllocated-1);
			   dicom.putInt(Tag.PixelRepresentation, VR.US, 0);
			   
			   dicom.putDate(Tag.InstanceCreationDate, VR.DA, new Date());
			   dicom.putDate(Tag.InstanceCreationTime, VR.TM, new Date());
			   
			   dicom.putString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID());
			   dicom.putString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID());
			   dicom.putString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID());
			   
			   dicom.initFileMetaInformation(UID.JPEGBaseline1);
			   
			   FileOutputStream fos = new FileOutputStream(dcmDestination);
			   BufferedOutputStream bos = new BufferedOutputStream(fos);
			   DicomOutputStream dos = new DicomOutputStream(bos);
			   dos.writeDicomFile(dicom);
			   
			   dos.writeHeader(Tag.PixelData, VR.OB, -1);  
			   dos.writeHeader(Tag.Item, null, 0);       
	
			   int jpgLen = (int) jpgSource.length(); 
			   dos.writeHeader(Tag.Item, null, (jpgLen+1)&~1);
			   
			   FileInputStream fis = new FileInputStream(jpgSource);
			   BufferedInputStream bis = new BufferedInputStream(fis);
			   DataInputStream dis = new DataInputStream(bis);

			   byte[] buffer = new byte[65536];       
			   int b;
			   while ((b = dis.read(buffer)) > 0) {
			      dos.write(buffer, 0, b);
			   }
			   if ((jpgLen&1) != 0) dos.write(0); 
			   dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
			   dos.close();
			   
	   }
		

		 catch(Exception e) {
		   System.out.println("ERROR: "+ e.getMessage());
		}

}

}
