package fr.entasia.sbshop.utils.shop;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum SubShop {
	BLOCKS("§2Blocks"),
	FOOD("§6Nourriture"),
	ORES("§3Ore"),
	LOOTS("§cLoots"),
	HARVEST("§5Récoltes");

	public String title;
	public List<ShopCat> cats = new ArrayList<>();
	public List<ShopItem> items = new ArrayList<>();
	public int price_modifier;

	SubShop(String title){
		this.title = title;
	}

	public ShopItem getItem(Material type){
		for(ShopItem sitem : items){
			if(sitem.type==type)return sitem;
		}
		return null;
	}

	public ShopCat getCategory(Material type){
		for(ShopCat scat : cats){
			if(scat.icon ==type)return scat;
		}
		return null;
	}
}
