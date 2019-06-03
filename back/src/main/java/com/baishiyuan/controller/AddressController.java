package com.baishiyuan.controller;

import com.baishiyuan.domain.WebResult;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.AddressInfo;
import com.baishiyuan.utils.AdressParse;
import com.baishiyuan.utils.ErrorInfo;
import com.baishiyuan.utils.StringConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/resolveAddresses")
public class AddressController {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "", method = RequestMethod.POST)
    public WebResult createGoods(@RequestParam String info, HttpServletRequest request) {
        AddressInfo addressInfo = AdressParse.parseAddress(info);
        if(addressInfo == null) {
            throw new MessageException(ErrorInfo.ERROR, "地址解析失败");
        }

        return new WebResult(StringConst.ERRCODE_SUCCESS, "地址解析成功", addressInfo);
    }

}
