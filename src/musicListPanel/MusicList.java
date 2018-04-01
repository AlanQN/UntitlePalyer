package musicListPanel;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import application.MusicPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class MusicList extends VBox{
	private static MusicList musicList;	//播放列表面板
	private TreeView<String> treeView;
	private TreeItem<String> rootItem;
	private ArrayList<TreeItem<String>> rootItmes = new ArrayList<>();
	//static int ID = MusicSqlite.getLatestID("我喜欢的歌");
	private MusicList(){
		initTopMenu();
		initTreeView();
	}

	//设置实例为null
	public static void clear() {
		musicList = null;
	}

	//获取播放列表实例
	public static MusicList getInstance() {
		if(musicList == null) {
			musicList = new MusicList();
		}
		return musicList;
	}

	//获取歌单结点集合
	public ArrayList<TreeItem<String>> getRootItems() {
		return this.rootItmes;
	}


	//**顶部菜单栏
	private void initTopMenu(){
		MenuBar menuBar = new MenuBar();
		menuBar.setVisible(true);
		Menu menu = new Menu("File");

		MenuItem AddMusicItem = new MenuItem("添加单首歌曲");
		MenuItem AddMusicsssItem = new MenuItem("添加文件夹内歌曲");

		menu.getItems().addAll(AddMusicItem,AddMusicsssItem);
		menuBar.getMenus().add(menu);
		this.getChildren().add(menuBar);



		AddMusicItem.setOnAction((ActionEvent e)->{
			//选择的不是歌单，则显示提示信息；否则，正常插入
			TreeItem<String> selectedItem = getSelectItem();
			if(selectedItem != null && this.isPlayListNode(selectedItem)){
				addMusicItemAction(getSelectItem());
			}else{
				//插入失败，弹出提示信息
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("错误");
				alert.setHeaderText(null);
				alert.setContentText("请选择要添加歌曲所在的歌单，\n以完成歌曲的添加！");
				alert.show();
			}


		});

		AddMusicsssItem.setOnAction((ActionEvent e)->{

		});
	}

	//获取播放列表
	public TreeView<String> getTreeView() {
		return this.treeView;
	}

	private TreeItem<String> getSelectItem() {
		int currentIndex = treeView.getSelectionModel().getSelectedIndex();
		return treeView.getTreeItem(currentIndex);
	}

	//重载addMusicItemAction，默认添加到我的歌曲
	/*private void addMusicItemAction() {
		addMusicItemAction(rootItem);
	}*/

	private void addMusicItemAction(TreeItem<String> list) {
		FileChooser MusicChooser = initMusicChooser();
		File file = MusicChooser.showOpenDialog(null);
		String listName = (String)list.getValue();	//当添加歌曲的歌单名
		String path = file.getPath();	//文件路径
		Music song = null;	//Music对象
		String songName = null;	//歌曲名称
		//选择文件不为空，以及文件没有在歌单中，则添加
		if(file!=null && !MusicSqlite.isExist(path, listName)){
			try {
				System.out.println("************insert***********");
				//获取信息，写入数据库
				URI url = file.toURI();
				//获取歌曲文件
				AudioFile audioFile = AudioFileIO.read(new File(url));
				// 获取歌曲文件的头信息
				AudioHeader audioHeader = audioFile.getAudioHeader();
				Tag musicTag = audioFile.getTag();

				songName =musicTag.getFirst(FieldKey.TITLE);
				String singer = musicTag.getFirst(FieldKey.ARTIST);  
				if(songName == null || songName.equals("") || singer == null || singer.equals("")) {
					String[] strs = path.split("\\\\");
					String str = strs[strs.length-1];
					strs = str.split("\\.");
					str = strs[0];
					strs = str.split("-");
					songName = strs[0];
					songName = songName.replace(" ", "");
					if(strs.length > 1) {
						singer = strs[strs.length-1];
						singer = singer.replace(" ", "");
					} else {
						singer = "";
					}
				}
				String album =musicTag.getFirst(FieldKey.ALBUM);
				int totalTime = audioHeader.getTrackLength();
				System.out.println(totalTime);
				int minute = totalTime/60;
				int second = totalTime%60;
				String time = String.format("%02d:%02d", minute, second);
				int ID = MusicSqlite.getLatestID((String)list.getValue());
				System.out.println((String)list.getValue());
				ID++;
				System.out.println(ID);
				song = new Music(ID, totalTime, songName, singer, album,time, path); 

				//TreeItem<String> songItem = new TreeItem<String>(file.getName());带.MP3的结点显示
				TreeItem<String>songItem = new TreeItem<String>(songName);
				list.getChildren().add(songItem);
				//获取插入数据的结果
				boolean success = MusicSqlite.insertSqlite(song, listName);
				//成功插入且歌单是当前播放的同一歌单，则将其添加到播放列表
				if(success) {
					if(MusicPane.getListName().equals(songItem.getParent().getValue().toString()))
						MusicPane.addMusicToPlayerPane(file.getAbsolutePath());
				}else {
					//插入失败，弹出提示信息
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("错误");
					alert.setHeaderText(null);
					alert.setContentText("请选择要添加歌曲所在的歌单，\n以完成歌曲的添加！");
					alert.show();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void initTreeView(){
		rootItem = new TreeItem<String>("我的歌单");
		rootItem.setExpanded(true);
		treeView = new TreeView<String>(rootItem);
		this.rootItmes.add(rootItem);

		MusicSqlite.readAllMusicSqlite(rootItem);

		for(TreeItem<String> item : rootItem.getChildren()) {
			this.rootItmes.add(item);
		}

		treeView.setPrefSize(250, 510);
		treeView.setRoot(rootItem);
		this.setRightMenu(treeView);
		this.getChildren().add(treeView);

	}

	public void setRightMenu(TreeView<String> treeView){
		ContextMenu rightMenu_rootItem =new ContextMenu();//根节点右键菜单
		ContextMenu rightMenu_songList = new ContextMenu();//歌单右键菜单
		ContextMenu rightMenu_song = new ContextMenu();//歌曲右键菜单
		MenuItem AddNewSongList_rootItem = new MenuItem("新建歌单");
		MenuItem AddNewSong_songList = new MenuItem("添加本地歌曲");
		MenuItem cleanSongList_songList = new MenuItem("清空歌单");
		MenuItem deleteSong_songList = new MenuItem("删除歌曲");
		MenuItem openLocalFileFolder_songList = new MenuItem("打开本地文件夹");
		MenuItem deleteSongList_songList = new MenuItem("删除歌单");
		//MenuItem collectToList = new MenuItem("收藏到歌单");


		//右键点击新建歌单事件
		AddNewSongList_rootItem.setOnAction((ActionEvent e)->{
			TextInputDialog dialog = new TextInputDialog("我喜欢的歌");
			dialog.setTitle(null);
			dialog.setHeaderText(null);
			dialog.setContentText("请输入创建歌单的名字：");

			// Traditional way to get the response value.
			Optional<String> result = dialog.showAndWait();
			String ListName = result.get();
			System.out.println(ListName);
			//歌单名不为null
			if(ListName != null) {
				int lastIndex = ListName.lastIndexOf(" ");
				//歌单名不能为空白
				if(lastIndex != ListName.length()-1) {
					if (result.isPresent()){
						//System.out.println("Your name: " + result.get());
						//获取歌单创建结果
						boolean success = MusicSqlite.createListName(ListName);
						//创建成功，添加歌单
						if(success) {
							TreeItem<String> treeItem = new TreeItem<>(ListName);
							treeItem.setExpanded(true);
							rootItem.getChildren().add(treeItem);
							this.rootItmes.add(treeItem);
						} else {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("错误");
							alert.setHeaderText(null);
							alert.setContentText("歌单名称不能重复,请输入正确的歌单信息以完成创建！");
							alert.show();
						}
					}
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("错误");
					alert.setHeaderText(null);
					alert.setContentText("歌单名称不能为空,请输入正确的歌单信息以完成创建！");
					alert.show();
				}
			} else{
				System.out.println("-----null-----");
			}

		});

		//右键点击添加本地歌曲
		AddNewSong_songList.setOnAction((ActionEvent e)->{
			int currentIndex = treeView.getSelectionModel().getSelectedIndex();
			this.addMusicItemAction(treeView.getTreeItem(currentIndex));

		});

		//右键删除表中歌曲
		deleteSong_songList.setOnAction((ActionEvent e)->{
			int currentIndex = treeView.getSelectionModel().getSelectedIndex();
			String musicName = (String)treeView.getTreeItem(currentIndex).getValue();
			String listName=(String)treeView.getTreeItem(currentIndex).getParent().getValue();
			//从数据库中删除歌曲，获取返回的路径
			String path = MusicSqlite.deleteMusicSqlite(listName, musicName);
			//如果path不为null或者是当前播放歌单里的歌曲，则从播放面板中删除歌曲
			if(path != null) {
				if(MusicPane.getListName().equals(listName))
					MusicPane.deleteMusicFromPlayerPane(path);
			}
			TreeItem<String> List = treeView.getTreeItem(currentIndex).getParent();//获取歌单节点
			List.getChildren().remove(treeView.getTreeItem(currentIndex));
		});

		//右键删除歌单
		deleteSongList_songList.setOnAction((ActionEvent e)->{
			int currentIndex = treeView.getSelectionModel().getSelectedIndex();
			String listName=(String)treeView.getTreeItem(currentIndex).getValue();

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("删除");
			alert.setHeaderText(null);
			alert.setContentText("确定删除该歌单？");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				MusicSqlite.deleteMusicList(listName);
				//获取选中的歌单名
				TreeItem<String> selectedItem = treeView.getTreeItem(currentIndex);
				String name = selectedItem.getValue().toString(); 
				//去除歌单结点
				TreeItem<String> List = treeView.getTreeItem(currentIndex).getParent();
				List.getChildren().remove(treeView.getTreeItem(currentIndex));
				System.out.println(MusicPane.getListName());
				System.out.println(name);
				//删除的是当前歌单，则清除播放列表并且终止音乐
				if(MusicPane.getListName().equals(name)) {
					MusicPane.clearAllMusic();
				}
			} else {
				System.out.println("*****notDelete*****");
			}
		});

		//右键清空歌单
		cleanSongList_songList.setOnAction((ActionEvent e)->{
			TreeItem<String> item = getSelectItem();
			item.getChildren().clear();
			MusicSqlite.deleteAllMusic(item.getValue());
			String name = item.getValue().toString(); 
			//删除的是当前歌单，则清除播放列表并且终止音乐
			if(MusicPane.getListName().equals(name)) {
				MusicPane.clearAllMusic();
			}
		});

		//打开本地文件夹
		openLocalFileFolder_songList.setOnAction((ActionEvent e)->{
			/*
			try {
				java.awt.Desktop.getDesktop().open(new File("D:\\Java"));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
		});

		//设置根节点、歌单右键菜单
		rightMenu_rootItem.getItems().add(AddNewSongList_rootItem);
		rightMenu_songList.getItems().add(AddNewSong_songList);
		rightMenu_songList.getItems().add(cleanSongList_songList);
		rightMenu_songList.getItems().add(deleteSongList_songList);
		//歌曲右键设置
		rightMenu_song.getItems().add(deleteSong_songList);
		rightMenu_song.getItems().add(openLocalFileFolder_songList);

		treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>(){
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				// TODO Auto-generated method stub
				int currentIndex = treeView.getSelectionModel().getSelectedIndex();
				switch(currentIndex){
				case 0:
					//DefaultMutableTreeNode node = new DefaultMutableTreeNode();
					treeView.setContextMenu(rightMenu_rootItem);
					break;
				default :
					TreeItem<String> item = treeView.getTreeItem(currentIndex);
					if(isPlayListNode(item)){
						//歌单的右键菜单
						treeView.setContextMenu(rightMenu_songList);
					}
					else if(item != rootItem) {
						//歌曲的右键菜单
						treeView.setContextMenu(rightMenu_song);
					}
					break;
				}
			}
		});
	}

	private FileChooser initMusicChooser(){
		File rootPath = new File("D:\\");
		FileChooser MusicChooser = new FileChooser();
		MusicChooser.setTitle("选择歌曲");
		MusicChooser.setInitialDirectory(rootPath); 
		MusicChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("音频文件(*.mid;*.mp3;*.wav;)",
				"*.mid", "*.MID", "*.mp3", "*.MP3", "*.wav", "*.WAV"));	
		return MusicChooser;
	}

	//根据歌单名获取歌单所有歌曲路径
	public ArrayList<String> getMusicPaths(TreeItem<String> item) {
		String tableName = item.getParent().getValue().toString();
		return MusicSqlite.getAllMusicPaths(tableName);
	}

	//根据歌单名获取歌单所有歌曲名称
	public ArrayList<String> getMusicNames(TreeItem<String> item) {
		String tableName = item.getParent().getValue().toString();
		return MusicSqlite.getAllMusicNames(tableName);
	}

	//判断当前结点是否为歌单结点
	private boolean isPlayListNode(TreeItem<String> item) {
		//记录是否为歌单结点
		boolean flag = false;
		//进行判断
		for(TreeItem<String> node : rootItmes) {
			if(rootItem != node && node == item) {
				flag = true;	//找到
				break;
			}
		}
		return flag;
	}

	/*public void IsListExpanded(){
		rootItem.getChildren().
		boolean[] Expanded = new boolean[];
	}*/

}
