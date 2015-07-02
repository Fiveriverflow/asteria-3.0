package com.asteria;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.asteria.game.character.npc.NpcDefinition;
import com.asteria.game.character.player.content.WeaponDelay;
import com.asteria.game.character.player.content.WeaponInterface;
import com.asteria.game.item.ItemDefinition;
import com.asteria.game.item.container.Equipment;

/**
 * Gathers definitions data from the 2007 RS Wiki.
 * *Caution* ugly...
 */
public class RuneWiki {
	
	public static void check() {
		
		try {
			for (String line : Files.readAllLines(Paths.get("./npcs.txt"))) {
			    line = line.replaceAll("NPC ID: ", "");
			    line = line.replaceAll("NPC name:-", "-");
			    String[] data = line.split("-");
			    NpcDefinition.DEFINITIONS[Integer.parseInt(data[0])].name = data[1];
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//getItemData(5736);
		
		JSONArray defs = new JSONArray();
		for(int i = 0; i < ItemDefinition.DEFINITIONS.length; i++) {//ItemDefinition.DEFINITIONS.length
			System.out.println(i);
			getWikiNpcData(i);
		}
		
		JSONObject result = new JSONObject();
		result.put("defs", defs);
		try {
			FileWriter jsonFileWriter = new FileWriter("./delays.json");
			jsonFileWriter.write(result.toJSONString());
			jsonFileWriter.flush();
			jsonFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void getWikiNpcData(int id) {
		try {
			boolean debug = false;
			
			String name = NpcDefinition.DEFINITIONS[id].getName().replaceAll(" ", "_");
			if(name.equals("Null") || name.equals("null") || name == "null" || name == "Null")
				return;
			URL url = new URL("http://2007.runescape.wikia.com/wiki/" + name);
			URLConnection con = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			
			String line;
			
			boolean combat = false;
			boolean hp = false;
			boolean slayerlevel = false;
			boolean slayerxp = false;
			boolean aggressive = false;
			boolean poisonous = false;
			boolean immunep = false;
			boolean hit = false;
			boolean examine = false;
			int bonus = 0;
			ItemDefinition.DEFINITIONS[id].bonus = new int[14];
			ItemDefinition.DEFINITIONS[id].twoHanded = false;
			
			while ((line = in.readLine()) != null) {
				
				//Tradeable?
				try {
				if(tradeable) {
					ItemDefinition.DEFINITIONS[id].tradeable = line.contains("Yes");
					tradeable = false;
					if(debug)
					System.out.println("tradeable:"+ItemDefinition.DEFINITIONS[id].tradeable);
				}
				if(line.contains("Tradeable</a>?")) {
					tradeable = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//Equipable?
				try {
				if(equipable) {
					ItemDefinition.DEFINITIONS[id].equipable = line.contains("Yes");
					equipable = false;
					if(debug)
					System.out.println("equipable:"+ItemDefinition.DEFINITIONS[id].equipable);
				}
				if(line.contains("Equipable</a>?")) {
					equipable = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//Stackable?
				/*try {
				if(stackable) {
					ItemDefinition.DEFINITIONS[id].stackable = line.contains("Yes");
					stackable = false;
					if(debug)
					System.out.println("stackable:"+ItemDefinition.DEFINITIONS[id].stackable);
				}
				if(line.contains("Stackable</a>?")) {
					stackable = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				
				//High alch.
				try {
				if(highAlch) {
					if(!line.contains("Unknown") && line.contains("coins")) {
						ItemDefinition.DEFINITIONS[id].highAlchValue = Integer.parseInt(
							line.replaceAll("</th><td> ", "").
							replaceAll("&#160;coins", "").
							replaceAll("&#160;coin", "").
							replaceAll("gp", "").
							replaceAll("\\(4\\)=9 \\(3\\)=", "").replace(",", ""));
					}
					highAlch = false;
					if(debug)
					System.out.println("high alching"+ItemDefinition.DEFINITIONS[id].highAlchValue);
				}
				if(line.contains("High Alch</a>")) {
					highAlch = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//High alch.
				try {
				if(lowAlch) {
					if(!line.contains("Unknown") && line.contains("coins")) {
						ItemDefinition.DEFINITIONS[id].lowAlchValue = Integer.parseInt(
								line.replaceAll("</th><td> ", "").
								replaceAll("&#160;coins", "").
								replaceAll("&#160;coin", "").
								replaceAll("\\(4\\)= 6 \\(3\\)=", "").
								replaceAll("\\(4\\)=9 \\(3\\)=", "").
								replaceAll("gp", "").replace(",", ""));
					}
					lowAlch = false;
					if(debug)
					System.out.println("low alching:"+ItemDefinition.DEFINITIONS[id].lowAlchValue);
				}
				if(line.contains("Low Alch</a>")) {
					lowAlch = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//store price.
				try {
				if(store) {
					if(!line.contains("Not sold")) {
						if(line.contains("coins") && !line.contains("pack") && !line.contains("N/A")  && !line.contains("Not")
								&& !line.contains("Yes") && !line.contains("(4)") && !line.contains("Alfonse_the_Waiter")
								&& !line.contains("The_Fremennik_Trials") && !line.contains("Slayer_reward_points") && !line.contains("for")) {
							line = line.replaceAll("</th><td> ", "").replace(",", "").replace(" ", "");
							String[] pris = line.split("&#160;coins");
							if(pris[0].contains("-")) {
								 String[] prices = pris[0].split("-");
								 int from = Integer.parseInt(prices[0]);
								 int to = Integer.parseInt(prices[1]);
								 ItemDefinition.DEFINITIONS[id].generalPrice = (from+to) / 2;
							} else {
								ItemDefinition.DEFINITIONS[id].generalPrice = Integer.parseInt(pris[0].split(" ")[0]);
							}
							ItemDefinition.DEFINITIONS[id].specialPrice = (int) ((ItemDefinition.DEFINITIONS[id].generalPrice/100.0) * 80);
						}
					}
					store = false;
					if(debug)
					System.out.println("price:"+ItemDefinition.DEFINITIONS[id].generalPrice);
					if(debug)
					System.out.println("special price:"+ItemDefinition.DEFINITIONS[id].specialPrice);
				}
				if(line.contains("Store price</a>")) {
					store = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//weights
				try {
				if(weight) {
					if(!line.contains("Unknown") && !line.contains("Inventory:") && !line.contains("empty")
							 && !line.contains("full") && !line.contains("(2)") && !line.contains("Half")) {
						ItemDefinition.DEFINITIONS[id].weight = Double.parseDouble(
								line.replaceAll("</th><td> ", "").
								replaceAll("&lt;", "").
								replaceAll("~", "").
								replaceAll("&#160;kg", ""));
					}
					weight = false;
					if(debug)
					System.out.println("weight:"+ItemDefinition.DEFINITIONS[id].weight);
				}
				if(line.contains("Weight</a>")) {
					weight = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//description
				try {
				if(examine) {
					if(line.equals("</th></tr>") || line.equals("<tr>"))
						continue;
					ItemDefinition.DEFINITIONS[id].description = line.replaceAll("<td colspan=\"2\" style=\"padding:3px 7px 3px 7px; line-height:140%; text-align:center;\"> ", "");
					examine = false;
					if(debug)
					System.out.println("Examine:"+ItemDefinition.DEFINITIONS[id].description);
				}
				if(line.contains("Examine</a>")) {
					examine = true;
				}
				
				if(line.contains("<td style=\"text-align: center; width: 35px;\">") ||
						line.contains("<td style=\"text-align: center; width: 30px;\">")) {
					line = line.replaceAll("</td>", "").
							replaceAll("<td style=\"text-align: center; width: 35px;\">", "").
							replaceAll("<td style=\"text-align: center; width: 30px;\">", "").
							replaceAll("%", "");
					if(debug)
					System.out.println("bonus:"+bonus+" is " + Integer.parseInt(line));
					if(bonus < 14)
					ItemDefinition.DEFINITIONS[id].bonus[bonus] = Integer.parseInt(line);
					bonus += 1;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
				if(line.contains("data-image-key=\"Shield_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.SHIELD_SLOT;
				}
				if(line.contains("data-image-key=\"Weapon_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.WEAPON_SLOT;
				}
				if(line.contains("data-image-key=\"Head_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.HEAD_SLOT;
				}
				if(line.contains("data-image-key=\"Cape_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.CAPE_SLOT;
				}
				if(line.contains("data-image-key=\"Neck_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.AMULET_SLOT;
				}
				if(line.contains("data-image-key=\"Torso_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.CHEST_SLOT;
				}
				if(line.contains("data-image-key=\"Legs_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.LEGS_SLOT;
				}
				if(line.contains("data-image-key=\"Gloves_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.HANDS_SLOT;
				}
				if(line.contains("data-image-key=\"Boots_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.FEET_SLOT;
				}
				if(line.contains("data-image-key=\"Ring slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.RING_SLOT;
				}
				if(line.contains("data-image-key=\"Ammo_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.ARROWS_SLOT;
				}
				if(line.contains("data-image-key=\"2h_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.WEAPON_SLOT;
					ItemDefinition.DEFINITIONS[id].twoHanded = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(debug)
				System.out.println("equipmentslot:"+ItemDefinition.DEFINITIONS[id].equipmentSlot);
			in.close();
			
			if(ItemDefinition.DEFINITIONS[id].name.contains("platebody"))
				ItemDefinition.DEFINITIONS[id].platebody = true;
			
			if(ItemDefinition.DEFINITIONS[id].name.contains("full helm"))
				ItemDefinition.DEFINITIONS[id].fullHelm = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void getWikiWeaponSpeed(int id) {
		try {
			boolean debug = false;
			
			if(ItemDefinition.DEFINITIONS[id].equipmentSlot != 3)
				return;
			
			String name = ItemDefinition.DEFINITIONS[id].getName().replaceAll(" ", "_");
			if(name.equals("Null") || name.equals("null") || name == "null" || name == "Null")
				return;
			URL url = new URL("http://2007.runescape.wikia.com/wiki/" + name);
			URLConnection con = url.openConnection();
			
			//System.out.println(name);
			
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} catch(FileNotFoundException ex) {
					name = name.replaceAll("\\([^()]*\\)|[*+ ]+", "");
					url = new URL("http://2007.runescape.wikia.com/wiki/" + name);
					con = url.openConnection();
					in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			}
			if(in == null)
				return;
			String line;
			
			boolean speed = false;
			
			while ((line = in.readLine()) != null) {
				
				//description
				try {
					if(line.contains("Monster attack speed 1")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(1));
						return;
					}
					if(line.contains("Monster attack speed 2")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(2));
						return;
					}
					if(line.contains("Monster attack speed 3")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(3));
						return;
					}
					if(line.contains("Monster attack speed 4")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(4));
						return;
					}
					if(line.contains("Monster attack speed 5")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(5));
						return;
					}
					if(line.contains("Monster attack speed 6")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(6));
						return;
					}
					if(line.contains("Monster attack speed 7")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(7));
						return;
					}
					if(line.contains("Monster attack speed 8")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(8));
						return;
					}
					if(line.contains("Monster attack speed 9")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(9));
						return;
					}
					if(line.contains("Monster attack speed 10")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(10));
						return;
					}
					if(line.contains("Monster attack speed 11")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(11));
						return;
					}
					if(line.contains("Monster attack speed 12")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(12));
						return;
					}
					if(line.contains("Monster attack speed 13")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(13));
						return;
					}
					if(line.contains("Monster attack speed 14")) {
						WeaponDelay.DELAYS.put(id, new WeaponDelay(14));
						return;
					}
				
				} catch(Exception ex) {
					ex.printStackTrace();
				}

			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void getWikiItemData(int id) {
		try {
			boolean debug = false;
			
			String name = ItemDefinition.DEFINITIONS[id].getName().replaceAll(" ", "_");
			if(name.equals("Null") || name.equals("null") || name == "null" || name == "Null")
				return;
			URL url = new URL("http://2007.runescape.wikia.com/wiki/" + name);
			URLConnection con = url.openConnection();
			
			//System.out.println(name);
			
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} catch(FileNotFoundException ex) {
					name = name.replaceAll("\\([^()]*\\)|[*+ ]+", "");
					url = new URL("http://2007.runescape.wikia.com/wiki/" + name);
					con = url.openConnection();
					in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			}
			if(in == null)
				return;
			String line;
			
			boolean tradeable = false;
			boolean equipable = false;
			boolean stackable = false;
			boolean highAlch = false;
			boolean lowAlch = false;
			boolean store = false;
			boolean weight = false;
			boolean examine = false;
			int bonus = 0;
			ItemDefinition.DEFINITIONS[id].bonus = new int[14];
			ItemDefinition.DEFINITIONS[id].twoHanded = false;
			
			while ((line = in.readLine()) != null) {
				
				//Tradeable?
				try {
				if(tradeable) {
					ItemDefinition.DEFINITIONS[id].tradeable = line.contains("Yes");
					tradeable = false;
					if(debug)
					System.out.println("tradeable:"+ItemDefinition.DEFINITIONS[id].tradeable);
				}
				if(line.contains("Tradeable</a>?")) {
					tradeable = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//Equipable?
				try {
				if(equipable) {
					ItemDefinition.DEFINITIONS[id].equipable = line.contains("Yes");
					equipable = false;
					if(debug)
					System.out.println("equipable:"+ItemDefinition.DEFINITIONS[id].equipable);
				}
				if(line.contains("Equipable</a>?")) {
					equipable = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//Stackable?
				/*try {
				if(stackable) {
					ItemDefinition.DEFINITIONS[id].stackable = line.contains("Yes");
					stackable = false;
					if(debug)
					System.out.println("stackable:"+ItemDefinition.DEFINITIONS[id].stackable);
				}
				if(line.contains("Stackable</a>?")) {
					stackable = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				
				//High alch.
				try {
				if(highAlch) {
					if(!line.contains("Unknown") && line.contains("coins")) {
						ItemDefinition.DEFINITIONS[id].highAlchValue = Integer.parseInt(
							line.replaceAll("</th><td> ", "").
							replaceAll("&#160;coins", "").
							replaceAll("&#160;coin", "").
							replaceAll("gp", "").
							replaceAll("\\(4\\)=9 \\(3\\)=", "").replace(",", ""));
					}
					highAlch = false;
					if(debug)
					System.out.println("high alching"+ItemDefinition.DEFINITIONS[id].highAlchValue);
				}
				if(line.contains("High Alch</a>")) {
					highAlch = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//High alch.
				try {
				if(lowAlch) {
					if(!line.contains("Unknown") && line.contains("coins")) {
						ItemDefinition.DEFINITIONS[id].lowAlchValue = Integer.parseInt(
								line.replaceAll("</th><td> ", "").
								replaceAll("&#160;coins", "").
								replaceAll("&#160;coin", "").
								replaceAll("\\(4\\)= 6 \\(3\\)=", "").
								replaceAll("\\(4\\)=9 \\(3\\)=", "").
								replaceAll("gp", "").replace(",", ""));
					}
					lowAlch = false;
					if(debug)
					System.out.println("low alching:"+ItemDefinition.DEFINITIONS[id].lowAlchValue);
				}
				if(line.contains("Low Alch</a>")) {
					lowAlch = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//store price.
				try {
				if(store) {
					if(!line.contains("Not sold")) {
						if(line.contains("coins") && !line.contains("pack") && !line.contains("N/A")  && !line.contains("Not")
								&& !line.contains("Yes") && !line.contains("(4)") && !line.contains("Alfonse_the_Waiter")
								&& !line.contains("The_Fremennik_Trials") && !line.contains("Slayer_reward_points") && !line.contains("for")) {
							line = line.replaceAll("</th><td> ", "").replace(",", "").replace(" ", "");
							String[] pris = line.split("&#160;coins");
							if(pris[0].contains("-")) {
								 String[] prices = pris[0].split("-");
								 int from = Integer.parseInt(prices[0]);
								 int to = Integer.parseInt(prices[1]);
								 ItemDefinition.DEFINITIONS[id].generalPrice = (from+to) / 2;
							} else {
								ItemDefinition.DEFINITIONS[id].generalPrice = Integer.parseInt(pris[0].split(" ")[0]);
							}
							ItemDefinition.DEFINITIONS[id].specialPrice = (int) ((ItemDefinition.DEFINITIONS[id].generalPrice/100.0) * 80);
						}
					}
					store = false;
					if(debug)
					System.out.println("price:"+ItemDefinition.DEFINITIONS[id].generalPrice);
					if(debug)
					System.out.println("special price:"+ItemDefinition.DEFINITIONS[id].specialPrice);
				}
				if(line.contains("Store price</a>")) {
					store = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//weights
				try {
				if(weight) {
					if(!line.contains("Unknown") && !line.contains("Inventory:") && !line.contains("empty")
							 && !line.contains("full") && !line.contains("(2)") && !line.contains("Half")) {
						ItemDefinition.DEFINITIONS[id].weight = Double.parseDouble(
								line.replaceAll("</th><td> ", "").
								replaceAll("&lt;", "").
								replaceAll("~", "").
								replaceAll("&#160;kg", ""));
					}
					weight = false;
					if(debug)
					System.out.println("weight:"+ItemDefinition.DEFINITIONS[id].weight);
				}
				if(line.contains("Weight</a>")) {
					weight = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//description
				try {
				if(examine) {
					if(line.equals("</th></tr>") || line.equals("<tr>"))
						continue;
					ItemDefinition.DEFINITIONS[id].description = line.replaceAll("<td colspan=\"2\" style=\"padding:3px 7px 3px 7px; line-height:140%; text-align:center;\"> ", "");
					examine = false;
					if(debug)
					System.out.println("Examine:"+ItemDefinition.DEFINITIONS[id].description);
				}
				if(line.contains("Examine</a>")) {
					examine = true;
				}
				
				if(line.contains("<td style=\"text-align: center; width: 35px;\">") ||
						line.contains("<td style=\"text-align: center; width: 30px;\">")) {
					line = line.replaceAll("</td>", "").
							replaceAll("<td style=\"text-align: center; width: 35px;\">", "").
							replaceAll("<td style=\"text-align: center; width: 30px;\">", "").
							replaceAll("%", "");
					if(debug)
					System.out.println("bonus:"+bonus+" is " + Integer.parseInt(line));
					if(bonus < 14)
					ItemDefinition.DEFINITIONS[id].bonus[bonus] = Integer.parseInt(line);
					bonus += 1;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
				if(line.contains("data-image-key=\"Shield_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.SHIELD_SLOT;
				}
				if(line.contains("data-image-key=\"Weapon_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.WEAPON_SLOT;
				}
				if(line.contains("data-image-key=\"Head_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.HEAD_SLOT;
				}
				if(line.contains("data-image-key=\"Cape_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.CAPE_SLOT;
				}
				if(line.contains("data-image-key=\"Neck_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.AMULET_SLOT;
				}
				if(line.contains("data-image-key=\"Torso_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.CHEST_SLOT;
				}
				if(line.contains("data-image-key=\"Legs_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.LEGS_SLOT;
				}
				if(line.contains("data-image-key=\"Gloves_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.HANDS_SLOT;
				}
				if(line.contains("data-image-key=\"Boots_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.FEET_SLOT;
				}
				if(line.contains("data-image-key=\"Ring slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.RING_SLOT;
				}
				if(line.contains("data-image-key=\"Ammo_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.ARROWS_SLOT;
				}
				if(line.contains("data-image-key=\"2h_slot.png\"")) {
					ItemDefinition.DEFINITIONS[id].equipmentSlot = Equipment.WEAPON_SLOT;
					ItemDefinition.DEFINITIONS[id].twoHanded = true;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(debug)
				System.out.println("equipmentslot:"+ItemDefinition.DEFINITIONS[id].equipmentSlot);
			in.close();
			
			if(ItemDefinition.DEFINITIONS[id].name.contains("platebody"))
				ItemDefinition.DEFINITIONS[id].platebody = true;
			
			if(ItemDefinition.DEFINITIONS[id].name.contains("full helm"))
				ItemDefinition.DEFINITIONS[id].fullHelm = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setWeaponInterface(int id) {
		ItemDefinition def = ItemDefinition.DEFINITIONS[id];
		if(def == null)
			return;
			if(def.equipmentSlot == 3) {//is weapon
				int i = def.id;
				String name = def.name;
				if(name.contains("staff") || name.contains("Staff")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.STAFF);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.STAFF);
				}
				if(name.contains("hammer") || name.contains("Hammer")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.WARHAMMER);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.WARHAMMER);
				}
				if(name.contains("scythe") || name.contains("Scythe")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.SCYTHE);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.SCYTHE);
				}
				if(name.contains("axe") || name.contains("Axe")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.BATTLEAXE);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.BATTLEAXE);
				}
				if(name.contains("crossbow") || name.contains("Crossbow")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.CROSSBOW);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.CROSSBOW);
				}
				if(name.contains("crossbow") || name.contains("Crossbow")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.CROSSBOW);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.CROSSBOW);
				}
				if(name.contains("shortbow") || name.contains("Shortbow")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.SHORTBOW);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.SHORTBOW);
				}
				if(name.contains("longbow") || name.contains("Longbow")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.LONGBOW);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.LONGBOW);
				}
				if(name.contains("dagger") || name.contains("Dagger")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.DAGGER);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.DAGGER);
				}
				if(name.contains("sword") || name.contains("Sword")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.SWORD);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.SWORD);
				}
				if(name.contains("scimitar") || name.contains("Scimitar")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.SCIMITAR);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.SCIMITAR);
				}
				if(name.contains("longsword") || name.contains("Longsword")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.LONGSWORD);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.LONGSWORD);
				}
				if(name.contains("mace") || name.contains("Mace")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.MACE);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.MACE);
				}
				if(name.contains("knife") || name.contains("Knife")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.KNIFE);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.KNIFE);
				}
				if(name.contains("spear") || name.contains("Spear")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.SPEAR);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.SPEAR);
				}
				if(name.contains("2h") || name.contains("2H")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.TWO_HANDED_SWORD);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.TWO_HANDED_SWORD);
				}
				if(name.contains("pickaxe") || name.contains("Pickaxe")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.PICKAXE);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.PICKAXE);
				}
				if(name.contains("claws") || name.contains("Claws")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.CLAWS);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.CLAWS);
				}
				if(name.contains("halberd") || name.contains("Halberd")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.HALBERD);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.HALBERD);
				}
				if(name.contains("whip") || name.contains("Whip")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.WHIP);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.WHIP);
				}
				if(name.contains("thrownaxe") || name.contains("Thrownaxe")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.THROWNAXE);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.THROWNAXE);
				}
				if(name.contains("dart") || name.contains("Dart")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.DART);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.DART);
				}
				if(name.contains("javelin") || name.contains("Javelin")) {
					if (WeaponInterface.INTERFACES.containsKey(i))
						WeaponInterface.INTERFACES.replace(i, WeaponInterface.JAVELIN);
					else
						WeaponInterface.INTERFACES.put(i, WeaponInterface.JAVELIN);
				}
			}
	}
}