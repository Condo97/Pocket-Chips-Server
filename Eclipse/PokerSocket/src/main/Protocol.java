package main;

import java.io.PrintWriter;
import java.util.ArrayList;

import objects.Chip;
import objects.GameObject;
import objects.PlayerObject;
import objects.PotObject;
import objects.UserObject;

public class Protocol {
	private static Driver dr;// = new Driver();
	
	public static void start() {
		dr = new Driver();
	}
	
	public static String[] processInput(String in) {
		String output = null;
		String command = "";
		
		if (in == null) {
			System.out.println("THE INPUT WAS NULL");
		}
		
		int commandIndex = in.indexOf(":");
		if (commandIndex != -1) {
			command = in.substring(0, commandIndex);
			in = in.substring(commandIndex+1, in.length());
		}
		
		return new String[] {command, in};
	}
	
	public static UserObject newUser(PrintWriter writer, String name) {
		UserObject uo = new UserObject(writer, name);
		
		if(dr.existsById(uo.getId()))
			return newUser(writer, name);
		
		dr.createUser(uo);
		dr.addNameToUser(uo, name);
		
		return uo;
	}
	
	public static void editUsername(UserObject u, String name) {
		dr.changeUsername(u, name);
	}
	
	public static GameObject newGame(String name, Chip c, double[] chipValues) {
		GameObject go = new GameObject(name, c);
		PotObject pot = new PotObject(chipValues);
		
		if(dr.existsById(go.getId()))
			return newGame(name, c, chipValues);
		
		dr.createGame(go);
		dr.createPot(pot);
		dr.addPotToGame(pot, go, name);
		dr.updatePotInDatabase(pot);
		go.setPot(pot);
		
		return go;
	}
	
	public static PlayerObject newPlayer(String userId) {
		PlayerObject po = new PlayerObject(userId);
		
		if(dr.existsById(po.getId()))
			return newPlayer(userId);
		
		dr.createPlayer(po);
		dr.updatePlayerInDatabase(po);
		
		return po;
	}
	
	public static boolean existsById(String id) {
		boolean r = dr.existsById(id);
		return r;
	}
	
	public static UserObject getUserById(String id, PrintWriter writer) {
		UserObject r = dr.getUserById(id, writer);
		return r;
	}
	
	public static String getUsernameById(String id) {
		return dr.getUsernameById(id);
	}
	
	public static GameObject getGameById(String id) {
		GameObject r = dr.getGameById(id);
		return r;
	}
	
	public static PlayerObject getPlayerById(String id) {
		PlayerObject r = dr.getPlayerById(id);
		return r;
	}
	
	public static void addPlayerToGame(PlayerObject p, GameObject g, UserObject u) {
		dr.addPlayerToGame(p, g);
		dr.addGameToUser(g, u);
		dr.updatePlayerInDatabase(p);
	}
	
	public static void removeUserFromGame(UserObject u, GameObject g) {
		ArrayList<PlayerObject> players = g.getPlayers();
		
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getUserId().equals(u.getId())) {
				dr.removePlayerFromGame(players.get(i), g);
			}
		}
		
		dr.removeGameFromUser(g, u);
	}
	
	public static void updatePlayerInDatabase(PlayerObject p) {
		dr.updatePlayerInDatabase(p);
	}
}
