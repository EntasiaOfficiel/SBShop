package fr.entasia.sbshop.utils;

import org.bukkit.Material;

public abstract class ShopProduct {

	public SubShop shop;
	public Material type;

	public int by;
	public int by_mult = 1; // Multiplicateur seulement


	public int buyPrice;
	public int sellPrice;

	public int modifier;

}
