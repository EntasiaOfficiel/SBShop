package fr.entasia.sbshop.utils.links;

import fr.entasia.sbshop.utils.ShopItem;
import fr.entasia.sbshop.utils.ShopProduct;
import fr.entasia.sbshop.utils.SubShop;
import fr.entasia.skycore.apis.SkyPlayer;

public class ItemLink extends MenuLink {

	public ShopItem sitem;

	public ItemLink(SubShop shop, int page){
		this.shop = shop;
		this.page = page;
	}

}
