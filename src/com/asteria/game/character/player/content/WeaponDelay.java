package com.asteria.game.character.player.content;

import java.util.HashMap;
import java.util.Map;

/**
 * The enumerated type whose elements represent the weapon delays.
 *
 * @author Artem Batutin<artembatutin@gmail.com>
 */
public class WeaponDelay {

	/**
	 * The hash collection of all weapon delays.
	 */
	public static final Map<Integer, WeaponDelay> DELAYS = new HashMap<>();
	
	/**
	 * The weapon delay in ticks(600milliseconds).
	 */
	private final int delay;
	
	/**
	 * Constructs a single weapon delay.
	 * @param delay
	 */
	public WeaponDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * Gets the weapon delay.
	 * @return weapon delay.
	 */
	public int getDelay() {
		return delay;
	}
}
