/* 
	Description:
		ZK Essentials
	History:
		Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import book.services.SidebarPage;
import book.services.SidebarPageConfig;

public class SidebarPageConfigAjaxBasedImpl implements SidebarPageConfig{
	
	HashMap<String,SidebarPage> pageMap = new LinkedHashMap<String,SidebarPage>();
	public SidebarPageConfigAjaxBasedImpl(){		
//		pageMap.put("zk",new SidebarPage("zk","www.zkoss.org","/imgs/site.png","http://www.zkoss.org/"));
//		pageMap.put("demo",new SidebarPage("demo","ZK Demo","/imgs/demo.png","http://www.zkoss.org/zkdemo"));
//		pageMap.put("devref",new SidebarPage("devref","ZK Developer Reference","/imgs/doc.png","http://books.zkoss.org/wiki/ZK_Developer's_Reference"));
//		
//		pageMap.put("fn1",new SidebarPage("fn1","Profile (MVC)","/imgs/fn.png","/chapter5/profile-mvc.zul"));
//		pageMap.put("fn2",new SidebarPage("fn2","Profile (MVVM)","/imgs/fn.png","/chapter5/profile-mvvm.zul"));
//		pageMap.put("fn3",new SidebarPage("fn3","Todo List (MVC)","/imgs/fn.png","/chapter6/todolist-mvc.zul"));
//		pageMap.put("fn4",new SidebarPage("fn4","Todo List (MVVM)","/imgs/fn.png","/chapter6/todolist-mvvm.zul"));
		
		pageMap.put("overview",new SidebarPage("overview","Overview","/images/demo.png","/book/chapters/overview.zul"));
		pageMap.put("mapping",new SidebarPage("mapping","Mapping Statistics","/images/demo.png","/book/chapters/mapping_statistics.zul"));
		pageMap.put("expression",new SidebarPage("expression","Expression analysis",null,"/book/chapters/expression.zul"));
		pageMap.put("introduction",new SidebarPage("introduction","Introduction","/images/demo.png","/book/chapters/introduction.zul"));
		pageMap.put("results",new SidebarPage("results","Results","/images/demo.png","/book/chapters/results.zul"));
	}
	
	public List<SidebarPage> getPages(){
		return new ArrayList<SidebarPage>(pageMap.values());
	}
	
	public SidebarPage getPage(String name){
		return pageMap.get(name);
	}
	
}