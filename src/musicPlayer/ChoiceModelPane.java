package musicPlayer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

public class ChoiceModelPane extends VBox {
	
	//模式标签
	private Label[] modeLabels;
	
	//构造方法
	public ChoiceModelPane(String[] modeArray) {
		//获取图片
		ImageView[] imageView = getLabelGraphics(modeArray);
		modeLabels = new Label[modeArray.length];
		for(int i = 0; i < modeArray.length; i++) {
			modeLabels[i] = new Label(modeArray[i]);
			modeLabels[i].setGraphic(imageView[i]);
			Label modeLabel = modeLabels[i];
			modeLabels[i].setFont(Font.font(13));
			modeLabels[i].setPrefSize(110,35);
			modeLabels[i].setAlignment(Pos.CENTER);
			//对文字设置渐变颜色
			modeLabels[i].setTextFill(new LinearGradient(0, 0, 1, 2, true, CycleMethod.REPEAT, new
         Stop[]{new Stop(0, Color.DARKGREY), new Stop(0.5f, Color.GAINSBORO)}));
			//为标签添加样式
			modeLabels[i].setStyle("-fx-background-color: rgba(60,60,60,0.9); ");
			//设置监听器
			modeLabels[i].setOnMouseEntered(e->{modeLabel.setStyle("-fx-background-color: rgba(30,30,30,0.9); -fx-border-width:0;");});
			modeLabels[i].setOnMouseExited(e->{modeLabel.setStyle("-fx-background-color: rgba(60,60,60,0.9); -fx-border-width:0;");});
			this.getChildren().add(modeLabels[i]);
			if(i != (modeArray.length-1)) {
				Separator line = new Separator();
				this.getChildren().add(line);
			}
		}
		this.setSpacing(3);
		this.setAlignment(Pos.CENTER);
		this.setStyle("-fx-background-color: rgba(40,40,40,0.9);");
	}
	
	//选择模式标签数组
	public Label[] getModeLabel() {
		return modeLabels;
	}
	
	public ImageView[] getLabelGraphics(String[] modeArray) {
		String imagePath = new String("/image/");
		ImageView[] imageView = new ImageView[modeArray.length];
		for(int i = 0; i < modeArray.length; i++) {
			imageView[i] = new ImageView(new Image(getClass().getResourceAsStream(imagePath+modeArray[i]+".png")));
			imageView[i].setOpacity(0.5);
			imageView[i].setFitWidth(18);
			imageView[i].setFitHeight(18);
		}
		return imageView;
	}
	

}
