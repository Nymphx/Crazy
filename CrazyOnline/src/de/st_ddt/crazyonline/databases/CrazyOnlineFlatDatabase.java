package de.st_ddt.crazyonline.databases;

import java.io.File;

import org.bukkit.configuration.ConfigurationSection;

import de.st_ddt.crazyonline.OnlinePlayerData;
import de.st_ddt.crazyutil.databases.FlatDatabase;

public class CrazyOnlineFlatDatabase extends FlatDatabase<OnlinePlayerData>
{

	public CrazyOnlineFlatDatabase(ConfigurationSection config, File file, String colName, String colFirstLogin, String colLastLogin, String colLastLogout, String colOnlineTime)
	{
		super(OnlinePlayerData.class, file, new String[] { colName, colFirstLogin, colLastLogin, colLastLogout, colOnlineTime });
	}
}
