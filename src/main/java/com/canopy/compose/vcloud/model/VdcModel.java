package com.canopy.compose.vcloud.model;

import com.google.gson.annotations.SerializedName;


public class VdcModel {

    public String user;

    public String password;

    public String organization;

    @SerializedName("vcloud_url")
    public String vcloudUrl;

    @SerializedName("vdc_name")
    public String vdcName;

}
