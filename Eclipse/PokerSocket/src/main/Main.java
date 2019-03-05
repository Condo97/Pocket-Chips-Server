package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import objects.*;

public class Main {
	private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	private static ArrayList<UserObject> loggedInUsers = new ArrayList<UserObject>();
	private static ArrayList<AuthenticationObject> aca = new ArrayList<AuthenticationObject>();
	
	private static boolean debug;
    //private static ArrayList<String> userList = new ArrayList<String>();
    //private static ArrayList<UserObject> registry = new ArrayList<UserObject>();
    
	public static void main(String[] args) throws IOException {
		debug = false;

		try {
			Protocol.start();

			if (args.length != 1) {
				System.err.println("Usage: java Main <port number>");
				System.exit(1);
			}

			int port = Integer.parseInt(args[0]);

			ServerSocket listener = new ServerSocket(port, 100);
			try {
				while (true) {
					new Handler(listener.accept()).start();
				}
			} finally {
				listener.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static class Handler extends Thread {
	    private String name;
	    private Socket socket;
	    private BufferedReader in;
	    private PrintWriter out;

	    public Handler(Socket socket) {
	        this.socket = socket;
	    }

		private void loginWithID(String userID) {
			//Login
			try {
				Date now = new java.util.Date();
				UserObject u = Protocol.getUserById(userID, out);

				if (u != null) {
					for (int i = 0; i < loggedInUsers.size(); i++) {
						if (loggedInUsers.get(i).getId().equals(u.getId()))
							loggedInUsers.remove(i);
					}

					loggedInUsers.add(u);
				}

				out.println("na:" + u.getName());

				System.out.print(now + " User: " + u.getName() + " logged in! | ");
				System.out.println(loggedInUsers.size() + " person(s) logged in!");

				//MSG format - msg:title:theMessage:doneActionTitle
				//out.println("msg:Test:This is a test message.:Done?");

				//UMSG (update message) format - umsg:title:theMessage:required[0 - no, 1 - yes]:minBuildVersion
				//out.println("umsg:Update!:A new update is required! Please update before using the app.:1:34");
//	        				out.println("umsg:Update:A new update is required! Please update before using the app.:1:37");
				out.println("umsg:Update:A new update is required! Please update before using the app.:0:55");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		private void createUser(String name) {
			//Create User
			try {
				UserObject u = Protocol.newUser(out, name);
				String output = "us:" + u.getId();
				System.out.println("User " + u.getName() + " created.");
				out.println(output);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void editUsername(String userID, String name) {
			//Edit Username
			try {
				UserObject u = Protocol.getUserById(userID, out);
				u.setName(name);
				Protocol.editUsername(u);
				out.println("na:" + name);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void getPokerGame(String gameID) {
			//Get All Players and Chips in Game
			try {
				PokerGameObject g = Protocol.getPokerGameByID(gameID);
				String output = "gg:";

				//for(UserObject u: loggedInUsers) {
				for (PokerPlayerObject p : g.getPlayers()) {
					Chip c = p.getChipObject();
					String temp = "|";

					String username = Protocol.getUsernameById(p.getUserId());
					temp += (username + ":");
					temp += (c.getRed() + ":");
					temp += (c.getBlue() + ":");
					temp += (c.getYellow() + ":");
					temp += (c.getGreen() + ":");
					temp += (c.getOrange() + ":");

					String loggedIn = "lo";

					for (UserObject u : loggedInUsers) {
						if (u.getId().equals(p.getUserId())) {
							if (u.getCurrentGame().getId().equals(g.getId())) {
								//System.out.println(username + " is LOGGED IN ");
								loggedIn = "li";
							}
						}
					}

					temp += loggedIn;

					output += temp;
				}

				String temp = "&";
				temp += (g.getChipValues()[0] + ":");
				temp += (g.getChipValues()[1] + ":");
				temp += (g.getChipValues()[2] + ":");
				temp += (g.getChipValues()[3] + ":");
				temp += (g.getChipValues()[4] + ":");

				output += temp;

				out.println(output);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void createPokerGame(String name, Chip defaultChips, double[] chipValues) {
			//Create Game
			try {
				PokerGameObject g = Protocol.newGame(name, defaultChips, chipValues);
				System.out.println("Game " + g.getName() + " created.");
				String output = "ga:" + g.getId();
				out.println(output);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public boolean verifyGame(String userID, String gameID) {
	    	//Verify game
			try {
				UserObject u = Protocol.getUserById(userID, out);

				if (u != null && Protocol.gameExists(gameID)) {
					out.println("vg:1");
					return true;
				}

				out.println("vg:0");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return false;
		}

		public void joinGame(String userID, String gameID) {
	    	//Join Game
			try {
				UserObject u = Protocol.getUserById(userID, out);

				if(!Protocol.gameExists(gameID)) throw new GameplayException("Game does not exist.", 40);

				PokerGameObject g = Protocol.getPokerGameByID(gameID);

				for (UserObject user : loggedInUsers) {
					if (user.getId().equals(u.getId())) {
						user.setCurrentGame(g);
					}
				}

				for (int i = 0; i < g.getPlayers().size(); i++) {
					if (g.getPlayers().get(i).getUserId().equals(u.getId())) {
						//System.out.println("If");
						PokerPlayerObject p = Protocol.getPokerPlayerById(g.getPlayers().get(i).getId());

						String output = "pl:" + p.getId() + ":" + g.getId() + ":" + p.getChipObject().getRed() + ":" + p.getChipObject().getBlue() + ":" + p.getChipObject().getYellow() + ":" + p.getChipObject().getGreen() + ":" + p.getChipObject().getOrange() + ":" + g.getPotChipObject().getRed() + ":" + g.getPotChipObject().getBlue() + ":" + g.getPotChipObject().getYellow() + ":" + g.getPotChipObject().getGreen() + ":" + g.getPotChipObject().getOrange();
						out.println(output);

						return;
					}
				}

				PokerPlayerObject p = Protocol.newPlayerInGame(userID, g);

				System.out.println("GETS PAST BREAK (add player to game)");

				p.addChips(g.getDefaultChips());
				Protocol.updatePlayerInDatabase(p);

				String output = "pl:" + p.getId() + ":" + g.getId() + ":" + p.getChipObject().getRed() + ":" + p.getChipObject().getBlue() + ":" + p.getChipObject().getYellow() + ":" + p.getChipObject().getGreen() + ":" + p.getChipObject().getOrange() + ":" + g.getPotChipObject().getRed() + ":" + g.getPotChipObject().getBlue() + ":" + g.getPotChipObject().getYellow() + ":" + g.getPotChipObject().getGreen() + ":" + g.getPotChipObject().getOrange();
				out.println(output);

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (GameplayException e) {
				String output = "plerror:";
				System.out.println(output);
				out.println(output);
				out.println("error:" + e.getDescription() + ":" + e.getErrorNumber());
			}
		}

		public void removePlayer(String userID, String gameID) {
			//Remove Player from Game
			try {
				UserObject u = Protocol.getUserById(userID, out);
				PokerGameObject g = Protocol.getPokerGameByID(gameID);
				Protocol.removeUserFromGame(u, g);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void betToPokerPot(String gameID, String playerID, Chip chips) {
			//Bet to Pot
			try {
				PokerGameObject g = Protocol.getPokerGameByID(gameID);
				if (g.getPlayers().size() <= 1) throw new GameplayException("Game has only one player.", 41);

				int count = 0;

				for (PokerPlayerObject player : g.getPlayers()) {
					for (UserObject user : loggedInUsers) {
						if (user.getId().equals(player.getUserId())) {
							if (user.getCurrentGame().getId().equals(g.getId())) {
								count++;
							}
						}
					}
				}

				if (count < g.getPlayers().size()) throw new GameplayException("Not all users are logged in.", 42);

				PokerPlayerObject p = Protocol.getPokerPlayerById(playerID);

				//System.out.println("b: " + chips.getBlue());

				Driver dr = new Driver();
				g.addToPot(chips); //Saved
				p.removeChips(chips); //Saved

				//System.out.println("r: " + g.getPotObject().getChipObject().getRed());

				dr.updatePotChips(g.getPotObject());
				dr.updatePlayerChips(p);
//		        				dr.updatePotInDatabase(g.getPotObject());
//		        				dr.updatePlayerInDatabase(p);

				String output = "bet:" + p.getChipObject().getRed() + ":" + p.getChipObject().getBlue() + ":" + p.getChipObject().getYellow() + ":" + p.getChipObject().getGreen() + ":" + p.getChipObject().getOrange();
				out.println(output);

				Chip potChips = g.getPotChipObject();

				for (PokerPlayerObject player : g.getPlayers()) {
					//System.out.println("First step!");
					for (UserObject user : loggedInUsers) {
						//System.out.println("Second step!");
						if (user.getId().equals(player.getUserId())) {
							//System.out.println("Third step!");
							output = "pot:" + potChips.getRed() + ":" + potChips.getBlue() + ":" + potChips.getYellow() + ":" + potChips.getGreen() + ":" + potChips.getOrange();
							user.getWriter().println(output);
						}
					}
				}

				dr.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (GameplayException e) {
	    		if(e.getErrorNumber() == 42) out.println("beterror:");
				out.println("error:" + e.getDescription() + ":" + e.getErrorNumber());
			}
		}

		private boolean allPlayersInGame(PokerGameObject g) {
	    	int count = 0;
	    	for(PokerPlayerObject p: g.getPlayers()) for(UserObject u: loggedInUsers) if(u.getId().equals(p.getUserId()) && u != null && u.getCurrentGame().equals(g)) count++;
	    	System.out.println(count);
	    	if(count < g.getPlayers().size()) return false;
	    	return true;
		}

		private boolean allPlayersInGame(BlackjackGameObject g) {
			int count = 0;
			for(BlackjackPlayerObject p: g.getPlayers()) for(UserObject u: loggedInUsers) if(u.getId().equals(p.getUserId()) && u != null && u.getCurrentGame().equals(g)) count++;

			if(count < g.getPlayers().size()) return false;
			return true;
		}

		private boolean playerIsInGame(PokerGameObject g, PokerPlayerObject p) {
	    	if(g.getPlayers().contains(p)) return true;
	    	return false;
		}

		private boolean playerIsInGame(BlackjackGameObject g, BlackjackPlayerObject p) {
			if(g.getPlayers().contains(p)) return true;
			return false;
		}

		private void authenticateWin(String playerID, String gameID) {
			//Authenticate Player Winning Pot
			try {
				PokerPlayerObject p = Protocol.getPokerPlayerById(playerID);
				PokerGameObject g = Protocol.getPokerGameByID(gameID);

				if(!playerIsInGame(g, p)) throw new GameplayException("You are not logged in!", 43);
				if(!allPlayersInGame(g)) throw new GameplayException("Not all players have logged in.", 42);

				UserObject u = null;
				for(UserObject user: loggedInUsers) if(user.getId().equals(p.getUserId())) u = user;
				if(u == null) throw new GameplayException("Login check failed. Please try restarting the app.", 69);

				AuthenticationObject a = new AuthenticationObject("win", p, g.getId());
				for(PokerPlayerObject player: g.getPlayers()) {
					if(!p.getId().equals(player.getId())) {
						UserObject user = null;
						for(UserObject userObject: loggedInUsers) if(userObject.getId().equals(player.getUserId())) user = userObject;
						if(u == null) throw new GameplayException("Login check failed. Please try restarting the app.", 69);

						a.addPlayer(player);

						String output = "wa:" + a.getId() + ":" + u.getName();
						user.getWriter().println(output);
					}
				}

				for (int i = 0; i < aca.size(); i++) {
					if (aca.get(i).getMainPlayer().getId().equals(playerID) && aca.get(i).getType().equals("win") && aca.get(i).getGameID().equals(gameID)) {
						aca.remove(i);
					}
				}

				aca.add(a);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (GameplayException e) {
				if(e.getErrorNumber() == 42) out.println("waerror:");
				if(e.getErrorNumber() == 69) e.printStackTrace();
				out.println("error:" + e.getDescription() + ":" + e.getErrorNumber());
			}
		}

		private void authenticateWinAccept(String playerID, String gameID) {
			try {
				PokerPlayerObject p = Protocol.getPokerPlayerById(playerID);
				PokerGameObject g = Protocol.getPokerGameByID(gameID);
				AuthenticationObject a = null;

				for(AuthenticationObject auth: aca) {
					if(auth.getType().equals("win") && auth.getPlayers().contains(p) && auth.getGameID().equals(gameID)) {
						a = auth;
					}
				}

				if(a == null) throw new GameplayException("You have already authenticated the player's win.", 44);

				a.removePlayer(p);

				if(a.getPlayers().size() == 0) {
					for(UserObject u: loggedInUsers) {
						if(u.getId().equals(p.getUserId())) {
							Chip c = g.getPotChipObject();

							p.addChips(c);
							g.removeFromPot(c);

							Protocol.updatePlayerInDatabase(p);
							Protocol.updatePotInDatabase(g.getPotObject());

							String output = "ch:" + c.getRed() + ":" + c.getBlue() + ":" + c.getYellow() + ":" + c.getGreen() + ":" + c.getOrange();
							u.getWriter().println(output);
						}

						for(PokerPlayerObject player: g.getPlayers()) if(u.getId().equals(player.getUserId())) u.getWriter().println("rp:");
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (GameplayException e) {
				out.println("error:" + e.getDescription() + ":" + e.getErrorNumber());
			}
		}

		private void authenticateWinDecline(String playerID, String gameID, String authenticationID) {
			//Player declined win
			try {
				PokerPlayerObject p = Protocol.getPokerPlayerById(playerID);

				UserObject u = null;
				for(UserObject user: loggedInUsers) if(user.getId().equals(p.getUserId())) u = user;
				if(u == null) throw new GameplayException("Login check failed. Please try restarting the app.", 69);

				AuthenticationObject a = null;
				for(AuthenticationObject auth: aca) {
					if(auth.getType().equals("win") && auth.getPlayers().contains(p) && auth.getGameID().equals(gameID)) {
						a = auth;
					}
				}

				if(a == null) throw new GameplayException("You have already authenticated the player's win.", 44);

				for(UserObject user: loggedInUsers) {
					if(user.getId().equals(a.getMainPlayer().getUserId())) {
						user.getWriter().println("wapd:" + u.getName());
						aca.remove(a);
						break;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (GameplayException e) {
				out.println("error:" + e.getDescription() + ":" + e.getErrorNumber());
			}
		}

		private void getChipsForPlayer(String playerID) {
			//Get Chips for Player
			try {
				PokerPlayerObject p = Protocol.getPokerPlayerById(playerID);
				Chip c = p.getChipObject();

				String output = "ch:" + c.getRed() + ":" + c.getBlue() + ":" + c.getYellow() + ":" + c.getGreen() + ":" + c.getOrange();
				out.println(output);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		private void authenticateChipAddition(String playerID, String gameID, Chip c) {
			//Authenticate Adding Chips
			try {
				PokerPlayerObject p = Protocol.getPokerPlayerById(playerID);
				PokerGameObject g = Protocol.getPokerGameByID(gameID);

				if(!playerIsInGame(g, p)) throw new GameplayException("You are not logged in!", 43);
				if(!allPlayersInGame(g)) throw new GameplayException("Not all players have logged in.", 42);

				UserObject u = null;
				for(UserObject user: loggedInUsers) if(user.getId().equals(p.getUserId())) u = user;
				if(u == null) throw new GameplayException("Login check failed. Please try restarting the app.", 69);

				AuthenticationObject a = new AuthenticationObject("aca", p, c, g.getId());
				for(PokerPlayerObject player: g.getPlayers()) {
					if(!p.getId().equals(player.getId())) {
						UserObject user = null;
						for(UserObject userObject: loggedInUsers) if(userObject.getId().equals(player.getUserId())) user = userObject;
						if(u == null) throw new GameplayException("Login check failed. Please try restarting the app.", 69);
						a.addPlayer(player);
						String properChipsString = "";
						ArrayList<String> chipsStringArray = new ArrayList<String>();

						if (c.getRed() != 0)
							chipsStringArray.add(c.getRed() + " red");
						if (c.getBlue() != 0)
							chipsStringArray.add(c.getBlue() + " blue");
						if (c.getYellow() != 0)
							chipsStringArray.add(c.getYellow() + " green");
						if (c.getGreen() != 0)
							chipsStringArray.add(c.getGreen() + " black");
						if (c.getOrange() != 0)
							chipsStringArray.add(c.getOrange() + " purple");

						for (int i = 0; i < chipsStringArray.size(); i++) {
							properChipsString += chipsStringArray.get(i);

							if (i == chipsStringArray.size())
								properChipsString += ". and ";
							else if (i == chipsStringArray.size() - 1)
								properChipsString += ".";
							else
								properChipsString += ", ";
						}

						String output = "aca:" + a.getId() + ":" + u.getName() + ":" + properChipsString;
						user.getWriter().println(output);
					}
				}

				for (int i = 0; i < aca.size(); i++) {
					if (aca.get(i).getMainPlayer().getId().equals(playerID) && aca.get(i).getType().equals("aca") && aca.get(i).getGameID().equals(gameID)) {
						aca.remove(i);
					}
				}

				aca.add(a);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (GameplayException e) {
				if(e.getErrorNumber() == 42) out.println("waerror:");
				out.println("error:" + e.getDescription() + ":" + e.getErrorNumber());
			}
		}

		private void authenticateChipAdditionAccept(String playerID) {
	    	try {
	    		PokerGameObject g = Protocol.getPokerGameByPlayerID(playerID);
	    		authenticateChipAdditionAccept(playerID, g.getId());
			} catch (SQLException e) {
	    		e.printStackTrace();
			}
		}

		private void authenticateChipAdditionAccept(String playerID, String gameID) {
			//Player Authenticates Adding Chips
			try {
				PokerPlayerObject p = Protocol.getPokerPlayerById(playerID);
				PokerGameObject g = Protocol.getPokerGameByID(gameID);
				AuthenticationObject a = null;

				for(AuthenticationObject auth: aca) {
					if(auth.getType().equals("aca") && auth.getPlayers().contains(p) && auth.getGameID().equals(gameID)) {
						a = auth;
					}
				}

				if(a == null) throw new GameplayException("You have already authenticated the player's chip addition.", 44);

				a.removePlayer(p);

				if(a.getPlayers().size() == 0) {
					for(UserObject u: loggedInUsers) {
						if(u.getId().equals(p.getUserId())) {
							Chip c = a.getChipObject();

							a.getMainPlayer().addChips(c);
							Protocol.updatePlayerInDatabase(a.getMainPlayer());

							String output = "ch:" + c.getRed() + ":" + c.getBlue() + ":" + c.getYellow() + ":" + c.getGreen() + ":" + c.getOrange();
							u.getWriter().println(output);
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (GameplayException e) {
				out.println("error:" + e.getDescription() + ":" + e.getErrorNumber());
			}
		}

		private void authenticateChipAdditionDecline(String playerID, String gameID, String authenticationID) {
			//Player declined adding chips
			try {
				PokerPlayerObject p = Protocol.getPokerPlayerById(playerID);

				UserObject u = null;
				for(UserObject user: loggedInUsers) if(user.getId().equals(p.getUserId())) u = user;
				if(u == null) throw new GameplayException("Login check failed. Please try restarting the app.", 69);

				AuthenticationObject a = null;
				for(AuthenticationObject auth: aca) {
					if(auth.getType().equals("aca") && auth.getPlayers().contains(p) && auth.getGameID().equals(gameID)) {
						a = auth;
					}
				}

				if(a == null) throw new GameplayException("You have already authenticated the player's chip addition.", 44);

				for(UserObject user: loggedInUsers) {
					if(user.getId().equals(a.getMainPlayer().getUserId())) {
						user.getWriter().println("acapd:" + u.getName());
						aca.remove(a);
						break;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (GameplayException e) {
				out.println("error:" + e.getDescription() + ":" + e.getErrorNumber());
			}
		}

		private void removeChipsFromPlayer(String playerID, Chip c) {
			//Remove Chips from Player
			try {
				PokerPlayerObject p = Protocol.getPokerPlayerById(playerID);
				p.removeChips(c);
				Protocol.updatePlayerInDatabase(p);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		private void getPokerGameOnLogin(String gameID) {
			//Get players in game and chips
			try {
				PokerGameObject g = Protocol.getPokerGameByID(gameID);
				String output = "na:" + g.getName();
				out.println(output);

				//NEED TO ADD FEATURE TO SEND GG TO PERSON AT LOGIN
				//Send gglog to players
				//Get All Players and Chips in Game

				String gglog = "gglog:";
				for (PokerPlayerObject player : g.getPlayers()) {
					Chip c = player.getChipObject();
					String temp = "|";

					String username = Protocol.getUsernameById(player.getUserId());
					temp += (username + ":");
					temp += (c.getRed() + ":");
					temp += (c.getBlue() + ":");
					temp += (c.getYellow() + ":");
					temp += (c.getGreen() + ":");
					temp += (c.getOrange() + ":");

					String loggedIn = "lo";

					for (UserObject user : loggedInUsers) {
						if (user.getId().equals(player.getUserId())) {
							if (user.getCurrentGame().getId().equals(g.getId())) {
								loggedIn = "li";
							}
						}
					}

					temp += loggedIn;
					gglog += temp;
				}

				String temp = "&";
				temp += (g.getChipValues()[0] + ":");
				temp += (g.getChipValues()[1] + ":");
				temp += (g.getChipValues()[2] + ":");
				temp += (g.getChipValues()[3] + ":");
				temp += (g.getChipValues()[4] + ":");

				gglog += temp;

				//Sends gglog to all players
				for (PokerPlayerObject player : g.getPlayers()) {
					for (UserObject user : loggedInUsers) {
						if (user.getId().equals(player.getUserId())) {
							if (user.getCurrentGame().getId().equals(g.getId())) {
								user.getWriter().println(gglog);
							}
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	    private void startGettingGames(String userID, int group) {
			//Start Getting Games for user
			try {
				UserObject u = Protocol.getUserById(userID, out);
				int exceedsUpperBounds = 0;

				if (group * 16 <= u.getGames().size()) {
					for (int i = (16 * group); i < u.getGames().size() && i < (16 * (group + 1)); i++) {
						String output = "ugs:" + u.getGames().get(i).getId() + ":" + u.getGames().get(i).getName();

						int count = 0;

						if (u.getGames().get(i) instanceof PokerGameObject) {
							for (int j = 0; j < ((PokerGameObject) u.getGames().get(i)).getPlayers().size() && count < 3; j++) {
								if (!((PokerGameObject) u.getGames().get(i)).getPlayers().get(j).getUserId().equals(u.getId())) {
									PokerPlayerObject player = ((PokerGameObject) u.getGames().get(i)).getPlayers().get(j);
									output += ":" + Protocol.getUsernameById(player.getUserId());
									count++;
								}
							}
						}

						if ((group + 1) * 16 >= u.getGames().size()) exceedsUpperBounds = 0;
						else exceedsUpperBounds = 1;

						out.println(output);
					}
				}

				//1 for there's more, 0 for no more
				out.println("uge:" + exceedsUpperBounds);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		private void logout(String userID) {
			//Logout
			for (int i = 0; i < loggedInUsers.size(); i++) {
				if (loggedInUsers.get(i).getId().equals(userID)) {
					Date now = new java.util.Date();
					System.out.println(now + " User " + loggedInUsers.get(i).getName() + " logged out.");
					loggedInUsers.remove(i);
				}
			}
		}

		private void listUsers() {
			//List Users
			System.out.println(loggedInUsers.size() + " Users:");

			for(int i = 0; i < loggedInUsers.size(); i++) {
				System.out.print(loggedInUsers.get(i).getName());
				if(i < loggedInUsers.size() - 1)
					System.out.print(", ");
			}

			System.out.println(".");
		}

		private void toggleDebug() {
			//Toggle debug mode
			if(debug) {
				System.out.println("Turning debug mode off...");
				debug = false;
			} else {
				System.out.println("Turning debug mode on!");
				debug = true;
			}
		}

	    public void run() {
	        try {
	            in = new BufferedReader(new InputStreamReader(
	                socket.getInputStream()));
	            out = new PrintWriter(socket.getOutputStream(), true);
	            writers.add(out);
	            
	            String inputLine, outputLine;
	            
	            while(true) {
					String input = in.readLine();
					if(input == null) {
						return;
				}
	            	
				int commandIndex = input.indexOf(":");
				String[] args;

				if(commandIndex != -1) {
					args = input.split(":");

					if(debug) {
						Date now = new java.util.Date();
						System.out.print(now + " Command: ");
						for(String arg: args) {
							System.out.print(arg + ", ");
						}
						System.out.println(".");
					}

					switch(args[0]) {
					//Login flow
					case "li": loginWithID(args[1]); break; //USAGE: li:UserID
					case "lo": logout(args[1]); break;	//USAGE lo:UserId
					case "reg": createUser(args[1]); break;	//USAGE: reg:name
					case "eun": editUsername(args[1], args[2]); break; //USAGE: eun:UserId:name

					//Common

					//Poker Flow
					case "gg": getPokerGame(args[1]); break; //USAGE: gg:GameId
					case "ng": {
						//USAGE: ng:name:red:blue:yellow:green:orange:chip0Value:chip1Value:chip2Value:chip3Value:chip4Value
						Chip c = new Chip(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
						double[] chipValues = new double[5];
						if (args.length == 12) for (int i = 0; i < chipValues.length; i++)
							chipValues[i] = Double.parseDouble(args[7 + i]);
						else {
							chipValues[0] = 1;
							chipValues[1] = 5;
							chipValues[2] = 10;
							chipValues[3] = 25;
							chipValues[4] = 100;
						}

						createPokerGame(args[1], c, chipValues);

						break;
					}
					case "vg": verifyGame(args[1], args[2]); break;	//USAGE: vg:UserId:GameId
					case "jg": joinGame(args[1], args[2]); break;	//USAGE: jg:UserId:GameId
					case "rp": removePlayer(args[1], args[2]); break;//USAGE: rp:UserId:GameId
					case "bet": {
						//USAGE: bet:GameId:PlayerId:red:blue:green:black:purple
						Chip c = new Chip(Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]), Double.parseDouble(args[7]));
						betToPokerPot(args[1], args[2], c);
						break;
					}
					case "wa": authenticateWin(args[1], args[2]); break;	//USAGE: wa:PlayerId:GameId
					case "wap": authenticateWinAccept(args[1], args[2]); break;	//USAGE: wap:PlayerId:GameId
					case "wapd": authenticateWinDecline(args[1], args[2], args[3]); break;	//USAGE: wapd:PlayerId:GameId:AuthenticationId
					case "gc": getChipsForPlayer(args[1]); break;	//USAGE gc:PlayerId
					case "aca": {
						//USAGE aca:PlayerId:GameId:red:blue:green:black:purple
						Chip c = new Chip(Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]), Double.parseDouble(args[7]));

						authenticateChipAddition(args[1], args[2], c);

						break;
					}
					case "acap": authenticateChipAdditionAccept(args[1]); break;	//USAGE acap:PlayerId
					case "acapd": authenticateChipAdditionDecline(args[1], args[2], args[3]);	//USAGE: acapd:PlayerId:GameId:AuthenticationId
					case "rc": {			//USAGE rc:PlayerId:red:blue:green:black:purple
						Chip c = new Chip(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));

						removeChipsFromPlayer(args[1], c);

						break;
					}
					case "na": getPokerGameOnLogin(args[1]); break;	//USAGE na:GameId
					case "ugs": startGettingGames(args[1], Integer.parseInt(args[2])); break;		//USAGE: ugs:UserID:GroupOf16

					case "lu": listUsers(); break;	//USAGE lu:
					case "db": toggleDebug(); break;	//USAGE db:
					}
				}
	        		
            }	
	        } catch (IOException e) {
	            System.out.println(e);
	        } finally {
	            // This client is going down!  Remove its name and its print
	            // writer from the sets, and close its socket.
	            if (name != null) {
	                names.remove(name);
	            }
	            if (out != null) {
	                writers.remove(out);
	            }
	            try {
	                socket.close();
	            } catch (IOException e) {
	            }
	        }
	    }
	}
}
	