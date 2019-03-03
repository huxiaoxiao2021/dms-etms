package com.jd.bluedragon.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormal;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopReceiveAbnormal;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @User：zhaohengchong
 * @E-mail: zhaohengchong@360buy.com
 * @Date：2011-10-27
 * @Time：下午01:10:01 类说明 导出对象
 */
public class WorkBookObject {

	private HSSFWorkbook wb = new HSSFWorkbook();

	private HSSFSheet sheet;

	private int cellSize;

	private List<String> titleList = new ArrayList<String>();

	public final static int TITLE_ROW = 0;
	/**
	 * 行游标
	 */
	private int rowCursor = TITLE_ROW;
	public final static String PATTERN_0 = "yyyy-MM-dd";
	public final static String PATTERN_1 = "yyyy-MM-dd HH:mm:ss";

	public WorkBookObject() {

	}

	/**
	 * 创建单一默认的sheet
	 * 
	 * @param sheetName
	 */
	public WorkBookObject(String sheetName) {
		this.sheet = ExportByPOIUtil.createSheet(wb, sheetName);
	}

	/**
	 * 增加标题栏
	 * 
	 * @param rowTitle
	 */
	public void addTitle(String rowTitle) {
		titleList.add(rowTitle);
	}

	/**
	 * 增加标题栏
	 * 
	 * @param rowTitles
	 */
	public void addTitle(String[] rowTitles) {
		if (rowTitles != null && rowTitles.length > 0) {
			for (String rowTitle : rowTitles) {
				titleList.add(rowTitle);
			}
		}
	}

	/**
	 * 设置标题栏
	 */
	public void saveTitle() {
		if (this.titleList != null && this.titleList.size() != 0) {
			saveTitle(this.titleList);
		}
	}

	/**
	 * 设置标题栏
	 * 
	 * @param titleList
	 */
	public void saveTitle(List<String> titleList) {
		if (titleList != null && titleList.size() != 0) {
			this.cellSize = titleList.size();
			HSSFRow row = ExportByPOIUtil.createRow(this.sheet, TITLE_ROW);
			for (int i = 0; i < this.cellSize; i++) {
				HSSFCell cell = ExportByPOIUtil.createCell(row, i);
				cell.setCellValue(titleList.get(i));
			}
		}
		rowCursor++;
	}

	/**
	 * 保存数据到 workbook
	 * 
	 * @param <T>
	 *            简单的对象
	 * @param dataList
	 *            对象集合
	 * @param T
	 * @param tAttrs
	 *            需要导出的属性
	 */
	public <T> void saveDataList(List<T> dataList, Class<T> T,
			String[] tAttrs) {
		try {
			for (T t : dataList) {
				HSSFRow row = ExportByPOIUtil.createRow(this.sheet,
						this.rowCursor++);
				for (int i = 0; i < cellSize; i++) {
					HSSFCell cell = ExportByPOIUtil.createCell(row, i);
					if (tAttrs != null && tAttrs.length != 0) {
						String methodName = "get"
								+ tAttrs[i].substring(0, 1).toUpperCase()
								+ tAttrs[i].substring(1);
						Method method = T.getMethod(methodName);
						Object obj = method.invoke(t);
						if (obj != null) {
							if (obj instanceof Integer) {
								// 增加类特殊属性处理：PopAbnormal
								if (PopAbnormal.class.equals(T.getClass())) {
									String thisCellValue = "";
									if ("abnormalState".equals(tAttrs[i])) {
										if ((Integer) obj == 1) {
											thisCellValue = "已发申请";
										} else {
											thisCellValue = "审核完毕";
										}
									} else if ("abnormalType".equals(tAttrs[i])
											&& (Integer) obj == 1) {
//										if ((Integer) obj == 1) {
											thisCellValue = "平台订单包裹数不一致";
//										}
									}
									if (!"".equals(thisCellValue)) {
										cell.setCellValue(thisCellValue);
										continue;
									}
								} else if (PopReceiveAbnormal.class.equals(T.getClass())) {
									String thisCellValue = "";
									if ("abnormalStatus".equals(tAttrs[i])) {
										if ((Integer) obj == 1) {
											thisCellValue = "未回复";
										} else if ((Integer) obj == 2) {
											thisCellValue = "已回复";
										} else if ((Integer) obj == 3) {
											thisCellValue = "已完成";
										}
									}
									if (!"".equals(thisCellValue)) {
										cell.setCellValue(thisCellValue);
										continue;
									}
								}
								cell.setCellValue((Integer) obj);
							} else if (obj instanceof Float) {
								cell.setCellValue((Float) obj);
							} else if (obj instanceof Double) {
								cell.setCellValue((Double) obj);
							} else if (obj instanceof String) {
								cell.setCellValue((String) obj);
							} else if (obj instanceof Date) {
								cell.setCellValue(parseDate2String((Date) obj));
							} else {
								cell.setCellValue(obj.toString());
							}
						}
					} else {
						cell.setCellValue(t.toString());
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据默认格式转换指定日期
	 * 
	 * @param date
	 * @return
	 */
	public String parseDate2String(Date date) {
		return parseDate2String(date, PATTERN_1);
	}

	/**
	 * 根据指定格式转换指定日期
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public String parseDate2String(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public HSSFWorkbook getWb() {
		return wb;
	}

	public void setWb(HSSFWorkbook wb) {
		this.wb = wb;
	}

	public HSSFSheet getSheet() {
		return sheet;
	}

	public void setSheet(HSSFSheet sheet) {
		this.sheet = sheet;
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}

	public List<String> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<String> titleList) {
		this.titleList = titleList;
	}

	public int getRowCursor() {
		return rowCursor;
	}

	public void setRowCursor(int rowCursor) {
		this.rowCursor = rowCursor;
	}
}
