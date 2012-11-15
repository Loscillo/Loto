package fr.jules_cesar.Loto;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class main extends JavaPlugin implements Listener{
	
	public List<Integer> valeur_id = new ArrayList<Integer>();
	public long delai = 1200L;
	public boolean protection = true;
	public boolean announce = false;
	public String joueur = null;
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		
		// Get config of plugin
		if(!this.getDataFolder().exists()) this.getDataFolder().mkdir();
		this.saveDefaultConfig();
		FileConfiguration loto = this.getConfig();
		valeur_id = loto.getIntegerList("item");
		delai = loto.getLong("delai") * 20;
		protection = loto.getBoolean("protection");
		announce = loto.getBoolean("announce");
		
		// Get permission plugin
		Vault.load(this);
		Vault.setupPermissions();
	}
	
	public void onDisable(){
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDamageLoto(BlockDamageEvent event){
		if(event.getBlock().getTypeId() == 7){
			final Location bloc = event.getBlock().getLocation();
			if(bloc.add(0, -1, 0).getBlock().getTypeId() == 35 && ((protection && event.getPlayer().getName() != joueur) || !protection)){
				// Inscription du dernier joueur
				joueur = event.getPlayer().getName();
				
				// Tirage au sort de l'item
				int min = 0;
				int max = valeur_id.size() - 1;
				if( max <= min){}
				else{
					int random = (int)(Math.random() * (max-min)) + min;
					// Cassage du bloc, passage item et attente avant respawn
					bloc.add(0, +1, 0).getBlock().breakNaturally(new ItemStack(Material.BEDROCK));
					ItemStack gain = new ItemStack(valeur_id.get(random), 1);
					bloc.getWorld().dropItemNaturally(bloc.add(0, +1, 0), gain);
					if(announce) getServer().broadcastMessage(ChatColor.GOLD+"[LOTO] "+ChatColor.BLUE+"Le joueur " + joueur + " a gagné " + gain.getType());
					getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							bloc.add(0, -1, 0).getBlock().setTypeId(7);
						}
						}, delai);
				}
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(sender instanceof Player && label.equals("loto")){
			if(args.length == 2){
				Player joueur = (Player) sender;
				if(Vault.perms.playerHas(joueur.getWorld(), joueur.getName(), "loto.modification")){
					if(args[0].equals("delai")){
						delai = Long.parseLong(args[1]);
						sender.sendMessage("Le dŽlai est fixŽ ˆ " + delai);
						this.getConfig().set("delai", delai);
						this.saveConfig();
						this.delai = delai * 20;
						return true;
					}
					else if(args[0].equals("protection")){
						protection = Boolean.parseBoolean(args[1]);
						sender.sendMessage("La protection est fixŽ ˆ " + protection);
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
					else return false;
				}
				
				else{
					sender.sendMessage("Vous devez avoir la permission loto.modification pour modifier le fichier de configuration.");
					return false;
				}
			}
		}
		
		return false;
	}
}