package com.sahilhans0605.firebaseusersignup.dataModel;

public class postDataModel {
    String description;
    String purl;
    String id;
    String postId;

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    String postDate;

    public postDataModel(String description, String purl, String id, String postId,String postDate) {
        this.description = description;
        this.purl = purl;
        this.id = id;
//        here id is obviously the publisher id
        this.postId = postId;
        this.postDate=postDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public postDataModel() {

    }

}
