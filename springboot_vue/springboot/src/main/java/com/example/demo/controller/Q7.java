//package com.example.demo.controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.example.demo.common.Result;
//import com.example.demo.enity.center;
//import com.example.demo.mapper.CenterMapper;
//import lombok.Data;
//import org.springframework.web.bind.annotation.*;
//import com.example.demo.method.loader.bestLoader;
//import javax.annotation.Resource;
//import java.sql.SQLException;
//import com.example.demo.method.client.DatabaseManipulation;
//
//@RestController
//@RequestMapping("/Q7")
//@CrossOrigin
//@ResponseBody
//public class Q7 {
//    DatabaseManipulation db=new DatabaseManipulation();
//    @Resource
//    CenterMapper centerMapper;
//
//    @GetMapping
//    public String find() throws SQLException {
//        String re=db.getContractCount();
//        return re;
//    }
//    @GetMapping("/Q8")
//    public String x1() throws SQLException {
//        String cv=db.getOrderCount();
//        return cv;
//    }
//}
