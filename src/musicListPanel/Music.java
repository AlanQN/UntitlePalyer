package musicListPanel;

public class Music {

	private int ID;
	private String songName;
	private String singer;
	private String album;
	private String time;
	private String path;
	private int totalTime;
	private String belongListName;
	
	public Music(int ID,int totalTime,String songName,String singer,String album,String time,String path)
	{
		this.ID = ID;
		this.totalTime = totalTime;
		this.songName = songName;
		this.singer = singer;
		this.album = album;
		this.time = time;
		this.path = path;
		this.belongListName = null;
	}
	

	public void setID(int ID){
		this.ID = ID;
	}
	
	public int getID(){
		return this.ID;
	}
	
	public int getTotalTime() {
		return totalTime;
	}


	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	
	public String getSongName(){
		return this.songName;
	}

	public String getSinger(){
		return this.singer;
	}
	
	public String getPath(){
		return this.path;
	}
	
	
	public String getAlbum(){
		return this.album;
	}
	
	public String getTime(){
		return this.time;
	}
	
	public void setBelongListName(String ListName){
		this.belongListName = ListName;
	}
	
	public String getBelongListName(){
		return this.belongListName;
	}
	
	public void print(){
		System.out.println(this.ID);
		System.out.println(this.songName);
		System.out.println(this.singer);
		System.out.println(this.album);
		System.out.println(this.time);
		System.out.println(this.path);
		System.out.println(this.belongListName);
	}
}
