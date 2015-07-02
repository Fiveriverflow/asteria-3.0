package com.asteria.game.item;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.asteria.game.item.container.Equipment;

/**
 * The container that represents an item definition.
 *
 * @author lare96 <http://github.com/lare96>
 */
public class ItemDefinition {

    /**
     * The array that contains all of the item definitions.
     */
    public static ItemDefinition[] DEFINITIONS = new ItemDefinition[13223];

    /**
     * The identifier for the item.
     */
    public int id;

    /**
     * The proper name of the item.
     */
    public String name;

    /**
     * The description of the item.
     */
    public String description;

    /**
     * The equipment slot of this item.
     */
    public int equipmentSlot;
    
    /**
     * The flag that determines if the item is equipable.
     */
    public boolean equipable;

    /**
     * The flag that determines if the item is noteable.
     */
    public boolean noteable;

    /**
     * The flag that determines if the item is stackable.
     */
    public boolean stackable;
    
    /**
     * The flag that determines if the item can be traded.
     */
    public boolean tradeable;

    /**
     * The special store price of this item.
     */
    public int specialPrice;

    /**
     * The general store price of this item.
     */
    public int generalPrice;

    /**
     * The low alch value of this item.
     */
    public int lowAlchValue;

    /**
     * The high alch value of this item.
     */
    public int highAlchValue;

    /**
     * The weight value of this item.
     */
    public double weight;

    /**
     * The array of bonuses for this item.
     */
    public int[] bonus;
    
    /**
     * The array of actions for this item.
     */
    public String[] actions;

    /**
     * The flag that determines if this item is two-handed.
     */
    public boolean twoHanded;

    /**
     * The flag that determines if this item is a full helmet.
     */
    public boolean fullHelm;
    
    /**
     * The flag that determines if this item is a platebody.
     */
    public boolean platebody;

    /**
     * Creates a new {@link ItemDefinition}.
     *
     * @param id
     *            the identifier for the item.
     * @param name
     *            the proper name of the item.
     * @param description
     *            the description of the item.
     * @param equipmentSlot
     *            the equipment slot of this item.
     * @param notedId
     * 			  the noted item id of this item.
     * @param noteable
     *            the flag that determines if the item is noteable.
     * @param stackable
     *            the flag that determines if the item is stackable.
     * @param specialPrice
     *            the special store price of this item.
     * @param generalPrice
     *            the general store price of this item.
     * @param lowAlchValue
     *            the low alch value of this item.
     * @param highAlchValue
     *            the high alch value of this item.
     * @param weight
     *            the weight value of this item.
     * @param bonus
     *            the array of bonuses for this item.
     * @param actions
     * 			  the array of actions for this item.
     * @param twoHanded
     *            the flag that determines if this item is two-handed.
     * @param fullHelm
     *            the flag that determines if this item is a full helmet.
     * @param platebody
     *            the flag that determines if this item is a platebody.
     */
    public ItemDefinition(int id, String name, String description, int equipmentSlot, boolean equipable, boolean noteable, boolean stackable, boolean tradeable,
    		int specialPrice, int generalPrice, int lowAlchValue, int highAlchValue, double weight, int[] bonus, String[] actions, boolean twoHanded,
        boolean fullHelm, boolean platebody) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.equipmentSlot = equipmentSlot;
        this.equipable = equipable;
        this.noteable = noteable;
        this.stackable = stackable;
        this.tradeable = tradeable;
        this.specialPrice = specialPrice;
        this.generalPrice = generalPrice;
        this.lowAlchValue = lowAlchValue;
        this.highAlchValue = highAlchValue;
        this.weight = weight;
        this.bonus = bonus;
        this.actions = actions;
        this.twoHanded = twoHanded;
        this.fullHelm = fullHelm;
        this.platebody = platebody;
        prayerBonus();
    }

    /**
     * The method that erases the prayer bonus from ranged weapons.
     */
    public void prayerBonus() {
        if (equipmentSlot == Equipment.ARROWS_SLOT || name.contains("knife") || name.contains("dart") || name.contains("thrownaxe") || name
            .contains("javelin")) {
        	if(bonus != null)
            bonus[11] = 0;
        }
    }

    /**
     * Gets the identifier for the item.
     *
     * @return the identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the proper name of the item.
     *
     * @return the proper name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the item.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the equipment slot of this item.
     *
     * @return the equipment slot.
     */
    public int getEquipmentSlot() {
        return equipmentSlot;
    }

    /**
     * Determines if the item is noted or not.
     *
     * @return {@code true} if the item is noted, {@code false} otherwise.
     */
    public boolean isNoted() {
        return description.equals("Swap this note at any bank for the " + "equivalent item.");
    }
    
    /**
     * Determines if the item is equipable or not.
     *
     * @return {@code true} if the item is equipable, {@code false} otherwise.
     */
    public boolean isEquipable() {
        return equipable;
    }

    /**
     * Determines if the item is noteable or not.
     *
     * @return {@code true} if the item is noteable, {@code false} otherwise.
     */
    public boolean isNoteable() {
        return noteable;
    }

    /**
     * Determines if the item is stackable or not.
     *
     * @return {@code true} if the item is stackable, {@code false} otherwise.
     */
    public boolean isStackable() {
        return stackable;
    }
    
    /**
     * Determines if the item is tradable or not.
     *
     * @return {@code true} if the item is tradeable, {@code false} otherwise.
     */
    public boolean isTradeable() {
        return tradeable;
    }

    /**
     * Gets the special store price of this item.
     *
     * @return the special price.
     */
    public int getSpecialPrice() {
        return specialPrice;
    }

    /**
     * Gets the general store price of this item.
     *
     * @return the general price.
     */
    public int getGeneralPrice() {
        return generalPrice;
    }

    /**
     * Gets the low alch value of this item.
     *
     * @return the low alch value.
     */
    public int getLowAlchValue() {
        return lowAlchValue;
    }

    /**
     * Gets the high alch value of this item.
     *
     * @return the high alch value.
     */
    public int getHighAlchValue() {
        return highAlchValue;
    }

    /**
     * Gets the weight value of this item.
     *
     * @return the weight value.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Gets the array of bonuses for this item.
     *
     * @return the array of bonuses.
     */
    public int[] getBonus() {
        return bonus;
    }

    /**
     * Determines if this item is two-handed or not.
     *
     * @return {@code true} if this item is two-handed, {@code false} otherwise.
     */
    public boolean isTwoHanded() {
        return twoHanded;
    }

    /**
     * Determines if this item is a full helmet or not.
     *
     * @return {@code true} if this item is a full helmet, {@code false}
     *         otherwise.
     */
    public boolean isFullHelm() {
        return fullHelm;
    }

    /**
     * Determines if this item is a platebody or not.
     *
     * @return {@code true} if this item is a platebody, {@code false}
     *         otherwise.
     */
    public boolean isPlatebody() {
        return platebody;
    }
    
    /**
     * Gets the flag if the item contrains the action.
     * 
     * @param action the action to check.
     * @return {@code true} if this item has the action, {@code false}
     *         otherwise.
     */
    public boolean hasAction(String action) {
    	for(String act : actions) {
    		if(act.equals(action))
    			return true;
    	}
    	return false;
    }
    
    /**
     * Gets the JSON object.
     * @return
     */
    public JSONObject getJson() {
    	JSONObject o = new JSONObject();
    	o.put("id", id);
    	o.put("name", name);
    	o.put("examine", description);
    	o.put("equipmentSlot", equipmentSlot);
    	o.put("equipable", equipable);
    	o.put("noteable", noteable);
    	o.put("stackable", stackable);
    	o.put("tradeable", tradeable);
    	o.put("specialStorePrice", specialPrice);
    	o.put("generalStorePrice", generalPrice);
    	o.put("highAlchValue", highAlchValue);
    	o.put("lowAlchValue", lowAlchValue);
    	o.put("weight", weight);
    	
    	JSONArray bonuses = new JSONArray();
    	for(int b = 0; b < 14; b++) {
    		bonuses.add(bonus == null ? 0 : (b >= bonus.length ? 0 : bonus[b]));
    	}
    	o.put("bonuses", bonuses);
    	
    	JSONArray acts = new JSONArray();
    	if(actions != null) {
    		for(String a : actions) {
    			acts.add(a);
    		}
    		o.put("actions", acts);
    	}
    	o.put("twoHanded", twoHanded);
    	o.put("platebody", platebody);
    	o.put("fullHelm", fullHelm);
    	return o;
    }
}