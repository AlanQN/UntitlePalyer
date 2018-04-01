package musicPlayer;

import java.util.ArrayList;
import java.util.Map;

import application.MusicPane;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class PlayerPane extends VBox {

	private static PlayerPane playerPane;	//播放器面板
	private int WIDTH = 900;	//面板宽度
	private static ArrayList<String> musicPaths;	//歌曲路径
	private static int currentIndex = 0;	//当前播放音乐的位置
	private static MediaPlayer currentPlayer = null;	//当前音乐的MediaPlayer
	private static ArrayList<Map<String, String>> musicInfor;	//所有歌曲的信息
	private static MusicPlayer musicPlayer;	//播放器类的一个播放对象
	private static Slider volumeSlider;	//调节音量的滑动条
	private static Slider processSlider;	//调节进度的滑动条
	private static Label label_message;		//显示歌曲名称和作者
	private static Label label_process;		//显示歌曲总时间和已经播放的时间
	private Label btn_next = null;	//下一首歌曲按钮
	private Label btn_front = null;	//上一首歌曲按钮
	private static Label btn_play = null;	//播放和暂停按钮
	private static int currentMinute = 0;	//当前播放的分钟
	private static int currentSecond = 0;	//当前播放的秒钟
	private static String totalTime = "";	//当前音乐的总时长
	private static Thread currentThread = null;	//当前自动更新的线程
	private static boolean suspend = false;	//当前线程是否暂停
	static Label modeLabel = new Label("");	//模式选择
	private static Label btn_playList = new Label("");	//显示播放列表的按钮
	private static PlayListPane listPane; //歌曲播放列表
	public static Stage listStage;	//播放列表
	private ImageView frontImage;
	private ImageView nextImage;
	private static ImageView stopImage;
	private static ImageView playImage;

	//构造方法 
	private PlayerPane(ArrayList<String> musicPaths, int currentIndex) {
		//获取播放位置
		PlayerPane.currentIndex = currentIndex;
		//获取音乐路径
		PlayerPane.musicPaths = musicPaths;
		//初始化
		initPlayerPane();
	}

	//获取PlayerPane的实例
	public static PlayerPane getInstance(ArrayList<String> musicPaths, int currentIndex) {
		if(playerPane == null) {
			playerPane = new PlayerPane(musicPaths, currentIndex);
		}
		return playerPane;
	}

	//更新相应的控件
	@SuppressWarnings("deprecation")
	public void update(ArrayList<String> musicPaths, int currentIndex) {
		//更新音乐列表
		PlayerPane.musicPaths = musicPaths;
		//更新当前播放索引
		PlayerPane.currentIndex = currentIndex;
		//暂停音乐
		if(currentPlayer != null) 
			currentPlayer.stop();
		currentPlayer = null;
		musicPlayer = null;
		//清空线程
		if(currentThread != null) {
			currentThread.stop();
			currentThread = null;
		}
		//更新音乐播放类对象
		this.updateMusicPlayer();
		//更新播放列表
		createListPane();
		//播放音乐
		PlayerPane.playMusicByIndex(currentIndex);
	}

	//更新音乐播放类
	private void updateMusicPlayer() {
		//创建一个音乐播放对象
		musicPlayer = new MusicPlayer(musicPaths, currentIndex);
		//获取所有歌曲信息
		musicInfor = musicPlayer.getMusicInformation();
		//更新歌曲数目
		if(musicPaths == null) {
			btn_playList.setText(" "+"0");
		} else{
			btn_playList.setText(" "+musicPaths.size());
		}
	}

	//添加单首歌曲
	public void addMusicToPlayerPane(String path) {
		//添加路径
		PlayerPane.musicPaths.add(path);
		//将歌曲添加到播放器
		musicPlayer.addMusicToPlayer();
		//将歌曲添加到播放列表
		listPane.addMusicToList();
		//更新歌曲数目
		if(musicPaths == null) {
			btn_playList.setText(" "+"0");
		} else{
			btn_playList.setText(" "+musicPaths.size());
		}
	}

	//删除单首歌曲
	public void deleteMusicFromPlayerPane(String path) {
		//获取位置
		int delIndex = musicPaths.indexOf(path);
		//如果删除的歌曲是当前播放的歌曲，则停止
		if(PlayerPane.currentIndex == delIndex) {
			stopPlayMusic();
			//更新索引
			PlayerPane.currentIndex = musicPlayer.getCurrentMusicIndex();
			//歌词面板清空
			MusicPane.clearLrcPane();
		}
		if(delIndex <= PlayerPane.currentIndex && PlayerPane.currentIndex != 0) {
			PlayerPane.currentIndex--;
		}
		//删除路径
		musicPaths.remove(delIndex);
		//将歌曲从播放器中删除
		musicPlayer.deleteMusicFromPlayer(delIndex);
		//将歌曲从播放列表中删除
		listPane.deleteMusicFromList(delIndex);
		//更新歌曲数目
		if(musicPaths == null) {
			btn_playList.setText(" "+"0");
		} else{
			btn_playList.setText(" "+musicPaths.size());
		}
	}

	//终止歌曲播放
	private static void stopPlayMusic() {
		if(currentPlayer != null) {
			currentPlayer.stop();
			currentPlayer = null;
			btn_play.setTooltip(new Tooltip("播放"));
			btn_play.setGraphic(stopImage);
		}
		processSlider.setValue(0);
		label_message.setText("音乐");
		label_process.setText("");
		//更新歌曲数目
		if(musicPaths == null) {
			btn_playList.setText(" "+"0");
		} else{
			btn_playList.setText(" "+musicPaths.size());
		}
	}

	//清空歌曲播放列表，终止音乐播放
	public static void clearAllMusic() {
		//清除歌曲路径
		PlayerPane.musicPaths.clear();
		//播放位置置为0
		PlayerPane.currentIndex = 0;
		//清除歌曲信息
		musicPlayer.clearMusicMessage();
		//清空播放列表
		listPane.clearPlayList();
		//终止歌曲播放
		stopPlayMusic();
		//清空歌词信息面板
		MusicPane.clearLrcPane();
	}

	//初始化播放器面板
	private void initPlayerPane() {
		//创建一个音乐播放对象
		musicPlayer = new MusicPlayer(musicPaths, currentIndex);
		//获取当前播放索引
		currentIndex = musicPlayer.getCurrentMusicIndex();
		//获取所有歌曲信息
		musicInfor = musicPlayer.getMusicInformation();

		//显示歌曲歌名和作者以及播放进度的标签
		label_message = new Label();
		label_process = new Label();
		label_message.setPrefWidth(270);
		label_message.setFont(new Font(13));
		label_process.setPrefWidth(80);
		label_process.setFont(new Font(12));
		//有播放记录，则设置；否则设置为"音乐"
		if(musicPaths != null && !musicPaths.isEmpty()) {
			label_message.setText(musicInfor.get(currentIndex).get("message"));
		} else{
			label_message.setText("音乐");
		}
		//有播放记录，则设置；否则设置为空
		if(musicPaths != null && !musicPaths.isEmpty()) {
			label_process.setText("00:00/"+musicInfor.get(currentIndex).get("time"));
		} else{
			label_process.setText("");
		}
		BorderPane labelPane = new BorderPane();
		labelPane.setLeft(label_message);
		labelPane.setRight(label_process);


		//调节播放进度的进度条
		processSlider = new Slider();
		processSlider.setPrefWidth(380);
		processSlider.setMaxWidth(380);
		VBox processBox = new VBox();
		processBox.setSpacing(5);
		processBox.getChildren().addAll(labelPane, processSlider);
		processBox.setPadding(new Insets(5,10,0,10));

		//播放按控制按钮
		//加载图片
		frontImage = new ImageView(new Image(getClass().getResourceAsStream("/image/front.png")));
		frontImage.setFitHeight(35);
		frontImage.setFitWidth(35);
		nextImage = new ImageView(new Image(getClass().getResourceAsStream("/image/next.png")));
		nextImage.setFitHeight(35);
		nextImage.setFitWidth(35);
		playImage = new ImageView(new Image(getClass().getResourceAsStream("/image/play.png")));
		playImage.setFitWidth(40);
		playImage.setFitHeight(40);
		stopImage = new ImageView(new Image(getClass().getResourceAsStream("/image/pause.png")));
		stopImage.setFitHeight(40);
		stopImage.setFitWidth(40);
		//播放上一首按钮
		btn_front = new Label();
		btn_front.setStyle("-fx-background-color:#FFFFFF00;");	//透明
		btn_front.setTooltip(new Tooltip("上一首"));
		btn_front.setGraphic(frontImage);
		//设置监听事件
		btn_front.setOnMouseEntered(e->{
			ImageView image1 = (ImageView) btn_front.getGraphic();
			image1.setFitWidth(36);
			image1.setFitHeight(36);
		});
		btn_front.setOnMouseExited(e->{
			ImageView image1 = (ImageView) btn_front.getGraphic();
			image1.setFitWidth(35);
			image1.setFitHeight(35);
		});
		btn_front.setFont(new Font(15));
		btn_front.setOnMouseClicked(new PlayFrontMusicListener());
		//播放和暂时按钮
		btn_play = new Label();
		btn_play.setOnMouseEntered(e->{
			ImageView image1 = (ImageView) btn_play.getGraphic();
			image1.setFitWidth(41);
			image1.setFitHeight(41);
		});
		btn_play.setOnMouseExited(e->{
			ImageView image1 = (ImageView) btn_play.getGraphic();
			image1.setFitWidth(40);
			image1.setFitHeight(40);
		});
		btn_play.setStyle("-fx-background-color:#FFFFFF00;");
		btn_play.setFont(new Font(15));
		btn_play.setGraphic(stopImage);
		btn_play.setTooltip(new Tooltip("播放"));
		btn_play.setOnMouseClicked(new PlayCurrentMusicListener());
		//播放下一首按钮
		btn_next = new Label();
		btn_next.setStyle("-fx-background-color:#FFFFFF00;");
		btn_next.setGraphic(nextImage);
		//设置监听事件
		btn_next.setOnMouseEntered(e->{
			ImageView image1 = (ImageView) btn_next.getGraphic();
			image1.setFitWidth(36);
			image1.setFitHeight(36);
		});
		btn_next.setOnMouseExited(e->{
			ImageView image1 = (ImageView) btn_next.getGraphic();
			image1.setFitWidth(35);
			image1.setFitHeight(35);
		});
		btn_next.setTooltip(new Tooltip("下一首"));
		btn_next.setFont(new Font(15));
		btn_next.setOnMouseClicked(new PlayNextMusicListener());
		//ButtonBox封装按钮
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(15);
		buttonBox.getChildren().addAll(btn_front, btn_play, btn_next);

		//音乐控制滑动条
		HBox volumeBox = new HBox();
		ImageView volume = new ImageView(new Image(getClass().getResourceAsStream("/image/volume.png")));
		volume.setFitWidth(20);
		volume.setFitHeight(20);
		Label volumeLabel = new Label();
		volumeLabel.setGraphic(volume);
		volumeLabel.setPrefSize(25, 25);
		volumeSlider = new Slider();
		volumeSlider.setPrefWidth(150);
		volumeSlider.setValue(50);
		volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
		volumeSlider.setMinWidth(50);
		volumeBox.getChildren().addAll(volumeLabel, volumeSlider);
		volumeBox.setAlignment(Pos.CENTER);
		volumeBox.setSpacing(5);
		//播放模式选择框
		String modeArray[] = new String[]{"列表循环", "随机播放", "单曲循环"};
		//所有组件的集合
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.setSpacing(15);
		hbox.getChildren().addAll(buttonBox, processBox, volumeBox, modeLabel, btn_playList);
		//设置样式
		hbox.setStyle("-fx-background-color:#A0A0A0;");
		hbox.setPrefSize(850, 60);

		//-------------------
		//以下是播放模式选择的弹出窗口
		modeLabel.setFont(new Font(15));
		modeLabel.setTooltip(new Tooltip("播放模式"));
		Stage modelStage = new Stage();
		modelStage.setOpacity(0.95);
		modelStage.initStyle(StageStyle.TRANSPARENT);
		ChoiceModelPane pane = new ChoiceModelPane(modeArray);
		//获取选择模式面板
		Label[] modelLabels = pane.getModeLabel();
		//为每个Label添加鼠标点击事件
		for(int i = 0; i < modelLabels.length; i++) {
			Label modelLabel = modelLabels[i];
			modelLabel.setOnMouseClicked(
					//获取选择的模式
					e->{String s_mode = modelLabel.getText();
					//设置图片
					ImageView imageView =new ImageView(new Image(getClass().getResourceAsStream("/image/"+s_mode+".png")));
					imageView.setOpacity(0.9);
					imageView.setFitHeight(22);
					imageView.setFitWidth(22);
					modeLabel.setGraphic(imageView);
					if(s_mode.equals("列表循环")) {
						musicPlayer.setPlayModel(MusicPlayer.LIST_CYCLE);
					}else if(s_mode.equals("单曲循环")) {
						musicPlayer.setPlayModel(MusicPlayer.SINGLE_CYCLE);
					}else{
						musicPlayer.setPlayModel(MusicPlayer.RANDOM_PLAY);
					}
					modelStage.hide();
					});
		}
		Scene scene = new Scene(pane);
		modelStage.setScene(scene);
		scene.getStylesheets().add("musicPlayer/seperetorStyle.css");
		ImageView imageView =new ImageView(new Image(getClass().getResourceAsStream("/image/"+modeArray[0]+".png")));
		imageView.setOpacity(0.9);
		imageView.setFitHeight(22);
		imageView.setFitWidth(22);
		modeLabel.setGraphic(imageView);

		//模式选择按钮点击事件
		modeLabel.setOnMouseClicked(e->{modelStage.setX(e.getScreenX() - 55);modelStage.setY(e.getScreenY() - 130);modelStage.show();});
		//检测窗口是否获得焦点
		modelStage.focusedProperty().addListener(e->{if(!modelStage.isFocused()) modelStage.hide();});
		//----------------------

		//----------------------
		//以下是播放列表窗口
		//读取图片
		ImageView listView = new ImageView(new Image(getClass().getResourceAsStream("/image/playList.png"))); 
		//设置大小
		listView.setFitWidth(22);
		listView.setFitHeight(22);
		//为按钮指定图片
		btn_playList.setGraphic(listView);
		if(musicPaths == null) {
			btn_playList.setText(" "+"0");
		} else{
			btn_playList.setText(" "+musicPaths.size());
		}
		btn_playList.setFont(Font.font(14));
		btn_playList.setTooltip(new Tooltip("播放列表"));

		//创建播放列表面板
		createListPane();

		this.setAlignment(Pos.CENTER);
		this.setSpacing(15);
		this.getChildren().add(hbox);
		this.setPrefWidth(WIDTH);
	}

	//创建播放列表窗口
	private void createListPane() {
		if(listStage == null) {
			listStage = new Stage();
			listStage.setOpacity(0.9);
			listStage.initStyle(StageStyle.TRANSPARENT);
			listPane = new PlayListPane(musicInfor); 
			Scene listScene = new Scene(listPane,listPane.WIDTH,listPane.HEIGHT);
			listScene.getStylesheets().add(this.getClass().getResource("list.css").toExternalForm());
			listStage.setScene(listScene);
			//模式选择按钮点击事件
			btn_playList.setOnMouseClicked(e->{
				listStage.show();
			});
			//检测窗口是否获得焦点
			listStage.focusedProperty().addListener(e->{
				if(!listStage.isFocused()) {
					//当窗口未获取到焦点时，将窗口隐藏
					listStage.hide();
					//将listView中的选项置为当前播放的位置
					listPane.updateItemSelectedState();
				}	
			});
		} else {
			listPane = null;
			listPane = new PlayListPane(musicInfor);
			listStage.getScene().setRoot(listPane);
		}
	}

	//播放上一首按钮的监听器
	class PlayFrontMusicListener implements EventHandler<javafx.scene.input.MouseEvent> {

		@Override
		public void handle(javafx.scene.input.MouseEvent event) {
			//获取上一首音乐的索引
			int index = musicPlayer.getFrontMusicIndex();
			//根据索引播放音乐
			playMusicByIndex(index);
		}

	}

	//播放下一首歌曲按钮的监听器
	class PlayNextMusicListener implements EventHandler<javafx.scene.input.MouseEvent>{

		@Override
		public void handle(javafx.scene.input.MouseEvent event) {
			//获取下一首音乐的索引
			int index = musicPlayer.getNextMusicIndex();
			//根据索引播放音乐
			playMusicByIndex(index);
		}

	}

	//播放暂停按钮的监听器
	class PlayCurrentMusicListener implements EventHandler<javafx.scene.input.MouseEvent>{

		@Override
		public void handle(javafx.scene.input.MouseEvent event) {
			//播放音乐
			playerPane.playCurrentMusic();
		}

	}

	//播放currentIndex指向的音乐
	private void playCurrentMusic() {
		//如果当前之态为等待播放，则播放音乐
		if(btn_play.getTooltip().getText().equals("播放")) {
			if(currentPlayer == null) {
				playMusicByIndex(currentIndex);
			} else {
				currentPlayer.play();
				//更新状态标签
				updateStateLabels();
			}
		} else if(btn_play.getTooltip().getText().equals("暂停")) {
			//如果当前状态为正在播放，则暂停
			if(currentPlayer != null) {
				currentPlayer.pause();
			}
			btn_play.setGraphic(stopImage);
			btn_play.getTooltip().setText("播放");
		}
	}

	//滑动条鼠标拖动滑块事件监听器
	static class ProcessBarDraggedListener implements EventHandler<Event> {

		@SuppressWarnings("deprecation")
		@Override
		public void handle(Event event) {
			if(!suspend) {
				currentThread.suspend();
				suspend = true;
				System.out.println("suspend");
			}
			//获取当前进度条的值
			double time = processSlider.getValue();
			//获取对应的分钟和秒钟
			currentMinute = (int) (time/60);
			currentSecond = (int) (time%60);
			//更新进度标签值
			updateMusicProgressLabel(currentMinute, currentSecond);
		}

	}

	//滑动条鼠标离开滑块事件监听器
	static class ProcessBarReleasedListener implements EventHandler<Event> {

		@SuppressWarnings("deprecation")
		@Override
		public void handle(Event event) {
			//获取当前进度条的值
			double time = processSlider.getValue();
			//判断是否鼠标点击是否改变滑动条的滑块所在的值
			if(Math.abs(time - currentPlayer.getCurrentTime().toSeconds()) >= 1) {
				//让播放器跳转到当前滑动条所指向的时间
				currentPlayer.seek(new Duration(time * 1000));
				//为确保显示正常，使用线程先暂停50ms后启动更新线程
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//如果当前线程处于暂停状态，则恢复当前线程
				if(suspend) {
					currentThread.resume();
					suspend = false;
					System.out.println("线程恢复");
				}
			}
		}

	}

	//设置播放进度标签
	private static void updateMusicProgressLabel(int currentMinute, int currentSecond)
	{
		totalTime = musicInfor.get(musicPlayer.getCurrentMusicIndex()).get("time");
		String s_currMinute = String.format("%02d", currentMinute);
		String s_currSecond = String.format("%02d", currentSecond);
		String time = s_currMinute+":"+s_currSecond+"/"+totalTime;
		label_process.setText(time);
	}

	//更新进度条的线程
	static class UpdateProgressBarThread extends Thread {

		@Override
		public void run() {
			double second;
			while(true) {
				if(currentPlayer != null) {
					//获取当前播放的时间
					second = currentPlayer.getCurrentTime().toSeconds();
					//每秒更新一次	
					processSlider.setMax(musicPlayer.getMusicLength(musicPlayer.getCurrentMusicIndex()));
					processSlider.setValue(second);
					//获取当前时间
					currentMinute = (int) (second/60);
					currentSecond = (int) (second%60);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							//修改进度标签的值
							updateMusicProgressLabel(currentMinute, currentSecond);
						}
					});
				}
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	//播放音乐时更新状态标签
	private static void updateStateLabels() {
		currentPlayer = musicPlayer.getCurrentMusicPlayer();
		//更新播放图标
		if(btn_play.getTooltip().getText().equals("播放")) {
			btn_play.setGraphic(playImage);
			btn_play.getTooltip().setText("暂停");
		}	
		//更新音乐信息标签
		label_message.setText(musicInfor.get(currentIndex).get("message"));
		//如果当前线程为null，则新建线程更新当前标签
		if(currentThread == null) {
			currentThread = new UpdateProgressBarThread();
			//执行线程
			currentThread.start();
		}
		//为进度滑动条设置监听事件
		processSlider.setOnMouseDragged(new ProcessBarDraggedListener());
		processSlider.setOnMouseReleased(new ProcessBarReleasedListener());	
		//播放音量与进度条绑定
		currentPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));
		//当音乐结束的时候，选择下一首歌曲继续播放
		currentPlayer.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				//如果是单曲循环模式，则不变；否则，获取下一首歌曲索引
				if(musicPlayer.getPlayModel() != MusicPlayer.SINGLE_CYCLE) {
					currentIndex = musicPlayer.getNextMusicIndex();
				}
				//播放歌曲
				playMusicByIndex(currentIndex);
			}
		});
	}

	//根据歌曲索引播放相应的歌曲
	public static void playMusicByIndex(int index) {
		//播放音乐
		musicPlayer.playMusicByIndex(index);
		//更新当前播放索引
		currentIndex = musicPlayer.getCurrentMusicIndex();
		//更新播放栏相关信息
		updateStateLabels();
		//更新播放列表的相关信息
		listPane.setPlayMusicLabelsStyle(currentIndex);
		//更新歌词面板信息
		MusicPane.updateLrcPane(currentIndex);
	}

	//根据索引删除选中歌曲
	public static void deleteMusicByIndex(int index) {
		System.out.println("******deleted1*****");
		//如果删除的歌曲是当前播放的歌曲，则停止
		if(PlayerPane.currentIndex == index) {
			stopPlayMusic();
			//更新索引
			PlayerPane.currentIndex = musicPlayer.getCurrentMusicIndex();
		}
		if(index <= PlayerPane.currentIndex && PlayerPane.currentIndex != 0) {
			PlayerPane.currentIndex--;
		}
		//删除路径
		musicPaths.remove(index);
		//将歌曲从播放器中删除
		musicPlayer.deleteMusicFromPlayer(index);
		//将歌曲从播放列表中删除
		listPane.deleteMusicFromList(index);
		//更新歌曲数目
		if(musicPaths == null) {
			btn_playList.setText(" "+"0");
		} else{
			btn_playList.setText(" "+musicPaths.size());
		}
		System.out.println("******deletedone*****");
	}

	//当插队歌曲播放时，更新播放顺序
	public static void updatePlayOrder(int index) {
		if(index < currentIndex) {
			currentIndex--;
		}
		//index为插队播放的歌曲索引
		musicPlayer.updateMusicList(index);
	}

	//获取当前索引
	public static int getCurrentIndex() {
		return PlayerPane.currentIndex;
	}

}
