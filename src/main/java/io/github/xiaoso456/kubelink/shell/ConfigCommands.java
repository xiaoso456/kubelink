package io.github.xiaoso456.kubelink.shell;

import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.config.KubeConfig;
import io.github.xiaoso456.kubelink.exception.LinkException;
import io.github.xiaoso456.kubelink.service.ConfigManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Command(command = "configs")
@Slf4j
public class ConfigCommands {

    // @Autowired
    // private ConfigManagementService configManagementService;
    //
    // @Command(command = "")
    // public String config(){
    //     String info = "current active configs [{}]";
    //     return StrUtil.format(info,configManagementService.getActiveName());
    // }
    //
    // @Command(command = "list")
    // public List<String> configList() {
    //     return configManagementService.getConfigNameList();
    // }
    //
    // @Command(command = "use")
    // public String configUse(@Option(required = true) String configName) {
    //     try {
    //         configManagementService.activeConfig(configName);
    //     } catch (LinkException e) {
    //         log.error("change configs failed",e);
    //         return e.getMessage();
    //     }
    //     String info = "configs change to [{}]";
    //     return StrUtil.format(info,configName);
    //
    // }
}
