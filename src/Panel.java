 // Import the basic graphics classes.  
 // The problem here is that we read the image with OpenCV into a Mat object.  
 // But OpenCV for java doesn't have the method "imshow", so, we got to use  
 // java for that (drawImage) that uses Image or BufferedImage.  
 // So, how to go from one the other... Here is the way...  
 import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;
 public class Panel extends JPanel{  
   private static final long serialVersionUID = 1L;  
   private BufferedImage image;  
   // Create a constructor method  
   public Panel(){  
     super();  
   }  
   private BufferedImage getimage(){  
     return image;  
   }  
   private void setimage(BufferedImage newimage){  
     image=newimage;  
     return;  
   }  
   /**  
    * Converts/writes a Mat into a BufferedImage.  
    *  
    * @param matrix Mat of type CV_8UC3 or CV_8UC1  
    * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY  
    */  
   public static BufferedImage matToBufferedImage(Mat matrix) {  
     int cols = matrix.cols();  
     int rows = matrix.rows();  
     int elemSize = (int)matrix.elemSize();  
     byte[] data = new byte[cols * rows * elemSize];  
     int type;  
     matrix.get(0, 0, data);  
     switch (matrix.channels()) {  
       case 1:  
         type = BufferedImage.TYPE_BYTE_GRAY;  
         break;  
       case 3:  
         type = BufferedImage.TYPE_3BYTE_BGR;  
         // bgr to rgb  
         byte b;  
         for(int i=0; i<data.length; i=i+3) {  
           b = data[i];  
           data[i] = data[i+2];  
           data[i+2] = b;  
         }  
         break;  
       default:  
         return null;  
     }  
     BufferedImage image2 = new BufferedImage(cols, rows, type);  
     image2.getRaster().setDataElements(0, 0, cols, rows, data);  
     return image2;  
   }  
   public void paintComponent(Graphics g){  
      BufferedImage temp=getimage();  
      g.drawImage(temp,10,10,temp.getWidth(),temp.getHeight(), this);  
   }  
   public static void main(String arg[]){  
    // Load the native library.  
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);    
    
    CascadeClassifier faceDetector = new CascadeClassifier("C:\\Users\\Lightning\\Pictures\\ReaperAI_Vision\\haarcascade_frontalface_alt.xml");
    MatOfRect faceDetections = new MatOfRect();
    
    JFrame frame = new JFrame("BasicPanel");  
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
    frame.setSize(400,400);  
    Panel panel = new Panel();  
    frame.setContentPane(panel);       
    frame.setVisible(true);       
    Mat webcam_image=new Mat();  
    BufferedImage temp;  
    VideoCapture capture =new VideoCapture(0);  
    if( capture.isOpened())  
     {  
      while( true )  
      {  
        capture.read(webcam_image);  
        if( !webcam_image.empty() )  
         {  
           frame.setSize(webcam_image.width()+40,webcam_image.height()+60);  
           faceDetector.detectMultiScale(webcam_image, faceDetections);
           //System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
           for (Rect rect : faceDetections.toArray()) {
               Core.rectangle(webcam_image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                       new Scalar(0, 255, 0));
           } 
           temp=matToBufferedImage(webcam_image);  
           panel.setimage(temp);  
           panel.repaint();  
         }  
         else  
         {  
           System.out.println(" --(!) No captured frame -- Break!");  
           break;  
         }  
        }  
       }  
       return;  
   }  
 }  