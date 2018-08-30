/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Production;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

class EnaRaw {

    String chipId, plateLoc, row;
    int index, col;
    float rawData;

    EnaRaw(String chipId, int index, String plateLoc, String row, int col, float rawData) {
        this.chipId = chipId;
        this.index = index;
        this.plateLoc = plateLoc;
        this.row = row;
        this.col = col;
        this.rawData = rawData;
    }
}

class EnaUnit {

    String sampleLoc, chipId, status;
    int index;
    float unit;

    EnaUnit(String sampleLoc, String chipId, int index, float unit, String status) {
        this.sampleLoc = sampleLoc;
        this.chipId = chipId;
        this.index = index;
        this.unit = unit;
        this.status = status;
    }
}

public class ENAFDA {

    //PDF HEADER INIT
    String[] product_header = {"Component", "PART#", "LOT#", "EXP.DATE"};
    String[] qc_header = {"QC_ID", "Anitibody", "Chip_ID", "Negative Ref.", "Moderate Ref.", "Postive Ref.", "Actual Value", "Pass/Fail"};
    String[] unit_header = {"Location", "Anitibody", "Chip_ID", "Negative Ref.", "Moderate Ref.", "Postive Ref.", "Actual Value", "Result"};
    String[] raw_header = {"ChipID" , "Index" , "Location" , "Row" , "Col" , "RawData"};
    float[] unit_column_width = new float[]{0.85f,1.75f,0.9f,1.25f,1.5f,1.25f,1.25f,1.25f};
    
    
    //init pillarPlateId ,pdf parameter
    String pillarId = "ENA10C001002000037";
    String path = "C://Users//Wei Wang//Desktop//PDF//test.PDF";
    String pathRaw = "C://Users//Wei Wang//Desktop//PDF//testRaw.PDF";
    String logoPath = "C://Users//Wei Wang//Desktop//PDF//logo//logo_dark.png";

    //init all the parameter
    double[] cutOff = {154, 130, 240, 121, 80, 125, 141, 100, 118, 109};
    double[] calibrator = {1.25, 1.8, 1, 1.4, 1.1, 1.2, 1.4, 1.3, 1.05, 1.02};
    int[] test_index = {4, 5, 6, 7, 8, 9, 10, 11, 13, 14};
    String[] test_name = {"Scl 70", "Samarium Sm", "RNA Polymerase III", "Chromatin",
        "Histone", "Jo 1", "RNP", "Centromere", "SS-A/Ro", "SS-B/La"};
    float[][] ref_range = {{20f, 80f}, {20f, 80f}, {20f, 80f}, {20f, 60f}, {1.0f, 2.5f}, {0.95f, 1.04f}, {0.95f, 1.04f}, {20f, 30f}, {0.95f, 1.04f}, {0.95f, 1.04f}};
    
    HashMap<Integer, String> indexTestMap = new HashMap();
    HashMap<Integer, float[]> indexRangeMap = new HashMap();

    // init filter
    HashSet<Integer> setIndex = new HashSet();
    HashMap<Integer, Double> mapCali = new HashMap();
    HashMap<String, String> mapSerum = new HashMap();

    // init container
    Comparator compare = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            String loc1 = findLocation(s1);
            String loc2 = findLocation(s2);
            int index1 = findIndex(s1);
            int index2 = findIndex(s2);
            if (loc1.compareTo(loc2) == 0) {
                return index1 - index2;
            }
            if(loc1.substring(0 , 1).compareTo(loc2.substring( 0 , 1)) == 0){
                return Integer.parseInt(loc1.substring(1)) - Integer.parseInt(loc2.substring(1));
            
            }
            return loc1.compareTo(loc2);
        }
    };
    TreeMap<String, EnaRaw> rawMap = new TreeMap(compare); // loc : EnaRaw
    TreeMap<String, EnaUnit> unitMap = new TreeMap(compare); // julien_barcode : EnaUnit
    TreeMap<String, EnaUnit> ctrlUnitMap = new TreeMap(compare);

    ENAFDA() {
        for (int i = 0; i < test_index.length; i++) {
            indexTestMap.put(test_index[i], test_name[i]);
            indexRangeMap.put(test_index[i], ref_range[i]);
        }
    }

    public static void main(String[] args) {
        ENAFDA ena = new ENAFDA();
        ena.run();
    }

    private void run() {

//    ----------------------------------------------GET THE RAW DATA -------------------------------------------------------------
        try {
            Connection con = DriverManager.getConnection(DataBaseInfo.name_V7, DataBaseInfo.user_V7, DataBaseInfo.pw_V7);
            Statement stmt = con.createStatement();
            String sql = "select c.test_name , a.index , concat(row,col) as plateLoc , row ,col , `signal` ,suspicious from\n"
                    + "(select * from vibrant_test_raw_data.ena10 where pillar_plate_id = '" + pillarId + "') as a\n"
                    + "join\n"
                    + "vibrant_test_raw_data.ena_index as c\n"
                    + "on c.index = a.index;";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                rawMap.put(rs.getInt("index") + rs.getString("plateLoc"), new EnaRaw(rs.getString("test_name"),
                        rs.getInt("index"), rs.getString("plateLoc"), rs.getString("row"), rs.getInt("col"), rs.getFloat("signal")));
            }

            String sql2 = "select concat(well_row , well_col) as loc , julien_barcode from vibrant_test_tracking.well_info \n"
                    + "where well_plate_id = (select distinct well_plate_id from vibrant_test_tracking.pillar_plate_info where pillar_plate_id = '" + pillarId + "');";
            ResultSet rs2 = stmt.executeQuery(sql2);
            while (rs2.next()) {
                mapSerum.put(rs2.getString("loc"), rs2.getString("julien_barcode"));
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        //        ------------------------------------------CALCULATE UNIT------------------------------------------------------------------

        //calculate each cf save to map
        for (int i = 0; i < test_index.length; i++) {
            setIndex.add(test_index[i]);
            Double cf = cutOff[i] / calibrator[i];
            mapCali.put(test_index[i], cf);
        }

        for (String indexLoc : rawMap.keySet()) {
            int index = rawMap.get(indexLoc).index;
            if (setIndex.contains(index)) {
                float unit = (float) (Math.sqrt(rawMap.get(indexLoc).rawData) / mapCali.get(index));
                String location = findLocation(indexLoc);

                if (location.equals("G12")) { // MAYBE STITICH FAILED
                    //judge pass condition
                    ctrlUnitMap.put(index + "PosCtrl", new EnaUnit("PosCtrl", rawMap.get(indexLoc).chipId, index, unit, "PASS"));
                    continue;
                }
                if (location.equals("H12")) {
                    //judge pass conditionuy

                    ctrlUnitMap.put(index + "NegCtrl", new EnaUnit("NegCtrl", rawMap.get(indexLoc).chipId, index, unit, "PASS"));
                    continue;
                }
                if (mapSerum.get(location) == null || Character.isLetter(mapSerum.get(location).charAt(0))) {
                    continue;
                }
                unitMap.put(index + location, new EnaUnit(mapSerum.get(location), rawMap.get(indexLoc).chipId, index, unit, ""));
            }
        }

//     ----------------------------------test print------------------------------------
        for (String key : rawMap.keySet()) {
            System.out.println(key + " " + rawMap.get(key).rawData);
        }
//        System.out.println(rawMap.size());
        for (String key : ctrlUnitMap.keySet()) {
            System.out.println(key + " " + ctrlUnitMap.get(key).unit + "    " + ctrlUnitMap.get(key).index);
        }

        for (String key : unitMap.keySet()) {
            System.out.println(key + " " + unitMap.get(key).unit);
        }
        System.out.println(unitMap.size());
//     ----------------------------------test print end------------------------------------        

        PDFExporter pdfex = new PDFExporter();
        pdfex.pDFExport();
    }

    private class PDFExporter extends PdfPageEventHelper {
        public PdfTemplate tpl;
        public BaseFont helv ;
        Image logo;
        private void pDFExport() {
            //font init
            Font fontTitle = new Font(Font.FontFamily.TIMES_ROMAN, 25);
            fontTitle.setColor(0, 204, 204);
            Font fontBody = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            Font font_tableHeader = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
            Font font_tableBody = new Font(Font.FontFamily.TIMES_ROMAN, 7);
            
            try {
                Document doc = new Document(PageSize.A4);
                Document docRaw = new Document(PageSize.A4);
     
                PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));
                PdfWriter writerRaw = PdfWriter.getInstance(docRaw, new FileOutputStream(pathRaw));
                writer.setPageEvent(this);
                writerRaw.setPageEvent(this);
                //
                logo = Image.getInstance(logoPath);
                logo.scaleToFit(110, 110);
                
                
                
                doc.open();
                Paragraph title = new Paragraph("Vibrant™ ENA10 Dx REPORT" + "\n" + "\n", fontTitle);
                title.setAlignment(Element.ALIGN_CENTER);
                Paragraph body1 = new Paragraph("File Name : " + path + "\n"
                        + "Pillar Plate ID : " + pillarId + "\n" 
                        + "Report Time : " +new SimpleDateFormat("yyyyMMdd").format(new Date())+ "\n" + "\n",
                        fontBody);
                Paragraph body2 = new Paragraph("QC Details" + "\n\n" , fontBody);
                Paragraph body3 = new Paragraph("Results " + "\n\n" , fontBody);
                
                
                body1.setAlignment(Element.ALIGN_CENTER);
                body2.setAlignment(Element.ALIGN_CENTER);
                body3.setAlignment(Element.ALIGN_CENTER);
              

   
//            ----------------------------------------------table init ------------------------------------------------------------
                PdfPTable table_product = tableInit(product_header, font_tableHeader);
                PdfPTable table_qc = tableInit(qc_header, font_tableHeader);
                table_qc.setWidths(unit_column_width);
                PdfPTable table_unit = tableInit(unit_header, font_tableHeader);
                table_unit.setWidths(unit_column_width);
//            -------------------------------------fill table -------------------------------------
                // table production
                String[][] contentPro =  {{"ENA10 Assay Kit","K901100","YYMMDD (901) 1","YYYYMMDD"},
{"ENA10 IgG Pillar Plate","K901101","YYMMDD (901101) 1","YYYYMMDD"},
{"ENA10 Software ","K901102","YYMMDD (901102) 1","YYYYMMDD"},
{"Blocking Buffer","K901103","YYMMDD (901103) 1","YYYYMMDD"},
{"20x Wash Buffer","K901104","YYMMDD (901104) 1","YYYYMMDD"},
{"ENA10 Negative Control","K901105","YYMMDD (901105) 1","YYYYMMDD"},
{"ENA10 Calibrator 1","K901106","YYMMDD (901106) 1","YYYYMMDD"},
{"ENA10 Calibrator 2","K901107","YYMMDD (901107) 1","YYYYMMDD"},
{"ENA10 Calibrator 3","K901108","YYMMDD (901108) 1","YYYYMMDD"},
{"ENA10 Positive Control","K901109","YYMMDD (901109) 1","YYYYMMDD"},
{"Vibrant Sample Diluent","K901110","YYMMDD (901110) 1","YYYYMMDD"},
{"ENA10 IgG Conjugate","K901111","YYMMDD (901111) 1","YYYYMMDD"},
{"Chemiluminescence Solution A","K901112","YYMMDD (901112) 1","YYYYMMDD"},
{"Chemiluminescence Solution B","K901113","YYMMDD (901113) 1","YYYYMMDD"}
};
                
                
                
                for (int i = 0; i < contentPro.length; i++) {
                    for ( int j =0 ; j < contentPro[i].length ; j++){
                        PdfPCell cell = new PdfPCell(new Phrase(new Chunk(contentPro[i][j], font_tableBody)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_product.addCell(cell);
                    }
                }

                // table table_qc
                
                String pre = "";
                String cur = "";
                BaseColor curColor = new BaseColor(240,248,255);
                BaseColor preColor = new BaseColor(211,211,211);
                BaseColor showColor = curColor;
                boolean first = true;
                boolean change = false;
                for (String indexLoc : ctrlUnitMap.keySet()) {
                    //color different sample
                    cur = String.valueOf(findLocation(indexLoc));
                    if(first){
                        pre = cur;
                        first = false;
                    }
                    
                    
                    int index = findIndex(indexLoc);
                    String floor = String.valueOf(indexRangeMap.get(index)[0]);
                    String ceil = String.valueOf(indexRangeMap.get(index)[1]);
                    float unit = ctrlUnitMap.get(indexLoc).unit;

                    String[] content = {cur, indexTestMap.get(index), String.valueOf(index),
                        " ≤ " + floor, floor + " ~ " + ceil, " ≥ " + ceil, String.format("%.2f", unit)};
                    
                    if(cur.equals(pre)){
                        showColor = preColor;
                    }
                    else{
                        showColor = curColor;
                        change = true;
                    }
                    
                    for (int i = 0; i < content.length; i++) {
                        PdfPCell cell = new PdfPCell(new Phrase(new Chunk(content[i], font_tableBody)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(showColor);
                        table_qc.addCell(cell);
                        
                    }
                    //
                    PdfPCell cell;
                    if (unit < Float.parseFloat(floor)) {
                        cell = new PdfPCell(new Phrase(new Chunk("Pass", font_tableBody)));
                        cell.setBackgroundColor(BaseColor.GREEN);
                    } else {
                        cell = new PdfPCell(new Phrase(new Chunk("Fail", font_tableBody)));
                        cell.setBackgroundColor(BaseColor.RED);
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table_qc.addCell(cell);
                    pre = cur;
                    if(change){
                       BaseColor tmp = preColor;
                       preColor = curColor;
                       curColor = tmp;
                       change = false;
                    }
                }

                // table_unit
                for (String indexLoc : unitMap.keySet()) {
                    cur = String.valueOf(findLocation(indexLoc));
                    if(first){
                        pre = cur;
                        first = false;
                    }
                    
                    
                    int index = findIndex(indexLoc);
                    String floor = String.valueOf(indexRangeMap.get(index)[0]);
                    String ceil = String.valueOf(indexRangeMap.get(index)[1]);
                    float unit = unitMap.get(indexLoc).unit;

                    String[] content = {String.valueOf(findLocation(indexLoc)), indexTestMap.get(index), String.valueOf(index),
                        " ≤ " + floor, floor + " ~ " + ceil, " ≥ " + ceil, String.format("%.2f", unit)};
                    if(cur.equals(pre)){
                        showColor = preColor;
                    }
                    else{
                        showColor = curColor;
                        change = true;
                    }
                    
                    for (int i = 0; i < content.length; i++) {
                        PdfPCell cell = new PdfPCell(new Phrase(new Chunk(content[i], font_tableBody)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(showColor);
                        table_unit.addCell(cell);

                    }
                    PdfPCell cell;
                    if (unit < Float.parseFloat(floor)) {
                        cell = new PdfPCell(new Phrase(new Chunk("Negative", font_tableBody)));
                    } else if (unit > Float.parseFloat(ceil)) {
                        cell = new PdfPCell(new Phrase(new Chunk("Postive", font_tableBody)));
                    } else {
                        cell = new PdfPCell(new Phrase(new Chunk("Moderate", font_tableBody)));
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(showColor);
                    table_unit.addCell(cell);
                    
                    
                    pre = cur;
                    if(change){
                       BaseColor tmp = preColor;
                       preColor = curColor;
                       curColor = tmp;
                       change = false;
                    }
                }

                doc.add(title);
                doc.add(body1);
                doc.add(table_product);
                doc.newPage();
                doc.add(body2);
                doc.add(table_qc);
                doc.newPage();
                doc.add(body3);
                doc.add(table_unit);
                doc.close();
                
                
                
//                ----------------------------------raw-----------------------------------------------------------------
                docRaw.open();
                Paragraph titleRaw = new Paragraph("Vibrant™ ENA10 Dx Raw Data" + "\n" + "\n", fontTitle);
                titleRaw.setAlignment(Element.ALIGN_CENTER);
                Paragraph bodyRaw = new Paragraph("File Name : " + pathRaw + "\n"
                    + "Pillar Plate ID : " + pillarId + "\n" +
                    "Report Time : " +new SimpleDateFormat("yyyyMMdd").format(new Date())+ "\n" + "\n",
                        fontBody);
                bodyRaw.setAlignment(Element.ALIGN_CENTER);
                PdfPTable table_raw = tableInit(raw_header, font_tableHeader);
                
                
                
                
                for(String indexLoc : rawMap.keySet()){
                    cur = rawMap.get(indexLoc).row + rawMap.get(indexLoc).col;
                    if(first){
                        pre = cur;
                        first = false;
                    }
                    if(cur.equals(pre)){
                        showColor = preColor;
                    }
                    else{
                        showColor = curColor;
                        change = true;
                    }
                    
                    
                    
                    PdfPCell[] cell = new PdfPCell[raw_header.length];
                    cell[0] = new PdfPCell(new Phrase(new Chunk(rawMap.get(indexLoc).chipId , font_tableBody)));
                    cell[1] = new PdfPCell(new Phrase(new Chunk(String.valueOf(rawMap.get(indexLoc).index) ,font_tableBody)));
                    cell[2] = new PdfPCell(new Phrase(new Chunk(cur, font_tableBody)));
                    cell[3] = new PdfPCell(new Phrase(new Chunk(rawMap.get(indexLoc).row , font_tableBody)));
                    cell[4] = new PdfPCell(new Phrase(new Chunk(String.valueOf(rawMap.get(indexLoc).col) , font_tableBody)));
                    cell[5] = new PdfPCell(new Phrase(new Chunk(String.valueOf(rawMap.get(indexLoc).rawData) , font_tableBody)));
                    for(int i = 0 ; i < cell.length ; i ++){
                        cell[i].setBackgroundColor(showColor);
                        cell[i].setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_raw.addCell(cell[i]);
                    }
                    
                    pre = cur;
                    if(change){
                       BaseColor tmp = preColor;
                       preColor = curColor;
                       curColor = tmp;
                       change = false;
                    }                  
                }
                
                docRaw.add(titleRaw);
                docRaw.add(bodyRaw);
                docRaw.add(table_raw);
                docRaw.close();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(ENAFDA.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadElementException ex) {
                Logger.getLogger(ENAFDA.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ENAFDA.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(ENAFDA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        public void onCloseDocument(PdfWriter writer , Document doc){
            tpl.beginText();  
            tpl.setFontAndSize(helv, 5);
            tpl.setTextMatrix(0, 0);  
            tpl.showText("" + writer.getPageNumber());  
            tpl.endText();
        }
        public void onStartPage(PdfWriter writer , Document doc){
            try {
                logo.setAbsolutePosition(doc.left(), doc.top() - 10);
                doc.add(logo);
                doc.add(new Paragraph("\n"));
                
            } catch (DocumentException ex) {
                Logger.getLogger(ENAFDA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        public void onEndPage(PdfWriter writer , Document doc){
     
            PdfContentByte cb = writer.getDirectContent();
            String text = "Page " + writer.getPageNumber() + " of ";
            cb.setFontAndSize(helv, 5);
            cb.beginText();
            cb.showTextAligned(0, text, (doc.right() - doc.left()) / 2 + doc.leftMargin(), doc.bottom() - 10, 0);
            cb.endText();
            cb.addTemplate(tpl,(doc.right() - doc.left()) / 2 + doc.leftMargin() + helv.getWidthPoint(text, 5) , doc.bottom() - 10);
        }
        public void onOpenDocument(PdfWriter writer ,Document doc ){
            try {
                tpl = writer.getDirectContent().createTemplate((doc.right() - doc.left()) / 2 + doc.leftMargin(), doc.bottom() - 10);
                helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
            } catch (DocumentException ex) {
                Logger.getLogger(ENAFDA.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ENAFDA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }

    private PdfPTable tableInit(String[] header, Font tableFont) {
        PdfPTable table = new PdfPTable(header.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);
        for (int i = 0; i < header.length; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(new Chunk(header[i], tableFont)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell);
        }
        table.setHeaderRows(1);
        return table;
    }

    private String findLocation(String indexLoc) {
        for (int i = 0; i < indexLoc.length(); i++) {
            if (Character.isLetter(indexLoc.charAt(i))) {
                return indexLoc.substring(i);
            }
        }
        return "location not exist";
    }

    private int findIndex(String indexLoc) {
        for (int i = 0; i < indexLoc.length(); i++) {
            if (Character.isLetter(indexLoc.charAt(i))) {
                return Integer.parseInt(indexLoc.substring(0, i));
            }
        }
        return -999;
    }
}
