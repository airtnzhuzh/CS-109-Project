package edu.sustech.game.pane;

/**
 * 回调接口
 */
public interface GameCallbacks {
	/**
	 * 重新开始
	 */
	public void afterRestart();
	
	/**
	 * 重设表格尺寸
	 * @param cols
	 * @param rows
	 */
	public void afterResetGridSize(int cols, int rows);
	
	/**
	 * 获取更详细的分数信息
	 */
	public void afterGetMoreScoreInfo();
	
	/**
	 * 分数变化
	 */
	public void afterScoreChange();
	
	/**
	 * 保存当前进度和得分
	 */
	public void save();
	
	/**
	 * 获取历史记录
	 */
	public void getPastRecords();

	public void logout();
}
