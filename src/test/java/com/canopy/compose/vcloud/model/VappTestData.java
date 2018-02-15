package com.canopy.compose.vcloud.model;

import com.canopy.compose.vcloud.core.VappManager;

/**
 * Created by a588232 on 8/16/2015.
 */
public class VappTestData {

    public VappManager vappManager;

    public String vappName;

    public VappTestData(final VappManager vappManager, final String vappName){
        this.vappManager = vappManager;
        this.vappName = vappName;
    }

}
