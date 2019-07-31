package com.baishiyuan.controller;

import com.baishiyuan.component.FlowComponent;
import com.baishiyuan.component.UserInfoComponent;
import com.baishiyuan.domain.SessionInfo;
import com.baishiyuan.domain.UserInfo;
import com.baishiyuan.domain.WebResult;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.AuthorityUtils;
import com.baishiyuan.utils.Page;
import com.baishiyuan.utils.StringConst;
import com.baishiyuan.utils.UserSessionFunCallUtil;
import com.baishiyuan.vo.FlowVO;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Administrator on 2019/7/24 0024.
 */
@RestController
@RequestMapping("/flow")
public class FlowController {

    private static final Logger logger = Logger.getLogger(FlowController.class);

    @Resource
    private FlowComponent flowComponent;

    @Resource
    private UserInfoComponent userInfoComponent;

    @RequestMapping(value = "/queryFlowBySelf", method = {RequestMethod.GET})
    public WebResult queryUserAccountFlowBySelf(@RequestParam int pageNo, @RequestParam int pageSize, HttpServletRequest request) {
        SessionInfo sessionInfo = getSession(request);

        Page page = flowComponent.queryFlowByPage(sessionInfo.getUserId(), pageNo, pageSize);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }

    @RequestMapping(value = "/queryFlowByNickName", method = {RequestMethod.GET})
    public WebResult queryFlowByNickName(@RequestParam int pageNo, @RequestParam int pageSize, @RequestParam String userName, HttpServletRequest request) {
        SessionInfo sessionInfo = checkFlowAuth(request);

        UserInfo userInfo = userInfoComponent.getUserInfoByNickName(userName);
        if(userInfo == null) {
            throw new MessageException(StringConst.ERRCODE_X, "未找到该用户");
        }

        Page page = flowComponent.queryFlowByPage(userInfo.getUserId(), pageNo, pageSize);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }

    @RequestMapping(value = "/exportFlowByNickName", method = {RequestMethod.GET})
    public void exportFlowByNickName(@RequestParam int pageNo, @RequestParam int pageSize, @RequestParam String userName, HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = checkFlowAuth(request);

        UserInfo userInfo = userInfoComponent.getUserInfoByNickName(userName);
        if(userInfo == null) {
            throw new MessageException(StringConst.ERRCODE_X, "未找到该用户");
        }

        Page page = flowComponent.queryFlowByPage(userInfo.getUserId(), pageNo, pageSize);
        List<FlowVO> flowVOS = page.getList();

        HSSFWorkbook workBook = new HSSFWorkbook();
        Sheet sheet = workBook.createSheet("收益榜导出表");
        Row row = sheet.createRow(0);
        HSSFCellStyle style = workBook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        Cell cell = row.createCell( 0);
        cell.setCellValue("昵称");
        cell.setCellStyle(style);
        cell = row.createCell( 1);
        cell.setCellValue("时间");
        cell.setCellStyle(style);
        cell = row.createCell( 2);
        cell.setCellValue("金额");
        cell.setCellStyle(style);
        cell = row.createCell( 3);
        cell.setCellValue("购买商品的客户");
        cell.setCellStyle(style);

        int totalPage = flowVOS.size();
        int rowCount = 1;
        for(int i = 0; i <= totalPage-1; i++){
            FlowVO flowVO = flowVOS.get(i);
            Row dataRow = sheet.createRow(rowCount);
            style.setAlignment(HorizontalAlignment.CENTER);
            Cell dataCell = dataRow.createCell( 0);
            dataCell.setCellValue(userInfo.getNickName());
            dataCell.setCellStyle(style);

            style.setAlignment(HorizontalAlignment.CENTER);
            dataCell = dataRow.createCell( 1);
            dataCell.setCellValue(flowVO.getGmtCreate().substring(0, 10));
            dataCell.setCellStyle(style);

            style.setAlignment(HorizontalAlignment.CENTER);
            dataCell = dataRow.createCell( 2);
            dataCell.setCellValue(new Double(flowVO.getChangeMoney()).toString().concat(", ").concat(flowVO.getReason()));
            dataCell.setCellStyle(style);

            style.setAlignment(HorizontalAlignment.CENTER);
            dataCell = dataRow.createCell( 3);
            if(!StringUtils.isEmpty(flowVO.getClientName())) {
                dataCell.setCellValue(flowVO.getClientName());
            }else {
                dataCell.setCellValue("");
            }
            dataCell.setCellStyle(style);
            rowCount++;
        }

        String fileName = "个人流水详情.xls";
        try {
            byte[] bytes = fileName.getBytes("gb2312");
            fileName = new String(bytes, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try
        {
            response.setContentType("application/msexcel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workBook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private SessionInfo getSession(HttpServletRequest request) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        return sessionInfo;
    }

    private SessionInfo checkFlowAuth(HttpServletRequest request) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkFlowScanAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        return sessionInfo;
    }

}
