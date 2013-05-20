package com.parser;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class Focalprice {
	private static final String SITE_URL = "http://www.focalprice.com";

	@Resource(name = "blCatalogService")
	CatalogService categoryService;

	public String getSiteUrl() {
		return SITE_URL;
	}

	public void parseCatalog() {
		List<Category> list = categoryService.findAllCategories();
		for (Category category : list) {
			System.out.println(category.getId() + " : " + category.getName());
		}

		Category root = categoryService.findCategoryById(2L);
		System.out.println("Root: " + root.getId() + " : " + root.getName());

		try {
			Document doc = Jsoup.connect(getSiteUrl()).get();

			for (Element litem : doc.select("div#sidenav div.sidebar_menu div.litem")) {
				Element category = litem.select("span > a").first();
				String ids = category.attr("href");
				int capos = ids.indexOf("ca-");
				if(capos < 0 ){
					continue;
				}
				ids = ids.substring(capos + 3, ids.indexOf(".html"));

				System.out.println(category.text() + " : " + category.attr("href") + " : " + ids);

				Category categoryEntity = new CategoryImpl();
				categoryEntity.setName(category.text());
				categoryEntity.setId(Long.valueOf("10" + ids));
				categoryEntity.setDefaultParentCategory(root);
				categoryEntity.getAllParentCategories().add(root);
				categoryEntity.setActiveStartDate(new Date());
				categoryEntity.setUrl("/ca-"+ids);
				categoryService.saveCategory(categoryEntity);
				
				for (Element subCat : litem.select("li.sub_category_self")) {
					Element subCategory = subCat.select("> a").first();
					System.out.println("  " + subCategory.text() + " : " + subCategory.attr("href"));

					for (Element subitem : subCat.select("ul li > a")) {
						System.out.println("    " + subitem.text() + " : " + subitem.attr("href"));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
