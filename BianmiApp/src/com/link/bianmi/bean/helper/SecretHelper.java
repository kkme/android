package com.link.bianmi.bean.helper;

import android.database.Cursor;

import com.link.bianmi.bean.Secret;
import com.link.bianmi.bean.Tmodel;

public class SecretHelper {

	public static class DB {

		public static void cleanSecret() {

		}

		public static Cursor fetch() {

			return null;
		}

		public static void addSecret(Secret[] t) {

		}

	}

	public static class API {

		/** 单页数量 **/
		private static final int pageSize = 20;

		public static Secret[] getSecrets() {

			return null;
		}

		public static Tmodel<Secret[]> getSecretsArray(int page) {

			return null;
		}

	}
}
