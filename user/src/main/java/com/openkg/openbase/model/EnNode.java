package com.openkg.openbase.model;

/**
 * Created by mi on 18-10-22.
 */
import com.openkg.openbase.model.Neo4jModule.Node;
public class EnNode extends Node{
    private String type;

    public String getId(){return super.getId();}
    public String getName(){return super.getName();}
    public String getType(){return type;}

    public EnNode(String id,String name, String type){
        super(id, name);
        this.type=type;
    }

}