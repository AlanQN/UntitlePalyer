package musicPlayer;

import java.util.ArrayList;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PlayListPane extends VBox{

	private Label play_queue;	//播放队列标签
	private Button btn_clear;	//清空按钮
	private ListView<BorderPane> listView;	//播放列表视图
	private ObservableList<BorderPane> list;	//歌曲的集合
	public int WIDTH = 320;
	public int HEIGHT = 350;
	private int currentIndex = 0;	//当前播放的音乐的索引
	private int selectedIndex = 0;	//记录上一个被选中的索引
	private ArrayList<Map<String, String>> musicInfor;
	private static String str1 = "message";
	private static String str2 = "time";

	//获得播放队列标签
	public Label getPlayQueueLabel() {
		return play_queue;
	}

	//获得歌曲列表
	public ObservableList<BorderPane> getMusicList() {
		return list;
	}

	//获得列表视图
	public ListView<BorderPane> getListView() {
		return listView;
	}

	//获取列表视图当前的选择的索引
	public int getSelectedIndex() {
		return selectedIndex;
	}

	///构造方法
	public PlayListPane(ArrayList<Map<String, String>> musicInfor) {
		this.musicInfor = musicInfor;
		//初始化
		initListPane();
	}

	//初始化播放列表
	private void initListPane() {
		//播放队列标签和清空按钮
		if(musicInfor != null) {
			play_queue = new Label("播放列表（共"+musicInfor.size()+"首）");
		} else {
			play_queue = new Label("播放列表（共0首）");
		}
		play_queue.setTextFill(Color.ALICEBLUE);
		play_queue.setFont(Font.font(14));
		btn_clear = new Button("清空");
		btn_clear.setTextFill(Color.DARKGRAY);
		btn_clear.setStyle("-fx-background-color:rgb(60,60,60);");
		btn_clear.setFont(Font.font(13));
		//为清空按钮绑定监听事件
		btn_clear.setOnMouseClicked(new ClearMusicListListener());
		btn_clear.setOnMouseEntered(e->{
			btn_clear.setTextFill(Color.ALICEBLUE);
		});
		btn_clear.setOnMouseExited(e->{
			btn_clear.setTextFill(Color.DARKGRAY);
		});
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(0,5,5,15));
		pane.setLeft(play_queue);
		pane.setRight(btn_clear);
		this.getChildren().add(pane);
		//分割线
		this.getChildren().add(new Separator());

		//播放列表
		listView = new ListView<BorderPane>();
		//播放列表元素的集合
		list = FXCollections.observableArrayList();
		//创建元素
		if(musicInfor != null) {
			for(int i = 0; i < musicInfor.size(); i++) {
				BorderPane musicPane = createListItem(musicInfor.get(i).get(str1), musicInfor.get(i).get(str2));
				list.add(musicPane);
			}
		}

		//------右键菜单
		ContextMenu rightMenu = new ContextMenu();
		//播放选项
		MenuItem playMenu = new MenuItem("\t  播放");
		playMenu.setOnAction(e->{
			System.out.println("play");
			int index = currentIndex;
			currentIndex = listView.getSelectionModel().getSelectedIndex();
			if(index != currentIndex) {
				//样式恢复
				resetMusicLabelsStyle(index);
			}
			//播放当前音乐
			playSelectedIndexMusic(currentIndex);

		});
		rightMenu.getItems().add(playMenu);
		//下一首播放选项
		MenuItem nextMenu = new MenuItem("下一首播放");
		nextMenu.setOnAction(e->{
			System.out.println("下一首播放");
			int index = listView.getSelectionModel().getSelectedIndex();
			//如果是正在播放歌曲前面的歌曲插队播放
			if(index >= 0 && index < list.size() && index != currentIndex) {
				if(index >= 0 && index < currentIndex) {
					currentIndex--;
				}
				//获取插队歌曲
				BorderPane item = list.get(index);
				System.out.println("********"+index+"*********");
				//调整插队歌曲的位置
				list.remove(index);
				System.out.println("********"+currentIndex+"*********");
				list.add(currentIndex+1, item);
				//调整歌曲列表中歌曲位置
				PlayerPane.updatePlayOrder(index);
				//更新当前选中的选项
				for(int i = 0; i < list.size(); i++) {
					if(i != currentIndex && list.get(i).isCache()) {
						resetMusicLabelsStyle(i);
						list.get(i).setCache(false);
					}
				}
				listView.getSelectionModel().select(currentIndex);
				selectedIndex = currentIndex;
			}
		});
		rightMenu.getItems().add(nextMenu);
		//删除选项
		MenuItem deleteMenu = new MenuItem("\t  删除");
		deleteMenu.setOnAction(e->{
			//获取要删除的索引
			int index = listView.getSelectionModel().getSelectedIndex();
			System.out.println(index);
			//当索引符合要求时
			if(index >= 0 && index < list.size()) {
				System.out.println("delete1");
				//歌曲列表移除歌曲
				PlayerPane.deleteMusicByIndex(index);
				System.out.println("delete2");
				//更新selectIndex
				selectedIndex = currentIndex;
			}
		});
		rightMenu.getItems().add(deleteMenu);
		listView.setContextMenu(rightMenu);

		listView.setStyle("-fx-background-color:#252525;");
		listView.setItems(list);
		//设置item选中的事件
		listView.getSelectionModel().selectedItemProperty().addListener(e->{
			if(selectedIndex != listView.getSelectionModel().getSelectedIndex()) {
				//借助cache数据域表示当前歌曲是否被选中，被选中的歌曲改变，则产生响应事件
				if(selectedIndex < musicInfor.size() && selectedIndex < list.size()) {
					list.get(selectedIndex).setCache(false);
					if(selectedIndex != currentIndex) {
						//获取先前选中的标签，恢复原先的状态
						resetMusicLabelsStyle(selectedIndex);
					}
					//更新当前索引
					selectedIndex = listView.getSelectionModel().getSelectedIndex();
					System.out.println(selectedIndex+"//////******");
					if(selectedIndex != currentIndex) {
						//改变当前选中项的样式
						BorderPane item = listView.getSelectionModel().getSelectedItem();
						item.setCache(true);
						setSelectedMusicLabelsStyle(selectedIndex);
					}
				}
			}
		});
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		listView.setOrientation(Orientation.VERTICAL);
		this.getChildren().add(listView);
		//为当前面板设置属性
		this.setStyle("-fx-background-color:rgb(60,60,60);");
		this.setPrefHeight(400);
		this.setPadding(new Insets(15,0,0,0));
		this.setPrefSize(WIDTH, HEIGHT);
	}


	//创建歌曲列表的一列
	private BorderPane createListItem(String music_title, String music_time) {
		//每首歌曲一个面板
		BorderPane musicPane = new BorderPane();
		musicPane.setPrefWidth(270);
		musicPane.setPrefHeight(28);
		//歌曲信息
		Label musicMessage = new Label(music_title);
		musicMessage.setPrefWidth(240);
		musicMessage.setPrefHeight(28);
		musicMessage.setFont(Font.font(13));
		//判断歌曲是否失效
		if(music_time.equals("00:00")) {
			musicMessage.setTextFill(Color.RED);
		} else{
			musicMessage.setTextFill(Color.DARKGRAY);
		}
		//歌曲时长
		Label musicTime = new Label(music_time);
		musicTime.setPrefHeight(28);
		musicTime.setFont(Font.font(12));
		//判断歌曲是否失效
		if(music_time.equals("00:00")) {
			musicTime.setTextFill(Color.RED);
		} else{
			musicTime.setTextFill(Color.DARKGRAY);
		}
		musicPane.setLeft(musicMessage);
		musicPane.setRight(musicTime);
		//添加鼠标事件
		musicPane.setOnMouseEntered(e->{
			if(musicPane != list.get(currentIndex)) {
				Label label = (Label) musicPane.getChildren().get(0);
				label.setTextFill(Color.ALICEBLUE);
				label = (Label) musicPane.getChildren().get(1);
				label.setTextFill(Color.ALICEBLUE);
			}
		});
		musicPane.setOnMouseExited(e->{
			if(!musicPane.isCache() && list.get(currentIndex) != musicPane) {
				Label label = (Label) musicPane.getChildren().get(0);
				//判断歌曲是否失效
				if(music_time.equals("00:00")) {
					label.setTextFill(Color.RED);
				} else{
					label.setTextFill(Color.DARKGRAY);
				}
				label = (Label) musicPane.getChildren().get(1);
				if(music_time.equals("00:00")) {
					label.setTextFill(Color.RED);
				} else{
					label.setTextFill(Color.DARKGRAY);
				}
			}
		});
		musicPane.setOnMouseClicked(e->{
			switch(e.getClickCount()) {
			case 2://鼠标双击
				System.out.println("鼠标双击");
				playSelectedIndexMusic(selectedIndex);
			}
		});
		musicPane.setPadding(new Insets(0,15,0,15));
		return musicPane;
	}

	//清空按钮等的监听事件
	class ClearMusicListListener implements EventHandler<Event> {

		@Override
		public void handle(Event event) {
			//清空歌曲列表并且终止歌曲播放
			PlayerPane.clearAllMusic();
		}
	}

	//清空播放列表
	public void clearPlayList() {
		//清空列表
		list.clear();
		//将歌曲数目设置为零
		play_queue.setText("播放列表（共0首）");
	}

	//恢复歌曲标签样式
	public void resetMusicLabelsStyle(int index) {
		if(index < musicInfor.size()) {
			for(int i = 0; i < 2; i++) {
				Label label = (Label) list.get(index).getChildren().get(i);
				//歌曲无效，设置为红色
				if(musicInfor.get(index).get(str2).equals("00:00")) {
					label.setTextFill(Color.RED);
				} else{
					label.setTextFill(Color.DARKGRAY);
				}
			}
		}
	}

	//设置选中歌曲的样式
	public void setSelectedMusicLabelsStyle(int index) {
		for(int i = 0; i < 2; i++) {
			Label label = (Label) list.get(index).getChildren().get(i);
			//歌曲无效，设置为红色
			if(musicInfor.get(index).get(str2).equals("00:00")) {
				label.setTextFill(Color.RED);
			} else{
				label.setTextFill(Color.DARKGRAY);
			}
		}
	}

	//设置播放歌曲的样式
	public void setPlayMusicLabelsStyle(int index) {
		//将先前播放歌曲的样式重新设置
		resetMusicLabelsStyle(currentIndex);
		//当前播放的索引更新
		currentIndex = index;
		for(int i = 0; i < 2; i++) {
			Label label = (Label) list.get(index).getChildren().get(i);
			label.setTextFill(Color.GREENYELLOW);
		}
		//设置歌曲列表选项状态
		updateItemSelectedState();
	}

	//更新歌曲列表选中状态
	public void updateItemSelectedState() {
		//将listView中的选项置为当前播放的位置
		if(selectedIndex != currentIndex && selectedIndex < musicInfor.size()) {
			//取消选中状态
			list.get(selectedIndex).setCache(false);
			//将选中状态置为当前索引
			listView.getSelectionModel().select(currentIndex);
			list.get(currentIndex).setCache(true);
		}
	}

	//播放给定索引的歌曲
	public void playSelectedIndexMusic(int index) {
		//播放当前选中的歌曲
		PlayerPane.playMusicByIndex(index);
	}

	//往播放列表中插入新的歌曲--单首
	public void addMusicToList() {
		int index = musicInfor.size() - 1;
		BorderPane musicPane = createListItem(musicInfor.get(index).get(str1), musicInfor.get(index).get(str2));
		list.add(musicPane);
	}

	//往播放列表中插入新的歌曲--一组
	public void addMusicToList(String[] musicPaths) {

	}

	//从播放列表中插删除歌曲--单首
	public void deleteMusicFromList(int delIndex) {
		BorderPane delPane = list.get(delIndex);
		list.remove(delPane);
		delPane = null;
		//更新索引
		this.currentIndex = PlayerPane.getCurrentIndex();
		this.selectedIndex = this.currentIndex;
	}
}
