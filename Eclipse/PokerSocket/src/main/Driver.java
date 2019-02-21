package main;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
			myConn = DriverManager.getConnection("jdbc:mysql://localhost/pokerChips?autoReconnect=true&useSSL=false", "pokerChipsRemote", "pokerChips14?!");
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public boolean existsById(String id) {
		try {
			Statement myStatement = myConn.createStatement();
			ResultSet rs = myStatement.executeQuery("show tables like '" + id + "'");
			
			int i = 0;
			while(rs.next()) {
				i++;
			}
			
			if(i != 0)
				return true;
			return false;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public UserObject getUserById(String id, PrintWriter writer) {
		try {
			Statement myStatement = myConn.createStatement();
			ResultSet rs = myStatement.executeQuery("select * from " + id);
			ArrayList<GameObject> go = new ArrayList<GameObject>();
			String name = "";
			
			while(rs.next()) {
				if(rs.getString(2) == null) {
					name = rs.getString(3);
 				} else {
 					String goId = rs.getString(2);
 					go.add(getGameById(goId));
 				}
			}
			
			UserObject uo = new UserObject(id, go, writer, name);
			
			return uo;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public String getUsernameById(String id) {
		try {
			Statement myStatement = myConn.createStatement();
			ResultSet rs = myStatement.executeQuery("select * from " + id);
			String name = "";
			
			while(rs.next()) {
				if(rs.getString(2) == null)
					name = rs.getString(3);
			}
			
			return name;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public GameObject getGameById(String id) {
		//System.out.println("init game object: " + id);
		try {
			Statement myStatement = myConn.createStatement();
			ResultSet rs = myStatement.executeQuery("select * from " + id);
			ArrayList<PlayerObject> po = new ArrayList<PlayerObject>();
			Chip defaultChips = new Chip();
			String potId = "";
			String name = "";
			//System.out.println("before while: " + id);
			
			while(rs.next()) {
				//System.out.println("3: " + rs.getString(3));
				if(rs.getString(2) == null || rs.getString(2).equals("")) {
					potId = rs.getString(3);
					name = rs.getString(4);
					//System.out.println("null or zero: " + id);
					try {
						//System.out.println(rs.getString(5));
						defaultChips.addChips(Double.parseDouble(rs.getString(5)), Double.parseDouble(rs.getString(6)), Double.parseDouble(rs.getString(7)), Double.parseDouble(rs.getString(8)), Double.parseDouble(rs.getString(9)));
					} catch (SQLException e) {
						System.out.println("Old game!");
					}
				} else {
					String poId = rs.getString(2);
					po.add(getPlayerById(poId));
					//System.out.println("in the else statement: " + id);
				}
			}
			
			PotObject pot = getPotById(potId);
			//System.out.println("after while: " + po.size());
			return new GameObject(id, po, pot, name, defaultChips);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PlayerObject getPlayerById(String id) {
		try {
			Statement myStatement = myConn.createStatement();
			ResultSet rs = myStatement.executeQuery("select * from " + id);
			double red = 0, blue = 0, green = 0, black = 0, purple = 0;
			String userId = "";
			
			while(rs.next()) {
				red = rs.getDouble(2);
				blue = rs.getDouble(3);
				green = rs.getDouble(4);
				black = rs.getDouble(5);
				purple = rs.getDouble(6);
				userId = rs.getString(7);
			}
			
			Chip chips = new Chip(red, blue, green, black, purple);
			return new PlayerObject(chips, id, userId);
		} catch (SQLException e) {
			return null;
		}
	}
	
	public PotObject getPotById(String id) {
		try {
			Statement myStatement = myConn.createStatement();
			ResultSet rs = myStatement.executeQuery("select * from " + id);
			double red = 0, blue = 0, green = 0, black = 0, purple = 0;
			double[] chipValues = new double[5];
			
			while(rs.next()) {
				red = rs.getDouble(2);
				blue = rs.getDouble(3);
				green = rs.getDouble(4);
				black = rs.getDouble(5);
				purple = rs.getDouble(6);
				
				try {
					chipValues[0] = rs.getDouble("chip0Value");
					chipValues[1] = rs.getDouble("chip1Value");
					chipValues[2] = rs.getDouble("chip2Value");
					chipValues[3] = rs.getDouble("chip3Value");
					chipValues[4] = rs.getDouble("chip4Value");
				} catch(SQLException e) {
					chipValues[0] = 1;
					chipValues[1] = 5;
					chipValues[2] = 10;
					chipValues[3] = 25;
					chipValues[4] = 100;
				}
			}
			
			Chip chips = new Chip(red, blue, green, black, purple);
			return new PotObject(id, chips, chipValues);
		} catch (SQLException e) {
			return null;
		}
	}
	
	public void createUser(UserObject u) {
		//System.out.println("User created: " + u.getId());
		
		try {
			Statement myStatement = myConn.createStatement();
			myStatement.executeUpdate("create table " + u.getId() + " (id int not null auto_increment, games longtext null, name longtext null, primary key(id))");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addNameToUser(UserObject u, String name) {
		try {
			Statement myStatement = myConn.createStatement();
			myStatement.executeUpdate("insert into " + u.getId() + " (name) values ('" + name + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void changeUsername(UserObject u, String name) {
		try {
			Statement myStatement = myConn.createStatement();
			myStatement.executeUpdate("delete from " + u.getId() + " where name=('" + u.getName() + "')");
			
			Statement myStatement2 = myConn.createStatement();
			myStatement2.executeUpdate("insert into " + u.getId() + " (name) values ('" + name + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addGameToUser(GameObject g, UserObject u) {
		try {
			Statement myStatement = myConn.createStatement();
			myStatement.executeUpdate("insert into " + u.getId() + " (games) values ('" + g.getId() + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createGame(GameObject g) {
		try {
			Statement myStatement = myConn.createStatement();
			myStatement.executeUpdate("create table " + g.getId() + " (id int not null auto_increment, players longtext null, pot longtext null, name longtext null, red longtext null, blue longtext null, green longtext null, black longtext null, purple longtext null, primary key(id))");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addPlayerToGame(PlayerObject p, GameObject g) {
		try {
			Statement myStatement = myConn.createStatement();
			ResultSet rs = myStatement.executeQuery("select * from " + g.getId());
			while(rs.next()) {
				//System.out.println("S" + rs.getString(1) + rs.getString(2) + rs.getString(3) + rs.getString(4) + "E");
			}
			String query = ("insert into " + g.getId() + " (players) values ('" + p.getId() + "')");//, ?, ?)");
			Statement myStatement2 = myConn.createStatement();
			myStatement2.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addPotToGame(PotObject p, GameObject g, String name) {
		try {
			Statement myStatement = myConn.createStatement();
			myStatement.executeUpdate("insert into " + g.getId() + " (pot,name,red,blue,green,black,purple) values ('" + p.getId() + "','" + name + "'," + g.getDefaultChips().getRed() + "," + g.getDefaultChips().getBlue() + "," + g.getDefaultChips().getGreen() + "," + g.getDefaultChips().getBlack() + "," + g.getDefaultChips().getPurple() + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removePlayerFromGame(PlayerObject p, GameObject g) {
		try {
			Statement myStatement = myConn.createStatement();
			myStatement.executeUpdate("delete from " + g.getId() + " where players=('" + p.getId() + "')");
			Statement myStatement2 = myConn.createStatement();
			myStatement2.executeUpdate("drop table if exists " + p.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeGameFromUser(GameObject g, UserObject u) {
		try {
			Statement myStatement = myConn.createStatement();
			myStatement.executeUpdate("delete from " + u.getId() + " where games=('" + g.getId() + "')");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createPlayer(PlayerObject p) {
		try {
			Statement myStatement = myConn.createStatement();
			myStatement.executeUpdate("create table " + p.getId() + " (id int not null auto_increment, red double null, blue double null, green double null, black double null, purple double null, userId longtext null, primary key(id))");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createPot(PotObject p) {
		try {
			Statement myStatement = myConn.createStatement();
			myStatement.executeUpdate("create table " + p.getId() + " (id int not null auto_increment, red double null, blue double null, green double null, black double null, purple double null, chip0Value double null, chip1Value double null, chip2Value double null, chip3Value double null, chip4Value double null, primary key(id))");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updatePlayerInDatabase(PlayerObject p) {
		try {
			Statement myStatement = myConn.createStatement();
			ResultSet rs = myStatement.executeQuery("select * from " + p.getId());
			
			while(rs.next()) {
				myConn.createStatement().executeUpdate("delete from " + p.getId() + " where id=" + rs.getInt(1));
			}
			
			myConn.createStatement().executeUpdate("insert into " + p.getId() + " (red, blue, green, black, purple, userId) values (" + p.getChipObject().getRed() + "," + p.getChipObject().getBlue() + "," + p.getChipObject().getGreen() + "," + p.getChipObject().getBlack() + "," + p.getChipObject().getPurple() + ",'" + p.getUserId() + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updatePotInDatabase(PotObject p) {
		try {
			Statement myStatement = myConn.createStatement();
			ResultSet rs = myStatement.executeQuery("select * from " + p.getId());
			boolean hasChipValues = true;
			
			while(rs.next()) {
				myConn.createStatement().executeUpdate("delete from " + p.getId() + " where id = " + rs.getInt(1));
				
				try {
					double a = rs.getDouble("chip0Value");
				} catch (SQLException e) {
					hasChipValues = false;
				}
			}
			
			String statementString = "insert into " + p.getId() + " (red, blue, green, black, purple) values (" + p.getChipObject().getRed() + "," + p.getChipObject().getBlue() + "," + p.getChipObject().getGreen() + "," + p.getChipObject().getBlack() + "," + p.getChipObject().getPurple() + ")";
			if(hasChipValues) {
				double[] chipValues = p.getChipValues();
				statementString = "insert into " + p.getId() + " (red, blue, green, black, purple, chip0Value, chip1Value, chip2Value, chip3Value, chip4Value) values (" + p.getChipObject().getRed() + "," + p.getChipObject().getBlue() + "," + p.getChipObject().getGreen() + "," + p.getChipObject().getBlack() + "," + p.getChipObject().getPurple() + "," + chipValues[0] + "," + chipValues[1] + "," + chipValues[2] + "," + chipValues[3] + "," + chipValues[4] + ")";
			}
			
			myConn.createStatement().executeUpdate(statementString);
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
