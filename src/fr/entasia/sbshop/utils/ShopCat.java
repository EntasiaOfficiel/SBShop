package fr.entasia.sbshop.utils;

import org.bukkit.Material;
import org.bukkit.Tag;

public class ShopCat extends ShopProduct {

	public Tag<Material> cat;

	public ShopCat(SubShop shop){
		this.shop = shop;
	}

//	public ShopItem(Material type, byte meta, int buy, int sell, int modifier){
//		this.meta = meta;
//		this.buy = buy;
//		this.sell = sell;
//		this.type = type;
//		this.modifier = modifier;
//	}

}
