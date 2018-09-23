package com.jd.common.print;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * 打印帮助类
 * 
 * @author Administrator
 * 
 */
public class PrintHelper {

	private static final Logger log = LogManager.getLogger(PrintHelper.class);

    public static void getPrintWaybillRma(String arg,OutputStream outputStream) throws DocumentException, IOException {
        // 定义A4页面大小
        Rectangle rectPageSize = new Rectangle(PageSize.A4);
        Document document = new Document(rectPageSize, 30, 30, 30, 30);
        PdfWriter.getInstance(document,outputStream);
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font FontChinese14 = new Font(bfChinese, 14, Font.NORMAL);
        Font FontChinese12 = new Font(bfChinese, 12, Font.NORMAL);
        Font FontChinese10 = new Font(bfChinese, 10, Font.NORMAL);
        document.open();
         //表头
        Paragraph p1 = new Paragraph("JD发货交接单",FontChinese14);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);
        //日期靠右--当前时间
        Paragraph p2 = new Paragraph("2018-09-21",FontChinese14);
        p2.setAlignment(Element.ALIGN_RIGHT);
        document.add(p2);

        Paragraph p3 = new Paragraph("地址信息",FontChinese12);
        p3.setAlignment(Element.ALIGN_LEFT);
        document.add(p3);

        float[] width1 = {90f,90f,90f,90f,90f,180f};
        PdfPTable table1 = new PdfPTable(width1);
        PdfPTable table2 = new PdfPTable(width1);
        PdfPTable table3 = new PdfPTable(width1);
        PdfPTable table4 = new PdfPTable(width1);
        //表格前有空白
        table1.setSpacingBefore(10);
        //设置表格的宽度
        table1.setTotalWidth(535);
        //设置表格的宽度固定
        table1.setLockedWidth(true);

        table2.setTotalWidth(535);
        table2.setLockedWidth(true);

        table3.setTotalWidth(535);
        table3.setLockedWidth(true);

        table4.setTotalWidth(535);
        table4.setLockedWidth(true);

        //发货城市、发货场地、目的城市
        PdfPCell cellA1 = new PdfPCell(new Paragraph("发货城市", FontChinese12));
        PdfPCell cellA2 = new PdfPCell(new Paragraph("发货场地", FontChinese12));
        PdfPCell cellA3 = new PdfPCell(new Paragraph("目的城市", FontChinese12));
        PdfPCell cellA11 = new PdfPCell(new Paragraph("北京", FontChinese12));
        PdfPCell cellA22 = new PdfPCell(new Paragraph("北京TC", FontChinese12));
        PdfPCell cellA33 = new PdfPCell(new Paragraph("通州", FontChinese12));
        cellA1.setMinimumHeight(16);
        table1.addCell(cellA1);
        table1.addCell(cellA11);
        table1.addCell(cellA2);
        table1.addCell(cellA22);
        table1.addCell(cellA3);
        table1.addCell(cellA33);
        //发货联系人\联系电话\商家名称
        PdfPCell cellB1 = new PdfPCell(new Paragraph("发货联系人", FontChinese12));
        PdfPCell cellB2 = new PdfPCell(new Paragraph("联系电话", FontChinese12));
        PdfPCell cellB3 = new PdfPCell(new Paragraph("商家名称", FontChinese12));
        PdfPCell cellB11 = new PdfPCell(new Paragraph("张三", FontChinese12));
        PdfPCell cellB22 = new PdfPCell(new Paragraph("1234567890", FontChinese12));
        PdfPCell cellB33 = new PdfPCell(new Paragraph("北京京东商城", FontChinese12));
        cellB1.setMinimumHeight(16);
        table2.addCell(cellB1);
        table2.addCell(cellB11);
        table2.addCell(cellB2);
        table2.addCell(cellB22);
        table2.addCell(cellB3);
        table2.addCell(cellB33);

        //发货联系人\联系电话\商家名称
        PdfPCell cellC1 = new PdfPCell(new Paragraph("收货人", FontChinese12));
        PdfPCell cellC2 = new PdfPCell(new Paragraph("联系电话", FontChinese12));
        PdfPCell cellC3 = new PdfPCell(new Paragraph("收货地址", FontChinese12));
        PdfPCell cellC11 = new PdfPCell(new Paragraph("李四", FontChinese12));
        PdfPCell cellC22 = new PdfPCell(new Paragraph("9012345678", FontChinese12));
        PdfPCell cellC33 = new PdfPCell(new Paragraph("北京通州马驹桥", FontChinese12));
        cellC1.setMinimumHeight(16);
        table2.addCell(cellC1);
        table2.addCell(cellC11);
        table2.addCell(cellC2);
        table2.addCell(cellC22);
        table2.addCell(cellC3);
        table2.addCell(cellC33);

        document.add(table1);
        document.add(table2);
        document.add(table3);

        Paragraph p4 = new Paragraph("货物信息",FontChinese12);
        p4.setAlignment(Element.ALIGN_LEFT);
        document.add(p4);
        //发货联系人\联系电话\商家名称
        PdfPCell cellD1 = new PdfPCell(new Paragraph("运单数量", FontChinese12));
        PdfPCell cellD2 = new PdfPCell(new Paragraph("包裹数", FontChinese12));
        PdfPCell cellD3 = new PdfPCell(new Paragraph("备件数量", FontChinese12));
        PdfPCell cellD11 = new PdfPCell(new Paragraph("4", FontChinese12));
        PdfPCell cellD22 = new PdfPCell(new Paragraph("230", FontChinese12));
        PdfPCell cellD33 = new PdfPCell(new Paragraph("460", FontChinese12));
        cellD1.setMinimumHeight(16);
        table4.addCell(cellD1);
        table4.addCell(cellD11);
        table4.addCell(cellD2);
        table4.addCell(cellD22);
        table4.addCell(cellD3);
        table4.addCell(cellD33);
        table4.setSpacingBefore(10);
        document.add(table4);
        Paragraph p5 = new Paragraph("JD发货明细交接单",FontChinese14);
        p5.setAlignment(Element.ALIGN_CENTER);
        p5.setSpacingBefore(10);
        document.add(p5);

        float[] width2 = {90f,90f,90f,90f,180f,90f};
        PdfPTable table = new PdfPTable(width2);
        table.setSpacingBefore(10);
        table.setTotalWidth(535);
        table.setLockedWidth(true);
        String[] titles = new String[]{"备件条码","运单号","出库单号","商品编号","商品名称","异常备注"};
        for(String s : titles){
            PdfPCell cell = new PdfPCell(new Paragraph(s, FontChinese12));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setMinimumHeight(16);
            table.addCell(cell);
        }
        //循环表格填充数据--Object换成遍历对象
//        for(Object dd : new ArrayList<Object>()){
        for(int i=0;i<100;i++){
            PdfPCell cell01 = new PdfPCell(new Paragraph(new Integer(i).toString(), FontChinese10));
            cell01.setMinimumHeight(16);
            table.addCell(cell01);

            PdfPCell cell02 = new PdfPCell(new Paragraph("2", FontChinese10));
            table.addCell(cell02);

            PdfPCell cell03 = new PdfPCell(new Paragraph("3", FontChinese10));
            table.addCell(cell03);

            PdfPCell cell04 = new PdfPCell(new Paragraph("4", FontChinese10));
            table.addCell(cell04);

            PdfPCell cell05 = new PdfPCell(new Paragraph("5", FontChinese10));
            table.addCell(cell05);

            PdfPCell cell06 = new PdfPCell(new Paragraph("6", FontChinese10));
            table.addCell(cell06);
        }
        document.add(table);
        document.close();
    }
}
