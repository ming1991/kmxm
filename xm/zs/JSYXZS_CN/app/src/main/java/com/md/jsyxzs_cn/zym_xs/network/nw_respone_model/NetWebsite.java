package com.md.jsyxzs_cn.zym_xs.network.nw_respone_model;

import java.util.List;

/**
 * Created by androidshuai on 2017/1/5.
 */

public class NetWebsite {

    /**
     * ver : 1.0.4.6.1.0.6.701
     * Mob_And : 1.0
     * Mob_IOS : 1.0
     * links : ["ju999.net","ju888.net","ju77.net"]
     */

    private String ver;
    private String Mob_And;
    private String Mob_IOS;
    private List<String> links;

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getMob_And() {
        return Mob_And;
    }

    public void setMob_And(String Mob_And) {
        this.Mob_And = Mob_And;
    }

    public String getMob_IOS() {
        return Mob_IOS;
    }

    public void setMob_IOS(String Mob_IOS) {
        this.Mob_IOS = Mob_IOS;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }
}
