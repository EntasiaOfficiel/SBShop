package fr.entasia.sbshop.invs;

import fr.entasia.apis.menus.MenuClickEvent;
import fr.entasia.apis.menus.MenuCreator;
import fr.entasia.apis.menus.MenuFlag;
import fr.entasia.apis.utils.ItemUtils;
import fr.entasia.apis.utils.ServerUtils;
import fr.entasia.sbshop.utils.links.MenuLink;
import fr.entasia.sbshop.utils.shop.ShopProduct;
import fr.entasia.sbshop.utils.shop.SubShop;
import fr.entasia.skycore.apis.BaseAPI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseInvs {

	public static MenuCreator mainShopMenu = new MenuCreator() {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			switch (e.item.getType()) {
				case APPLE:
					openSubShop(e.player, SubShop.FOOD, 0);
					break;
				case GRASS_BLOCK:
					openSubShop(e.player, SubShop.BLOCKS, 0);
					break;
				case LAPIS_ORE:
					openSubShop(e.player, SubShop.ORES, 0);
					break;
				case BLAZE_ROD:
					openSubShop(e.player, SubShop.LOOTS, 0);
					break;
				case SUGAR_CANE:
					openSubShop(e.player, SubShop.HARVEST, 0);
			}
		}
	};

	public static void openMainShop(Player p) {
		Inventory inv = mainShopMenu.createInv(1, "§5Shop>> §cAccueil");

		ItemStack item = new ItemStack(Material.APPLE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§cNourriture");
		item.setItemMeta(meta);
		inv.setItem(0, item);

		item = new ItemStack(Material.GRASS_BLOCK);
		meta = item.getItemMeta();
		meta.setDisplayName("§aBlocs");
		item.setItemMeta(meta);
		inv.setItem(2, item);

		item = new ItemStack(Material.LAPIS_ORE);
		meta = item.getItemMeta();
		meta.setDisplayName("§3Minerais");
		item.setItemMeta(meta);
		inv.setItem(4, item);

		item = new ItemStack(Material.BLAZE_ROD);
		meta = item.getItemMeta();
		meta.setDisplayName("§5Loots");
		item.setItemMeta(meta);
		inv.setItem(6, item);

		item = new ItemStack(Material.SUGAR_CANE);
		meta = item.getItemMeta();
		meta.setDisplayName("§6Récoltes");
		item.setItemMeta(meta);
		inv.setItem(8, item);

		p.openInventory(inv);
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.5f, 1.1f);
	}



	public static MenuCreator subShopMenu = new MenuCreator() {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			MenuLink ml = (MenuLink)e.data;
			if (e.slot == 53)openMainShop(e.player);
			else if (e.slot == 48 && e.item != null)openSubShop(e.player, ml.shop, ml.page - 1);
			else if (e.slot == 50 && e.item != null)openSubShop(e.player, ml.shop, ml.page + 1);
			else if(e.slot<36){
				ml.sp = BaseAPI.getOnlineSP(e.player);
				ml.sproduct = ml.shop.getItem(e.item.getType());
				if(ml.sproduct==null){
					ml.sproduct = ml.shop.getCategory(e.item.getType());
					if(ml.sproduct==null){
						e.player.sendMessage("§cUne erreur s'est produite ! Merci de contacter un membre du Staff");
						ServerUtils.permMsg("log.shoperror", "§cShop : Item invalide demandé dans le shop ! "+e.item.getType());
					}else{
						BuySellInvs.openCatShop(e.player, ml);
					}
				}else {
					ml.selected = ml.sproduct.icon;
					if (e.click == MenuClickEvent.ClickType.LEFT) {
						if (ml.sproduct.buyPrice != 0) BuySellInvs.openBuyShop(e.player, ml);
					} else if (e.click == MenuClickEvent.ClickType.RIGHT) {
						if (ml.sproduct.sellPrice != 0) BuySellInvs.openSellShop(e.player, ml);
					} else {
						e.player.sendMessage("§cUne erreur s'est produite, contacte un membre du Staff ! (No such action)");
						e.player.closeInventory();
						ServerUtils.permMsg("log.shoperror", "§cShop : "+e.player+" a fait une action de click non reconnue !");
					}
				}
			}
		}
	}.setFlags(MenuFlag.AllItemsTrigger);

	public static void openSubShop(Player p, SubShop sub, int pagen) {
		Inventory inv = subShopMenu.createInv(6, "§5Shop>> " + sub.title + " §5Page: §6 " + (pagen+1), new MenuLink(sub, pagen));

		// 36 = 4 lignes
		int min = pagen*36;
		int count = 0;
		boolean nextPage = true;
		ItemStack item;
		Iterator<? extends ShopProduct> ite;

		if(min<sub.items.size()){
			ite = iterator(sub.items, min);
			while(ite.hasNext()&&count<36){
				item = setupSellItem(ite.next(), false);
				inv.setItem(count, item);
				count++;
			}
			min = 0;
		}else min = min - sub.items.size();

//		p.sendMessage("§cCe numéro de page est trop grand !");
//		return;

		if(min<sub.cats.size()){

			// for line change
			int temp = count%9;
			if(temp!=0){
				count+=9-(temp);
			}

			ite = iterator(sub.cats, min);
			while(count<36){
				if(!ite.hasNext()){
					nextPage = false;
					break;
				}

				item = setupSellItem(ite.next(), true);
				inv.setItem(count, item);
				count++;
			}
		}else count = 36; // just for future condition



		// footer | deux lignes
		item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		for(int i=36;i<45;i++)inv.setItem(i, item);
		SkullMeta smeta;

		if (pagen != 0) {
			item = new ItemStack(Material.PLAYER_HEAD);
			smeta = (SkullMeta)item.getItemMeta();
			smeta.setDisplayName("§cPage précédente");
			item.setItemMeta(smeta);
			ItemUtils.placeSkullAsync(inv, 48, item, "MHF_ArrowLeft");
		}
		if (nextPage) {
			item = new ItemStack(Material.PLAYER_HEAD);
			smeta = (SkullMeta)item.getItemMeta();
			smeta.setDisplayName("§cPage suivante");
			item.setItemMeta(smeta);
			ItemUtils.placeSkullAsync(inv, 50, item, "MHF_ArrowRight");
		}

		item = new ItemStack(Material.WRITABLE_BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§cRetour au menu principal");
		item.setItemMeta(meta);
		inv.setItem(53, item);

		p.openInventory(inv);
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.5f, 1.1f);
	}


	private static ItemStack setupSellItem(ShopProduct product, boolean cat){
		ItemStack item = new ItemStack(product.icon, product.by);
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		if (product.buyPrice == 0) lore.add("§cAchat Impossible");
		else lore.add("§2Prix: §a" + product.buyPrice + "§2 (Click gauche pour acheter)");
		if (product.sellPrice == 0) lore.add("§cVente impossible");
		else lore.add("§2Vente: §a" + product.sellPrice + "§2 (Click droit pour vendre)");

		if (cat) lore.add("§6Clique pour voir plus de choix");

		meta.setLore(lore);
		item.setItemMeta(meta);

		return item;
	}

	private static Iterator<? extends ShopProduct> iterator(List<? extends ShopProduct> list, int index){
		Iterator<? extends ShopProduct> ite = list.iterator();
		try{
			Field f = ite.getClass().getDeclaredField("cursor");
			f.setAccessible(true);
			f.set(ite, index);
		}catch(ReflectiveOperationException e){
			e.printStackTrace();
		}
		return ite;
	}

}
