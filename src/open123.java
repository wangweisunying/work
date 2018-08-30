
import java.awt.Desktop;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javafx.stage.FileChooser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Wei Wang
 */
public class open123 {
    public static void main(String[] args) throws IOException {
        File dir = new File("C:\\Users\\Wei Wang\\Desktop\\PDF");
   
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(dir);

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "test.pdf"));
        
    }
}
