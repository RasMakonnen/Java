package test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.BasicConfigurator;
import org.dcm4che2.data.DicomElement;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.util.TagUtils;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;

public class Dicomanalyser extends JFrame {
	// Variables
	public File file;
	public BufferedImage img=null;
	public String filepath;
	public  final static String newline = "\n";
	public String text = ""; 
	DicomObject object = null;  
	public JTextArea textArea;
	String extension="jpg";
	JLabel lblImage;
	JPanel panel;
	File toread;
	String actualimg;
	
	//Classes
	
	public class ListDicomHeader {	
		//Class is used to print header from .dcm file
		
	public void headerfunction(DicomObject object , JTextArea textArea)
	{
		   Iterator<DicomElement> iter = object.datasetIterator();
		   while(iter.hasNext()) {
		      DicomElement element = (DicomElement) iter.next();
		      int tag = element.tag();
		      try {
		         String tagName = object.nameOf(tag);
		         String tagAddr = TagUtils.toString(tag);
		         String tagVR = object.vrOf(tag).toString();
		         
		         if (tagVR.equals("SQ")) {
		        	 if (element.hasItems()) {
			               textArea.setText(tagAddr +" ["+  tagVR +"] "+ tagName);
			               headerfunction(element.getDicomObject(),textArea);
			               continue;
			            }
			         }    
			         String tagValue = object.getString(tag);   
			         text=text+(tagAddr +" ["+ tagVR +"] "+ tagName +" ["+ tagValue+"]")+"\n";
			        // textArea.append(text + "\n");
			         textArea.setText(text+ "\n");   //fill text arena
			         textArea.setCaretPosition(textArea.getDocument().getLength());
			       
			         
		      } catch (Exception e) {
		         e.printStackTrace();
		      }
		      
		   }  
		}
	
	}
	
	// Methods
	
	public String saver(){
		// Open browser and returns path for saving file
		
		JFileChooser Filechoose=new JFileChooser();
		
		int retval=Filechoose.showOpenDialog(Dicomanalyser.this);
		if (retval == JFileChooser.APPROVE_OPTION) {
           //... The user selected a file, get it, use it.
    	   File newfile = Filechoose.getSelectedFile();
    	   filepath=newfile.getPath();
       }
		return	filepath; 
	}

	
	public void viewimage(File toread, JLabel lblImage, JPanel panel){
		/* Shows loaded image on label given as argument
		 * toread- image file
		 * lblImage- label, when image should be displayed
		 * panel- panel with label
		 */
		img = null;
		try {
			System.out.println("-------------------------------------------");
			System.out.println(toread);  
			img = ImageIO.read(toread);
			System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		} 
		catch (IOException a) {
			a.printStackTrace();   
		}
    
		Image fittedimafe = img.getScaledInstance(panel.getWidth(),panel.getHeight(),Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(fittedimafe);
		lblImage.setIcon(imageIcon);
		}
	

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				try {
					Dicomanalyser frame = new Dicomanalyser();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Dicomanalyser() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnConversion = new JMenu("Conversion");
		menuBar.add(mnConversion);
		
		JMenuItem mntmDicomJpeg = new JMenuItem("Dicom -> Jpeg");
		mnConversion.add(mntmDicomJpeg);
		mntmDicomJpeg.addActionListener(new ActionListener() {
			/* 
			  Dicom -> Jpeg is clicked. Conversion of showed dcm image to jpeg format 
			 */
			public void actionPerformed(ActionEvent e) {					
						filepath=saver();
						Dcm2jpg tojpg=new Dcm2jpg(file.getPath(), filepath);
						 file = new File(filepath+".jpg");						
						try {
								ImageIO.write(img, "jpg", file);
							} 
						catch (IOException e1) {
								e1.printStackTrace();
							}
	                	}
					});
		
		JMenuItem mntmJpegDicom = new JMenuItem("Jpeg -> Dicom");
		mnConversion.add(mntmJpegDicom);
		mntmJpegDicom.addActionListener(new ActionListener() {
			/*
				Jpeg -> Dicom clicked. Conversion of showed jpg format to dcm format
			 */
			public void actionPerformed(ActionEvent e) {
						
						filepath=saver();
						Jpg2dcm todcm=new Jpg2dcm(file.getPath(), filepath+".dcm");

	                }
			});
		
		JMenu mnProcessing = new JMenu("Processing");
		menuBar.add(mnProcessing);
		
		JMenuItem mntmKernelConvolution = new JMenuItem("Kernel convolution");
		mnProcessing.add(mntmKernelConvolution);
		
		JMenuItem mntmGaussianFilter = new JMenuItem("Gaussian filter");
		mnProcessing.add(mntmGaussianFilter);
		
		JMenuItem mntmErosion = new JMenuItem("Erosion");
		mnProcessing.add(mntmErosion);
		
		JMenuItem mntmDilatation = new JMenuItem("Dilatation");
		mnProcessing.add(mntmDilatation);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Image", null, panel, null);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Header", null, panel_1, null);

		JLabel lblImage = new JLabel("");
		panel.add(lblImage);
		
		JMenuItem mntmSaveHeader = new JMenuItem("Save Header");
		mnFile.add(mntmSaveHeader);
		mntmSaveHeader.addActionListener(new ActionListener() {
			/*
				"Save Header" clicked. Saving header of dcm image into txt file.
			 */
			public void actionPerformed(ActionEvent e) {
						
						filepath=saver();
						
						try {
							PrintWriter out = new PrintWriter(filepath+".txt");
							
							out.println(text);
							out.close();
							} 
						catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
	                }
				}
				);
		
		JMenuItem mntmSaveImage = new JMenuItem("Save image");
		mnFile.add(mntmSaveImage);
		mntmSaveImage.addActionListener(new ActionListener() {
			/*
				"Save image" clicked. Saving image as dcm  file.
			 */
			public void actionPerformed(ActionEvent e) {
				
						filepath=saver();
						System.out.println(filepath);
						 Jpg2dcm todcm=new Jpg2dcm(actualimg+".jpg",filepath+".dcm"); 
	                	}
					}
				);
		
		mntmGaussianFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				"Gaussian filter" clicked. Filtering image.
			 */

						gaussianfilter filter=new gaussianfilter(actualimg+".jpg");

						file=new File(actualimg+".jpg");
						viewimage(file,lblImage,panel);
	                	}
					});
		
		mntmErosion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				"Erosion" clicked. Eroding image..
			 */
						Erode erose = new Erode(actualimg+".jpg");
						file=new File(actualimg+".jpg");
						viewimage(file,lblImage,panel);
	                	}
					});
		
		
		mntmDilatation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				"Dilatation" clicked. Dilatating image.
			 */

						Dilate dilatation= new Dilate(actualimg+".jpg");
						file=new File(actualimg+".jpg");
						viewimage(file,lblImage,panel);
						}
					});
		
		
		mntmKernelConvolution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				"Kernel convolution" clicked. Convolution by kernel matrix.
			 */

						Jpg2dcm todcm= new Jpg2dcm(actualimg+".jpg",actualimg+".dcm" );
						extension="dcm";
			
						Kernelfilter kernel=new Kernelfilter(actualimg+".dcm", actualimg+".dcm");
						Dcm2jpg tojpg=new Dcm2jpg(actualimg+".dcm", actualimg+".jpg");
						extension="jpg";
						file=new File(actualimg+".jpg");
						toread=new File(actualimg+".dcm");
						viewimage(file,lblImage,panel);
						//toread.delete();
						
	                	}
					});
		
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		/*
		 Opening image file and displaing it on "Image" tab. Reading header file if it is possible.
		 */
		textArea = new JTextArea(panel_1.getHeight()/20, panel_1.getWidth()/12);// sizing image
		panel_1.add(textArea);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane ( textArea );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );// adding scroll for text arena
        panel_1.add ( scroll );
		
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					File toremove=new File(actualimg+".jpg");  //remove temporal file
					toremove.delete();
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "dcm");
				 JFileChooser Filechoose=new JFileChooser();
				 Filechoose.setFileFilter(filter);  // choosing file to open with filter for image files
	                int retval=Filechoose.showOpenDialog(Dicomanalyser.this);
	                
	                if (retval == JFileChooser.APPROVE_OPTION) {
	                    //... The user selected a file, get it, use it.
	                    file = Filechoose.getSelectedFile();
	                    filepath=file.getPath();
	                    extension=filepath.split("\\.")[1];  // get read file extension
						filepath=filepath.split("\\.")[0];   // get read file path

	                }
	                
	                System.out.println("ext= "+extension); 
	                if(extension.equals("dcm")){

	                	Dcm2jpg tojpg=new Dcm2jpg(file.toString(),filepath+"temp.jpg");  // convert file to jpg if necessary
	                	toread = new File(filepath+"temp.jpg");   // create temporal file
	                	
	                }
	                else{
	                	toread = new File(filepath+".jpg");
	                
	                }
	                actualimg=filepath+"temp";        // actual showed image
	                viewimage(toread, lblImage,panel);
	                
	                if(extension.equals("dcm")){
	                	
	                	
	                if(file!=null){
	                	try {
	                		// prepare to read header file
	                		DicomInputStream dis = new DicomInputStream(file);
	                		object = dis.readDicomObject();
	                		dis.close();
	                		} 
	                	catch (Exception c) {
	                		System.out.println(c.getMessage());
	                		System.exit(0);
	                		}
	                
	                
	                }
	                // sizing text arena with header information
					textArea.setColumns(panel_1.getWidth()/12);
					textArea.setRows(panel_1.getHeight()/20);

	                ListDicomHeader list1 = new ListDicomHeader();
		     		list1.headerfunction(object, textArea);
		     	
	                } 

				}
			});
		
		
		
		contentPane.addComponentListener(new ComponentAdapter(){
		    public void componentResized(ComponentEvent e) {
		        // do stuff      
		    	
		    	if(img!=null){	   
		    		// scalling text arena with header information and image
		    		Image scaledImage = img.getScaledInstance(panel.getWidth(),panel.getHeight(),Image.SCALE_SMOOTH);
		    		ImageIcon imageIcon = new ImageIcon(scaledImage);
		    		lblImage.setIcon(imageIcon);
				
		    		textArea.setColumns(panel_1.getWidth()/12);
		    		textArea.setRows(panel_1.getHeight()/20);
		    	}
		    }
		});
		
	}

}
