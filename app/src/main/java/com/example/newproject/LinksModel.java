package com.example.newproject;

public class LinksModel {
    String impLinks,linkDesc;

    public LinksModel() {

    }

    public LinksModel(String impLinks, String linkDesc) {
        this.impLinks = impLinks;
        this.linkDesc = linkDesc;
    }

    public String getImpLinks() {
        return impLinks;
    }

    public void setImpLinks(String impLinks) {
        this.impLinks = impLinks;
    }

    public String getLinkDesc() {
        return linkDesc;
    }

    public void setLinkDesc(String linkDesc) {
        this.linkDesc = linkDesc;
    }
}
