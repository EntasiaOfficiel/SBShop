package fr.entasia.sbshop.utils;

import fr.entasia.skycore.apis.SkyPlayer;

public class MenuLink {

	public SkyPlayer sp;
	public ShopItem sitem;
	public short meta;

	public SubShop shop;
	public int page;

	public MenuLink(SubShop shop, int page){
		this.shop = shop;
		this.page = page;
	}
}
