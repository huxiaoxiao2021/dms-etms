package com.jd.common.print;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;
import com.jd.bluedragon.distribution.rma.response.RmaHandoverPrint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.util.List;

/**
 * 打印帮助类
 *
 * @author Administrator
 */
public class PrintHelper {

    private static final Logger log = LogManager.getLogger(PrintHelper.class);

    private static final int COMMON_CELL_HEIGHT = 20;

    public static void printRmaHandoverPDF(List<RmaHandoverPrint> rmaHandoverPrintList, OutputStream outputStream) throws Exception {
        // 定义A4页面大小
        Rectangle rectPageSize = new Rectangle(PageSize.A4);
        Document document = new Document(rectPageSize, 30, 30, 30, 30);
        PdfWriter.getInstance(document, outputStream);
        for (RmaHandoverPrint rmaHandoverPrint : rmaHandoverPrintList) {
            buildRmaHandoverPDF(document, rmaHandoverPrint);
            document.newPage();
        }
        document.close();
    }

    private static void buildRmaHandoverPDF(Document document, RmaHandoverPrint rmaHandoverPrint) throws Exception {
        if (rmaHandoverPrint == null) {
            throw new Exception("获取打印数据为空");
        }

        List<RmaHandoverDetail> handoverDetails = rmaHandoverPrint.getHandoverDetails();

        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fnCH14 = new Font(bfChinese, 14, Font.BOLD);
        Font fnCH12 = new Font(bfChinese, 12, Font.NORMAL);
        Font fnBoldCH12 = new Font(bfChinese, 12, Font.BOLD);
        Font fnCH10 = new Font(bfChinese, 10, Font.NORMAL);
        Font fnCH8 = new Font(bfChinese, 8, Font.NORMAL);

        document.open();
        //表头
        Paragraph p1 = new Paragraph("JD发货交接单", fnCH14);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);
        //日期靠右--当前时间
        Paragraph p2 = new Paragraph("打印日期：" + rmaHandoverPrint.getPrintDate(), fnCH8);
        p2.setAlignment(Element.ALIGN_RIGHT);
        document.add(p2);

        PdfPTable table1 = createTitleTable();
        //表格前有空白
        table1.setSpacingBefore(10);

        createOneTitleCell(table1, "地址信息", fnBoldCH12);

        //发货城市、发货场地、目的城市
        createNameValueCell(table1, "发货城市", rmaHandoverPrint.getSendCityName(), fnCH12);
        createNameValueCell(table1, "发货场地", rmaHandoverPrint.getCreateSiteName(), fnCH12);
        createNameValueCell(table1, "目的城市", rmaHandoverPrint.getTargetCityName(), fnCH12);

        PdfPTable table2 = createTitleTable();
        //发货联系人\联系电话\商家名称
        createNameValueCell(table2, "发货联系人", rmaHandoverPrint.getSendUserName(), fnCH12);
        createNameValueCell(table2, "联系电话", rmaHandoverPrint.getSendUserMobile(), fnCH12);
        createNameValueCell(table2, "商家名称", rmaHandoverPrint.getBusiName(), fnCH12);

        //发货联系人\联系电话\商家名称
        createNameValueCell(table2, "收货人", rmaHandoverPrint.getReceiver(), fnCH12);
        createNameValueCell(table2, "联系电话", rmaHandoverPrint.getReceiverMobile(), fnCH12);
        createNameValueCell(table2, "收货地址", rmaHandoverPrint.getReceiverAddress(), fnCH12);

        document.add(table1);
        document.add(table2);

        PdfPTable table4 = createTitleTable();
        table4.setSpacingBefore(10);
        createOneTitleCell(table4, "货物信息", fnBoldCH12);
        //发货联系人\联系电话\商家名称
        createNameValueCell(table4, "运单数量", rmaHandoverPrint.getWaybillCount().toString(), fnCH12);
        createNameValueCell(table4, "包裹数", rmaHandoverPrint.getPackageCount().toString(), fnCH12);
        createNameValueCell(table4, "备件数量", rmaHandoverPrint.getSpareCount().toString(), fnCH12);
        document.add(table4);

        Paragraph p5 = new Paragraph("JD发货明细交接单", fnCH14);
        p5.setAlignment(Element.ALIGN_CENTER);
        p5.setSpacingBefore(10);
        document.add(p5);

        PdfPTable table = createDetailTable(handoverDetails, fnCH12, fnCH10);
        document.add(table);
    }

    private static PdfPTable createTitleTable() {
        float[] width = {90f, 90f, 90f, 90f, 90f, 180f};
        PdfPTable table = new PdfPTable(width);
        //设置表格的宽度
        table.setTotalWidth(535);
        //设置表格的宽度固定
        table.setLockedWidth(true);
        return table;
    }

    private static PdfPCell getCommonCell(String value, Font font) {
        PdfPCell valueCell = new PdfPCell(new Paragraph(value, font));
        //垂直居中
        valueCell.setUseAscender(true);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valueCell.setMinimumHeight(COMMON_CELL_HEIGHT);
        return valueCell;
    }

    private static void createOneTitleCell(PdfPTable table, String title, Font font) {
        PdfPCell leftTitleOne = getCommonCell(title, font);
        leftTitleOne.setColspan(6);
        leftTitleOne.setHorizontalAlignment(Element.ALIGN_LEFT);
        leftTitleOne.setBackgroundColor(new BaseColor(128, 128, 128));
        table.addCell(leftTitleOne);
    }

    private static void createNameValueCell(PdfPTable table, String name, String value, Font font) {
        PdfPCell nameCell = getCommonCell(name, font);
        nameCell.setBackgroundColor(new BaseColor(191, 191, 191));
        nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell valueCell = getCommonCell(value, font);
        table.addCell(nameCell);
        table.addCell(valueCell);
    }

    private static PdfPTable createDetailTable(List<RmaHandoverDetail> data, Font tableFont, Font cellFont) {
        float[] width = {90f, 90f, 90f, 90f, 180f, 90f};
        PdfPTable table = new PdfPTable(width);
        table.setSpacingBefore(10);
        table.setTotalWidth(535);
        table.setLockedWidth(true);
        String[] titles = new String[]{"备件条码", "运单号", "出库单号", "商品编号", "商品名称", "异常备注"};
        for (String s : titles) {
            PdfPCell cell = getCommonCell(s, tableFont);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new BaseColor(191, 191, 191));
            table.addCell(cell);
        }
        //循环表格填充数据--Object换成遍历对象
        for (RmaHandoverDetail rmaHandoverDetail : data) {
            table.addCell(getCommonCell(rmaHandoverDetail.getSpareCode(), cellFont));
            table.addCell(getCommonCell(rmaHandoverDetail.getWaybillCode(), cellFont));
            table.addCell(getCommonCell(rmaHandoverDetail.getOutboundOrderCode(), cellFont));
            table.addCell(getCommonCell(rmaHandoverDetail.getSkuCode(), cellFont));
            table.addCell(getCommonCell(rmaHandoverDetail.getGoodName(), cellFont));
            table.addCell(getCommonCell(rmaHandoverDetail.getExceptionRemark(), cellFont));
        }
        return table;
    }
}
