package lrcPane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class Overwatch extends HBox {

	private static Overwatch ov1;	//歌词信息面板
	GridPane grid = new GridPane();	
	LrcParser lp = new LrcParser();
	private String songName;	//歌名
	private String singer;	//歌手
	private String album;	//专辑
	private String lrcPath;	//歌词路径
	private String imagePath;	//专辑图片
	private Label singername;
	private Label albumname;
	private Label songname;
	private Label lrcLabel;
	private ImageView imageHouse;
	private Pane imagePane = new Pane();

	//构造方法
	private Overwatch(String songName, String singer, String album, String lrcPath, String imagePath) {
		this.songName = songName;
		this.singer = singer;
		this.album = album;
		this.lrcPath = lrcPath;
		this.imagePath = imagePath;
		//初始化
		init();
	}

	//更新面板值
	public void update(String songName, String singer, String album, String lrcPath, String imagePath) {
		this.songName = songName;
		this.singer = singer;
		this.album = album;
		this.lrcPath = lrcPath;
		this.imagePath = imagePath;
		//更新信息
		if(songName == null || songName.equals(""))
			songname.setText("未知"); 
		else
			songname.setText(songName);
		if(album == null || album.equals(""))
			albumname.setText("未知");
		else
			albumname.setText(album);
		if(singer == null || singer.equals(""))
			singername.setText("未知");
		else
			singername.setText(singer);
		LrcInfo lrcInfo;
		try {
			lrcInfo = lp.parser(lrcPath);
			
			lrcLabel.setText(lrcInfo.getLrc());
		} catch (Exception e) {
			lrcLabel.setText("歌词加载失败....\n还没有可以显示的歌词哦");
		}
		//更新图片
		try {
			String path = imagePath.replace(" ", "");
			if(path.equals(""))
				path = null;
			if(path == null) {
				throw new NullPointerException();
			} else{
				imagePane.getChildren().clear();
				imageHouse = new ImageView(
						new Image(Overwatch.class.getResourceAsStream(imagePath)));
			}
		} catch(NullPointerException exception) {
			System.out.println("yichang");
			imageHouse = new ImageView(
					new Image(Overwatch.class.getResourceAsStream("/AlbumImage/default.jpg")));
		}
		imageHouse.setFitWidth(250);//设置图片宽度
		imageHouse.setFitHeight(400);//设置图片高度
		imageHouse.setPreserveRatio(true);//保持图片宽高比
		imagePane.getChildren().add(imageHouse);
	}


	//初始化
	private void init() {
		this.setSpacing(40);
		this.setPadding(new Insets(30, 30, 30, 50));
		//this.setLeft(vbox);//添加一个堆栈面板到上方区域的HBox中
		this.getChildren().add(addGridPane());
		lrcLabel = new Label();
		lrcLabel.setMinWidth(280);
		lrcLabel.setMinHeight(380);
		lrcLabel.setAlignment(Pos.CENTER);
		lrcLabel.setFont(Font.font("宋体", FontWeight.MEDIUM , 14));
		lrcLabel.setTextFill(Color.CORNFLOWERBLUE);
		ScrollPane lrcPane = new ScrollPane();
		lrcPane.setMinWidth(300);
		lrcPane.setMaxWidth(350);
		lrcPane.setMinHeight(400);
		lrcPane.setMaxHeight(450);
		lrcPane.setContent(lrcLabel);
		lrcPane.setPadding(new Insets(20, 5, 5, 0));
		lrcPane.getStyleClass().add("edge-to-edge");// 除去自带的边框
		lrcPane.setStyle(
				"-fx-border-width:0.1; -fx-border-color:#dadada;" + "-fx-border-radius:3; -fx-background:#FFFFFF00;");
		lrcPane.setHbarPolicy(ScrollBarPolicy.NEVER);// 横向永不显示滚动条
		lrcPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);// 竖向按需要显示滚动条
		this.getChildren().add(lrcPane);
		try {
			LrcInfo lrc = lp.parser(lrcPath);
			lrcLabel.setText(lrc.getLrc());
		} catch (Exception e) {
			lrcLabel.setText("歌词加载失败....\n还没有可以显示的歌词哦");
		}
	}

	//获取单一实例
	public static Overwatch getInstance(String songName, String singer, String album, String lrcPath, String imagePath) {
		if(ov1 == null) {
			ov1 = new Overwatch(songName, singer, album, lrcPath, imagePath);
		}
		return ov1;
	}


	public VBox addGridPane(){
		VBox grid = new VBox();
		grid.setSpacing(20);
		grid.setPadding(new Insets(0, 0, 0, 20));
		grid.setAlignment(Pos.CENTER_LEFT);

		// 将album图标放在第1列，占第1和第2行
		try {
			String path = imagePath.replace(" ", "");
			if(path.equals(""))
				path = null;
			if(path == null) {
				throw new NullPointerException();
			} else{
				imageHouse = new ImageView(
						new Image(Overwatch.class.getResourceAsStream(imagePath)));
			}
		} catch(NullPointerException exception) {
			imageHouse = new ImageView(
					new Image(Overwatch.class.getResourceAsStream("/AlbumImage/default.jpg")));
		}
		imageHouse.setFitWidth(250);//设置图片宽度
		imageHouse.setFitHeight(400);//设置图片高度
		imageHouse.setPreserveRatio(true);//保持图片宽高比
		imagePane.getChildren().add(imageHouse);
		grid.getChildren().add(imagePane);

		// 将左边的标签songname放在第5行，第1列，靠下对齐
		if(songName == null || songName.equals("")) {
			songname = new Label("未知");
		} else {
			songname = new Label(songName);
		}
		songname.setPrefWidth(200);
		songname.setFont(Font.font("Microsoft YaHei", 16));
		//GridPane.setValignment(songname, VPos.TOP);
		grid.getChildren().add(songname);

		// 将singer节点放在第6行,第1列
		Text singer1 = new Text("歌手 :");
		singer1.setFill(Color.GRAY);
		singer1.setFont(Font.font("宋体", FontWeight.LIGHT, 14));

		if(singer == null || singer.equals("")) {
			singername = new Label("未知");
		} else {
			singername = new Label(singer);
		}
		singername.setPrefWidth(100);
		singername.setTextFill(Color.BLUE);
		singername.setFont(Font.font("Microsoft YaHei", 14));
		HBox temp1 = new HBox();
		temp1.getChildren().addAll(singer1, singername);

		// 将album节点放在第7行,第1列
		Text album1 = new Text("专辑 :");
		album1.setFill(Color.GRAY);
		album1.setFont(Font.font("宋体", FontWeight.LIGHT, 14));

		if(album == null || album.equals("")) {
			albumname = new Label("未知");
		} else {
			albumname = new Label(album);
		}
		albumname.setPrefWidth(140);
		albumname.setTextFill(Color.BLUE);
		albumname.setFont(Font.font("Microsoft YaHei", 14));
		HBox temp2 = new HBox();
		temp2.getChildren().addAll(album1, albumname);

		HBox hBox = new HBox();
		hBox.setSpacing(5);
		hBox.getChildren().addAll(temp1,temp2);
		grid.getChildren().add(hBox);

		return grid;
	}


}
