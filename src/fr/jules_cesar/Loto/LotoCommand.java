package fr.jules_cesar.Loto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LotoCommand extends JavaPlugin implements CommandExecutor {
	
	public static Plugin plugin = null;
	
	public static void load(Plugin p){
		plugin = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String argument_1, String[] arguments) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(Vault.perms.has(player, "loto.modification")){
				if(arguments.length == 0) return showCommands(player);
				else{
					/* List of lotos */
					if(arguments[0].equalsIgnoreCase("select") && arguments.length == 1){
						return showLotos(player);
					}
					/* Selection */
					else if(arguments[0].equalsIgnoreCase("select")){
						if(isNumber(arguments[1])){
							if(Integer.parseInt(arguments[1]) >= 0 && Integer.parseInt(arguments[1]) <= main.loto_list.size()-1){
								main.selected_id = Integer.parseInt(arguments[1]);
								sender.sendMessage(ChatColor.AQUA + "Loto n¡"+arguments[1]+" choisi");
								return true;
							}
							else return showLotos(player);
						}
						else{
							sender.sendMessage(ChatColor.AQUA + "/loto select [number]");
							return true;
						}
					}
					
					/* Create */
					else if(arguments[0].equalsIgnoreCase("create")) return create(player);
					
					/* Delete */
					else if(arguments[0].equalsIgnoreCase("delete")) return delete(player);
					
					/* Other */
					else return showCommands(player);
				}
			}
			else{
				sender.sendMessage(ChatColor.AQUA + "Permission necessaire : loto.modification");
				return false;
			}
		}
		else{
			sender.sendMessage("Only player use this.");
			return false;
		}
	}
	
	/* Create a loto */
	private boolean create(Player player){
		Loto nouveau = new Loto();
		int x = player.getLocation().getBlockX();
		int y = player.getLocation().getBlockY();
		int z = player.getLocation().getBlockZ();
		World world = player.getLocation().getWorld();
		nouveau.position = new Location(world, x, y ,z);
		main.loto_list.add(nouveau);
		main.loto_position_list.add(new Location(world, x, y ,z));
		DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date date = new Date();
        String name = dateFormat.format(date);
		plugin.getConfig().set("loto."+name+".announce", nouveau.announce);
		plugin.getConfig().set("loto."+name+".protection", nouveau.protection);
		plugin.getConfig().set("loto."+name+".delay", nouveau.delay);
		plugin.getConfig().set("loto."+name+".location.world", nouveau.position.getWorld().getName());
		plugin.getConfig().set("loto."+name+".location.x", nouveau.position.getBlockX());
		plugin.getConfig().set("loto."+name+".location.y", nouveau.position.getBlockY());
		plugin.getConfig().set("loto."+name+".location.z", nouveau.position.getBlockZ());
		plugin.saveConfig();
		main.selected_id = main.loto_list.size() - 1;
		player.sendMessage(ChatColor.GOLD + "[LOTO] " + ChatColor.AQUA + "Loto created and selected !");
		return true;
	}

	/* Return list of all lotos */
	private boolean showLotos(Player player){
		player.sendMessage(ChatColor.GOLD + "LOTO : Liste des lotos");
		for(int i = 0; i < main.loto_list.size(); i++){
			Loto loto = main.loto_list.get(i);
			String world = loto.position.getWorld().getName();
			int x = loto.position.getBlockX();
			int y = loto.position.getBlockY();
			int z = loto.position.getBlockZ();
			player.sendMessage(ChatColor.GOLD + "n¡" + i + ChatColor.AQUA + " " + world + " : " + x + "," + y + "," + z);
		}
		return true;
	}

	/* Return list of commands */
	private boolean showCommands(Player player){
		player.sendMessage(ChatColor.GOLD + "LOTO : Liste des commandes");
		player.sendMessage(ChatColor.AQUA + "/loto create : Creer un loto");
		player.sendMessage(ChatColor.AQUA + "/loto select [id] : Selectionner un loto");
		player.sendMessage(ChatColor.AQUA + "/loto delete : Supprimer le loto selectionne");
		player.sendMessage(ChatColor.AQUA + "/loto [attribut] [value] : Modifier l'attribut du loto selectionne");
		return true;
	}

	/* Verify is the string not contain a letter */
	private boolean isNumber(String string){
		Pattern pattern = Pattern.compile("[a-zA-Z]");
		Matcher matcher = pattern.matcher(string);
		if(matcher.find()) return false;
		return true;
	}

	/* Delete a loto */
	private boolean delete(Player player){
		if(main.selected_id == -1){
			player.sendMessage(ChatColor.AQUA + "Please choice a loto before delete it.");
			return true;
		}
		else{
			int id = main.selected_id;
			plugin.getConfig().set("loto."+main.loto_list.get(id).name, null);
			plugin.saveConfig();
			main.loto_position_list.get(id).getBlock().setTypeId(0);
			main.loto_list.remove(id);
			main.loto_position_list.remove(id);
			main.selected_id = -1;
			player.sendMessage(ChatColor.AQUA + "Loto number "+id+" deleted.");
			return true;
		}
	}
}
