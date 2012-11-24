package fr.jules_cesar.Loto;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Loto {
	public Object name;
	public List<Integer> id_list;
	public long delay;
	public boolean protection;
	public boolean announce;
	public String player;
	public Location position;
	
	public Loto(){
		name = null;
		id_list = new ArrayList<Integer>();
		delay = 1200L;
		protection = true;
		announce = false;
		player = null;
		position = new Location(null, 0, 0, 0);
	}
	
	public void modify(int id, String container, Object value){
	
	}
}
