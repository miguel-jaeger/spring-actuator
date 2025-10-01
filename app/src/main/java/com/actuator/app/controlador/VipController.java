package com.actuator.app.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.actuator.app.service.VipAccessService;

@RestController
public class VipController {

    private final VipAccessService vipAccessService;

    // Inyección del servicio
    public VipController(VipAccessService vipAccessService) {
        this.vipAccessService = vipAccessService;
    }

    @GetMapping("/vip/resource")
    public String accessVip(@RequestParam String role) {
        if (vipAccessService.tryVipAccess(role)) {
            return "ACCESS GRANTED: Welcome Admin.";
        } else {
            return "ACCESS DENIED: Role " + role + " is not authorized.";
        }
    }
}

