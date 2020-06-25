package fr.entasia.sbshop;

import fr.entasia.apis.ItemUtils;
import fr.entasia.apis.ServerUtils;
import fr.entasia.apis.menus.MenuClickEvent;
import fr.entasia.apis.menus.MenuCreator;
import fr.entasia.apis.menus.MenuFlag;
import fr.entasia.sbshop.utils.MenuLink;
import fr.entasia.sbshop.utils.ShopItem;
import fr.entasia.sbshop.utils.SubShop;
import fr.entasia.skycore.apis.BaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Material.APPLE;
import static org.bukkit.Material.STAINED_GLASS_PANE;

public class MenusManager {

	public static MenuCreator mainShopMenu = new MenuCreator(null, null) {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			switch (e.item.getType()) {
				case APPLE:
					openSubShop(e.player, SubShop.FOOD, 0);
					break;
				case GRASS:
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

		ItemStack item = new ItemStack(APPLE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§cNourriture");
		item.setItemMeta(meta);
		inv.setItem(0, item);

		item = new ItemStack(Material.GRASS);
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



	public static MenuCreator subShopMenu = new MenuCreator(new MenuFlag[]{MenuFlag.AllItemsTrigger}, null) {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			MenuLink ml = (MenuLink)e.data;
			if (e.slot == 53)openMainShop(e.player);
			else if (e.slot == 48 && e.item != null)openSubShop(e.player, ml.shop, ml.page - 1);
			else if (e.slot == 50 && e.item != null)openSubShop(e.player, ml.shop, ml.page + 1);
			else if(e.slot<36){
				ml.item = ml.shop.getItem(e.item.getType(), e.item.getDurability());
				if(ml.item==null){
					e.player.sendMessage("§cUne erreur s'est produite ! Merci de contacter un membre du Staff");
					ServerUtils.permMsg("errorlog", "Item invalide demandé dans le shop ! "+e.item.getType()+":"+e.item.getDurability());
				}else{
					ml.sp = BaseAPI.getOnlineSP(e.player.getUniqueId());
					if(ml.item.getBuyPrice()<=1){
						e.player.sendMessage("§cUne erreur s'est produite, contacte un membre du Staff ! (Invalid buy price)");
						e.player.closeInventory();
						ServerUtils.permMsg("errorlog", "§cShop : L'item "+ml.item.type+":"+ml.item.meta+" à un prix invalide !");
					}
					if(e.click== MenuClickEvent.ClickType.LEFT) openBuyShop(e.player, ml);
					else if(e.click== MenuClickEvent.ClickType.RIGHT) openSellShop(e.player, ml);
					else{
						e.player.sendMessage("§cUne erreur s'est produite, contacte un membre du Staff ! (No such action)");
						e.player.closeInventory();
						ServerUtils.permMsg("errorlog", "§cShop : Action de click non reconnue !");
					}
				}
			}
		}
	};


	public static void openSubShop(Player p, SubShop sub, int pagen) {
		Inventory inv = subShopMenu.createInv(6, "§5Shop>> " + sub.title + " §5Page: §6 " + (pagen+1), new MenuLink(sub, pagen));

		ItemStack item;
		ItemMeta meta;
		ArrayList<String> lore;

		int min = pagen*36;
		int max = (pagen+1)*36;

		if(max>sub.items.size()){
			max = sub.items.size();
			if(min>=max){
				p.sendMessage("§cCe numéro de page est trop grand !");
				return;
			}
		}

		int it=0;
		for(ShopItem sitem : sub.items.subList(pagen*36, max)){

			item = new ItemStack(sitem.type, 1, sitem.meta);
			meta = item.getItemMeta();
			lore = new ArrayList<>();
			lore.add("§2Prix : " + sitem.getBuyPrice() + " (Click gauche pour acheter)");
			lore.add("§2Vente: " + sitem.getSellPrice() + " (Click droit pour vendre)");
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.setItem(it, item);
			it++;
		}



		// footer | deux lignes
		item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
		for(int i=36;i<45;i++)inv.setItem(i, item);
		SkullMeta smeta;

		if (pagen != 0) {
			item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
			smeta = (SkullMeta)item.getItemMeta();
			smeta.setDisplayName("§cPage précédente");
			item.setItemMeta(smeta);
			ItemUtils.placeSkullAsync(inv, 48, item, "MHF_ArrowLeft", Main.main);
		}
		Bukkit.broadcastMessage(sub.items.size() +  " " + max);
		if (sub.items.size() > max) {
			item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
			smeta = (SkullMeta)item.getItemMeta();
			smeta.setDisplayName("§cPage suivante");
			item.setItemMeta(smeta);
			ItemUtils.placeSkullAsync(inv, 48, item, "MHF_ArrowRight", Main.main);
		}

		item = new ItemStack(Material.BOOK_AND_QUILL, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("§cRetour au menu principal");
		item.setItemMeta(meta);
		inv.setItem(53, item);

		p.openInventory(inv);
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.5f, 1.1f);
	}

	public static MenuCreator buyShopMenu = new MenuCreator(null, null) {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			MenuLink ml = (MenuLink) e.data;
			if (e.slot == 0) openSubShop(e.player, ml.shop, ml.page);
			else{
				int itemNum;
				if (e.slot == 11) itemNum = 1;
				else if (e.slot == 15) itemNum = 64;
				else return;
				if (ml.sp.getMoney() >= ml.item.getBuyPrice()) {
					if (e.player.getInventory().firstEmpty() == -1) {
						int possible = 0;
						for (Map.Entry<Integer, ? extends ItemStack> slot : e.player.getInventory().all(ml.item.type).entrySet()) {
							if (slot.getValue().getDurability() == ml.item.meta) { // ca passera pas les enchants etc...
								possible += (64 - slot.getValue().getAmount());
								if (possible >= itemNum) break;
							}
						}
						if (possible < itemNum) {
							e.player.sendMessage("§cPas assez de slots libres");
							return;
						}
					}
					e.player.getInventory().addItem(new ItemStack(ml.item.type, itemNum, ml.item.meta));
					ml.sp.withdrawMoney(ml.item.getBuyPrice() * itemNum);
				}else{
					e.player.sendMessage("§cVous n'avez pas assez d'argent !");
					e.player.closeInventory();
				}
			}
		}
	};

	public static void openBuyShop(Player p, MenuLink ml) {
		Inventory inv = buyShopMenu.createInv(3, "§cAchat>>", ml);

		ItemStack item = new ItemStack(Material.BOOK_AND_QUILL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§cRetour au menu précédent");
		item.setItemMeta(meta);
		inv.setItem(0, item);

		item = new ItemStack(ml.item.type, 1);
		meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§3Unité: " + ml.item.getBuyPrice() + "$");
		lore.add("§3Stack: " + (ml.item.getBuyPrice() * 64)+ "$");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(13, item);

		item = new ItemStack(STAINED_GLASS_PANE, 1, (short)5);
		meta = item.getItemMeta();
		meta.setDisplayName("§2Acheter à l'unité");
		lore = new ArrayList<>();
		lore.add("§3Prix: " +  ml.item.getBuyPrice() + "$");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(11, item);

		item = new ItemStack(STAINED_GLASS_PANE, 1, (short)5);
		meta = item.getItemMeta();
		meta.setDisplayName("§2Acheter par stack");
		lore = new ArrayList<>();
		lore.add("§3Prix: " + (ml.item.getBuyPrice() * 64) + "$");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(15, item);

		item = new ItemStack(STAINED_GLASS_PANE, 1, (short)8);
		meta = item.getItemMeta();
		meta.setDisplayName("/");
		item.setItemMeta(meta);
		for (int i = 0; i < 27; i++) if (!(i == 0 || i == 11 || i == 13 || i == 15)) inv.setItem(i, item);

		p.openInventory(inv);
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.5f, 1.1f);
	}

	public static MenuCreator sellShopMenu = new MenuCreator(null, null) {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			MenuLink ml = (MenuLink) e.data;
			int itemNum,cu = 0;
			if (e.slot == 0) openSubShop(e.player, ml.shop, ml.page);
			else{
				if (e.slot == 11) itemNum = 1;
				else if (e.slot == 15) itemNum = 64;
				else return;
				HashMap<Integer, ItemStack> real = new HashMap<>();
				for(Map.Entry<Integer, ? extends ItemStack> item : e.player.getInventory().all(ml.item.type).entrySet()){
					if(item.getValue().getDurability()==ml.item.meta){
						cu+=item.getValue().getAmount();
						real.put(item.getKey(), item.getValue());
						if(cu>=itemNum)break;
					}
				}
				if(cu>=itemNum){
					for	(Map.Entry<Integer, ItemStack> slot: real.entrySet()) {
						if (slot.getValue().getAmount() < itemNum){
							e.player.getInventory().setItem(slot.getKey(), null);
							itemNum-=slot.getKey();
						}else{
							e.player.getInventory().setItem(slot.getKey(), slot.getValue().subtract(itemNum));
							break;
						}
					}
					ml.sp.addMoney(ml.item.getSellPrice() * itemNum);
				}else e.player.sendMessage("§cTu n'as pas assez d'items dans ton inventaire !");
			}
		}
	};

	public static void openSellShop(Player p, MenuLink ml) {
		Inventory inv = sellShopMenu.createInv(3, "§cVendre>>", ml);

		ItemStack item = new ItemStack(Material.BOOK_AND_QUILL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§cRetour au menu précédent");
		item.setItemMeta(meta);
		inv.setItem(0, item);

		item = new ItemStack(ml.item.type, 1);
		meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§3Unité: " + ml.item.getSellPrice() + "$");
		lore.add("§3Stack: " + (ml.item.getSellPrice() * 64) + "$");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(13, item);

		item = new ItemStack(STAINED_GLASS_PANE, 1, (short)5);
		meta = item.getItemMeta();
		meta.setDisplayName("§2Vendre à l'unité");
		lore = new ArrayList<>();
		lore.add("§3Prix: " + ml.item.getSellPrice() + "$");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(11, item);

		item = new ItemStack(STAINED_GLASS_PANE, 1, (short)5);
		meta = item.getItemMeta();
		meta.setDisplayName("§2Vendre par stack");
		lore = new ArrayList<>();
		lore.add("§3Prix: " + (ml.item.getSellPrice() * 64) + "$");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(15, item);

		item = new ItemStack(STAINED_GLASS_PANE, 1, (short)8);
		meta = item.getItemMeta();
		meta.setDisplayName("/");
		item.setItemMeta(meta);
		for (int i = 0; i < 27; i++) if (!(i == 0 || i == 11 || i == 13 || i == 15)) inv.setItem(i, item);

		p.openInventory(inv);
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.5f, 1.1f);
	}
}
