package fr.jules_cesar.Loto;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener{
	
	private List<Integer> valeur_id = new ArrayList<Integer>();
	private long delai = 1200L;
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		if(!this.getDataFolder().exists()) this.getDataFolder().mkdir();
		this.saveDefaultConfig();
		FileConfiguration loto = this.getConfig();
		valeur_id = loto.getIntegerList("item");
		delai = loto.getLong("delai") * 20;
	}
	
	public void onDisable(){
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDamageLoto(BlockDamageEvent event){
		if(event.getBlock().getTypeId() == 7){
			final Location bloc = event.getBlock().getLocation();
			if(bloc.add(0, -1, 0).getBlock().getTypeId() == 35){
				// Tirage au sort de l'item
				int min = 0;
				int max = valeur_id.size() - 1;
				if( max <= min){}
				else{
					int random = (int)(Math.random() * (max-min)) + min;
					// Cassage du bloc, passage item et attente avant respawn
					bloc.add(0, +1, 0).getBlock().breakNaturally(new ItemStack(Material.BEDROCK));
					bloc.getWorld().dropItemNaturally(bloc.add(0, +1, 0), new ItemStack(valeur_id.get(random), 1));
					getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							bloc.add(0, -1, 0).getBlock().setTypeId(7);
						}
						}, delai);
				}
			}
		}
	}
}