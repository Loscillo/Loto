package fr.jules_cesar.Loto;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener{
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable(){
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDamageLoto(BlockDamageEvent event){
		if(event.getBlock().getTypeId() == 7){
			final Location bloc = event.getBlock().getLocation();
			if(bloc.add(0, -1, 0).getBlock().getTypeId() == 35){
				// DŽfinition des items possibles
				ArrayList<String> valeur_id = new ArrayList<String>(); 
				valeur_id.add("133"); valeur_id.add("264"); valeur_id.add("388"); valeur_id.add("41"); valeur_id.add("266"); valeur_id.add("371"); valeur_id.add("42"); valeur_id.add("265"); valeur_id.add("57");
				
				// Tirage au sort de l'item
				int min = 0;
				int max = valeur_id.size() - 1;
				int random = (int)(Math.random() * (max-min)) + min;
				
				// Cassage du bloc, passage item et attente avant respawn
				bloc.add(0, +1, 0).getBlock().breakNaturally(new ItemStack(Material.BEDROCK));
				bloc.getWorld().dropItemNaturally(bloc.add(0, +1, 0), new ItemStack(Integer.parseInt(valeur_id.get(random)), 1));
				getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					   public void run() {
						   bloc.add(0, -1, 0).getBlock().setTypeId(7);
					   }
					}, 6000L);
			}
		}
	}
}