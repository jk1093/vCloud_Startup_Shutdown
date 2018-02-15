package com.canopy.compose.vcloud.model;

import java.io.Serializable;
import java.util.Set;


public class VappModel implements Serializable {

    public VdcModel vdc;

    public Set<String> vapps;
}
