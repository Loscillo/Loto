package fr.jules_cesar.Loto;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class main extends JavaPlugin implements Listener{
	
	private LotoCommand CommandExecutor = new LotoCommand();
	
	static List<Loto> loto_list = new ArrayList<Loto>();
	static List<Location> loto_position_list = new ArrayList<Location>();
	static int selected_id = -1;
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		
		// Get config of plugin
		if(!this.getDataFolder().exists()) this.getDataFolder().mkdir();
		this.saveDefaultConfig();
		FileConfiguration loto = this.getConfig();
		if(loto.isConfigurationSection("loto")){
		Object[] loto_list_temp = loto.getConfigurationSection("loto").getKeys(false).toArray();
		System.out.println("Chargement de " + loto_list_temp.length + " lotos.");
		
		// Initialize all Loto.
		for(int i = 0; i < loto_list_temp.length; i++){
			loto_list.add(new Loto());
			loto_list.get(i).announce = loto.getBoolean("loto."+loto_list_temp[i]+".announce");
			loto_list.get(i).protection = loto.getBoolean("loto."+loto_list_temp[i]+".protection");
			loto_list.get(i).delay = loto.getLong("loto."+loto_list_temp[i]+".delay") * 20;
			loto_list.get(i).id_list = loto.getIntegerList("loto."+loto_list_temp[i]+".items");
			World world = getServer().getWorld(loto.getString("loto."+loto_list_temp[i]+".location.world"));
			int x = loto.getInt("loto."+loto_list_temp[i]+".location.x");
			int y = loto.getInt("loto."+loto_list_temp[i]+".location.y");
			int z = loto.getInt("loto."+loto_list_temp[i]+".location.z");
			Location position = new Location(world, x, y, z);
			loto_list.get(i).position = position;
			loto_position_list.add(position);
			position.getBlock().setTypeId(7);
		}
		}
		
		// Get permission plugin
		Vault.load(this);
		Vault.setupPermissions();
		
		// Initialize command listener
		LotoCommand.load(this);
		getCommand("loto").setExecutor(CommandExecutor);
	}
	
	public void onDisable(){
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDamageLoto(BlockDamageEvent event){
		if(loto_position_list.contains(event.getBlock().getLocation())){
			final Location bloc = event.getBlock().getLocation();
			int i = 0;
			while(0 < bloc.distance(loto_list.get(i).position)){
				i++;
			}
			Loto loto = loto_list.get(i);
			if((loto.protection && !event.getPlayer().getName().equals(loto.player)) || !loto.protection){
				loto_list.get(i).player = event.getPlayer().getName();
				int min = 0;
				int max = loto.id_list.size();
				if(max <= min){}
				else{
					int random = (int)(Math.random() * (max-min)) + min;
					bloc.getBlock().setTypeId(0);
					ItemStack gain = new ItemStack(loto.id_list.get(random), 1);
					event.getPlayer().getInventory().addItem(gain);
					if(loto.announce) getServer().broadcastMessage(ChatColor.GOLD+"[LOTO] "+ChatColor.AQUA+"Le joueur " + event.getPlayer().getDisplayName() + ChatColor.AQUA + " a gagn\u00e9 " + gain.getType());
					getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							bloc.getBlock().setTypeId(7);
						}
					}, loto.delay);
				}
			}
		}
	}
	
	
	/*public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(sender instanceof Player && label.equals("loto")){
			if(args.length > 0){
				Player joueur = (Player) sender;
				if(Vault.perms.playerHas(joueur, "loto.modification")){
					if(args[0].equals("create")){
						joueur.getLocation().getBlock().setTypeId(7);
						Loto nouveau = new Loto();
						World world = joueur.getWorld();
						int x = joueur.getLocation().getBlockZ();
						int y = joueur.getLocation().getBlockY();
						int z = joueur.getLocation().getBlockY();
						nouveau.position = new Location(world, x, y, z);
						DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
				        Date date = new Date();
				        String name = dateFormat.format(date);
						this.getConfig().set("loto."+name+".announce", nouveau.announce);
						this.saveConfig();
					}
					/*if(args[0].equals("delai")){
						delai = Long.parseLong(args[1]);
						sender.sendMessage("Le d�lai est fix� � " + delai);
						this.getConfig().set("delai", delai);
						this.saveConfig();
						this.delai = delai * 20;
						return true;
					}
					else if(args[0].equals("protection")){
						protection = Boolean.parseBoolean(args[1]);
						sender.sendMessage("La protection est fix� � " + protection);
						this.getConfig().set("protection", protection);
						this.saveConfig();
						return true;
					}
					else if(args[0].equals("announce")){
						announce = Boolean.parseBoolean(args[1]);
						sender.sendMessage("Le message est active : " + announce);
						this.getConfig().set("announce", announce);
						this.saveConfig();
						return true;
					}
					else if(args[0].equals("add")){
						valeur_id.add(Integer.parseInt(args[1]));
						this.getConfig().set("item", valeur_id);
						this.saveConfig();
						return true;
					}
					else if(args[0].equals("remove")){
						valeur_id.remove(new Integer(Integer.parseInt(args[1])));
						this.getConfig().set("item", valeur_id);
						this.saveConfig();
						return true;
					}
					else return false;
				}
				
				else{
					sender.sendMessage(ChatColor.AQUA + "Permission : loto.modification");
					return false;
				}
			}
		}
		
		return false;
	}*/
}