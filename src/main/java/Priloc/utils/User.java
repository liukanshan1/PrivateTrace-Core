package Priloc.utils;

import sg.smu.securecom.keys.PaillierPrivateKey;
import sg.smu.securecom.keys.PaillierThdPrivateKey;
import sg.smu.securecom.protocol.Paillier;
import sg.smu.securecom.protocol.PaillierThdDec;

import java.io.IOException;

public class User {

	public static PaillierPrivateKey prikey = null;
	public static Paillier pai = null;
	private static PaillierThdPrivateKey[] ThdKey = null;
	public static PaillierThdDec cp = null;
	public static PaillierThdDec csp = null;

	static {
		Keys key;
		try {
			key = (Keys) Utils.readObject("./keys");
		} catch (Exception e) {
			key = new Keys(Constant.KEY_LEN);
			try {
				Utils.writeObject(key, "./keys");
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		prikey = key.prikey;
		pai = key.pai;
		ThdKey = key.ThdKey;
		cp = key.cp;
		csp = key.csp;
	}
}
