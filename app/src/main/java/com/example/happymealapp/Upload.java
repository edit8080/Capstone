package com.example.happymealapp;
public class Upload {
    private String mName;
    private String mImageUrl;
    private String mReview;
    private String mHashTag;
    private String mUser;
    private String mProfile_img_url;
    public Upload()
    {
        //empty construct needed
    }

    public Upload(String name, String imageUrl, String review, String hashtag,String username, String profile_img_url)
    {
        if(name.trim().equals(""))
        {
            name = "No Name";

        }

        mName = name;
        mImageUrl = imageUrl;
        mReview = review;
        mHashTag = hashtag;
        mUser = username;
        mProfile_img_url = profile_img_url;
    }
    public String getmName()
    {
        return mName;
    }
    public String getmReview() { return mReview;}
    public void setName(String name)
    {
        mName=name;
    }
    public String getmImageUrl()
    {
        return mImageUrl;
    }
    public String getmUser()
    {
        return mUser;
    }
    public String getmProfile_img_url()
    {
        return mProfile_img_url;
    }
    public String getmHashTag()
    {
        return mHashTag;
    }
    public void setmUser(String username)
    {
        mUser=username;
    }
    public void setmProfile_img_url(String profile_img_url)
    {
        mProfile_img_url=profile_img_url;
    }
    public void setImageUrl(String imageUrl)
    {
        mImageUrl=imageUrl;
    }
    public void setmHashTag(String hashTag){mHashTag = hashTag;}
}
