import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 
 * @create Apr 23, 2013 1:00:37 AM
 * @author 玄玉<http://jadyer.cn/>
 */
public class OpenLocalFile {  
    /**
     * 借助java.awt.Desktop打开
     * @see 打开的目录或文件名中允许包含空格
     */
    static void useAWTDesktop(String fileName) throws IOException{
        Desktop.getDesktop().open(new File("C:\\Users\\Wei Wang\\Desktop\\work\\food sensitivity hamiltion clarity run\\"+fileName+".xlsx"));
    }


}