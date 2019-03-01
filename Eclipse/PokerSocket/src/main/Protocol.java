package main;

import java.io.PrintWriter;
import java.sql.SQLException;

import objects.*;

public class Protocol {
	private static Driver dr;// = new Driver();
	
	public static void start() throws SQLException, ClassNotFoundException {
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
	
	public static UserObject newUser(PrintWriter writer, String name) throws SQLException {
		UserObject uo = new UserObject(writer, name);
		
		if(dr.userExists(uo.getId()))
			return newUser(writer, name);
		
		dr.createUser(uo);
		
		return uo;
	}
	
	public static void editUsername(UserObject u) throws SQLException {
		dr.setUsername(u);
	}
	
	public static PokerGameObject newGame(String name, Chip c, double[] chipValues) throws SQLException {
		PokerGameObject go = new PokerGameObject(name, c, chipValues);
		PotObject pot = new PotObject();
		
		if(dr.gameExists(go.getId()) || dr.potExists(pot.getId()))
			return newGame(name, c, chipValues);

		go.setPot(pot);
		dr.createGame(go);
		dr.createPotInGame(pot, go);

		return go;
	}

	public static boolean gameExists(String gameID) throws SQLException {
		return dr.gameExists(gameID);
	}
	
	public static PokerPlayerObject newPlayerInGame(String userId, PokerGameObject game) throws SQLException {
		PokerPlayerObject po = new PokerPlayerObject(userId);
		
		if(dr.playerExists(po.getId()))
			return newPlayerInGame(userId, game);
		
		dr.createPlayerInGame(po, game);
		
		return po;
	} //HERE V
	
	public static UserObject getUserById(String id, PrintWriter writer) throws SQLException {
		UserObject r = dr.getUserById(id, writer);
		return r;
	}
	
	public static String getUsernameById(String id) throws SQLException {
		return dr.getUsernameById(id);
	}
	
	public static PokerGameObject getPokerGameByID(String id) throws SQLException {
		PokerGameObject r = dr.getPokerGameByID(id);
		return r;
	}
	
	public static PokerPlayerObject getPokerPlayerById(String id) throws SQLException {
		PokerPlayerObject r = dr.getPlayerById(id);
		return r;
	}
	
	public static void removeUserFromGame(UserObject u, GameObject g) throws SQLException {
		dr.removeUserFromGame(u, g);
	}
	
	public static void updatePlayerInDatabase(PokerPlayerObject p) throws SQLException {
		dr.updatePlayerChips(p);
	}

	//Blackjack Game
	public static BlackjackGameObject getBlackjackGameByID(String id) throws SQLException {
		BlackjackGameObject r = dr.getBlackjackGameByID(id);
		return r;
	}

	public static BlackjackPlayerObject getBlackjackPlayerByID(String id) throws SQLException {
		BlackjackPlayerObject r = dr.getBlackjackPlayerByID(id);
		return r;
	}

	public static BlackjackGameObject newGame(String name, Chip defaultChips, double[] chipValues, double ratio) throws SQLException {
		BlackjackGameObject g = new BlackjackGameObject(name, defaultChips, chipValues, ratio);
		if(dr.getBlackjackGameByID(g.getId()) != null) return newGame(name, defaultChips, chipValues, ratio);

		dr.createBlackjackGame(g);

		return g;
	}

	public static BlackjackPlayerObject newPlayerInGame(String userID, boolean isDealer, BlackjackGameObject g) throws SQLException {
		BlackjackPlayerObject p = new BlackjackPlayerObject(userID, isDealer);
		if(dr.getBlackjackPlayerByID(p.getId()) != null) return newPlayerInGame(userID, isDealer, g);

		dr.createBlackjackPlayerInGame(p, g);

		return p;
	}

	public static void updatePlayerInDatabase(BlackjackPlayerObject p) throws SQLException {
		dr.updateBlackjackPlayer(p);
	}

	public static BlackjackPlayerObject blackjackBet(String playerID, Chip c) throws SQLException {
		BlackjackPlayerObject p = dr.getBlackjackPlayerByID(playerID);
		p.addBetChips(c);
		p.removeChips(c);

		dr.updateBlackjackPlayer(p);

		return p;
	}

	public static BlackjackPlayerObject blackjackBust(String playerID) throws SQLException {
		BlackjackPlayerObject p = dr.getBlackjackPlayerByID(playerID);

		if(p.isDealer()) {
			BlackjackGameObject g = dr.getBlackjackGameByPlayerID(playerID);
			for(BlackjackPlayerObject pl: g.getPlayers()) {
				double red = pl.getBetChips().getRed() * 2 + pl.getSecondaryChips().getRed() * 2;
				double blue = pl.getBetChips().getBlue() * 2 + pl.getSecondaryChips().getBlue() * 2;
				double yellow = pl.getBetChips().getYellow() * 2 + pl.getSecondaryChips().getYellow() * 2;
				double green = pl.getBetChips().getGreen() * 2 + pl.getSecondaryChips().getGreen() * 2;
				double orange = pl.getBetChips().getOrange() * 2 + pl.getSecondaryChips().getOrange() * 2;

				pl.addChips(red, blue, yellow, green, orange);

				dr.updateBlackjackPlayer(pl);
			}

			return p;
		}

		p.clearBetChips();
		p.clearSecondaryBetChips();

		dr.updateBlackjackPlayer(p);

		return p;
	}

	public static BlackjackPlayerObject blackjackBlackjack(String playerID, String gameID, boolean secondary) throws SQLException {
		BlackjackPlayerObject p = dr.getBlackjackPlayerByID(playerID);
		BlackjackGameObject g = dr.getBlackjackGameByID(gameID);

		if(p.isDealer()) {
			for(BlackjackPlayerObject pl: g.getPlayers()) {
				if(pl.getOption() == 2) {
					double red = pl.getSecondaryChips().getRed() * 2;
					double blue = pl.getSecondaryChips().getBlue() * 2;
					double yellow = pl.getSecondaryChips().getYellow() * 2;
					double green = pl.getSecondaryChips().getGreen() * 2;
					double orange = pl.getSecondaryChips().getOrange() * 2;

					pl.addChips(red, blue, yellow, green, orange);
				}

				pl.clearBetChips();
				pl.clearSecondaryBetChips();

				dr.updateBlackjackPlayer(pl);
			}

			return p;
		}

		Chip c;
		if(secondary) {
			c = p.getSecondaryChips();
			p.clearSecondaryBetChips();
		} else {
			c = p.getBetChips();
			p.clearBetChips();
		}

		double red = c.getRed() * (1 + g.getRatio());
		double blue = c.getBlue() * (1 + g.getRatio());
		double yellow = c.getYellow() * (1 + g.getRatio());
		double green = c.getGreen() * (1 + g.getRatio());
		double orange = c.getOrange() * (1 + g.getRatio());

		p.addChips(red, blue, yellow, green, orange);

		dr.updateBlackjackPlayer(p);

		return p;
	}

	public static BlackjackPlayerObject blackjackWin(String playerID, boolean secondary) throws SQLException {
		BlackjackPlayerObject p = dr.getBlackjackPlayerByID(playerID);

		if(p.isDealer()) {
			BlackjackGameObject g = dr.getBlackjackGameByPlayerID(playerID);
			for(BlackjackPlayerObject pl: g.getPlayers()) {
				pl.clearBetChips();
				pl.clearSecondaryBetChips();

				dr.updateBlackjackPlayer(pl);
			}

			return p;
		}

		Chip c;
		if(secondary) {
			c = p.getSecondaryChips();
			p.clearSecondaryBetChips();
		} else {
			c = p.getBetChips();
			p.clearBetChips();
		}

		double red = c.getRed() * 2;
		double blue = c.getBlue() * 2;
		double yellow = c.getYellow() * 2;
		double green = c.getGreen() * 2;
		double orange = c.getOrange() * 2;

		p.addChips(red, blue, yellow, green, orange);

		dr.updateBlackjackPlayer(p);

		return p;
	}

	public static BlackjackPlayerObject playerDoubleDown(String playerID, boolean secondary) throws SQLException, GameplayException {
		BlackjackPlayerObject p = dr.getBlackjackPlayerByID(playerID);
		if(p.getOption() == 1) throw new GameplayException("You have already doubled down.", 10);
		else if(p.getOption() == 11 && !secondary) throw new GameplayException("You have already doubled down on their first hand.", 11);
		else if(p.getOption() == 12 && secondary) throw new GameplayException("You have already doubled down on their second hand.", 12);
		else if(p.getOption() == 2) throw new GameplayException("You have insurance and cannot double down.", 13);
		else if(p.getOption() < 10 && secondary) throw new GameplayException("You have not split.", 14);

		if(secondary && p.getOption() >= 10) {
			if(p.getOption() == 11) p.setOption(13);
			else p.setOption(12);

			Chip c = p.getSecondaryChips();
			p.addSecondaryBetChips(c);
			p.removeChips(c);
		} else {
			if(p.getOption() == 12) p.setOption(13);
			else if(p.getOption() == 10) p.setOption(11);
			else p.setOption(1);

			Chip c = p.getBetChips();
			p.addBetChips(c);
			p.removeChips(c);
		}

		dr.updateBlackjackPlayer(p);

		return p;
	}

	public static BlackjackPlayerObject playerSplit(String playerID) throws SQLException, GameplayException {
		BlackjackPlayerObject p = dr.getBlackjackPlayerByID(playerID);
		if(p.getOption() >= 10) throw new GameplayException("You have already split.", 20);
		else if(p.getOption() == 1) throw new GameplayException("You have doubled down.", 21);
		else if(p.getOption() == 2) throw new GameplayException("You have insurance.", 22);
		else if(p.getOption() != 0) throw new GameplayException("Unknown error occured. Please try again soon.", 69);

		p.setOption(10);

		Chip c = p.getBetChips();
		p.addSecondaryBetChips(c);
		p.removeChips(c);

		dr.updateBlackjackPlayer(p);

		return p;
	}

	public static BlackjackPlayerObject playerInsurance(String playerID, Chip c) throws  SQLException, GameplayException {
		BlackjackPlayerObject p = dr.getBlackjackPlayerByID(playerID);
		if(p.getOption() == 2) throw new GameplayException("You already have insurance.", 30);
		else if(p.getOption() >= 10) throw new GameplayException("You have split.", 31);
		else if(p.getOption() == 1) throw new GameplayException("You have doubled down.", 32);
		else if(p.getOption() != 0) throw new GameplayException("Unknown error occured. Please try again soon.", 69);

		p.setOption(2);

		p.addSecondaryBetChips(c);
		p.removeChips(c);

		dr.updateBlackjackPlayer(p);

		return p;
	}
}
