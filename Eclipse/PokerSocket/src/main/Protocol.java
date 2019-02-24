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
		
		if(dr.userExists(uo.getId()))
			return newUser(writer, name);
		
		dr.createUser(uo);
		
		return uo;
	}
	
	public static void editUsername(UserObject u) {
		dr.setUsername(u);
	}
	
	public static GameObject newGame(String name, Chip c, double[] chipValues) {
		GameObject go = new GameObject(name, c, chipValues);
		PotObject pot = new PotObject();
		
		if(dr.gameExists(go.getId()) || dr.potExists(pot.getId()))
			return newGame(name, c, chipValues);

		go.setPot(pot);
		dr.createGame(go);
		dr.createPotInGame(pot, go);

		return go;
	}

	public static boolean gameExists(String gameID) {
		return dr.gameExists(gameID);
	}
	
	public static PlayerObject newPlayerInGame(String userId, GameObject game) {
		PlayerObject po = new PlayerObject(userId);
		
		if(dr.playerExists(po.getId()))
			return newPlayerInGame(userId, game);
		
		dr.createPlayerInGame(po, game);
		
		return po;
	} //HERE V
	
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
	
//	public static void addPlayerToGame(PlayerObject p, GameObject g, UserObject u) {
//		dr.addPlayerToGame(p, g);
//		dr.addGameToUser(g, u);
//		dr.updatePlayerInDatabase(p);
//	}
	
	public static void removeUserFromGame(UserObject u, GameObject g) {
		dr.removeUserFromGame(u, g);
	}
	
	public static void updatePlayerInDatabase(PlayerObject p) {
		dr.updatePlayerChips(p);
	}
}
