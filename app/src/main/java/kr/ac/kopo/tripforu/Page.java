package kr.ac.kopo.tripforu;

import android.app.Activity;

class Page{
    private Object pageObject = null;
    private byte type = 0;
    
    public Page(Object page, byte type){
        this.pageObject = page;
        this.type = (byte) type;
    }
    public Page(Activity page, byte type){
        this.pageObject = (Object) page;
        this.type = (byte) type;
    }
    
    public Object GetPageObject(){
        return this.pageObject;
    }
    public byte GetPageType(){
        return this.type;
    }
}