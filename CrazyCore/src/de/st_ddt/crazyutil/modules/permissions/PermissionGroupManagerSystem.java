package de.st_ddt.crazyutil.modules.permissions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.st_ddt.crazyutil.ChatHelper;
import de.st_ddt.crazyutil.modules.Module.Named;
import de.st_ddt.crazyutil.modules.Module.PluginDepency;

@Named(name = "GroupManager")
@PluginDepency(depend = "GroupManager")
class PermissionGroupManagerSystem implements PermissionSystem
{

	private final GroupManager plugin;

	public PermissionGroupManagerSystem()
	{
		super();
		plugin = (GroupManager) Bukkit.getPluginManager().getPlugin("GroupManager");
		if (plugin == null)
			throw new IllegalArgumentException("GroupManager plugin cannot be null!");
	}

	@Override
	public String getName()
	{
		return "GroupManager";
	}

	@Override
	public boolean hasPermission(final CommandSender sender, final String permission)
	{
		if (sender instanceof Player)
			return hasPermission((Player) sender, permission);
		else
			return true;
	}

	public boolean hasPermission(final Player player, final String permission)
	{
		final AnjoPermissionsHandler handler = getHandler(player);
		if (handler == null)
			return false;
		else
			return handler.has(player, permission);
	}

	@Override
	public boolean hasGroup(final Player player, final String name)
	{
		final Set<String> groups = getGroups(player);
		if (groups == null)
			return false;
		else
			return groups.contains(name);
	}

	@Override
	public String getGroup(final Player player)
	{
		final AnjoPermissionsHandler handler = getHandler(player);
		if (handler == null)
			return null;
		else
			return handler.getGroup(player.getName());
	}

	@Override
	public String getGroupPrefix(final Player player)
	{
		final AnjoPermissionsHandler handler = getHandler(player);
		if (handler == null)
			return null;
		else
			return ChatHelper.colorise(handler.getGroupPrefix(handler.getGroup(player.getName())));
	}

	@Override
	public String getGroupSuffix(final Player player)
	{
		final AnjoPermissionsHandler handler = getHandler(player);
		if (handler == null)
			return null;
		else
			return ChatHelper.colorise(handler.getGroupSuffix(handler.getGroup(player.getName())));
	}

	@Override
	public Set<String> getGroups(final Player player)
	{
		final String[] groupArray = getGroupArray(player);
		if (groupArray == null)
			return null;
		final Set<String> groups = new LinkedHashSet<String>(groupArray.length);
		for (final String group : groupArray)
			groups.add(group);
		return groups;
	}

	private AnjoPermissionsHandler getHandler(final Player player)
	{
		final WorldsHolder worldsHolder = plugin.getWorldsHolder();
		if (worldsHolder == null)
			return null;
		else
			return worldsHolder.getWorldPermissions(player);
	}

	public String[] getGroupArray(final Player player)
	{
		final AnjoPermissionsHandler handler = getHandler(player);
		if (handler == null)
			return null;
		else
			return handler.getGroups(player.getName());
	}
}
