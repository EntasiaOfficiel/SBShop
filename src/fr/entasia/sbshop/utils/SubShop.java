package fr.entasia.sbshop.utils;

import org.bukkit.Material;

import java.util.ArrayList;

public enum SubShop {
	BLOCKS("§2Blocks"),
	FOOD("§6Nourriture"),
	ORES("§3Ore"),
	LOOTS("§cLoots"),
	HARVEST("§5Récoltes");

	public String title;
	public ArrayList<ShopItem> items;
	public int price_modifier;

	SubShop(String title){
		this.title = title;
	}

	public ShopItem getItem(Material type, short meta){
		for(ShopItem sitem : items){
			if(sitem.type==type&&sitem.meta==meta)return sitem;
		}
		return null;
	}
}
