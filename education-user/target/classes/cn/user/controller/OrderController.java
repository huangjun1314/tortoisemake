package cn.user.controller;

import cn.beans.dto.Dto;
import cn.beans.entity.OrderInfo;
import cn.beans.entity.OrderPay;
import cn.common.utils.DtoUtil;
import cn.common.utils.MD5;
import cn.dao.courseservice.order.OrderInfoService;
import cn.dao.courseservice.order.OrderPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Api(tags = "生成订单接口")
public class OrderController {
    @Autowired
    OrderInfoService orderInfoService;  //订单信息

    @Autowired
    OrderPayService orderPayService;     //订单支付信息


    @PostMapping("/addOrder")
    @ApiOperation(value = "生成订单",notes = "对象")
    public Dto addOrder(@RequestBody OrderInfo info){
        String orderNo= MD5.getMd5(new Date().toLocaleString(),10);
        info.setGmtCreate(new Date());
        info.setOrderNo(Long.valueOf(orderNo));
        OrderInfo info1=orderInfoService.insert(info);
        if (info1!=null){
            return DtoUtil.returnSuccess("200",info1);
        }
        return DtoUtil.returnSuccess("失败");
    }
}
