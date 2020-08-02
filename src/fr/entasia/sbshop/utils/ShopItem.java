package fr.entasia.sbshop.utils;

import fr.entasia.sbshop.Main;
import org.bukkit.Material;

public class ShopItem {

	public SubShop shop;

	public Material type;
	public short meta;
	public short maxMeta;

	public int by;
	public int by_mult = 1; // Multiplicateur seulement


	public int buyPrice;
	public int sellPrice;

	public int modifier;

	public ShopItem(){
	}

//	public ShopItem(Material type, byte meta, int buy, int sell, int modifier){
//		this.meta = meta;
//		this.buy = buy;
//		this.sell = sell;
//		this.type = type;
//		this.modifier = modifier;
//	}

}
