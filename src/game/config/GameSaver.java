package edu.sustech.game.config;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sustech.game.pane.CardMatrixPane;
import edu.sustech.game.pane.CardPane;
import edu.sustech.util.FileUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import static edu.sustech.util.FileUtil.isModified;
import static java.lang.String.valueOf;


public class GameSaver {
	private static String progressFilePrefix = "C:\\Users\\zhuzh\\IdeaProjects\\Project\\data\\progress";
	private static String scoreFilePrefix = "C:\\Users\\zhuzh\\IdeaProjects\\Project\\data\\score";
	private static String recordFilePrefix = "C:\\Users\\zhuzh\\IdeaProjects\\Project\\data\\record";
	private String progressFile = "";
	private String scoreFile = "";
	private String recordFile = "";
	private String progressFileHash = "";
	private String scoreFileHash = "";
	private String recordFileHash = "";
	private String username;

	public GameSaver(String username) {
		progressFile = progressFilePrefix + "-" + username + ".txt";
		scoreFile = scoreFilePrefix + "-" + username + ".txt";
		recordFile= recordFilePrefix + ".txt";
		progressFileHash = progressFilePrefix + "-" + username + "-h.txt";
		scoreFileHash = scoreFilePrefix + "-" + username + "-h.txt";
		recordFileHash = recordFilePrefix + "-h.txt";
		this.username = username;
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
			String hashFilePath = progressFileHash;
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
					bw.write(valueOf(cardPane[j][i].getType()));
				}
			}

			bw.flush();

			bw.close();
			FileUtil.saveHashFile(new File(progressFile), progressFileHash);

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
		int i = 0;
		try {
			File file = new File(progressFile);
			File hashFile = new File(progressFileHash);
			if (isModified(file, progressFileHash)) {
				throw new IOException("进度文件被篡改");
//				Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), ev -> {
//				Alert alert=new Alert(Alert.AlertType.INFORMATION);
//				alert.setTitle(alert.getAlertType().toString());
//				alert.setContentText("进度文件被篡改");
//				alert.show();
//				}));
//				timeline.play();
			}
			if (!file.exists()) {
				throw new IOException("进度文件不存在");
			}
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;
			while ((line=br.readLine()) != null) {
				String[] str = line.split(" ");
				if (i==0) {
					if (str.length==2){
						cols = Integer.valueOf(str[0]).intValue();
						rows = Integer.valueOf(str[1]).intValue();
						if (cols == rows && cols > 0){
							cardPane = new CardPane[cols][rows];
						}else {
							throw new IOException("行列数为0或不相同");
						}
					}
					else{
						throw new IOException("文件第一行非行列数");
					}
				}else {
					for (int j=0; j<str.length; j++) {
						if (str.length == cols) {
							int type = Long.valueOf(str[j]).intValue();
							if (type >= 0 && type <= 16) {
								cardPane[j][i - 1] = new CardPane(type);
							} else {
								throw new IOException("第" + j + "行，" + (i-1) + "列数字不符合要求");
							}
						}else{
							throw new IOException("第" + j + "行列数不一致");
						}
					}
				}
				i++;
			}
		}catch(NumberFormatException nex) {
			throw new IOException("存在非数字的字符");
		}catch(IOException ex) {
			//ex.printStackTrace();
			throw new IOException("用户进度文件读取异常", ex);
		}finally {
			if (br != null) br.close();
			if (fr != null) fr.close();
		}
		if (i == 0 || i-1 != cols){
			throw new IOException("用户进度读取异常");
		}
		map.put("cols", cols);
		map.put("rows", rows);
		map.put("cardPane", cardPane);

		return map;
	}
	
	/**
	 * 存储分数和各数字的分数
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
			bw.write(valueOf(cardMatrixPane.getUsecount()));
			bw.newLine();
			bw.write(valueOf(cardMatrixPane.getAiuse()));
			bw.newLine();
			bw.write(valueOf(cardMatrixPane.getStep()));
			bw.newLine();
			bw.write(valueOf(cardMatrixPane.getScore()));

			int[] quantities = cardMatrixPane.getMcQuantities();
			for (int i=0; i<quantities.length; i++) {
				bw.newLine();
				bw.write(quantities[i]+"");
			}
			bw.flush();

			bw.close();
			FileUtil.saveHashFile(new File(scoreFile), scoreFileHash);

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
	public Map<String, Object> getScoreAndQuantities() throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		int[] qty = new int[15];
		FileReader fr = null;
		BufferedReader br = null;
		int i = 0;

		try {
			File file = new File(scoreFile);
			File hashFile = new File(scoreFileHash);
			if (isModified(file, scoreFileHash)) {
				throw new IOException("得分文件被篡改");

			}
			if (!file.exists()) {
				throw new IOException("得分文件不存在");
			}

			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;

			while ((line=br.readLine()) != null) {
				if (i == 0) {
					int usecount = Integer.valueOf(line);//读取第一行数字
					if (usecount < 0) {
						throw new IOException("数字有负数");
					}
					map.put("usecount", usecount);
				} else if (i == 1) {
					int aiuse = Integer.valueOf(line);//读取第二行数字
					if (aiuse < 0) {
						throw new IOException("数字有负数");
					}
					map.put("aiuse", aiuse);
				} else if (i == 2) {
					int step = Integer.valueOf(line);//读取第四行数字
					if (step < 0) {
						throw new IOException("数字有负数");
					}
					map.put("step", step);
				}else if (i == 3) {
					Long score = Long.valueOf(line);//读取第三行数字
					if (score.longValue() < 0) {
						throw new IOException("数字有负数");
					}
					map.put("score", score);
				}else if(i>3){
					qty[i-4] = Integer.valueOf(line).intValue();
					if (qty[i-4] < 0 || (i > 4 && qty[i-5] < qty[i-4]))
						throw new IOException("得分数字不符合要求");
				}
				i++;
			}

			map.put("quantities", qty);
		}catch(NumberFormatException ex) {
			throw new IOException("存在非数字的字符");
		}catch(Exception ex) {
			throw new IOException("得分文件读取异常");
		}finally {
			if (br != null) br.close();
			if (fr != null) fr.close();
		}

		if (i !=19) {
			throw new IOException("得分文件行数不对");
		}

		return map;
	}

	public void saveRecord(CardMatrixPane cardMatrixPane) throws IOException {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			List<String> list = getRecord();
			File file = new File(recordFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			//设为可覆盖
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			StringBuffer sb = new StringBuffer();
			long currentScore = cardMatrixPane.getScore();
			int i = 0;
			int j=0;
			if (list != null && list.size()>0) {
				for (String userScore : list) {
					String[] str = userScore.split(" ");
					if (i == 0 && str.length > 1 && currentScore > Long.valueOf(str[str.length - 1]).longValue()) {
						if (j > 0) {
							sb.append("\n");
						}

						sb.append(username + " " + currentScore);
						i++;
						j++;
					}

					if (j > 0) {
						sb.append("\n");
					}
					sb.append(userScore);
					j++;
				}
			}
			if (i==0) {
				if(j > 0){
					sb.append("\n");
				}
				sb.append(username + " " + currentScore);
			}

			bw.write(sb.toString());
			bw.flush();

			bw.close();
			FileUtil.saveHashFile(new File(recordFile), recordFileHash);

		}catch(IOException ex) {
			throw new IOException("保存记录异常", ex);
		}finally {
			if (bw != null) bw.close();
			if (fw != null) fw.close();
		}
	}

	public List<String> getRecord() throws IOException{
		List<String> list = new ArrayList<String>();
		FileReader fr = null;
		BufferedReader br = null;

		try {
			if (FileUtil.isModified(new File(recordFile), recordFileHash)) {
				throw new IOException("历史记录文件被篡改");
			}

			File file = new File(recordFile);
			if (!file.exists()) {
				throw new IOException("历史记录文件不存在");
			}

			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;
			while ((line=br.readLine()) != null) {
				String[] str = line.split(" ");
				if (str.length!=2 || !str[str.length-1].matches("\\d+")) {
					throw new IOException("历史记录文件格式错误");
				}
					list.add(line);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			throw new IOException("用户得分读取异常");
		}finally {
			if (br != null) br.close();
			if (fr != null) fr.close();
		}

		return list;
	}
}
