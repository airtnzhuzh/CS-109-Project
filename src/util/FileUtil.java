package edu.sustech.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {
	
	public static void main(String[] args) {
		String filePath = "C:\\Users\\zhuzh\\IdeaProjects\\Project\\game\\test.txt";
		String hashFilePath = "C:\\Users\\zhuzh\\IdeaProjects\\Project\\game\\test-h.txt";
		
		try {
			File file = new File(filePath);
			saveHashFile(file, hashFilePath);
			
			if (isModified(file, hashFilePath)) {
				System.out.println("文件被篡改");
			}else {
				System.out.println("文件正常");
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 检查文件是否被篡改
	 * @param file 源文件
	 * @param hashFilePath 哈希值文件路径
	 * @return
	 * @throws Exception
	 */
	public static boolean isModified(File file, String hashFilePath) throws Exception{
		File hashFile = new File(hashFilePath);
		if (!file.exists() && !hashFile.exists()) {
			return false;
		}
		
		String hashString = generateChecksum(file);
		String hashFileToString = fileToString(hashFilePath);
		

		
		if (hashString.equals(hashFileToString)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 保存文件为哈希值的文件
	 * @param file 源文件
	 * @param hashFilePath 存储哈希值的文件
	 * @throws IOException
	 */

	public static void saveHashFile(File file, String hashFilePath) throws IOException {
		FileWriter hfw = null;
		BufferedWriter hbw = null;
		
		try {
			//Hash File
			String hashString = generateChecksum(file);
			File hashFile = new File(hashFilePath);

			if (!file.exists()){
				return;
			}
			
			if (!hashFile.exists()) {
				hashFile.createNewFile();
			}

			hfw = new FileWriter(hashFile);
			hbw = new BufferedWriter(hfw);
			hbw.write(hashString);
			
		}catch(Exception ex) {
			ex.printStackTrace();
			throw new IOException("保存Hash File异常", ex);
		}finally {
			if (hbw != null) hbw.close();
			if (hfw != null) hfw.close();
		}
	}
	
	/**
	 * 读取文件内容到字符串
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String fileToString(String filePath) throws IOException{
		FileInputStream fis = null;
		try {
			File file = new File(filePath);
			if (!file.exists()){
				return null;
			}
	        fis = new FileInputStream(filePath);
	        byte[] data = new byte[fis.available()];
	        fis.read(data);
	
	        return new String(data);
	    } catch (IOException ex) {
	        //e.printStackTrace();
	        throw new IOException(ex);
	    } finally {
			if(fis != null)fis.close();
	    }
	}
	
	/**
	 * 产生哈希值
	 * @param file
	 * @return 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static String generateChecksum(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);
 
        byte[] byteArray = new byte[1024];
        int bytesCount;
 
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
 
        fis.close();
        byte[] md5Bytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
 
        for (byte md5Byte : md5Bytes) {
            hexString.append(Integer.toString((md5Byte & 0xff) + 0x100, 16).substring(1));
        }
 
        return hexString.toString();
    }
	
	/**
	 * 清空文件内容
	 * @param filePath
	 * @throws IOException
	 */
	public static void clearFile(String filePath) throws IOException {
        BufferedWriter bufferedWriter = null;
		try {
            // 使用FileWriter和BufferedWriter清空文件内容
            bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            bufferedWriter.write(""); // 清空文件内容
            
       } catch (IOException ex) {
            throw new IOException("文件清空出错", ex);
       }finally {
    	   if (bufferedWriter != null) bufferedWriter.close();
       }
	}
}
