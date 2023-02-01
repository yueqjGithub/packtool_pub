package com.avalon.packer.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/test")
@Api(tags = "巡检")
public class ProbeController {
    @GetMapping("/probe")
    @ApiOperation(value = "巡检接口")
    public String CheckProbe () {
        return "ok";
    }
}
