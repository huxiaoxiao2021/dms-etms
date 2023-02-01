package com.jd.bluedragon.distribution.test.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.bluedragon.utils.StringHelper;
/**
 * 
 * @author wuyoude
 *
 */
public class FileHelper {
	private static Logger logger = LoggerFactory.getLogger(FileHelper.class);
	private static final int BUFFER_SIZE = 5 * 1024 * 1024; // 上传时缓存区的大小
	public final static long ONE_KB = 1024;
	public final static long ONE_MB = ONE_KB * 1024;
	public final static long ONE_GB = ONE_MB * 1024;
	public final static long ONE_TB = ONE_GB * (long) 1024;
	public final static long ONE_PB = ONE_TB * (long) 1024;
	/**
	 * 将内容保存到文件中
	 * @param data 内容
	 * @param fileName 文件名
	 * @param code 内容编码
	 * @return
	 */
	public static boolean save(String data,String fileName,String code){
	    File f = new File(fileName);
	    if(!f.getParentFile().exists()){
	    	f.getParentFile().mkdirs();
	    }
	    FileOutputStream foss = null;
	    logger.info("save to file:"+f.getAbsolutePath());
	    try {
	      foss = new FileOutputStream(f);
	      foss.write(data.getBytes(code));
	      foss.flush();
	      foss.close();
	    }catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    	return false;
	    } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static boolean isImage(File file) {
		// TODO Auto-generated method stub
		BufferedImage bi;
		try {
			bi = ImageIO.read(file);
			if(bi != null){ 
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * 得到文件大小
	 * @param fileSize
	 * @return
	 */
	public static String getHumanReadableFileSize(long fileSize) {
		if (fileSize < 0) {
			return String.valueOf(fileSize);
		}
		String result = getHumanReadableFileSize(fileSize, ONE_PB, "PB");
		if (result != null) {
			return result;
		}

		result = getHumanReadableFileSize(fileSize, ONE_TB, "TB");
		if (result != null) {
			return result;
		}
		result = getHumanReadableFileSize(fileSize, ONE_GB, "GB");
		if (result != null) {
			return result;
		}
		result = getHumanReadableFileSize(fileSize, ONE_MB, "MB");
		if (result != null) {
			return result;
		}
		result = getHumanReadableFileSize(fileSize, ONE_KB, "KB");
		if (result != null) {
			return result;
		}
		return String.valueOf(fileSize) + "B";
	}

	private static String getHumanReadableFileSize(long fileSize, long unit,
			String unitName) {
		if (fileSize == 0)
			return "0";

		if (fileSize / unit >= 1) {
			double value = fileSize / (double) unit;
			DecimalFormat df = new DecimalFormat("######.##" + unitName);
			return df.format(value);
		}
		return null;
	}
	
	public static String getFileExt(String fileName) {
		if (fileName == null)
			return "";

		String ext = "";
		int lastIndex = fileName.lastIndexOf(".");
		if (lastIndex >= 0) {
			ext = fileName.substring(lastIndex + 1).toLowerCase();
		}

		return ext;
	}

	/**
	 * 得到不包含后缀的文件名字 
	 * @return
	 */
	public static String getRealName(String name) {
		if (name == null) {
			return "";
		}

		int endIndex = name.lastIndexOf(".");
		if (endIndex == -1) {
			return null;
		}
		return name.substring(0, endIndex);
	}
	/**
	 * 复制一个文件
	 * 
	 * @param srcFileName
	 * @param dstFileName
	 * @return
	 */
	public static boolean copy(String srcFileName, String dstFileName) {
		File srcFile = new File(srcFileName);
		File dstFile = new File(dstFileName);
		return copy(srcFile, dstFile);
	}
	public static void mkdirsParent(String filePath){
		if(StringHelper.isNotEmpty(filePath)){
			File file = new File(filePath);
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
		}
	}
	public static void mkdirs(String path){
		if(StringHelper.isNotEmpty(path)){
			File file = new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
		}
	}
	/**
	 * 复制一个文件
	 * 
	 * @param srcFile
	 * @param dstFile
	 * @return
	 */
	public static boolean copy(File srcFile, File dstFile) {
		boolean result = false;
		InputStream in = null;
		OutputStream out = null;
		try {
			if(!dstFile.getParentFile().exists()){
				dstFile.getParentFile().mkdirs();
			}
			if(!dstFile.exists()){
				dstFile.createNewFile();
			}
			System.out.println(srcFile + "->" + dstFile);
			if (!srcFile.exists()) {
				return false;
			}
			in = new BufferedInputStream(new FileInputStream(srcFile),
					BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(dstFile),
					BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				for (int i = 0; i < len; i++) {
					// System.out.println(buffer[i] + "->" + (buffer[i] ^
					// 0xFF));
					// buffer[i] = (byte) (buffer[i] ^ 0xFF);
				}
				out.write(buffer, 0, len);
				// System.out.println("...");
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	/**
	 * 列出所有文件
	 * @param dir
	 * @return
	 */
	public static List<File> listFiles(File dir){
		List<File> files = new ArrayList<File>(16);
		if(dir.isFile()){
			files.add(dir);
		}else{
			File[] fs = dir.listFiles();
			if(fs != null){
				for(File f:fs){
					files.addAll(listFiles(f));
				}
			}
		}
		return files;
	}
	/**
	 * 获取文件路径
	 * @param path
	 * @param depth-指定目录深度 <0，代表从左到右
	 * @return
	 */
	public static String getPath(String path,int depth){
		return getPath(path,depth);
	}
    public static String loadFile(String file) {
        InputStream inputStream = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder(1024);
        try {
            inputStream = new FileInputStream(new File(file));
            reader = new InputStreamReader(inputStream, Charset.forName("utf-8"));
            bufferedReader = new BufferedReader(reader);

            String line;
            do {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
                sb.append("\n");
            } while (line != null);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
                if (reader != null) reader.close();
                if (inputStream != null) inputStream.close();
            } catch (IOException ex) {
                //do nothing
            }
        }

        return sb.toString();
    }
    public static List<String> loadFileToList(String file) {
    	return loadFileToList(file,true);
    }
    public static List<String> loadFileToList(String file,boolean trimFlg) {
        InputStream inputStream = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        List<String> lines = new ArrayList<String>();
        try {
            inputStream = FileHelper.class.getClassLoader().getResourceAsStream(file);
            if(inputStream==null){
            	inputStream = new FileInputStream(new File(file));
            }
            reader = new InputStreamReader(inputStream, Charset.forName("utf-8"));
            bufferedReader = new BufferedReader(reader);

            String line;
            do {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                if(trimFlg){
                	line = line.trim();
                }
                lines.add(line);
            } while (line != null);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
                if (reader != null) reader.close();
                if (inputStream != null) inputStream.close();
            } catch (IOException ex) {
                //do nothing
            }
        }

        return lines;
    }
    /**
     * 获取文件后缀
     * @param fileName
     * @return
     */
    public static String getPrefix(String fileName){
    	String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
    	return prefix;
    }
}
