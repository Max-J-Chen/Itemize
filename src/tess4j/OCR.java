package tess4j;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;


public class OCR {

    public String scanFile(File imageFile) throws TesseractException
    {
        // Create an instance from Tesseract
        ITesseract instance = new Tesseract();  // JNA Interface Mapping

        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping - is faster than JNA Interface Mapping

        // Set path to english library that contains "eng.traineddata"
        instance.setDatapath("E:\\Itemize\\tessdata");

        // Outputs entire text into result variable with each line broken up by /n
        return instance.doOCR(imageFile);
    }

}
