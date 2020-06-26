package tk.yallandev.saintmc.common.controller;

import java.util.Map;

import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.giftcode.Giftcode;

public interface GiftcodeController {
	
	/**
	 * Load all gift codes from database
	 */
	
	Map<String, Giftcode> load();
	
	/**
	 * Register a new giftcode
	 * 
	 * @param code
	 * @param giftcode
	 * @return
	 */
	
	boolean registerGiftcode(String code, Giftcode giftcode);
	
	/**
	 * 
	 * Delete a exists giftcode
	 * 
	 * @param code
	 * @return
	 */
	
	boolean deleteGiftcode(String code);
	
	/**
	 * 
	 * Execute giftcode to member
	 * 
	 * @param member
	 * @param code
	 * @return
	 */
	
	ExecutionResponse execute(Member member, String code);
	
	public enum ExecutionResponse {
		
		NOT_FOUND, ALREADY_USED, SUCCESS;
		
	}
}
