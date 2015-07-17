package com.jd.bluedragon.core.redis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import redis.clients.util.Hashing;
import redis.clients.util.SafeEncoder;

public class MD5Hash implements Hashing {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static final ThreadLocal<MessageDigest> md5Holder = new ThreadLocal() {
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
			}
			throw new IllegalStateException("MD5 Algorithm is not found");
		}
	};

	static final MD5Hash Instance = new MD5Hash();

	public long hash(String key) {
		return hash(SafeEncoder.encode(key));
	}

	public long hash(byte[] key) {
		MessageDigest md5 = (MessageDigest) md5Holder.get();

		md5.reset();
		md5.update(key);
		byte[] bKey = md5.digest();
		long res = (bKey[3] & 0xFF) << 24 | (bKey[2] & 0xFF) << 16
				| (bKey[1] & 0xFF) << 8 | bKey[0] & 0xFF;
		return res;
	}
}
