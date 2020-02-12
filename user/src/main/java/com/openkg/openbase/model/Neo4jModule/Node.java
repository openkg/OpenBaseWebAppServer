package com.openkg.openbase.model.Neo4jModule;

/**
 * Created by mi on 18-10-22.
 */
public class Node {
    private String id;
    private String name;

    public String getId(){return id;}
    public String getName(){return name;}

    public Node(String id,String name){
        this.id = id;
        this.name= name;
    }
    Node(){}



}
