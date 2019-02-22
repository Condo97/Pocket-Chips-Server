package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import objects.AuthenticationObject;
import objects.Chip;
import objects.GameObject;
import objects.PlayerObject;
import objects.UserObject;

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
	}
	
	private static class Handler extends Thread {
	    private String name;
	    private Socket socket;
	    private BufferedReader in;
	    private PrintWriter out; 

	    /**
	     * Constructs a handler thread, squirreling away the socket.
	     * All the interesting work is done in the run method.
	     */
	    public Handler(Socket socket) {
	        this.socket = socket;
	    }

	    /**
	     * Services this thread's client by repeatedly requesting a
	     * screen name until a unique one has been submitted, then
	     * acknowledges the name and registers the output stream for
	     * the client in a global set, then repeatedly gets inputs and
	     * broadcasts them.
	     */
	    public void run() {
	        try {
	            in = new BufferedReader(new InputStreamReader(
	                socket.getInputStream()));
	            out = new PrintWriter(socket.getOutputStream(), true);
	            writers.add(out);
	            
	            //Creates User
	            
	            
	            //Adds User to array
	            
	            String inputLine, outputLine;
	            
	            while(true) {
		            	String input = in.readLine();
		            	if(input == null) {
		            		return;
	            	}
	            	
	            	//.println(input);
	            	//Object[] theWriters = writers.toArray();
	            	
	            	//Register User
//	            	if(protocol.shouldRegisterNewUser(input)) {
//	            		UserObject newUser = new UserObject(out, protocol.getUserNumber(), protocol.getUserName());
//	            		registry.add(newUser);
//	            	}
	            	
	            	
	            	int commandIndex = input.indexOf(":");
	            	String[] args;
	            	
	        		if(commandIndex != -1) {
	        			//System.out.println("Input: " + input);
	        			args = input.split(":");
	        			//System.out.println("This is the command: " + args[0]);
	        			Date now = new java.util.Date();
	        			
	        			if(debug) {
		        			System.out.print(now + " Command: ");
		        			for(String arg: args) {
		        				System.out.print(arg + ", ");
		        			}
		        			System.out.println(".");
	        			}
	        			
	        			switch(args[0]) {
	        			case "li": {			//USAGE: li:UserId
	        				//Login
	        				UserObject u = Protocol.getUserById(args[1], out);
	        				
	        				if(u != null) {
	        					for(int i = 0; i < loggedInUsers.size(); i++) {
	        						if(loggedInUsers.get(i).getId().equals(u.getId()))
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

	        				break;
	        			}
	        			
	        			case "reg": {		//USAGE: reg:name
	        				//Create User
	        				UserObject u = Protocol.newUser(out, args[1]);
	        				String output = "us:" + u.getId();
	        				System.out.println("User " + u.getName() + " created.");
	        				out.println(output);
		    	            	
		    	            	break;
	        			}
	        			
	        			case "eun": {		//USAGE: eun:UserId:name
	        				//Edit Username
	        				UserObject u = Protocol.getUserById(args[1], out);
	        				Protocol.editUsername(u, args[2]);
	        				out.println("na:" + args[2]);
	        				
	        				break;
	        			}
	        			
	        			case "gg": {			//USAGE: gg:GameId
	        				//Get All Players and Chips in Game
	        				GameObject g = Protocol.getGameById(args[1]);
	        				String output = "gg:";
	        				
	        				//for(UserObject u: loggedInUsers) {
        					for(PlayerObject p: g.getPlayers()) {
    							Chip c = p.getChipObject();
    							String temp = "|";
    							
    							String username = Protocol.getUsernameById(p.getUserId());
    							temp += (username + ":");
								temp += (c.getRed() + ":");
								temp += (c.getBlue() + ":");
								temp += (c.getGreen() + ":");
								temp += (c.getBlack() + ":");
								temp += (c.getPurple() + ":");
								
								String loggedIn = "lo";
								
								for(UserObject u: loggedInUsers) {
									if(u.getId().equals(p.getUserId())) {
										if(u.getCurrentGame().getId().equals(g.getId())) {
											//System.out.println(username + " is LOGGED IN ");
											loggedIn = "li";
										}
									}
								}
								
								temp += loggedIn;
								
								output += temp;
        					}
        					
        					String temp = "&";
        					temp += (g.getPotObject().getChipValues()[0] + ":");
        					temp += (g.getPotObject().getChipValues()[1] + ":");
        					temp += (g.getPotObject().getChipValues()[2] + ":");
        					temp += (g.getPotObject().getChipValues()[3] + ":");
        					temp += (g.getPotObject().getChipValues()[4] + ":");
        					
        					output += temp;
	        				
	        				out.println(output);
	        				
	        				break;
	        			}
	        			
	        			case "ng": {			//USAGE: ng:name:red:blue:green:black:purple:chip0Value:chip1Value:chip2Value:chip3Value:chip4Value
	        				//Create Game
	        				Chip c = new Chip(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
	        				double[] chipValues = new double[5];
	        				if(args.length == 12) for(int i = 0; i < chipValues.length; i++) chipValues[i] = Double.parseDouble(args[7 + i]);
	        				else {
	        					chipValues[0] = 1;
	        					chipValues[1] = 5;
	        					chipValues[2] = 10;
	        					chipValues[3] = 25;
	        					chipValues[4] = 100;
	        				}
	        				
	        				GameObject g = Protocol.newGame(args[1], c, chipValues);
	        				System.out.println("Game " + g.getName() + " created.");
	        				String output = "ga:" + g.getId();
	        				out.println(output);
	        				
	        				break;
	        			}
	        			
	        			case "vg": {			//USAGE: vg:UserId:GameId
	        				//Verify Game
	        				String username = args[1];
	        				String gameID = args[2];
	        				
	        				UserObject u = Protocol.getUserById(username, out);
	        				
	        				if(u != null && Protocol.existsById(gameID)) out.println("vg:1");
	        				else out.println("vg:0");
	        				
	        				break;
	        			}
	        			
	        			case "jg": {			//USAGE: jg:UserId:GameId
	        				//Join Game 
        					UserObject u = Protocol.getUserById(args[1], out);
        					
        					if(Protocol.existsById(args[2])) {
		        				GameObject g = Protocol.getGameById(args[2]);
		        				PlayerObject p = Protocol.newPlayer(args[1]);
		        				boolean shouldBreak = false;
		        				
		        				//System.out.println("Size: " + g.getPlayers().size());
		        				
		        				for(UserObject user: loggedInUsers) {
		        					if(user.getId().equals(p.getUserId())) {
		        						user.setCurrentGame(g);
		        					}
		        				}
		        				
	//	        				if(g.getPlayers().size() == 0) {
	//	        					Protocol.addPlayerToGame(p, g);
	//	        					String output = "pl:" + p.getId() + ":" + g.getId();
	//		        				out.println(output);
	//	        					break;
	//	        				}
		        				
	//	        				Chip potChips = g.getPotChipObject();
	//	        				String output = "pot:" + potChips.getRed() + ":" + potChips.getBlue() + ":" + potChips.getGreen() + ":" + potChips.getBlack() + ":" + potChips.getPurple();
	//	        				out.println(output);
		        				
		        				for(int i = 0; i < g.getPlayers().size(); i++) {
		        					if(g.getPlayers().get(i).getUserId().equals(u.getId())) {
		        						//System.out.println("If");
		        						p = Protocol.getPlayerById(g.getPlayers().get(i).getId());
		        						
		        						String output = "pl:" + p.getId() + ":" + g.getId() + ":" + p.getChipObject().getRed() + ":" + p.getChipObject().getBlue() + ":" + p.getChipObject().getGreen() + ":" + p.getChipObject().getBlack() + ":" + p.getChipObject().getPurple() + ":" + g.getPotChipObject().getRed() + ":" + g.getPotChipObject().getBlue() + ":" + g.getPotChipObject().getGreen() + ":" + g.getPotChipObject().getBlack() + ":" + g.getPotChipObject().getPurple();
	    	        					out.println(output);
	    	        					
	    	        					shouldBreak = true;
	    	        					
	    	        					break;
		        					}
		        				}
		        				
		        				if(shouldBreak) break;
		        				
		        				System.out.println("GETS PAST BREAK (add player to game)");
		        				
		        				p.addChips(g.getDefaultChips());
		        				
		        				Protocol.addPlayerToGame(p, g, u);
		        				
		        				//System.out.println("User " + u.getName() + " added to game \n-> " + u.getId() + "\n-> " + g.getId());
		        				
		        				String output = "pl:" + p.getId() + ":" + g.getId() + ":" + p.getChipObject().getRed() + ":" + p.getChipObject().getBlue() + ":" + p.getChipObject().getGreen() + ":" + p.getChipObject().getBlack() + ":" + p.getChipObject().getPurple() + ":" + g.getPotChipObject().getRed() + ":" + g.getPotChipObject().getBlue() + ":" + g.getPotChipObject().getGreen() + ":" + g.getPotChipObject().getBlack() + ":" + g.getPotChipObject().getPurple();
		        				out.println(output);
		        				
		        				
        					} else {
        						String output = "plerror:";
        						System.out.println(output);
        						out.println(output);
        					}
	        				
	        				break;
	        			}
	        			
	        			case "rp": {			//USAGE: rp:UserId:GameId
	        				//Remove Player from Game
	        				UserObject u = Protocol.getUserById(args[1], out);
	        				GameObject g = Protocol.getGameById(args[2]);
	        				Protocol.removeUserFromGame(u, g);
	        					        				
	        				break;
	        			}
	        			
	        			case "bet": {		//USAGE: bet:GameId:PlayerId:red:blue:green:black:purple
	        				//Bet to Pot
	        				GameObject g = Protocol.getGameById(args[1]);
	        				if(g.getPlayers().size() <= 1) {
	        					break;
	        				}
	        				
	        				int count = 0;
	        				
	        				for(PlayerObject player: g.getPlayers()) {
	        					for(UserObject user: loggedInUsers) {
	        						if(user.getId().equals(player.getUserId())) {
        								if(user.getCurrentGame().getId().equals(g.getId())) {
        									count++;
        								}
	        						}
	        					}
	        				}
	        				
	        				if(count < g.getPlayers().size()) {
	        					out.println("beterror:");
		        			} else {
		        				PlayerObject p = Protocol.getPlayerById(args[2]);
		        				Chip chips = new Chip(Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]), Double.parseDouble(args[7]));
		        				
		        				//System.out.println("b: " + chips.getBlue());
		        				
		        				Driver dr = new Driver();
		        				g.addToPot(chips); //Saved
		        				p.removeChips(chips); //Saved
		        				
		        				//System.out.println("r: " + g.getPotObject().getChipObject().getRed());
		        				
		        				dr.updatePotInDatabase(g.getPotObject());
		        				dr.updatePlayerInDatabase(p);
		        				
		        				String output = "bet:" + p.getChipObject().getRed() + ":" + p.getChipObject().getBlue() + ":" + p.getChipObject().getGreen() + ":" + p.getChipObject().getBlack() + ":" + p.getChipObject().getPurple();
		        				out.println(output);
		        				
		        				Chip potChips = g.getPotChipObject();
		        				
		        				for(PlayerObject player: g.getPlayers()) {
		        					//System.out.println("First step!");
		        					for(UserObject user: loggedInUsers) {
		        						//System.out.println("Second step!");
		        						if(user.getId().equals(player.getUserId())) {
		        							//System.out.println("Third step!");
		        							output = "pot:" + potChips.getRed() + ":" + potChips.getBlue() + ":" + potChips.getGreen() + ":" + potChips.getBlack() + ":" + potChips.getPurple();
		        							user.getWriter().println(output);
		        						}
		        					}
		        				}
		        				
		        				dr.close();
		        			}
	        				
	        				break;
	        			}
	        			
	        			case "wa": {			//USAGE: wa:PlayerId:GameId
	        				//Authenticate Player Winning Pot
	        				PlayerObject p = Protocol.getPlayerById(args[1]);
	        				GameObject g = Protocol.getGameById(args[2]);
	        				AuthenticationObject a = new AuthenticationObject("win", p);
	        				String playerUsername = "";
	        				
	        				//if(loggedInUsers.size())
	        				int count = 0;
	        				for(PlayerObject player: g.getPlayers()) {
		        				for(UserObject user: loggedInUsers) {
		        					if(user.getId().equals(player.getUserId())) {
		        						if(user.getCurrentGame().getId().equals(g.getId())) {
		        							count++;
		        						}
		        					}
		        				}
	        				}
	        				
	        				if(count < g.getPlayers().size()) {
	        					out.println("waerror:");
	        				} else {	        					
		        				for(UserObject user: loggedInUsers) {
		        					if(user.getId().equals(p.getUserId())) {
		        						playerUsername = user.getName();
		        					}
		        				}
		        				
		        				for(PlayerObject player: g.getPlayers()) {
		        					for(UserObject user: loggedInUsers) {
		        						if(user.getId().equals(player.getUserId())) {
		        							if(!player.getId().equals(p.getId())) {
		        								if(user.getCurrentGame().getId().equals(g.getId())) {
			        								a.addPlayer(player);
				        							
				        							String output = "wa:" + a.getId() + ":" + playerUsername;
				        							user.getWriter().println(output);
		        								}
		        							}
		        						}
		        					}
		        				}
		        				
		        				for(int i = 0; i < aca.size(); i++) {
	        						if(aca.get(i).getMainPlayer().getId().equals(args[1])) {
	        							if(aca.get(i).getType().equals("win")) {
	        								aca.remove(i);
	        							}
	        						}
	        					}
		        				
		        				aca.add(a);
	        				}
	        				
	        				break;
	        			}
	        			
	        			case "wap": {		//USAGE: wap:PlayerId:GameId:AuthenticationObject
	        				PlayerObject p = Protocol.getPlayerById(args[1]);
	        				GameObject g = Protocol.getGameById(args[2]);
	        				
	        				//System.out.println("There are " + aca.size() + " AuthenticationObjects");
	        				
	        				for(AuthenticationObject a: aca) {
	        					if(a.getType().equals("win")) {
		        					for(int i = 0; i < a.getPlayers().size(); i++) {
		        						PlayerObject player = a.getPlayers().get(i);
			        					if(player.getId().equals(p.getId())) {
			        						a.removePlayer(player);
			        						
			        						//System.out.println("This is how many players are in object a: " + a.getPlayers().size());
			        						if(a.getPlayers().size() == 0) {
			        							for(UserObject user: loggedInUsers) {
				    	        						if(user.getId().equals(a.getMainPlayer().getUserId())) {
				    	        							Chip c = g.getPotChipObject().clone();
				    	        	        				
					    	        	        				Driver dr = new Driver();
					    	        	        				g.removeFromPot(c); //Saved
					    	        	        				a.getMainPlayer().addChips(c); //Saved
					    	        	        				
					    	        	        				dr.updatePotInDatabase(g.getPotObject());
					    	        	        				dr.updatePlayerInDatabase(a.getMainPlayer());
					    	        	        				
					    	        	        				String output = "ch:" + c.getRed() + ":" + c.getBlue() + ":" + c.getGreen() + ":" + c.getBlack() + ":" + c.getPurple();
					    	        	        				user.getWriter().println(output);
					    	        	        				
					    	        	        				for(PlayerObject player2: g.getPlayers()) {
					    	        	        					for(UserObject user2: loggedInUsers) {
					    	        	        						if(user2.getId().equals(player2.getUserId())) {
					    	        	        							output = "rp:";
					    	        	        							user2.getWriter().println(output);
					    	        	        						}
					    	        	        					}
					    	        	        				}
				    	        						}
				    	        					}
			        						}
			        					}
			        				}
		        				}
	        				}
	        				
	        				break;
	        			}
	        			
	        			case "wapd": {		//USAGE: wapd:PlayerId:GameId:AuthenticationId
	        				//Player declined win
	        				PlayerObject p = Protocol.getPlayerById(args[1]);
	        				
	        				String output = "wapd:";
	        				
	        				for(UserObject user: loggedInUsers) {
        						if(p.getUserId().equals(user.getId())) {
        							output += user.getName();
        						}
						}
						
	        				for(int i = 0; i < aca.size(); i++) {
	        					if(aca.get(i).getType().equals("win")) {
		        					for(UserObject user: loggedInUsers) {
		        						if(user.getId().equals(aca.get(i).getMainPlayer().getUserId())) {
		        							if(aca.get(i).getId().equals(args[3])) {
		        								user.getWriter().println(output);
		        								aca.remove(i);
		        								break;
		        							}
		        						}
								}
	        					}
	        				}
	        				
	        				//System.out.println("There are " + aca.size() + " AuthenticationObjects");
	        				
//	        				for(int i = 0; i < aca.size(); i++) {
//	        					if(aca.get(i).getType().equals("win")) {
//		        					for(int j = 0; j < aca.get(i).getPlayers().size(); j++) {
//		        						PlayerObject player = aca.get(i).getPlayers().get(j);
//			        					if(player.getId().equals(p.getId())) {
//			        						aca.remove(i);
//			        						break;
//			        					}
//		        					}
//	        					}
//	        				}
	        				
	        				break;
	        			}
	        			
	        			case "win": {		//USAGE: won:GameId:PlayerId
	        				//Player Won Pot
	        				GameObject g = Protocol.getGameById(args[1]);
	        				PlayerObject p = Protocol.getPlayerById(args[2]);
	        				Chip c = g.getPotChipObject().clone();
	        				
	        				Driver dr = new Driver();
	        				g.removeFromPot(c); //Saved
	        				p.addChips(c); //Saved
	        				
	        				dr.updatePotInDatabase(g.getPotObject());
	        				dr.updatePlayerInDatabase(p);
	        				
	        				String output = "ch:" + c.getRed() + ":" + c.getBlue() + ":" + c.getGreen() + ":" + c.getBlack() + ":" + c.getPurple();
	        				out.println(output);
	        				
	        				for(PlayerObject player: g.getPlayers()) {
	        					for(UserObject user: loggedInUsers) {
	        						if(user.getId().equals(player.getUserId())) {
	        							output = "rp:";
	        							user.getWriter().println(output);
	        						}
	        					}
	        				}
	        				
	        				break;
        				}
	        			
	        			case "gc": {			//USAGE gc:PlayerId
	        				//Get Chips for Player
	        				PlayerObject p = Protocol.getPlayerById(args[1]);
	        				Chip c = p.getChipObject();
	        				
	        				String output = "ch:" + c.getRed() + ":" + c.getBlue() + ":" + c.getGreen() + ":" + c.getBlack() + ":" + c.getPurple();
	        				out.println(output);
	        				
	        				break;
	        			}
	        			
	        			case "aca": {		//USAGE aca:PlayerId:GameId:red:blue:green:black:purple
	        				//Authenticate Adding Chips
	        				PlayerObject p = Protocol.getPlayerById(args[1]);
	        				GameObject g = Protocol.getGameById(args[2]);
	        				Chip c = new Chip(Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]), Double.parseDouble(args[7]));
	        				AuthenticationObject ao = new AuthenticationObject("aca", p, c);
						String playerUsername = "";
						
	        				for(UserObject user: loggedInUsers) {
	        					if(user.getId().equals(p.getUserId())) {
	        						playerUsername = user.getName();
	        					}
	        				}
	        				
	        				int count = 0;
	        				for(PlayerObject player: g.getPlayers()) {
		        				for(UserObject user: loggedInUsers) {
		        					if(user.getId().equals(player.getUserId())) {
		        						if(user.getCurrentGame().getId().equals(g.getId())) {
		        							count++;
		        						}
		        					}
		        				}
	        				}
	        				
	        				if(count < g.getPlayers().size()) {
	        					out.println("acaerror:");
	        				} else {
	        				//System.out.println("The size of G: " + g.getPlayers().size());
	        				
//	        				if(g.getPlayers().size() == 1 || count == 1) {
//	        					p.addChips(c);
//	        					Protocol.updatePlayerInDatabase(p);
//    	        				
//    							String output = "ch:" + c.getRed() + ":" + c.getBlue() + ":" + c.getGreen() + ":" + c.getBlack() + ":" + c.getPurple();
//    							out.println(output);
//    							
//    							//System.out.println(output);
//    							
//    							break;
//	        				}
	        				
		        				for(PlayerObject player: g.getPlayers()) {
		        					for(UserObject user: loggedInUsers) {
		        						if(user.getId().equals(player.getUserId())) {
		        							if(!player.getId().equals(p.getId())) {
		        								if(user.getCurrentGame().getId().equals(g.getId())) {
			        								ao.addPlayer(player);
				        							String properChipsString = "";
				        							ArrayList<String> chipsStringArray = new ArrayList<String>();
				        							
				        							if(c.getRed() != 0)
				        								chipsStringArray.add(c.getRed() + " red");
				        							if(c.getBlue() != 0)
				        								chipsStringArray.add(c.getBlue() + " blue");
				        							if(c.getGreen() != 0)
				        								chipsStringArray.add(c.getGreen() + " green");
				        							if(c.getBlack() != 0)
				        								chipsStringArray.add(c.getBlack() + " black");
				        							if(c.getPurple() != 0)
				        								chipsStringArray.add(c.getPurple() + " purple");
				        							
				        							for(int i = 0; i < chipsStringArray.size(); i++) {
				        								properChipsString += chipsStringArray.get(i);
				        								
				        								if(i == chipsStringArray.size())
				        									properChipsString += ". and ";
				        								else if(i == chipsStringArray.size() - 1)
				        									properChipsString += ".";
				        								else
				        									properChipsString += ", ";
				        							}
				        							
				        							String output = "aca:" + ao.getId() + ":" + playerUsername + ":" + properChipsString;
				        							user.getWriter().println(output);
		        								}
		        							}
		        						}
		        					}
		        				}
		        				
		        				for(int i = 0; i < aca.size(); i++) {
		        					if(aca.get(i).getMainPlayer().getId().equals(args[1])) {
		        						if(aca.get(i).getType().equals("aca")) {
		        							aca.remove(i);
		        						}
		        					}
		        				}
		        				
		        				aca.add(ao);
	        				}
	        				
	        				break;
	        			}
	        			
	        			case "acap": {		//USAGE acap:PlayerId:GameId:AuthenticationId
	        				//Player Authenticates Adding Chips
	        				PlayerObject p = Protocol.getPlayerById(args[1]);
	        				
	        				//System.out.println("There are " + aca.size() + " AuthenticationObjects");
	        				
	        				for(AuthenticationObject a: aca) {
	        					if(a.getType().equals("aca")) {
		        					for(int i = 0; i < a.getPlayers().size(); i++) {
		        						PlayerObject player = a.getPlayers().get(i);
			        					if(player.getId().equals(p.getId())) {
			        						a.removePlayer(player);
			        						
			        						//System.out.println("This is how many players are in object a: " + a.getPlayers().size());
			        						if(a.getPlayers().size() == 0) {
			        							for(UserObject user: loggedInUsers) {
				    	        						if(user.getId().equals(a.getMainPlayer().getUserId())) {
				    	        	        					Chip c = a.getChipObject();
					    	        	        				a.getMainPlayer().addChips(c);
					    	        	        				Protocol.updatePlayerInDatabase(a.getMainPlayer());
					    	        	        				
			    		        							String output = "ch:" + c.getRed() + ":" + c.getBlue() + ":" + c.getGreen() + ":" + c.getBlack() + ":" + c.getPurple();
			    		        							user.getWriter().println(output);
				    	        						}
				    	        					}
			        						}
			        					}
			        				}
		        				}
	        				}
	        				
	        				break;
	        			}
	        			
	        			case "acapd": {		//USAGE: acapd:PlayerId:GameId:AuthenticationId
	        				//Player declined adding chips
	        				PlayerObject p = Protocol.getPlayerById(args[1]);
	        				
	        				String output = "acapd:";
	        				
	        				for(UserObject user: loggedInUsers) {
        						if(p.getUserId().equals(user.getId())) {
        							output += user.getName();
        						}
						}
						
	        				for(int i = 0; i < aca.size(); i++) {
	        					if(aca.get(i).getType().equals("aca")) {
		        					for(UserObject user: loggedInUsers) {
		        						if(user.getId().equals(aca.get(i).getMainPlayer().getUserId())) {
		        							if(aca.get(i).getId().equals(args[3])) {
		        								user.getWriter().println(output);
		        								aca.remove(i);
		        								break;
		        							}
		        						}
								}
	        					}
	        				}
	        				
	        				//System.out.println("There are " + aca.size() + " AuthenticationObjects");
	        				
//	        				for(int i = 0; i < aca.size(); i++) {
//	        					if(aca.get(i).getType().equals("aca")) {
//	        						//System.out.println("ACA Size: " + aca.get(i).getPlayers().size());
//		        					for(int j = 0; j < aca.get(i).getPlayers().size(); j++) {
//		        						//System.out.println("j:" + j);
//		        						PlayerObject player = aca.get(i).getPlayers().get(j);
//		        						//System.out.println("PlayerObject Created");
//			        					if(player.getId().equals(p.getId())) {
//			        						//System.out.println("p.getId(): " + p.getId());
//			        						aca.remove(i);
//			        						break;
//			        					}
//		        					}
//	        					}
//	        				}
	        				
	        				break;
	        			}
	        			
	        			case "ac": {			//USAGE ac:PlayerId:red:blue:green:black:purple
	        				//Add Chips to Player
	        				PlayerObject p = Protocol.getPlayerById(args[1]);
	        				p.addChips(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
	        				Protocol.updatePlayerInDatabase(p);
	        				
	        				break;
	        			}
	        			
	        			case "rc": {			//USAGE rc:PlayerId:red:blue:green:black:purple
	        				//Remove Chips from Player
	        				PlayerObject p = Protocol.getPlayerById(args[1]);
	        				p.removeChips(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
	        				Protocol.updatePlayerInDatabase(p);
	        				
	        				break;
	        			}
	        			
	        			case "na": {			//USAGE na:GameId
	        				//Get game name
	        				GameObject g = Protocol.getGameById(args[1]);
	        				String output = "na:" + g.getName();
	        				out.println(output);
	        				
	        				//NEED TO ADD FEATURE TO SEND GG TO PERSON AT LOGIN
	        				//Send gglog to players
	        				//Get All Players and Chips in Game
	        				
	        				String gglog = "gglog:";
	        				for(PlayerObject player: g.getPlayers()) {
    							Chip c = player.getChipObject();
    							String temp = "|";
    							
    							String username = Protocol.getUsernameById(player.getUserId());
    							temp += (username + ":");
								temp += (c.getRed() + ":");
								temp += (c.getBlue() + ":");
								temp += (c.getGreen() + ":");
								temp += (c.getBlack() + ":");
								temp += (c.getPurple() + ":");
								
								String loggedIn = "lo";
								
								for(UserObject user: loggedInUsers) {
									if(user.getId().equals(player.getUserId())) {
										if(user.getCurrentGame().getId().equals(g.getId())) {
											loggedIn = "li";
										}
									}
								}
								
								temp += loggedIn;
								gglog += temp;
        					}
	        				
	        				String temp = "&";
        					temp += (g.getPotObject().getChipValues()[0] + ":");
        					temp += (g.getPotObject().getChipValues()[1] + ":");
        					temp += (g.getPotObject().getChipValues()[2] + ":");
        					temp += (g.getPotObject().getChipValues()[3] + ":");
        					temp += (g.getPotObject().getChipValues()[4] + ":");
        					
        					gglog += temp;
	        				
	        				//Sends gglog to all players
	        				for(PlayerObject player: g.getPlayers()) {
	        					for(UserObject user: loggedInUsers) {
	        						if(user.getId().equals(player.getUserId())) {
	        							if(user.getCurrentGame().getId().equals(g.getId())) {
	        								user.getWriter().println(gglog);
	        							}
	        						}
	        					}
	        				}
	        				
	        				break;
	        			}
	        			
	        			case "rg": {			//USAGE: rg:GameId
	        				//Recover Game
	        				UserObject u = Protocol.getUserById(args[1], out);
	        				
	        				break;
	        			}
	        			
	        			case "ug": {			//USAGE: ug:UserId
	        				//DEPRECATED, DELETE SOON
	        				//Get Games for user
	        				UserObject u = Protocol.getUserById(args[1], out);
	        				String output = "ug:";
	        				
	        				for(GameObject g: u.getGames()) {
	        					output += ("|" + g.getId() + ":" + g.getName());
	        				}
	        				
	        				out.println(output);
	        				
	        				break;
	        			}
	        			
	        			case "ugs": {		//USAGE: ugs:UserID:GroupOf16
	        				//Start Getting Games for user
	        				UserObject u = Protocol.getUserById(args[1], out);
	        				int group = Integer.parseInt(args[2]);
	        				int exceedsUpperBounds = 0;
	        				
	        				if(group * 16 <= u.getGames().size()) {
	        					if((group + 1) * 16 >= u.getGames().size()) {
	        						for(int i = (16 * group); i < u.getGames().size(); i++) {
	        							String output = "ugs:" + u.getGames().get(i).getId() + ":" + u.getGames().get(i).getName();
	        							
	        							int count = 0;
	        							for(int j = 0; j < u.getGames().get(i).getPlayers().size() && count < 3; j++) {
	        								if(!u.getGames().get(i).getPlayers().get(j).getUserId().equals(u.getId())) {
	        									PlayerObject player = u.getGames().get(i).getPlayers().get(j);
	        									output += ":" + Protocol.getUsernameById(player.getUserId()); 
	        									count++;
	        								}
	        							}
	        							
	        							out.println(output);
			        				}
	        					} else {
	        						exceedsUpperBounds = 1;
	        						
			        				for(int i = (16 * group); i < (16 * (group + 1)); i++) {
			        					out.println("ugs:" + u.getGames().get(i).getId() + ":" + u.getGames().get(i).getName());
			        				}
	        					}
	        				}
	        				
	        				//1 for there's more, 0 for no more
	        				out.println("uge:" + exceedsUpperBounds);
	        				
	        				break;
        				}
	        			
	        			case "lo": {			//USAGE lo:UserId
	        				//Logout
	        				
	        				for(int i = 0; i < loggedInUsers.size(); i++) {
	        					if(loggedInUsers.get(i).getId().equals(args[1])) {
	        						System.out.println(now + " User " + loggedInUsers.get(i).getName() + " logged out.");
	        						loggedInUsers.remove(i);
	        					}
	        				}
	        				
	        				break;
	        			}
	        			
	        			case "lu": {			//USAGE lu:
	        				//List Users
	        				System.out.println(loggedInUsers.size() + " Users:");
	        				
	        				for(int i = 0; i < loggedInUsers.size(); i++) {
	        					System.out.print(loggedInUsers.get(i).getName());
	        					if(i < loggedInUsers.size() - 1)
	        						 System.out.print(", ");
	        				}
	        				
	        				System.out.println(".");
	        				
	        				break;
	        			}
	        			
	        			case "db": {			//USAGE db:
	        				//Toggle debug mode
	        				if(debug) {
	        					System.out.println("Turning debug mode off...");
	        					debug = false;
	        				} else {
	        					System.out.println("Turning debug mode on!");
	        					debug = true;
	        				}
	        				
	        				break;
	        			}
	        			
//	        			case "
	        			
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
	