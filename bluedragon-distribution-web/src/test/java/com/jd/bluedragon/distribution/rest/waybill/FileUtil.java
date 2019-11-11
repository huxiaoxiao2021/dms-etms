package com.jd.bluedragon.distribution.rest.waybill;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 创建目录
	 * 
	 * @param dir
	 *            要创建目录
	 * @param recursive 是否递归创建目录，就是说如果父目录不存在的话就先创建父目录            
	 */
	public static void mkdir(String dir, boolean recursive) {
		try {
			String dirTemp = dir;
			File dirPath = new File(dirTemp);
			if (!dirPath.exists()) {
				if(recursive)//如果递归创建，则先创建父目录 
					mkdir(dirPath.getParent(), recursive);
				dirPath.mkdir();
			}
		} catch (Exception e) {
			log.error("Creat dir fail,dir={}",dir,e);
		}
	}
	
	/**
	 * 获取文件 可以根据正则表达式查找
	 * 
	 * @param dir
	 *            String 文件夹名称
	 * @param fileNamePattern 用于查找匹配的正则表达式
	 *           
	 * @return File[] 找到的文件
	 */
	public static ArrayList<File> getFiles(String dir, String fileNamePattern) {
		if(log.isDebugEnabled()){
			log.debug("start getFiles get files from:{} where file name like {}",dir, fileNamePattern);
		}
		ArrayList<File> list = new ArrayList<File>();
		// 开始的文件夹
		File file = new File(dir);
		if(log.isDebugEnabled()){
			log.debug(fileNamePattern);
		}
		Pattern p = Pattern.compile(fileNamePattern);

		// 是从文件夹中找，如果源不是文件夹，那么不用找了
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory())// 只取当前目录下的文件，不递归查找
						continue;
					Matcher fMatcher = p.matcher(files[i].getName());
					if (fMatcher.matches()) {
						list.add(files[i]);
					}
				}
			}
		}
		if(log.isDebugEnabled()){
			log.debug("end getFiles get files from:{} where file name like {}",dir, fileNamePattern);
		}
		// 即使为空，也返回一个空的列表，方便判断
		return list;
	}

	/**
	 * 读取文件的内容，返回列表，保存每一行的文件内容，只限于小文件，对于大文件不可这样处理
	 * 当为文件夹时返回空列表
	 * @param file 要处理的文件
	 * 
	 * @return ArrayList<String>  每行文件内容
	 */
	public static ArrayList<String> getFileContent(File file) {
		if(log.isDebugEnabled()){
			log.debug("start getFileContent read file:{}",file.getAbsolutePath());
		}
		ArrayList<String> list = new ArrayList<String>();
		if (file.isFile()) {//读取文件内容并保存到ArrayList中
			BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String tempString = null;
	            // 一次读入一行，直到读入null为文件结束
	            while ((tempString = reader.readLine()) != null) {
	                // 显示行号
	                list.add(tempString);
	            }
	            reader.close();
	        } catch (IOException e) {
	            log.error("Get file :{} content fail!\n",file.getAbsolutePath(),e);
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e) {
	                	log.error("Close file:{} fail!\n",file.getAbsolutePath(),e);
	                	e.printStackTrace();
	                }
	            }
	        }
		}else if(log.isDebugEnabled()){
			log.debug("Argument is a dir not file!");
		}
		if(log.isDebugEnabled()){
			log.debug("end getFileContent read file:{}",file.getAbsolutePath());
		}
		return list;
	}

	/**
	 * 文件拷贝方法
	 * @param srcPath
	 * @param destPath
	 * @throws IOException
	 */
	public static void doCopyFile(String srcPath, String destPath) throws IOException{
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		FileInputStream input = new FileInputStream(srcFile);
        try {
            FileOutputStream output = new FileOutputStream(destFile);
            try {
            	byte[] buffer = new byte[4096];
                int n = 0;
                while (-1 != (n = input.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            } finally {
            	try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException ioe) {
                }
            }
        } finally {
        	try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ioe) {
            }
        }
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println(FileUtil.chkFile("D:/testFtpDir/P1111120120327000001.AVL", "17748", "438"));
//		FileUtil.mkdir("d:/123/123/123/123", true);
		ArrayList<String> waybillCodeArr = FileUtil.getFileContent(new File("d:/waybillCodes.txt"));
		for(String code:waybillCodeArr){
			System.out.println(code);
		}
	}

}
