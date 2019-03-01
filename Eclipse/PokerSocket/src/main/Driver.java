package main;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import objects.*;

public class Driver {
	private Connection myConn;

	public Driver() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		myConn = DriverManager.getConnection("jdbc:mysql://localhost/pocketChips?autoReconnect=true&useSSL=false", "pokerChipsRemote", "pokerChips14?!");
	}

	public boolean userExists(String userID) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("select name from Users where userID=?");
		ps.setString(1, userID);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) return true;

		return false;
	}

	public boolean gameExists(String gameID) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("select name from Games where gameID=?");
		ps.setString(1, gameID);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) return true;
		return false;
	}

	public boolean playerExists(String playerID) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("select red from Players where playerID=?");
		ps.setString(1, playerID);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) return true;

		return false;
	}

	public boolean potExists(String potID) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("select red from Pot where userID=?");
		ps.setString(1, potID);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) return true;

		return false;
	}

	public UserObject getUserById(String id, PrintWriter writer) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("select * from Users where userID=?");
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		String name = "";

		while(rs.next()) {
			name = rs.getString("name");

			//Need to get Games for User ID
//				if(rs.getString(2) == null) {
//					name = rs.getString(3);
// 				} else {
// 					String goId = rs.getString(2);
// 					go.add(getPokerGameByID(goId));
// 				}
		}

		UserObject uo = new UserObject(id, getGamesForUser(id), writer, name); //Tests

		return uo;
	}

	public ArrayList<GameObject> getGamesForUser(String userID) throws SQLException {
		PreparedStatement playerPS = myConn.prepareStatement("select gameID from Players where userID=?");
		playerPS.setString(1, userID);
		ResultSet playerRS = playerPS.executeQuery();
		ArrayList<GameObject> games = new ArrayList<GameObject>();

		while(playerRS.next()) {
			games.add(getPokerGameByID(playerRS.getString("gameID")));
		}

		return games;
	}

	public ArrayList<PokerPlayerObject> getPokerPlayersForGame(String gameID) throws SQLException {
		PreparedStatement playerPS = myConn.prepareStatement("select * from Players where gameID=?");
		playerPS.setString(1, gameID);
		ResultSet playerRS = playerPS.executeQuery();
		ArrayList<PokerPlayerObject> players = new ArrayList<PokerPlayerObject>();

		while(playerRS.next()) {
			Chip c = new Chip(playerRS.getInt("red"), playerRS.getInt("blue"), playerRS.getInt("yellow"), playerRS.getInt("green"), playerRS.getInt("orange"));
			players.add(new PokerPlayerObject(c, playerRS.getString("playerID"), playerRS.getString("userID")));

		}

		return players;
	}

	public PotObject getPotForGame(String gameID) throws SQLException {
		PreparedStatement playerPS = myConn.prepareStatement("select * from Pots where gameID=?");
		playerPS.setString(1, gameID);
		ResultSet playerRS = playerPS.executeQuery();
		PotObject pot = null;

		while(playerRS.next()) {
			Chip c = new Chip(playerRS.getInt("red"), playerRS.getInt("blue"), playerRS.getInt("yellow"), playerRS.getInt("green"), playerRS.getInt("orange"));
			pot = new PotObject(playerRS.getString("potID"), c);

		}

		return pot;
	}

	public String getUsernameById(String id) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("select name from Users where userID=?");
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		String name = "";

		while(rs.next()) name = rs.getString("name");

		return name;
	}

	public PokerGameObject getPokerGameByID(String id) throws SQLException {
		//System.out.println("init game object: " + id);
		PreparedStatement ps = myConn.prepareStatement("select * from Games where gameID=?");
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		PokerGameObject go = null;

		while(rs.next()) {
			//System.out.println("3: " + rs.getString(3));
			Chip c = new Chip(rs.getInt("red"), rs.getInt("blue"), rs.getInt("yellow"), rs.getInt("green"), rs.getInt("orange"));
			double[] cv = new double[5];
			cv[0] = rs.getDouble("redV");
			cv[1] = rs.getDouble("blueV");
			cv[2] = rs.getDouble("yellowV");
			cv[3] = rs.getDouble("greenV");
			cv[4] = rs.getDouble("orangeV");
			go = new PokerGameObject(id, getPokerPlayersForGame(id), getPotForGame(id), rs.getString("name"), c, cv);
		}

		if(go == null) System.out.println(id);

		return go;
	}

	public PokerPlayerObject getPlayerById(String id) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("select * from Players where playerID=?");
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		PokerPlayerObject p = null;

		while(rs.next()) {
			Chip c = new Chip(rs.getInt("red"), rs.getInt("blue"), rs.getInt("yellow"), rs.getInt("green"), rs.getInt("orange"));
			p = new PokerPlayerObject(c, id, rs.getString("userID"));
		}

		return p;
	}

	public PotObject getPotById(String id) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("select * from Pots where potID=?");
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		PotObject p = null;

		while(rs.next()) {
			Chip c = new Chip(rs.getInt("red"), rs.getInt("blue"), rs.getInt("yellow"), rs.getInt("green"), rs.getInt("orange"));
			p = new PotObject(id, c);
		}

		return p;
	}

	public void createUser(UserObject u) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("insert into Users (userID, name) values (?,?)");
		ps.setString(1, u.getId());
		ps.setString(2, u.getName());
		ps.executeUpdate();
	}

	public void setUsername(UserObject u) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("update Users set name=? where userID=?");
		ps.setString(1, u.getName());
		ps.setString(2, u.getId());
		ps.executeUpdate();
	}

//	public void changeUsername(UserObject u, String name) {
//		try {
//			Statement myStatement = myConn.createStatement();
//			myStatement.executeUpdate("delete from " + u.getId() + " where name=('" + u.getName() + "')");
//
//			Statement myStatement2 = myConn.createStatement();
//			myStatement2.executeUpdate("insert into " + u.getId() + " (name) values ('" + name + "')");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

//	public void addGameToUser(PokerGameObject g, UserObject u) {
//		try {
//			Statement myStatement = myConn.createStatement();
//			myStatement.executeUpdate("insert into " + u.getId() + " (games) values ('" + g.getId() + "')");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

	public void createGame(PokerGameObject g) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("insert into Games (gameID, name, redV, blueV, yellowV, greenV, orangeV, red, blue, yellow, green, orange) values (?,?,?,?,?,?,?,?,?,?,?,?)");
		ps.setString(1, g.getId());
		ps.setString(2, g.getName());
		ps.setDouble(3, g.getChipValues()[0]);
		ps.setDouble(4, g.getChipValues()[1]);
		ps.setDouble(5, g.getChipValues()[2]);
		ps.setDouble(6, g.getChipValues()[3]);
		ps.setDouble(7, g.getChipValues()[4]);
		ps.setDouble(8, g.getDefaultChips().getRed());
		ps.setDouble(9, g.getDefaultChips().getBlue());
		ps.setDouble(10, g.getDefaultChips().getYellow());
		ps.setDouble(11, g.getDefaultChips().getGreen());
		ps.setDouble(12, g.getDefaultChips().getOrange());
		ps.executeUpdate();
	}

	public void createPlayerInGame(PokerPlayerObject p, PokerGameObject g) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("insert into Players (playerID, userID, gameID, red, blue, yellow, green, orange) values (?,?,?,?,?,?,?,?)");
		ps.setString(1, p.getId());
		ps.setString(2, p.getUserId());
		ps.setString(3, g.getId());
		ps.setDouble(4, p.getChipObject().getRed());
		ps.setDouble(5, p.getChipObject().getBlue());
		ps.setDouble(6, p.getChipObject().getYellow());
		ps.setDouble(7, p.getChipObject().getGreen());
		ps.setDouble(8, p.getChipObject().getOrange());
		ps.executeUpdate();
	}

	public void createPotInGame(PotObject p, PokerGameObject g) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("insert into Pots (potID, gameID, red, blue, yellow, green, orange) values (?,?,?,?,?,?,?)");
		ps.setString(1, p.getId());
		ps.setString(2, g.getId());
		ps.setDouble(3, p.getChipObject().getRed());
		ps.setDouble(4, p.getChipObject().getBlue());
		ps.setDouble(5, p.getChipObject().getYellow());
		ps.setDouble(6, p.getChipObject().getGreen());
		ps.setDouble(7, p.getChipObject().getOrange());
		ps.executeUpdate();
	}

	public void removePlayerFromGame(PokerPlayerObject p) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("delete from Players where playerID=?");
		ps.setString(1, p.getId());
		ps.executeUpdate();
	}

	public void removeUserFromGame(UserObject u, GameObject g) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("delete from Players where userID=? and gameID=?");
		ps.setString(1, u.getId());
		ps.setString(2, g.getId());
		ps.executeUpdate();
	}

//	public void removeGameFromUser(PokerGameObject g, UserObject u) {
//		try {
//			Statement myStatement = myConn.createStatement();
//			myStatement.executeUpdate("delete from " + u.getId() + " where games=('" + g.getId() + "')");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public void createPlayer(PokerPlayerObject p) {
//		try {
//			Statement myStatement = myConn.createStatement();
//			myStatement.executeUpdate("create table " + p.getId() + " (id int not null auto_increment, red double null, blue double null, green double null, black double null, purple double null, userId longtext null, primary key(id))");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

//	public void createPot(PotObject p) {
//		try {
//			Statement myStatement = myConn.createStatement();
//			myStatement.executeUpdate("create table " + p.getId() + " (id int not null auto_increment, red double null, blue double null, green double null, black double null, purple double null, chip0Value double null, chip1Value double null, chip2Value double null, chip3Value double null, chip4Value double null, primary key(id))");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

	public void updatePlayerChips(PokerPlayerObject p) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("update Players set red=?, blue=?, yellow=?, green=?, orange=? where playerID=?");
		ps.setDouble(1, p.getChipObject().getRed());
		ps.setDouble(2, p.getChipObject().getBlue());
		ps.setDouble(3, p.getChipObject().getYellow());
		ps.setDouble(4, p.getChipObject().getGreen());
		ps.setDouble(5, p.getChipObject().getOrange());
		ps.setString(6, p.getId());
		ps.executeUpdate();
	}

	public void updatePotChips(PotObject p) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("update Pots set red=?, blue=?, yellow=?, green=?, orange=? where potID=?");
		ps.setDouble(1, p.getChipObject().getRed());
		ps.setDouble(2, p.getChipObject().getBlue());
		ps.setDouble(3, p.getChipObject().getYellow());
		ps.setDouble(4, p.getChipObject().getGreen());
		ps.setDouble(5, p.getChipObject().getOrange());
		ps.setString(6, p.getId());
		ps.executeUpdate();
	}

	//Blackjack Flow

	public void createBlackjackGame(BlackjackGameObject g) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("insert into BlackjackGames (gameID, name, redV, blueV, yellowV, greenV, orangeV, red, blue, yellow, green, orange, decimalRatio) values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
		ps.setString(1, g.getId());
		ps.setString(2, g.getName());
		ps.setDouble(3, g.getChipValues()[0]);
		ps.setDouble(4, g.getChipValues()[1]);
		ps.setDouble(5, g.getChipValues()[2]);
		ps.setDouble(6, g.getChipValues()[3]);
		ps.setDouble(7, g.getChipValues()[4]);
		ps.setDouble(8, g.getDefaultChips().getRed());
		ps.setDouble(9, g.getDefaultChips().getBlue());
		ps.setDouble(10, g.getDefaultChips().getYellow());
		ps.setDouble(11, g.getDefaultChips().getGreen());
		ps.setDouble(12, g.getDefaultChips().getOrange());
		ps.setDouble(13, g.getRatio());
		ps.executeUpdate();

	}

	public void createBlackjackPlayerInGame(BlackjackPlayerObject p, BlackjackGameObject g) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("insert into BlackjackPlayers (playerID, userID, gameID, red, blue, yellow, green, orange, betRed, betBlue, betYellow, betGreen, betOrange, secondaryBetRed, secondaryBetBlue, secondaryBetYellow, secondaryBetGreen, secondaryBetOrange, isDealer, option) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		ps.setString(1, p.getId());
		ps.setString(2, p.getUserId());
		ps.setString(3, g.getId());
		ps.setDouble(4, p.getChipObject().getRed());
		ps.setDouble(5, p.getChipObject().getBlue());
		ps.setDouble(6, p.getChipObject().getYellow());
		ps.setDouble(7, p.getChipObject().getGreen());
		ps.setDouble(8, p.getChipObject().getOrange());
		ps.setDouble(9, p.getBetChips().getRed());
		ps.setDouble(10, p.getBetChips().getBlue());
		ps.setDouble(11, p.getBetChips().getYellow());
		ps.setDouble(12, p.getBetChips().getGreen());
		ps.setDouble(13, p.getBetChips().getOrange());
		ps.setDouble(14, p.getSecondaryChips().getRed());
		ps.setDouble(15, p.getSecondaryChips().getBlue());
		ps.setDouble(16, p.getSecondaryChips().getYellow());
		ps.setDouble(17, p.getSecondaryChips().getGreen());
		ps.setDouble(18, p.getSecondaryChips().getOrange());
		ps.setBoolean(19, p.isDealer());
		ps.setInt(20, p.getOption());
		ps.executeUpdate();
	}

	public void updateBlackjackPlayer(BlackjackPlayerObject p) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("update BlackjackPlayers set red=?, blue=?, yellow=?, green=?, orange=?, betRed=?, betBlue=?, betYellow=?, betGreen=?, betOrange=?, secondaryBetRed=?, secondaryBetBlue=?, secondaryBetYellow=?, secondaryBetGreen=?, secondaryBetOrange=?, isDealer=?, option=? where playerID=?");
		ps.setDouble(1, p.getChipObject().getRed());
		ps.setDouble(2, p.getChipObject().getBlue());
		ps.setDouble(3, p.getChipObject().getYellow());
		ps.setDouble(4, p.getChipObject().getGreen());
		ps.setDouble(5, p.getChipObject().getOrange());
		ps.setDouble(6, p.getBetChips().getRed());
		ps.setDouble(7, p.getBetChips().getBlue());
		ps.setDouble(8, p.getBetChips().getYellow());
		ps.setDouble(9, p.getBetChips().getGreen());
		ps.setDouble(10, p.getBetChips().getOrange());
		ps.setDouble(11, p.getSecondaryChips().getRed());
		ps.setDouble(12, p.getSecondaryChips().getBlue());
		ps.setDouble(13, p.getSecondaryChips().getYellow());
		ps.setDouble(14, p.getSecondaryChips().getGreen());
		ps.setDouble(15, p.getSecondaryChips().getOrange());
		ps.setBoolean(16, p.isDealer());
		ps.setInt(17, p.getOption());
		ps.setString(18, p.getId());
		ps.executeUpdate();
	}

	public boolean isBlackjackGame(String gameID) throws SQLException {
		PreparedStatement ps = myConn.prepareStatement("select decimalRatio from BlackjackGames where gameID=?");
		ps.setString(1, gameID);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) return true;


		return false;
	}

	public BlackjackGameObject getBlackjackGameByID(String id) throws SQLException {
		//System.out.println("init game object: " + id);
		PreparedStatement ps = myConn.prepareStatement("select * from BlackjackGames where gameID=?");
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		BlackjackGameObject go = null;

		while(rs.next()) {
			//System.out.println("3: " + rs.getString(3));
			Chip c = new Chip(rs.getInt("red"), rs.getInt("blue"), rs.getInt("yellow"), rs.getInt("green"), rs.getInt("orange"));
			double[] cv = new double[5];
			cv[0] = rs.getDouble("redV");
			cv[1] = rs.getDouble("blueV");
			cv[2] = rs.getDouble("yellowV");
			cv[3] = rs.getDouble("greenV");
			cv[4] = rs.getDouble("orangeV");
			go = new BlackjackGameObject(id, getBlackjackPlayersForGame(id), rs.getString("name"), c, cv, rs.getDouble("decimalRatio"));
		}

		if(go == null) System.out.println(id);

		return go;
	}

	public BlackjackGameObject getBlackjackGameByPlayerID(String playerID) throws SQLException {
		//System.out.println("init game object: " + id);
		PreparedStatement ps = myConn.prepareStatement("select * from BlackjackGames where playerID=?");
		ps.setString(1, playerID);
		ResultSet rs = ps.executeQuery();
		BlackjackGameObject go = null;

		while(rs.next()) {
			//System.out.println("3: " + rs.getString(3));
			Chip c = new Chip(rs.getInt("red"), rs.getInt("blue"), rs.getInt("yellow"), rs.getInt("green"), rs.getInt("orange"));
			double[] cv = new double[5];
			cv[0] = rs.getDouble("redV");
			cv[1] = rs.getDouble("blueV");
			cv[2] = rs.getDouble("yellowV");
			cv[3] = rs.getDouble("greenV");
			cv[4] = rs.getDouble("orangeV");
			go = new BlackjackGameObject(playerID, getBlackjackPlayersForGame(playerID), rs.getString("name"), c, cv, rs.getDouble("decimalRatio"));
		}

		if(go == null) System.out.println(playerID);

		return go;
	}

	public ArrayList<BlackjackPlayerObject> getBlackjackPlayersForGame(String gameID) throws SQLException {
		PreparedStatement playerPS = myConn.prepareStatement("select * from BlackjackPlayers where gameID=?");
		playerPS.setString(1, gameID);
		ResultSet playerRS = playerPS.executeQuery();
		ArrayList<BlackjackPlayerObject> players = new ArrayList<BlackjackPlayerObject>();

		while(playerRS.next()) {
			Chip c = new Chip(playerRS.getInt("red"), playerRS.getInt("blue"), playerRS.getInt("yellow"), playerRS.getInt("green"), playerRS.getInt("orange"));
			Chip bc = new Chip(playerRS.getInt("betRed"), playerRS.getInt("betBlue"), playerRS.getInt("betYellow"), playerRS.getInt("betGreen"), playerRS.getInt("betOrange"));
			Chip sbc = new Chip(playerRS.getInt("SecondaryBetRed"), playerRS.getInt("SecondaryBetBlue"), playerRS.getInt("SecondaryBetYellow"), playerRS.getInt("SecondaryBetGreen"), playerRS.getInt("SecondaryBetOrange"));
			players.add(new BlackjackPlayerObject(c, bc, sbc, playerRS.getBoolean("isDealer"), playerRS.getInt("option"), playerRS.getString("playerID"), playerRS.getString("userID")));
		}

		return players;
	}

	public BlackjackPlayerObject getBlackjackPlayerByID(String playerID) throws SQLException {
		PreparedStatement playerPS = myConn.prepareStatement("select * from BlackjackPlayers where playerID=?");
		playerPS.setString(1, playerID);
		ResultSet playerRS = playerPS.executeQuery();
		BlackjackPlayerObject player = null;

		while(playerRS.next()) {
			Chip c = new Chip(playerRS.getInt("red"), playerRS.getInt("blue"), playerRS.getInt("yellow"), playerRS.getInt("green"), playerRS.getInt("orange"));
			Chip bc = new Chip(playerRS.getInt("betRed"), playerRS.getInt("betBlue"), playerRS.getInt("betYellow"), playerRS.getInt("betGreen"), playerRS.getInt("betOrange"));
			Chip sbc = new Chip(playerRS.getInt("SecondaryBetRed"), playerRS.getInt("SecondaryBetBlue"), playerRS.getInt("SecondaryBetYellow"), playerRS.getInt("SecondaryBetGreen"), playerRS.getInt("SecondaryBetOrange"));
			player = new BlackjackPlayerObject(c, bc, sbc, playerRS.getBoolean("isDealer"), playerRS.getInt("option"), playerRS.getString("playerID"), playerRS.getString("userID"));
		}

		return player;
	}

	public void close() throws SQLException {
		myConn.close();
	}
}
