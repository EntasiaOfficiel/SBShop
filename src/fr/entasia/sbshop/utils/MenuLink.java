package fr.entasia.sbshop.utils;

import fr.entasia.skycore.apis.SkyPlayer;

public class MenuLink {

	public SkyPlayer sp;
	public SubShop shop;
	public int page;
	public ShopItem item;

	public MenuLink(ShopItem item, int page){
		this.item = item;
		this.page = page;
	}

	public MenuLink(SubShop shop, int page){
		this.shop = shop;
		this.page = page;
	}
}
