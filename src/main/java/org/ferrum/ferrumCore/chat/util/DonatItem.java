package org.ferrum.ferrumCore.chat.util;

import org.bukkit.Material;

public class DonatItem{

    private final String id;
    private final String name;
    private final Material material;
    private final String permission;
    private final String content;

    public DonatItem(String id, String name, Material material, String permission, String content){
        this.id = id;
        this.name = name;
        this.material = material;
        this.permission = permission;
        this.content = content;
    }

    public String getId(){
        return this.id;
    }
    public String getName(){ return this.name; }
    public Material getMaterial(){
        return this.material;
    }
    public String getPermission(){
        return this.permission;
    }
    public String getContent(){
        return this.content;
    }

}