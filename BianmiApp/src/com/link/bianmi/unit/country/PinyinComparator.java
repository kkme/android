package com.link.bianmi.unit.country;

import java.util.Comparator;

public class PinyinComparator implements Comparator<Country> {

	public int compare(Country o1, Country o2) {
		if (o1.letter.equals("@") || o2.letter.equals("#")) {
			return -1;
		} else if (o1.letter.equals("#") || o2.letter.equals("@")) {
			return 1;
		} else {
			return o1.letter.compareTo(o2.letter);
		}
	}

}
