package lrcPane;

import java.util.Map;  
  
/** 
 * 用来封装歌词信息的类 
 * @author Administrator 
 * 
 */  
public class LrcInfo {  
    private String title;//歌曲名  
    private String singer;//演唱者  
    private String album;//专辑     
    private Map<Long,String> infos;//保存歌词信息和时间点一一对应的Map 
    private String lrc;
   public String getLrc() {
		return lrc;
	}

	public void setLrc(String lrc) {
		this.lrc = lrc;
	}

	//以下为getter()  setter()  
    public String getTitle() {
		return title;
	}

	public String getSinger() {
		return singer;
	}

	public String getAlbum() {
		return album;
	}

	public Map<Long, String> getInfos() {
		return infos;
	}
	
	public void setTitle(String t) {
		title = t;
	}
	
	public void setAlbum(String s) {
		album = s;
	}
	
	public void setSinger(String ss) {
		singer = ss;
	}
	
	public void setInfos(Map<Long, String> i)
    {
    	infos=i;
    }  

}  