package edu.sustech.game.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserAuth {
	
	private static String filePath = "C:\\Users\\zhuzh\\IdeaProjects\\Project\\src\\user.txt";
	
	/**
	 * 用户登录
	 * @param username
	 * @param password
	 * @return true: 登录成功，false: 登录失败
	 * @throws IOException
	 */
	public static boolean login(String username, String password) throws IOException {
		FileReader fr = null;
		BufferedReader br = null;
		
		try {
			File file = new File(filePath);
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] str = line.split(" ");
				if (str[0].equals(username) && str[1].equals(password)) {
					return true;
				}
			}
		}catch(IOException ex) {
			//ex.printStackTrace();
			throw new IOException("用户登录异常", ex);
		}finally {
			if (br != null) br.close();
			if (fr != null) fr.close();
		}
		
		return false;
	}
	
	/**
	 * 用户注册
	 * @param username
	 * @param password
	 * @return true: 注册成功
	 * @throws IOException
	 */
	public static boolean signup(String username, String password) throws IOException {
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			File file = new File(filePath);
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			bw.newLine();
			bw.write(username + " " + password);
			bw.flush();
			
			return true;
		}catch(IOException ex) {
			throw new IOException("用户注册异常", ex);
		}finally {
			if (bw != null) bw.close();
			if (fw != null) fw.close();
		}
		
	}

	public static boolean exist(String username) throws IOException {
		FileReader fr = null;
		BufferedReader br = null;

		try {
			File file = new File(filePath);
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;
			while ((line=br.readLine()) != null) {
				String[] str = line.split(" ");
				if (str[0].equals(username)) {
					return true;
				}
			}
		}catch(IOException ex) {
			//ex.printStackTrace();
			throw new IOException("用户登录异常", ex);
		}finally {
			if (br != null) br.close();
			if (fr != null) fr.close();
		}

		return false;
	}

}
