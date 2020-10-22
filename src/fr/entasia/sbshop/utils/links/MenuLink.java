package fr.entasia.sbshop.utils.links;

import fr.entasia.sbshop.utils.shop.ShopProduct;
import fr.entasia.sbshop.utils.shop.SubShop;
import fr.entasia.skycore.apis.SkyPlayer;
import org.bukkit.Material;

public class MenuLink {

	public SkyPlayer sp;

	public SubShop shop;
	public int page;
	public ShopProduct sproduct;
	public Material selected;

	public MenuLink(SubShop shop, int page){
		this.shop = shop;
		this.page = page;
	}
}
