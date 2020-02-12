package com.openkg.openbase.model;

import com.openkg.openbase.model.Neo4jModule.Node;
public class DataPropertyNode extends Node{
    private String value;
    private String type = "field";

    public String getId(){return super.getId();}
    public String getName(){return super.getName();}
    public String getValue(){return value;}
    public String getType(){return type;}

    public DataPropertyNode(String id,String property_name, String property_value){
        super(id, property_name);
        this.value=property_value;
    }
}
