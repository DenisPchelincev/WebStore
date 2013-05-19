package com.parser;

import javax.annotation.Resource;


public class StartParsing {
	@Resource
	Focalprice focalPrice;
	
	public void init() {
		focalPrice.parseCatalog();
	}
}
