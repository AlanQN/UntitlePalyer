package musicListPanel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import application.MusicPane;
import javafx.scene.control.TreeItem;
import musicPlayer.MusicPlayer;
public class MusicSqlite {

	public static boolean insertSqlite(Music music,String listName){
		//能否添加歌曲
		boolean success = true;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			System.out.println("InsertSqlite Opened database successfully");
			stmt = c.createStatement();

			music.setBelongListName(listName);

			int value1 = music.getID();
			int totalTime = music.getTotalTime();
			String value2 = "'"+music.getSongName()+"'";
			String value3 = "'"+music.getSinger()+"'";
			String value4 = "'"+music.getAlbum()+"'";
			String value5 = "'"+music.getTime()+"'";
			String value6 = "'"+music.getPath()+"'";
			String value7 = "'"+music.getBelongListName()+"'";

			String sql = "INSERT INTO '%s' VALUES(" + value1+"," +totalTime+"," +value2+","  +value3+","+
					value4+","+value5+","+value6+","+value7+");";
			sql = String.format(sql, listName);
			stmt.executeUpdate(sql);
			System.out.println("添加成功!");
			stmt.close();
			//c.commit();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			success = false;
		}
		return success;
	}
	//重载，默认添加歌曲添加到Music表中
	public static void insertSqlite(Music music){
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			System.out.println("InsertSqlite Opened database successfully");
			stmt = c.createStatement();

			int value1 = music.getID();
			String value2 = "'"+music.getSongName()+"'";
			String value3 = "'"+music.getSinger()+"'";
			String value4 = "'"+music.getAlbum()+"'";
			String value5 = "'"+music.getTime()+"'";
			String value6 = "'"+music.getPath()+"'";
			String value7 = "'Music'";

			String sql = "INSERT INTO Music VALUES(" + value1+","  +value2+","  +value3+","+
					value4+","+value5+","+value6+","+value7+");";
			stmt.executeUpdate(sql);
			System.out.println("插入成功!");
			stmt.close();
			//c.commit();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	public static boolean createListName(String ListName){
		//歌单是否创建成功
		boolean success = true;
		//判断歌单名是否已经存在，不存在则创建；否则，不创建
		if(!MusicSqlite.isExist(ListName)) {
			Connection c = null;
			Statement stmt = null;
			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
				System.out.println("createListName Opened database successfully");
				stmt = c.createStatement();
				String sql = "CREATE TABLE '%s'"+
						"(ID		INT PRIMARY KEY		NOT NULL," +
						" TOTALTIME INT," +
						" SONGNAME 	TEXT,"+
						" SINGER		TEXT,"+
						" ALBUM		TEXT,"+
						" TIME		TEXT,"+
						" PATH		TEXT,"+
						" LISTNAME	TEXT)";
				sql=String.format(sql, ListName);
				stmt.executeUpdate(sql); 
				stmt.close();
				//c.commit();
				c.close();
				success = true;
			} catch ( Exception e ) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				success = false;
			}
		} else {
			success = false;
		}
		return success;
	}

	//判断table是否存在
	private static boolean isExist(String tableName) {
		//默认不存在
		boolean exist = false;
		//获取所有表名
		ArrayList<String> musicList = getAllMusicList();
		System.out.println(musicList);
		//匹配表名，判断其是否已经存在
		for(String name : musicList) {
			if(tableName.equals(name))	{
				exist = true;
				break;
			}
		}
		return exist;
	}

	//判断歌曲在歌单中是否存在
	public static boolean isExist(String path, String listName) {
		//默认不存在
		boolean exist = false;
		Connection c = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "SELECT SINGER FROM '%s' WHERE PATH = '%s';";
			sql = String.format(sql, listName, path);
			rs = stmt.executeQuery( sql );
			//结果不为null，则说明已经存在
			if(rs.next()) {
				exist = true;
				System.out.println("----exist-----");
			} else {
				System.out.println("----noExist-----");
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			c.close();
			c = null;
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			exist = true;
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(stmt != null)
					stmt.close();
				if(c != null)
					c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return exist;
	}

	//获取所有歌单名
	private static ArrayList<String> getAllMusicList() {
		ArrayList<String> musicList = new ArrayList<>();
		Connection c = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();

			String sql = "select name from sqlite_master where type='table' and name!='sqlite_sequence' order by name;";
			rs = stmt.executeQuery( sql );
			while ( rs.next()) {
				String tableName = rs.getString("NAME");
				musicList.add(tableName);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		return musicList;
	}

	//创建存储播放列表音乐路径的数据table
	public static void createPlayListTable(String tableName){
		//判断歌单名是否已经存在，不存在则创建
		if(!MusicSqlite.isExist(tableName)) {
			Connection c = null;
			Statement stmt = null;
			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
				System.out.println("InsertSqlite Opened database successfully");
				stmt = c.createStatement();
				String sql = "CREATE TABLE '%s'"+
						"(ID		INT PRIMARY KEY		NOT NULL," +
						" PATH		TEXT," + 
						" PLAY		INT," + 
						" LISTNAME  TEXT)";
				sql=String.format(sql, tableName);
				stmt.executeUpdate(sql); 
				stmt.close();
				//c.commit();
				c.close();
			} catch ( Exception e ) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			}
		}
	}

	//将播放列表信息插入所在的table中
	public static void insertPlayList(ArrayList<String> musicPaths, int index){
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			stmt = c.createStatement();

			String listName = "'"+MusicPane.getListName()+"'";
			for(int i = 0; i < musicPaths.size(); i++) {
				String path = "'"+musicPaths.get(i)+"'";
				int play = 0;	//判断是否是当前播放的音乐，1表示是；否则，不是
				if(i == index) {
					play = 1;
				}
				String sql = "INSERT INTO PlayList VALUES("+i+","+path+","+play+","+listName+");";
				stmt.executeUpdate(sql);
			}
			System.out.println("插入成功!");
			stmt.close();
			//c.commit();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}


	///获取指定专辑里面的歌曲名
	public static void readMusicSqlite(String tableName,TreeItem<String> table){
		Connection c = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			String sql = "SELECT * FROM '%s';";
			sql = String.format(sql, tableName);
			rs = stmt.executeQuery( sql );
			while ( rs.next() ) {
				String songName = rs.getString("SONGNAME");
				TreeItem<String> musicName = new TreeItem<String>(songName);
				table.getChildren().add(musicName);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	//先获取不同的表名（即歌单名）
	public  static void readAllMusicSqlite(TreeItem<String> rootItem){
		Connection c = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();

			String sql = "select name from sqlite_master where type='table' and name!='sqlite_sequence' order by name;";
			rs = stmt.executeQuery( sql );
			while ( rs.next()) {
				String tableName = rs.getString("NAME");
				if(!tableName.equals("PlayList")) {
					TreeItem<String> treeItem = new TreeItem<String>(tableName);
					rootItem.getChildren().add(treeItem);
					//treeItem.setExpanded(true);
					readMusicSqlite(tableName,treeItem);
				}
			}
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	//根据歌单获取歌单里面所有歌曲的绝对路径
	public  static ArrayList<String> getAllMusicPaths(String tableName){
		ArrayList<String> paths = new ArrayList<>();
		System.out.println("*********1");
		//如果PlayList存在，则读取
		if(MusicSqlite.isExist(tableName)) {
			System.out.println("*********2");
			Connection c = null;
			Statement stmt = null;
			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
				c.setAutoCommit(false);
				stmt = c.createStatement();

				String sql = "SELECT PATH FROM '%s';";
				sql = String.format(sql, tableName);
				ResultSet rs = stmt.executeQuery( sql );
				while ( rs.next()) {
					paths.add(rs.getString("PATH"));
				}
				rs.close();
				stmt.close();
				c.close();
			} catch ( Exception e ) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);
			}
		}
		return paths;
	}

	//根据歌单获取歌单里面所有歌曲的名称
	public  static ArrayList<String> getAllMusicNames(String tableName){
		ArrayList<String> paths = new ArrayList<>();
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();

			String sql = "SELECT SONGNAME FROM '%s';";
			sql = String.format(sql, tableName);
			ResultSet rs = stmt.executeQuery( sql );
			while ( rs.next()) {
				paths.add(rs.getString("SONGNAME"));
			}
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return paths;
	}

	//根据播放列表中的所有play值, 获取当前播放位置
	public  static int getCurrentIndex(String tableName){
		int currentIndex = 0, temp = 0;
		if(MusicSqlite.isExist(tableName)) {
			Connection c = null;
			Statement stmt = null;
			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
				c.setAutoCommit(false);
				stmt = c.createStatement();

				String sql = "SELECT PLAY FROM '%s';";
				sql = String.format(sql, tableName);
				ResultSet rs = stmt.executeQuery( sql );
				while ( rs.next()) {
					if(rs.getInt("PLAY") == 1) {
						currentIndex = temp;
					}
					temp += 1;
				}
				rs.close();
				stmt.close();
				c.close();
			} catch ( Exception e ) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);
			}
		}
		return currentIndex;
	}

	//获取播放列表的歌单名
	public  static String getListName(String tableName){
		String listName = "";
		if(MusicSqlite.isExist(tableName)) {
			Connection c = null;
			Statement stmt = null;
			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
				c.setAutoCommit(false);
				stmt = c.createStatement();

				String sql = "SELECT LISTNAME FROM '%s';";
				sql = String.format(sql, tableName);
				ResultSet rs = stmt.executeQuery( sql );
				if( rs.next()) {
					listName = rs.getString("LISTNAME");
				}
				rs.close();
				stmt.close();
				c.close();
			} catch ( Exception e ) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);
			}
		}
		return listName;
	}

	//根据歌单删除所有内容
	public static void deleteAllMusic(String tableName){
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sql = "DELETE FROM '%s';";
			sql = String.format(sql,tableName);
			stmt.executeUpdate(sql);
			c.commit();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("delete done successfully");
	}

	///取得当前歌单的最新的歌曲ID
	@SuppressWarnings("finally")
	public static int getLatestID(String tableName){
		int  latestID = 0;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			String sql = "SELECT * FROM '%s';";
			sql = String.format(sql, tableName);
			ResultSet rs = stmt.executeQuery( sql );
			while ( rs.next() ) {
				latestID = rs.getInt("ID");
			}

			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}finally {
			return latestID;
		}

	}

	public static String deleteMusicSqlite(String ListName,String MusicName){
		//获取路径
		String path = null;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			//获取删除路径
			String sql1 = "SELECT PATH FROM '%s' WHERE SONGNAME = '%s';";
			sql1 = String.format(sql1, ListName, MusicName);
			ResultSet set = stmt.executeQuery(sql1);
			if(set.next()) {
				path = set.getString("PATH");
				System.out.println(path);
			}

			//从数据库中删除数据
			String sql2 = "DELETE FROM '%s' WHERE SONGNAME = '%s';";
			sql2 = String.format(sql2,ListName, MusicName);
			stmt.executeUpdate(sql2);
			c.commit();

			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("delete done successfully");
		return path;
	}

	public static void deleteMusicList(String ListName){
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");


			stmt = c.createStatement();
			String sql = "DROP TABLE '%s';";
			sql = String.format(sql, ListName);
			stmt.executeUpdate(sql);
			c.commit();

			stmt.close();
			c.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

	}

	public static String getTreeItemPath(String ListName,String MusicName){
		Connection c = null;
		Statement stmt = null;
		String path = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			String sql = "SELECT PATH FROM '%s' WHERE SONGNAME = '%s';";
			sql = String.format(sql, ListName,MusicName);
			ResultSet rs = stmt.executeQuery( sql );
			if(rs!=null){
				path = rs.getString("PATH");
			}else{
				path = null;
			}
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return path;

	}

	//根据路径获取相应的歌名、作者和时间
	public static Map<String, String> getMusicInforByPath(String path,String listName){
		//歌曲信息
		Map<String, String> infor = new HashMap<>();
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:musicListInfos.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			String sql = "SELECT TOTALTIME, SONGNAME, SINGER, ALBUM, TIME FROM '%s' WHERE PATH = '%s';";
			sql = String.format(sql, listName, path);
			ResultSet rs = stmt.executeQuery( sql );
			if(rs != null){
				infor.put("SONGNAME", rs.getString("SONGNAME"));
				infor.put("SINGER", rs.getString("SINGER"));
				infor.put("ALBUM", rs.getString("ALBUM"));
				//如果文件失效了，则把时间置为0；否则，正常读取
				if(MusicPlayer.isFailed(path)) {
					infor.put("TIME", "00:00");
					infor.put("TOTALTIME", "0");
				} else{
					infor.put("TIME", rs.getString("TIME"));
					infor.put("TOTALTIME", rs.getInt("TOTALTIME")+"");
				}
			} else {
				infor.put("SONGNAME", "歌曲不存在");
				infor.put("SONGER", "");
				infor.put("TIME", "00:00");
				infor.put("TOTALTIME", "0");
			}
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.out.println("读取信息错误");
		}
		return infor;

	}


}
