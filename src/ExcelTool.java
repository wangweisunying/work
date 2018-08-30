

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Wei Wang
 */
public class ExcelTool {
    public static void main(String args[]){
//        updateZig(DataBaseTool.getZigData(DataBaseTool.serverInfo("V7"),"zig201802161"));
          System.out.println(readZigExcel("zig201803021"));
    }
    
    public static ArrayList<ArrayList<String>> readZigExcel(String fileName){
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        try{
            FileInputStream fileIn = new FileInputStream("C:\\Users\\Wei Wang\\Desktop\\work\\Zonulin production\\"+fileName+".xlsx");
            System.out.println("C:\\Users\\Wei Wang\\Desktop\\work\\Zonulin production\\"+fileName+".xlsx");
            Workbook wb = new XSSFWorkbook(fileIn);
           
            Sheet sheet = wb.getSheetAt(0);        
            
           
            for(int i = 0 ;i <sheet.getPhysicalNumberOfRows() ; i++){
                Row row = sheet.getRow(i);
                if(!row.getCell(1).getStringCellValue().isEmpty()){
                    ArrayList<String> resCell = new ArrayList<String>();

                    

                    resCell.add(row.getCell(1).getStringCellValue()); //julienbarcode
                    resCell.add(String.valueOf(row.getCell(6).getNumericCellValue())); 
                    resCell.add(row.getCell(0).getStringCellValue()); 
                    resCell.add(row.getCell(2).getStringCellValue()); 
                    resCell.add(row.getCell(3).getStringCellValue()); 


                    result.add(resCell);
                    
                }
                
                
               
            }
            
          
        }
        catch(IOException ex){
        
        }
        System.out.println(result);
        return result;
    }
    
    
    public static void updateZig(ArrayList<ArrayList<String>> list){
        try{
            //1.創建工作簿 
                FileInputStream fileIn = new FileInputStream("C:\\Users\\Wei Wang\\Desktop\\work\\Zonulin production\\template\\zig_template.xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook(fileIn);
		//2. 創建工作表
                
                workbook.setSheetName(0, list.get(0).get(0));
		XSSFSheet zig_sheet = workbook.getSheetAt(0);
                

                for(int i = 0; i <list.get(0).size()/5;i++){
                    Row row = zig_sheet.getRow(i);
                    for(int j = 0 ;j<4;j++){
                        row.createCell(j).setCellValue(list.get(0).get(5*i+j));
                    }
                    row.createCell(4).setCellValue(Double.parseDouble(list.get(0).get(5*i+4)));
                                                  
                    }
                
                
                
	
		//輸出到硬盤
		FileOutputStream out = new FileOutputStream("C:\\Users\\Wei Wang\\Desktop\\work\\Zonulin production\\"+list.get(1).get(0)+".xlsx");
		workbook.setForceFormulaRecalculation(true);
		workbook.write(out);
		workbook.close();
		out.close();
                
                
                Desktop.getDesktop().open(new File("C:\\Users\\Wei Wang\\Desktop\\work\\Zonulin production\\"+list.get(1).get(0)+".xlsx"));
     
                
        }
        catch(IOException ex){
            ex.printStackTrace();
            System.out.println("well plate id not exsist in db");
        }
        
        
   
    }
    
    
    
}
