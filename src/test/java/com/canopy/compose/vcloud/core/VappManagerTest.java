package com.canopy.compose.vcloud.core;

import com.canopy.compose.vcloud.model.VappModel;
import com.canopy.compose.vcloud.model.VappTestData;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VappManagerTest {

    private static final Logger LOG = LoggerFactory.getLogger(VappManagerTest.class);

    private final static Gson gson = new Gson();

    @DataProvider(name = "vappData")
    public static Object[][] vappData() {
        try {
            Type listType = new TypeToken<ArrayList<VappModel>>() {
            }.getType();
            List<VappModel> vcloudVappData = gson.fromJson(loadVdcData(), listType);

            List<VappTestData> vappTestDataList = new ArrayList<VappTestData>();
            for (VappModel vappModel : vcloudVappData) {
                if (vappModel.vapps.size() > 0) {
                    final VappManager vappManager = new VappManager(vappModel.vdc);
                    for (String vappName : vappModel.vapps) {
                        final VappTestData vappTestData = new VappTestData(vappManager, vappName);
                        vappTestDataList.add(vappTestData);
                    }
                }
            }

            Object[][] vappData = new Object[vappTestDataList.size()][];
            for (int i = 0; i < vappTestDataList.size(); i++) {
                vappData[i] = new Object[] { vappTestDataList.get(i) };
            }
            return vappData;
        } catch (Exception ie) {
            LOG.error(ie.getMessage(), ie);
            // Suppress Exceptions
        }
        return new Object[][] { };
    }

    /**
     * Return server configuration content as a String.
     *
     * @return
     */
    private static String loadVdcData() {
        try {
            URL url = Resources.getResource("vcloud-vdc-data.json");
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage(), ioe);
            throw new IllegalStateException("Server Configuration missing");
        }
    }

    @Test(dataProvider = "vappData") public void testVappPowerOff(final VappTestData vappTestData) throws Exception {
        try {
            LOG.info("Powering off >> " + vappTestData.vappName);
            final VappManager vappManager = vappTestData.vappManager;
            vappManager.powerOffVapp(vappTestData.vappName);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

}
