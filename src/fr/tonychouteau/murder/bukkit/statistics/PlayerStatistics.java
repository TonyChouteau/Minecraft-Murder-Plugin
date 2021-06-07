package fr.tonychouteau.murder.bukkit.statistics;

// Own import

// Java Import

// Bukkit Import

public class PlayerStatistics {

	public int gameCount = 0;
	public int gameCountAsMurderer = 0;

	public int winAsMurderer = 0;
	public int winAsGuardian = 0;
	public int winAsInnocent = 0;

	public int totalPoints = 0;
	public int currentPoints = 0;

	public PlayerStatistics() {
	}

	public PlayerStatistics(String[] data) {
		gameCount = Integer.parseInt(data[1]);
		gameCountAsMurderer = Integer.parseInt(data[2]);
		winAsMurderer = Integer.parseInt(data[3]);
		winAsGuardian = Integer.parseInt(data[4]);
		winAsInnocent = Integer.parseInt(data[5]);
		totalPoints = Integer.parseInt(data[6]);
		currentPoints = Integer.parseInt(data[7]);
	}

	public String getStringSave() {
		return gameCount + ":" + gameCountAsMurderer + ":" + winAsMurderer + ":" + winAsGuardian + ":" + winAsInnocent
				+ ":" + totalPoints + ":" + currentPoints;
	}

	public String getDisplay() {
		return "=================================\nGame count : " + gameCount + "\nGame count as murderer : "
				+ gameCountAsMurderer + "\nWin as murderer : " + winAsMurderer + "\nWin as innocent : " + winAsInnocent
				+ "\nWin as guardian : " + winAsGuardian + "\nCurrent points : " + currentPoints + "\nTotal points : "
				+ totalPoints + "\n=================================";
	}

	public void addPoints(int points) {
		totalPoints += points;
		currentPoints += points;
	}

	public void clearPoints() {
		currentPoints = 0;
	}
}