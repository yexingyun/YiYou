package yiyou.heedu.com.yiyou.model.domain;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Imguser extends BmobObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private BmobFile file;

	public Imguser(){
		
	}
	
	public Imguser(String name, BmobFile file){
		this.name =name;
		this.file = file;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BmobFile getFile() {
		return file;
	}

	public void setFile(BmobFile file) {
		this.file = file;
	}
	
	
	
}
