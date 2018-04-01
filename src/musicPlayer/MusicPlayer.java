package musicPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import application.MusicPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import musicListPanel.MusicSqlite;

/**
 * 此类为音乐播放类，包括音乐播放的播放、暂停等等播放功能
 */

public class MusicPlayer {

	private ArrayList<String> musicPaths;	//音乐路径
	private int index = 0;	//当前播放音乐的索引位置
	private Media currentMedia = null;	//当前音乐的Media
	private MediaPlayer currentPlayer = null;	//当前音乐的MediaPlayer
	private ArrayList<File> musicList;	//音乐列表
	private String music_name;	//当前歌曲名称
	private String music_author;	//当前歌曲的作者
	private ArrayList<Map<String, String>> musicInfor;	//全部歌曲的信息集合
	private ArrayList<Integer> timeSet;	//全部歌曲时间
	private String music_time; //当前歌曲时间
	private int totalTime;	//获取总时间
	private int play_mode = 0;	//播放模式（0--列表循环，1---单曲循环，2--随机播放）
	public static final int LIST_CYCLE = 0;	//列表循环
	public static final int SINGLE_CYCLE = 1;	//单曲循环
	public static final int RANDOM_PLAY = 2;	//随机播放

	//构造方法
	public MusicPlayer(ArrayList<String> musicPaths, int index) {
		//获取播放路径
		this.musicPaths = musicPaths;
		//获取播放位置
		this.index = index;
		//初始化
		initMusicPlayer();
	}

	//初始化
	private void initMusicPlayer() {
		musicList = new ArrayList<File>();
		//获取音乐文件
		if(musicPaths != null) {
			for(int i = 0; i < musicPaths.size(); i++) {
				File music = new File(musicPaths.get(i));
				if(music.exists()) {
					musicList.add(music);
				} else {
					musicList.add(null);
				}
			}
			//初始化音乐信息
			initMusicInformation();
			if(getCurrentMusic() != null) {
				currentPlayer = new MediaPlayer(getCurrentMusic());
			}
		}
	}

	//返回指向正在播放音乐的MediaPlayer对象
	public MediaPlayer getCurrentMusicPlayer() {
		return currentPlayer;
	}

	//返回歌曲的作者名
	public String getMusicAuthor() {
		return this.music_author;
	}

	//返回歌曲的名称
	public String getMusicName() {
		return this.music_name;
	}

	//设置播放模式
	public void setPlayModel(int model) {
		this.play_mode = model;
	}

	//获取当前播放模式
	public int getPlayModel() {
		return this.play_mode;
	}

	//播放下一首音乐
	public void playNextMusic() {
		//当前音乐暂停
		currentPlayer.stop();
		currentPlayer.dispose();
		//更新当前音乐Media
		currentMedia = getNextMusic();
		//更新当前音乐播放类
		currentPlayer = new MediaPlayer(currentMedia);
		//播放音乐
		playCurrentMusic();
	}

	//播放上一首歌曲
	public void playFrontMusic() {
		//当前音乐暂停
		currentPlayer.stop();
		currentPlayer.dispose();
		//更新当前音乐Media
		currentMedia = getFrontMusic();
		//更新当前音乐播放类
		currentPlayer = new MediaPlayer(currentMedia);
		//播放音乐
		playCurrentMusic();
	}

	//播放当前音乐
	public void playCurrentMusic() {
		//Media为null时，先获取
		if(currentMedia == null || currentPlayer == null) {
			currentMedia = getCurrentMusic();
			currentPlayer = new MediaPlayer(currentMedia);
		}
		//播放音乐
		currentPlayer.play();
	}


	//播放指定索引的音乐
	public void playMusicByIndex(int index) {
		this.index = index;
		if(currentPlayer != null) {
			currentPlayer.pause();
			currentPlayer.dispose();
		}
		currentPlayer = null;
		//播放当前音乐
		playCurrentMusic();
	}

	//获取上一首音乐
	private Media getFrontMusic() {
		//记录当前索引
		int currentIndex = index;
		//获取上一索引
		index = getFrontMusicIndex();
		//如果对象为null，则一直往上寻找
		while(musicList.get(index) == null) {
			//向上寻找
			index = (index - 1 + musicList.size()) % musicList.size();
			//整个列表为null时，退出
			if(index == currentIndex) {
				break;
			}
		}
		//如果找不到不为null的音乐对象，则返回null；否则，返回当前Media对象
		if(musicList.get(index) == null) {
			return null;
		} else{
			return new Media(musicList.get(index).toURI().toString());
		}
	}

	//获取当前音乐
	private Media getCurrentMusic() {
		//当前音乐不为null，则返回当前Media对象；否则，往下寻找
		if(!musicList.isEmpty()) {
			try {
				if(musicList.get(index) != null) {
					return new Media(musicList.get(index).toURI().toString());
				} else{
					return getNextMusic();
				}
			} catch(MediaException exception) {
				System.out.println("********");
				return getNextMusic();
			}
		} else{
			return null;
		}
	}

	//获取下一首音乐
	private Media getNextMusic() {
		//记录当前索引
		int currentIndex = index;
		//获取下一索引
		index = getNextMusicIndex();
		while(musicList.get(index) == null) {
			//向下寻找
			index = (index + 1) % musicList.size();
			//音乐列表为null时，退出
			if(index == currentIndex) {
				break;
			}
		}
		//音乐列表为null，找不到可播放的音乐，返回null；否则，返回Media对象
		if(musicList.get(index) == null) {
			return null;
		} else{
			return new Media(musicList.get(index).toURI().toString());
		}
	}

	//获取上一首音乐的索引
	public int getFrontMusicIndex() {
		int frontIndex = index;
		if(play_mode == LIST_CYCLE || play_mode == SINGLE_CYCLE) {
			//如果是列表播放，则为musicList上一首
			frontIndex = (frontIndex - 1 + musicList.size()) % musicList.size();
		} else if(play_mode == RANDOM_PLAY) {
			//如果是随机播放，则产生一个随机数
			while(true) {
				int random = (int) (Math.random()*musicList.size());
				if(random != frontIndex) {
					frontIndex = random;
					break;
				}
			}
		}
		//如果是单曲循环模式，则不变
		return frontIndex;
	}

	//获取当前播放歌曲的索引
	public int getCurrentMusicIndex() {
		return index;
	}

	//设置当前播放歌曲的索引
	public void setCurrentMusicIndex(int index) {
		this.index = index;
	}

	//获取下一首音乐的索引
	public int getNextMusicIndex() {
		int nextIndex = index;
		if(play_mode == LIST_CYCLE || play_mode == SINGLE_CYCLE) {
			//如果是列表播放，则为musicList下一首
			nextIndex = (nextIndex + 1)%musicList.size();
		} else if(play_mode == RANDOM_PLAY) {
			//如果是随机播放，则产生一个随机数
			while(true) {
				int random = (int) (Math.random()*musicList.size());
				if(random != nextIndex) {
					nextIndex = random;
					break;
				}
			}
		}
		//如果是单曲循环模式，则不变
		return nextIndex;
	}

	//获取音乐的总时间,歌曲名称以及歌曲作者
	private void getMusicMessage(String path) {
		//从数据库中获取信息
		Map<String, String> message = MusicSqlite.getMusicInforByPath(path, MusicPane.getListName());
		//音乐信息
		music_name = message.get("SONGNAME");
		music_author = message.get("SONGER");
		music_time = message.get("TIME");
		totalTime = Integer.parseInt(message.get("TOTALTIME"));
	}


	//获取所有音乐的歌名、作者和总时间
	public ArrayList<Map<String, String>> getMusicInformation() {
		return musicInfor;
	}

	//初始化歌曲信息
	private void initMusicInformation() {
		//音乐信息集合
		musicInfor = new ArrayList<Map<String, String>>();
		//音乐时间（秒为单位）集合
		timeSet = new ArrayList<>();
		for(int i = 0; i < musicList.size(); i++) {
			addMusicMessage(i);
		}
	}

	//根据索引删除歌曲
	public void deleteSelectedMusic(int index) {
		//列表删除
		musicList.remove(index);
		//删除歌曲信息
		musicInfor.remove(index);
	}

	//插队播放时更新歌曲播放列表
	public void updateMusicList(int index) {
		//index为插队播放的歌曲索引
		if(index < this.index) {
			this.index--;
		}
		//调整播放列表顺序
		File music = musicList.get(index);
		musicList.remove(index);
		musicList.add(this.index+1, music);
		//调整歌曲信息顺序
		Map<String, String> infor = musicInfor.get(index);
		musicInfor.remove(index);
		musicInfor.add(this.index+1, infor);
		//更新音乐路径顺序
		String path = musicPaths.get(index);
		musicPaths.remove(index);
		musicPaths.add(this.index+1, path);
	}

	//添加单首歌曲到musicList并且更新musicInfor
	public void addMusicToPlayer() {
		//添加位置
		int addIndex = musicPaths.size() - 1;
		//打开文件
		File musicFile = new File(musicPaths.get(addIndex));
		//文件存在，则添加到musicList；否则，添加null
		if(musicFile.exists()) {
			musicList.add(musicFile);
		} else{
			musicList.add(null);
		}
		//添加音乐信息
		addMusicMessage(addIndex);
	}

	//删除单首歌曲，更新musicList、timeSet和musicInfor
	public void deleteMusicFromPlayer(int delIndex) {
		musicList.remove(delIndex);
		Map<String, String> infor = musicInfor.get(delIndex);
		musicInfor.remove(infor);
		infor = null;
		Integer time = timeSet.get(delIndex);
		timeSet.remove(time);
		time = null;
		//更新索引
		this.index = PlayerPane.getCurrentIndex();
	}

	//添加音乐信息
	private void addMusicMessage(int addIndex) {
		//音乐信息
		Map<String, String> infor = new HashMap<>();
		//获取信息
		getMusicMessage(musicPaths.get(addIndex));
		//整合信息
		if(music_author != null && !music_author.equals("")) {
			infor.put("message", music_name+" - "+music_author);
		} else {
			infor.put("message", music_name);
		}
		infor.put("time", music_time);
		musicInfor.add(infor);
		//音乐总时间
		timeSet.add(totalTime);
	}

	//清空歌曲文件，信息和时间信息
	public void clearMusicMessage() {
		this.musicList.clear();
		this.musicInfor.clear();
		this.timeSet.clear();
		this.index = 0;
	}

	//返回歌曲总时间
	public int getMusicLength(int index) {
		this.totalTime = timeSet.get(index);
		return this.totalTime;
	}

	//根据索引判断歌曲是否失效
	public static boolean isFailed(String path) {
		File musicFile = new File(path);
		//文件失效，返回true；否则，返回false
		if(!musicFile.exists()) {
			return true;
		} else{
			return false;
		}
	}

}
