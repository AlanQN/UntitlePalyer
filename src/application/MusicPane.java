package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lrcPane.Overwatch;
import musicListPanel.MusicList;
import musicListPanel.MusicSqlite;
import musicPlayer.PlayerPane;

public class MusicPane extends Application {

	private MusicList listPane;	//播放列表面板
	private ArrayList<TreeItem<String>> rootItems;	//歌单结点集合
	private TreeView<String> treeview;	//播放列表
	private static PlayerPane playerPane;	//播放器面板
	private static Overwatch lrcPane;	//歌词信息面板
	private static ArrayList<String> musicPaths;	//要播放音乐的路径
	private	BorderPane root = new BorderPane();	//主面板
	private static int currentIndex = 0;	//播放位置
	private Stage listStage;	//播放列表
	private static String tableName = "PlayList";
	private static String listName;	//当前的歌单名
	private static Map<String, String> musicInfo;	//创建歌词面板时的信息
	private static String pathHead = "lrc/";

	@Override
	public void start(Stage primaryStage) throws Exception {
		//获取currentIndex
		currentIndex = MusicSqlite.getCurrentIndex(tableName);
		System.out.println(currentIndex);
		//获取歌单名
		listName = MusicSqlite.getListName(tableName);
		System.out.println(listName);
		//获取播放列表所有音乐路径
		musicPaths = MusicSqlite.getAllMusicPaths(tableName);
		//获取歌曲信息
		if(musicPaths != null && musicPaths.size() > 0) {
			musicInfo = MusicSqlite.getMusicInforByPath(musicPaths.get(currentIndex), listName);
		} else {
			musicInfo = new HashMap<>();
		}
		System.out.println("*********2");
		if(musicPaths == null) {
			System.out.println("null");
		}
		for(String path : musicPaths) {
			System.out.println(path);
		}
		System.out.println("-----1-----");
		//添加播放栏面板
		playerPane = PlayerPane.getInstance(musicPaths, currentIndex);
		System.out.println("-----2-----");
		root.setBottom(playerPane);
		//获取播放列表
		listStage = PlayerPane.listStage;
		//添加播放列表面板
		listPane = MusicList.getInstance();
		//获取歌单结点
		rootItems = listPane.getRootItems();
		//添加播放列表面板
		root.setLeft(listPane);
		//获取歌词信息面板
		lrcPane = Overwatch.getInstance(musicInfo.get("SONGNAME"), musicInfo.get("SINGER"), musicInfo.get("ALBUM"),
				pathHead+musicInfo.get("SONGNAME")+".lrc", "/AlbumImage/"+musicInfo.get("SONGNAME")+".jpg");
		//添加歌词信息面板
		root.setCenter(lrcPane);
		//获取播放列表
		this.treeview = listPane.getTreeView();
		//设置播放列表双击播放事件
		this.treeview.setOnMouseClicked(mouseEventHandler);
		//设定播放列表位置
		primaryStage.xProperty().addListener(e->{
			listStage.setX(primaryStage.xProperty().get()+primaryStage.widthProperty().get()-320 - 8);
			listStage.setY(primaryStage.yProperty().get() + primaryStage.heightProperty().get()-350 - 68);
		});
		primaryStage.widthProperty().addListener(e->{
			listStage.setX(primaryStage.xProperty().get()+primaryStage.widthProperty().get()-320 - 8);
			listStage.setY(primaryStage.yProperty().get() + primaryStage.heightProperty().get()-350 - 68);
		});
		primaryStage.yProperty().addListener(e->{
			listStage.setX(primaryStage.xProperty().get()+primaryStage.widthProperty().get()-320 - 8);
			listStage.setY(primaryStage.yProperty().get() + primaryStage.heightProperty().get()-350 - 68);
		});
		primaryStage.heightProperty().addListener(e->{
			listStage.setX(primaryStage.xProperty().get()+primaryStage.widthProperty().get()-320 - 8);
			listStage.setY(primaryStage.yProperty().get() + primaryStage.heightProperty().get()-350 - 68);
		});
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("UntitlePlayer");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/stage.png")));
		primaryStage.show();
	}

	public void update() {
		System.out.println("----2----");
		//更新播放器面板栏
		playerPane.update(musicPaths, currentIndex);
		//更新播放列表
		listStage = PlayerPane.listStage;

	}

	//双击歌曲，传送歌曲地址
	EventHandler<javafx.scene.input.MouseEvent> mouseEventHandler = new EventHandler<javafx.scene.input.MouseEvent>() {
		public void handle(javafx.scene.input.MouseEvent mouseEvent){
			if(mouseEvent.getClickCount()==2){
				boolean root = false;
				//获取双击结点
				TreeItem<String> item = treeview.getSelectionModel().getSelectedItem();
				//判断是否是歌单结点，是，退出，不是，进行如下操作
				for(int i = 0; i < rootItems.size(); i++) {
					if(item == rootItems.get(i)) {
						root = true;
						break;
					}
				}
				System.out.println(root);
				//不是歌单结点
				if(!root){
					//获取所在的歌单名
					listName = item.getParent().getValue().toString();
					//获取歌单全部歌曲路径
					musicPaths = listPane.getMusicPaths(item);
					//获取歌曲位置
					currentIndex = getSelectedIndex(item, item.getValue());
					System.out.println("*******"+currentIndex+"********");
					//更新播放信息
					update();
				}
			}
		}
	};

	//更新歌词面板信息
	public static void updateLrcPane(int index) {
		//更新当前位置
		currentIndex = index;
		//更新当前歌曲信息
		musicInfo = MusicSqlite.getMusicInforByPath(musicPaths.get(currentIndex), listName);
		String songName = musicInfo.get("SONGNAME");
		String singer = musicInfo.get("SINGER");
		String album = musicInfo.get("ALBUM");
		String lrcPath = pathHead+songName+".lrc";
		System.out.println(lrcPath);
		String imagePath = "/AlbumImage/"+songName+".jpg";
		System.out.println(imagePath);
		//更新歌词信息面板
		lrcPane.update(songName, singer, album, lrcPath, imagePath);
	}

	//歌词面板信息清空
	public static void clearLrcPane() {
		//清空歌词信息面板
		lrcPane.update(null, null, null, null, null);
	}

	//获取双击歌曲在歌单中的位置
	private int getSelectedIndex(TreeItem<String> item, String name) {
		int index = 0;
		ArrayList<String> names = listPane.getMusicNames(item);
		for(int i = 0; i < names.size(); i++) {
			//找到歌曲位置
			if(names.get(i) != null && name.equals(names.get(i))) {
				index = i;
				break;
			}
		}
		return index;
	}

	//添加歌曲到播放面板
	public static void addMusicToPlayerPane(String path) {
		playerPane.addMusicToPlayerPane(path);
	}

	//从播放面板中删除歌曲
	public static void deleteMusicFromPlayerPane(String path) {
		playerPane.deleteMusicFromPlayerPane(path);
	}

	//清除播放歌曲
	public static void clearAllMusic() {
		//清空歌曲列表并且终止歌曲播放
		PlayerPane.clearAllMusic();
		//清空歌词信息面板
		clearLrcPane();
	}

	//获取当前播放的歌单名
	public static String getListName() {
		return listName;
	}

	//设置当前播放歌单名
	public static void setListName(String listName) {
		MusicPane.listName = listName;
	}

	public static void main(String[] args) {
		launch(args);
		//结束前，将播放列表的内容保存到数据库
		System.out.println("-----保存播放列表------");
		//创建播放列表歌单		
		MusicSqlite.createPlayListTable(tableName);
		//清空PlayList里面的内容
		MusicSqlite.deleteAllMusic(tableName);
		//将歌曲列表内容插入PlayList中
		if(musicPaths != null && musicPaths.size() > 0)
			MusicSqlite.insertPlayList(musicPaths, PlayerPane.getCurrentIndex());
	}

}
