package fr.jules_cesar.Loto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
				if(arguments.length == 0){
					sender.sendMessage(ChatColor.GOLD + "LOTO : Liste des commandes");
					sender.sendMessage(ChatColor.AQUA + "/loto create : Creer un loto");
					sender.sendMessage(ChatColor.AQUA + "/loto select [id] : Selectionner un loto");
					sender.sendMessage(ChatColor.AQUA + "/loto delete : Supprimer le loto selectionne");
					sender.sendMessage(ChatColor.AQUA + "/loto [attribut] [value] : Modifier l'attribut du loto selectionne");
					return true;
				}
				else{
					/* List of lotos */
					if(arguments[0].equalsIgnoreCase("select") && arguments.length == 1){
						sender.sendMessage(ChatColor.GOLD + "LOTO : Liste des lotos");
						for(int i = 0; i < main.loto_list.size(); i++){
							Loto loto = main.loto_list.get(i);
							String world = loto.position.getWorld().getName();
							int x = loto.position.getBlockX();
							int y = loto.position.getBlockY();
							int z = loto.position.getBlockZ();
							sender.sendMessage(ChatColor.GOLD + "n¡" + i + ChatColor.AQUA + " " + world + " : " + x + "," + y + "," + z);
						}
						return true;
					}
					/* Selection */
					else if(arguments[0].equalsIgnoreCase("select")){
						if(Integer.parseInt(arguments[1]) >= 0 && Integer.parseInt(arguments[1]) <= main.loto_list.size()-1){
							main.selected_id = Integer.parseInt(arguments[1]);
							sender.sendMessage(ChatColor.AQUA + "Loto n¡"+arguments[1]+" choisi");
							return true;
						}
						else{
							sender.sendMessage(ChatColor.AQUA + "Merci de choisir un loto existant");
							return false;
						}
					}
					
					/* Create */
					else if(arguments[0].equalsIgnoreCase("create")){
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
						return true;
					}
					
					/* Other */
					else{
						return false;
					}
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

}
