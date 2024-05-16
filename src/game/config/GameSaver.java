package edu.sustech.game.config;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import edu.sustech.game.pane.CardMatrixPane;
import edu.sustech.game.pane.CardPane;
import javafx.scene.layout.GridPane;

public class GameSaver {
	private static String progressFilePrefix = "C:\\Users\\zhuzh\\IdeaProjects\\Project\\data\\progress";
	private static String scoreFilePrefix = "C:\\Users\\zhuzh\\IdeaProjects\\Project\\data\\score";
	private String progressFile = "";
	private String scoreFile = "";

	public GameSaver(String username) {
		progressFile = progressFilePrefix + "-" + username + ".txt";
		scoreFile = scoreFilePrefix + "-" + username + ".txt";
	}

	/**
	 * 检查进度文件是否存在
	 */
	public boolean exist() {
		File file = new File(progressFile);
		if (file.exists()) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 清空进度和分数文件
	 */
	public void clear() {
		clearFile(progressFile);
		clearFile(scoreFile);
	}

	private static void clearFile(String filePath) {
		try {
            // 使用FileWriter和BufferedWriter清空文件内容
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(""); // 清空文件内容
            bufferedWriter.close();
            fileWriter.close();
       } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * 保存进度、分数和各数字的得分
	 * @throws IOException
	 */

	public void save(CardMatrixPane cardMatrixPane) throws IOException {
		saveProgress(cardMatrixPane);
		saveQuantities(cardMatrixPane);
	}


	/**
	 * 保存行列数和游戏盘各卡片数字
	 * @param cardMatrixPane
	 */

	public void saveProgress(CardMatrixPane cardMatrixPane) throws IOException {
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		CardPane[][] cardPane = cardMatrixPane.getCardPane();
		GridPane gridPane = cardMatrixPane.getGridPane();
		
		try {
			File file = new File(progressFile);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			//设为可续写
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			//记录行列数
			bw.write(gridPane.getColumnCount() + " " + gridPane.getRowCount());

			for (int i=0; i<cardPane.length; i++){
				bw.newLine();
				for (int j=0; j<cardPane[i].length; j++) {
					if (j>0) bw.write(" ");
					bw.write(String.valueOf(cardPane[j][i].getType()));
				}
			}
			
			bw.flush();
		}catch(IOException ex) {
			throw new IOException("保存进度异常", ex);
		}finally {
			if (bw != null) bw.close();
			if (fw != null) fw.close();
		}
	}
	
	/**
	 * 读取文件并初始化游戏盘
	 * @return
	 */
	public Map<String, Object> getCardPane() throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		CardPane[][] cardPane = null;
		FileReader fr = null;
		BufferedReader br = null;
		int cols = 0;
		int rows = 0;

		try {
			File file = new File(progressFile);
			if (!file.exists()) {
				return null;
			}

			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;
			int i = 0;
			while ((line=br.readLine()) != null) {
				String[] str = line.split(" ");
				if (i==0) {
					cols = Integer.valueOf(str[0]).intValue();
					rows = Integer.valueOf(str[1]).intValue();
					cardPane = new CardPane[cols][rows];
				}else {
					for (int j=0; j<str.length; j++) {
						int type = Long.valueOf(str[j]).intValue();
						cardPane[j][i-1] = new CardPane(type);
					}
				}

				i++;
			}
		}catch(IOException ex) {
			//ex.printStackTrace();
			throw new IOException("用户进度读取异常", ex);
		}finally {
			if (br != null) br.close();
			if (fr != null) fr.close();
		}
		map.put("cols", cols);
		map.put("rows", rows);
		map.put("cardPane", cardPane);

		return map;
	}
	
	/**
	 * 存储分数和各数字的分数
	 * @param score
	 * @param quantities
	 */
	public void saveQuantities(CardMatrixPane cardMatrixPane) throws IOException{
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File file = new File(scoreFile);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			//设为可续写
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			bw.write(String.valueOf(cardMatrixPane.getScore()));

			int[] quantities = cardMatrixPane.getMcQuantities();
			for (int i=0; i<quantities.length; i++) {
				bw.newLine();
				bw.write(String.valueOf(quantities[i]));
			}
			bw.flush();
		}catch(IOException ex) {
			throw new IOException("保存得分异常", ex);
		}finally {
			if (bw != null) bw.close();
			if (fw != null) fw.close();
		}
	}
	
	/**
	 * 读取分数和各数字的分数
	 * @return Map
	 */
	public Map<String, Object> getScoreAndQuantities() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		int[] qty = new int[15];
		FileReader fr = null;
		BufferedReader br = null;

		try {
			File file = new File(scoreFile);
			if (!file.exists()) {
				return null;
			}

			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;
			int i = 0;
			while ((line=br.readLine()) != null) {
				if (i == 0) {
					Long score = Long.valueOf(line);//读取第一行数字
					map.put("score", score);
				}else {
					qty[i-1] = Integer.valueOf(line).intValue();
				}
			}

			map.put("quantities", qty);
		}catch(Exception ex) {
			throw new Exception("用户得分读取异常");
		}finally {
			if (br != null) br.close();
			if (fr != null) fr.close();
		}

		return map;
	}
}
