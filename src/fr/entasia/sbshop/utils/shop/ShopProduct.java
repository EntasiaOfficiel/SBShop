package fr.entasia.sbshop.utils.shop;

import org.bukkit.Material;

public abstract class ShopProduct {

	public SubShop shop;
	public Material icon;

	public int by;
	public int by_mult = 1; // Multiplicateur seulement


	public int buyPrice;
	public int sellPrice;

	public int modifier;

}
