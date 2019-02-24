package main;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.sun.jdi.ClassNotPreparedException;
import objects.Chip;
import objects.GameObject;
import objects.PlayerObject;
import objects.PotObject;
import objects.UserObject;

public class Driver {
	private Connection myConn;
	
	public Driver() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			myConn = DriverManager.getConnection("jdbc:mysql://localhost/pocketChips?autoReconnect=true&useSSL=false", "pokerChipsRemote", "pokerChips14?!");
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public boolean userExists(String userID) {
		try {
			PreparedStatement ps = myConn.prepareStatement("select name from Users where userID=?");
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) return true;
		} catch (SQLException p) {
			System.out.println("User Exists SQL error");
		}

		return false;
	}

	public boolean gameExists(String gameID) {
		try {
			PreparedStatement ps = myConn.prepareStatement("select name from Games where gameID=?");
			ps.setString(1, gameID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) return true;
		} catch (SQLException p) {
			System.out.println("Game Exists SQL error");
		}

		return false;
	}

	public boolean playerExists(String playerID) {
		try {
			PreparedStatement ps = myConn.prepareStatement("select red from Players where playerID=?");
			ps.setString(1, playerID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) return true;
		} catch (SQLException p) {
			System.out.println("Player Exists SQL error");
		}

		return false;
	}

	public boolean potExists(String potID) {
		try {
			PreparedStatement ps = myConn.prepareStatement("select red from Pot where userID=?");
			ps.setString(1, potID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) return true;
		} catch (SQLException p) {
			System.out.println("Pot Exists SQL error");
		}

		return false;
	}
	
	public UserObject getUserById(String id, PrintWriter writer) {
		try {
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
// 					go.add(getGameById(goId));
// 				}
			}
			
			UserObject uo = new UserObject(id, getGamesForUser(id), writer, name); //Tests
			
			return uo;
		} catch (SQLException e) {
			return null;
		}
	}

	public ArrayList<GameObject> getGamesForUser(String userID) {
		try {
			PreparedStatement playerPS = myConn.prepareStatement("select gameID from Players where userID=?");
			playerPS.setString(1, userID);
			ResultSet playerRS = playerPS.executeQuery();
			ArrayList<GameObject> games = new ArrayList<GameObject>();

			while(playerRS.next()) {
				games.add(getGameById(playerRS.getString("gameID")));
			}

			return games;
		} catch (SQLException e) {
			return null;
		}
	}

	public ArrayList<PlayerObject> getPlayersForGame(String gameID) {
		try {
			PreparedStatement playerPS = myConn.prepareStatement("select * from Players where gameID=?");
			playerPS.setString(1, gameID);
			ResultSet playerRS = playerPS.executeQuery();
			ArrayList<PlayerObject> players = new ArrayList<PlayerObject>();

			while(playerRS.next()) {
				Chip c = new Chip(playerRS.getInt("red"), playerRS.getInt("blue"), playerRS.getInt("yellow"), playerRS.getInt("green"), playerRS.getInt("orange"));
				players.add(new PlayerObject(c, playerRS.getString("playerID"), playerRS.getString("userID")));

			}

			return players;
		} catch (SQLException e) {
			return null;
		}
	}

	public PotObject getPotForGame(String gameID) {
		try {
			PreparedStatement playerPS = myConn.prepareStatement("select * from Pots where gameID=?");
			playerPS.setString(1, gameID);
			ResultSet playerRS = playerPS.executeQuery();
			PotObject pot = null;

			while(playerRS.next()) {
				Chip c = new Chip(playerRS.getInt("red"), playerRS.getInt("blue"), playerRS.getInt("yellow"), playerRS.getInt("green"), playerRS.getInt("orange"));
				pot = new PotObject(playerRS.getString("potID"), c);

			}

			return pot;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public String getUsernameById(String id) {
		try {
			PreparedStatement ps = myConn.prepareStatement("select name from Users where userID=?");
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			String name = "";
			
			while(rs.next()) name = rs.getString("name");

			return name;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public GameObject getGameById(String id) {
		//System.out.println("init game object: " + id);
		try {
			PreparedStatement ps = myConn.prepareStatement("select * from ?");
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			GameObject go = null;

			while(rs.next()) {
				//System.out.println("3: " + rs.getString(3));
				Chip c = new Chip(rs.getInt("red"), rs.getInt("blue"), rs.getInt("yellow"), rs.getInt("green"), rs.getInt("orange"));
				double[] cv = new double[5];
				cv[0] = rs.getDouble("red");
				cv[1] = rs.getDouble("blue");
				cv[2] = rs.getDouble("yellow");
				cv[3] = rs.getDouble("green");
				cv[4] = rs.getDouble("orange");
				go = new GameObject(id, getPlayersForGame(id), getPotForGame(id), rs.getString("name"), c, cv);
			}
			
			return go;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PlayerObject getPlayerById(String id) {
		try {
			PreparedStatement ps = myConn.prepareStatement("select * from Players where playerID=?");
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			PlayerObject p = null;

			while(rs.next()) {
				Chip c = new Chip(rs.getInt("red"), rs.getInt("blue"), rs.getInt("yellow"), rs.getInt("green"), rs.getInt("orange"));
				p = new PlayerObject(c, id, rs.getString("userID"));
			}

			return p;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public PotObject getPotById(String id) {
		try {
			PreparedStatement ps = myConn.prepareStatement("select * from Pots where potID=?");
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			PotObject p = null;

			while(rs.next()) {
				Chip c = new Chip(rs.getInt("red"), rs.getInt("blue"), rs.getInt("yellow"), rs.getInt("green"), rs.getInt("orange"));
				p = new PotObject(id, c);
			}
			
			return p;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public void createUser(UserObject u) {
		try {
			PreparedStatement ps = myConn.prepareStatement("insert into Users (userID, name) values (?,?)");
			ps.setString(1, u.getId());
			ps.setString(2, u.getName());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setUsername(UserObject u) {
		try {
			PreparedStatement ps = myConn.prepareStatement("update Users set name=? where userID=?");
			ps.setString(1, u.getName());
			ps.setString(2, u.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
//	public void addGameToUser(GameObject g, UserObject u) {
//		try {
//			Statement myStatement = myConn.createStatement();
//			myStatement.executeUpdate("insert into " + u.getId() + " (games) values ('" + g.getId() + "')");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
	public void createGame(GameObject g) {
		try {
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
			ps.setDouble(10, g.getDefaultChips().getGreen());
			ps.setDouble(11, g.getDefaultChips().getBlack());
			ps.setDouble(12, g.getDefaultChips().getPurple());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createPlayerInGame(PlayerObject p, GameObject g) {
		try {
			PreparedStatement ps = myConn.prepareStatement("insert into Players (playerID, userID, gameID, red, blue, yellow, green, orange) values (?,?,?,?,?,?,?,?)");
			ps.setString(1, p.getId());
			ps.setString(2, p.getUserId());
			ps.setString(3, g.getId());
			ps.setDouble(4, p.getChipObject().getRed());
			ps.setDouble(5, p.getChipObject().getBlue());
			ps.setDouble(6, p.getChipObject().getGreen());
			ps.setDouble(7, p.getChipObject().getBlack());
			ps.setDouble(8, p.getChipObject().getPurple());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createPotInGame(PotObject p, GameObject g) {
		try {
			PreparedStatement ps = myConn.prepareStatement("insert into Pots (potID, gameID, red, blue, yellow, green, orange) values (?,?,?,?,?,?,?)");
			ps.setString(1, p.getId());
			ps.setString(2, g.getId());
			ps.setDouble(3, p.getChipObject().getRed());
			ps.setDouble(4, p.getChipObject().getBlue());
			ps.setDouble(5, p.getChipObject().getGreen());
			ps.setDouble(6, p.getChipObject().getBlack());
			ps.setDouble(7, p.getChipObject().getPurple());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removePlayerFromGame(PlayerObject p) {
		try {
			PreparedStatement ps = myConn.prepareStatement("delete from Players where playerID=?");
			ps.setString(1, p.getId());
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeUserFromGame(UserObject u, GameObject g) {
		try {
			PreparedStatement ps = myConn.prepareStatement("delete from Players where userID=? and gameID=?");
			ps.setString(1, u.getId());
			ps.setString(2, g.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//	public void removeGameFromUser(GameObject g, UserObject u) {
//		try {
//			Statement myStatement = myConn.createStatement();
//			myStatement.executeUpdate("delete from " + u.getId() + " where games=('" + g.getId() + "')");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	public void createPlayer(PlayerObject p) {
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
	
	public void updatePlayerChips(PlayerObject p) {
		try {
			PreparedStatement ps = myConn.prepareStatement("update Players set red=?, blue=?, yellow=?, green=?, orange=? where playerID=?");
			ps.setDouble(1, p.getChipObject().getRed());
			ps.setDouble(2, p.getChipObject().getBlue());
			ps.setDouble(3, p.getChipObject().getGreen());
			ps.setDouble(4, p.getChipObject().getBlack());
			ps.setDouble(5, p.getChipObject().getPurple());
			ps.setString(6, p.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updatePotChips(PotObject p) {
		try {
			PreparedStatement ps = myConn.prepareStatement("update Pots set red=?, blue=?, green=?, yellow=?, orange=? where potID=?");
			ps.setDouble(1, p.getChipObject().getRed());
			ps.setDouble(2, p.getChipObject().getBlue());
			ps.setDouble(3, p.getChipObject().getGreen());
			ps.setDouble(4, p.getChipObject().getBlack());
			ps.setDouble(5, p.getChipObject().getPurple());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			myConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
