package com.simon.core.confiog;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program: atlas_oversea_micro_services
 * @description: TODO
 * @author: renBo
 * @create: 2025-07-25 17:02
 **/

@Component
@Data
public class HttpProxyConfigIpIdea {
    @Value(value = "${simon.proxy.host:d1cab39eec96a5ac.gtz.as.ipidea.online}")
    private String proxyHost;
    @Value(value = "${simon.proxy.proxyPort:2333}")
    private Integer proxyPort;
    @Value(value = "${simon.proxy.proxyUsername:atlas_aff_sys-zone-custom-region-}")
    private String proxyUsername;
    @Value(value = "${simon.proxy.proxyPassword:xxxx}")
    private String proxyPassword;
}
