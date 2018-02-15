package com.canopy.compose.vcloud.core;

import com.canopy.compose.vcloud.factory.FakeSSLSocketFactory;
import com.canopy.compose.vcloud.model.VdcModel;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.*;
import com.vmware.vcloud.sdk.constants.VMStatus;
import com.vmware.vcloud.sdk.constants.VappStatus;
import com.vmware.vcloud.sdk.constants.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

/**
 * Created by a588232 on 8/15/2015.
 */
public class VappManager {

    private static final Logger LOG = LoggerFactory.getLogger(VappManager.class);

    private final VdcModel vdcModel;

    public VappManager(VdcModel vdcModel) {
        this.vdcModel = vdcModel;
    }

    private VcloudClient getVcloudClient() {
        VcloudClient.setLogLevel(Level.OFF);
        LOG.info("Vcloud Login");
        VcloudClient vcloudClient = new VcloudClient(this.vdcModel.vcloudUrl, Version.V5_1);
        try {
            vcloudClient.registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
            vcloudClient.login(this.vdcModel.user, this.vdcModel.password);
        } catch (KeyManagementException ke) {
            LOG.error(ke.getMessage(), ke);
            throw new RuntimeException(ke.getMessage());
        } catch (UnrecoverableKeyException ue) {
            LOG.error(ue.getMessage(), ue);
            throw new RuntimeException(ue.getMessage());
        } catch (NoSuchAlgorithmException ne) {
            LOG.error(ne.getMessage(), ne);
            throw new RuntimeException(ne.getMessage());
        } catch (KeyStoreException ke) {
            LOG.error(ke.getMessage(), ke);
            throw new RuntimeException(ke.getMessage());
        } catch (VCloudException ve) {
            LOG.error(ve.getMessage(), ve);
            throw new RuntimeException(ve.getMessage());
        }
        return vcloudClient;
    }

    private Vdc findVdcByName(final VcloudClient vcloudClient, String vdcName) throws VCloudException {
        ReferenceType orgRef = vcloudClient.getOrgRefsByName().get(vcloudClient.getOrgName());
        Organization org = Organization.getOrganizationByReference(vcloudClient, orgRef);
        ReferenceType vdcRef = org.getVdcRefByName(vdcName);
        return Vdc.getVdcByReference(vcloudClient, vdcRef);
    }

    /**
     * Poweroff vApp
     * @param vAppName
     * @throws VCloudException
     */
    public void powerOffVapp(String vAppName) throws VCloudException {
        final VcloudClient vcloudClient = getVcloudClient();
        try {
            Vdc vdc = findVdcByName(vcloudClient, vdcModel.vdcName);
            ReferenceType vappRef = vdc.getVappRefByName(vAppName);
            if(vappRef != null){
               Vapp vapp = Vapp.getVappByReference(vcloudClient, vappRef);
               if(vapp != null){
                    powerOffVappVMs(vapp);
                }
            }
            /*
                No need to poweroff vApp as all the vm's are powered off.
                if (vapp.getVappStatus() == VappStatus.POWERED_OFF) {
                    LOG.info(String.format("Vapp '%s' status is powered off.", vAppName));
                } else if (vapp.getVappStatus() == VappStatus.POWERED_ON) {
                    LOG.info(String.format("Vapp '%s' status is powered on, powering off now ...", vAppName));
                    vapp.powerOff().waitForTask(0);
                }
            */
        } /* catch (TimeoutException te) {
            LOG.error(te.getMessage(), te);
            throw new RuntimeException(te.getMessage());
        } */ finally {
            vcloudClient.logout();
        }
    }

    /**
     * Poweroff VM's of vAPP.
     * @param vapp
     * @throws VCloudException
     */
    private void powerOffVappVMs(Vapp vapp) throws VCloudException {
        try {
            for (VM vm : vapp.getChildrenVms()) {
                if (vm.getVMStatus() == VMStatus.POWERED_OFF) {
                    LOG.info(String.format("VM '%s' status is powered off.", vm));
                } else if (vm.getVMStatus() == VMStatus.POWERED_ON) {
                    LOG.info(String.format("VM '%s' status is powered on, powering off now ...", vm));
                    vm.powerOff().waitForTask(0);
                }
            }
        } catch (TimeoutException te) {
            LOG.error(te.getMessage(), te);
            throw new RuntimeException(te.getMessage());
        }
    }

}
