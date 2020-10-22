package fr.entasia.sbshop.utils.links;

import fr.entasia.sbshop.utils.ShopCat;
import fr.entasia.sbshop.utils.ShopProduct;
import fr.entasia.sbshop.utils.SubShop;
import fr.entasia.skycore.apis.SkyPlayer;

public class CatLink extends MenuLink {

	public ShopCat scat;

	public CatLink(SubShop shop, int page){
		this.shop = shop;
		this.page = page;
	}
}
