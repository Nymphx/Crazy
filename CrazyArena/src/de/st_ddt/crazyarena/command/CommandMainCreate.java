package de.st_ddt.crazyarena.command;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;

import de.st_ddt.crazyarena.CrazyArena;
import de.st_ddt.crazyarena.CrazyArenaPlugin;
import de.st_ddt.crazyarena.arenas.Arena;
import de.st_ddt.crazyarena.events.CrazyArenaArenaCreateEvent;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandAlreadyExistsException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandErrorException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandNoSuchException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandParameterException;
import de.st_ddt.crazyplugin.exceptions.CrazyCommandUsageException;
import de.st_ddt.crazyplugin.exceptions.CrazyException;
import de.st_ddt.crazyutil.modules.permissions.PermissionModule;
import de.st_ddt.crazyutil.source.Localized;

public class CommandMainCreate extends CommandExecutor
{

	public CommandMainCreate(final CrazyArena plugin)
	{
		super(plugin);
	}

	@Override
	@Localized({ "CRAZYARENA.COMMAND.ARENA.CREATED $Name$ $Type$", "CRAZYARENA.COMMAND.ARENA.SELECTED $Name$" })
	public void command(final CommandSender sender, final String[] args) throws CrazyException
	{
		if (args.length != 2)
			throw new CrazyCommandUsageException("<Name> <ArenaClass/Type>");
		final String name = args[0];
		if (plugin.getArenaByName(name) != null)
			throw new CrazyCommandAlreadyExistsException("Arena", name);
		final String type = args[1];
		@SuppressWarnings("rawtypes")
		Class<? extends Arena> clazz = CrazyArenaPlugin.getRegisteredArenaTypes().get(type.toLowerCase());
		if (clazz == null)
			try
			{
				clazz = Class.forName(type).asSubclass(Arena.class);
			}
			catch (final ClassNotFoundException e)
			{
				try
				{
					clazz = Class.forName("de.st_ddt.crazyarena.arenas." + type).asSubclass(Arena.class);
				}
				catch (final ClassNotFoundException e2)
				{
					throw new CrazyCommandNoSuchException("ArenaClass/Type", type);
				}
				catch (final ClassCastException e2)
				{
					throw new CrazyCommandParameterException(1, type, CrazyArenaPlugin.getRegisteredArenaTypes().keySet());
				}
			}
			catch (final ClassCastException e2)
			{
				throw new CrazyCommandParameterException(1, type, CrazyArenaPlugin.getRegisteredArenaTypes().keySet());
			}
		Arena<?> arena = null;
		try
		{
			arena = clazz.getConstructor(String.class).newInstance(name);
		}
		catch (final Exception e)
		{
			throw new CrazyCommandErrorException(e);
		}
		if (arena == null)
			throw new CrazyCommandException();
		plugin.getArenas().add(arena);
		plugin.getArenasByName().put(name.toLowerCase(), arena);
		if (!plugin.getArenasByType().containsKey(arena.getType().toLowerCase()))
			plugin.getArenasByType().put(arena.getType().toLowerCase(), new HashSet<Arena<?>>());
		plugin.getArenasByType().get(arena.getType().toLowerCase()).add(arena);
		plugin.sendLocaleMessage("COMMAND.ARENA.CREATED", sender, arena.getName(), arena.getType());
		new CrazyArenaArenaCreateEvent(arena, false).callEvent();
		plugin.getSelections().put(sender, arena);
		plugin.sendLocaleMessage("COMMAND.ARENA.SELECTED", sender, arena.getName());
		arena.saveToFile();
	}

	@Override
	public List<String> tab(final CommandSender sender, final String[] args)
	{
		if (args.length != 2)
			return null;
		final List<String> res = new LinkedList<String>();
		final String arg = args[1].toLowerCase();
		for (final String type : CrazyArenaPlugin.getRegisteredArenaTypes().keySet())
			if (type.startsWith(arg))
				res.add(type);
		return res;
	}

	@Override
	public boolean hasAccessPermission(final CommandSender sender)
	{
		return PermissionModule.hasPermission(sender, "crazyarena.arena.create");
	}
}
