package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.enity.center;
import com.example.demo.mapper.CenterMapper;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import com.example.demo.method.loader.bestLoader;
import javax.annotation.Resource;
import java.sql.SQLException;
import com.example.demo.method.client.DatabaseManipulation;

@RestController
@RequestMapping("/center")
@CrossOrigin
@ResponseBody
public class CenterController {
    DatabaseManipulation db=new DatabaseManipulation();
    bestLoader bs=new bestLoader();
    @Resource
    CenterMapper centerMapper;

    @PostMapping("/load")
    public Result<?> load() throws SQLException {
        bestLoader.loadData();
        return Result.success("[{\"huanyuhai\": \"sb\"}]");

    }
    @GetMapping("/Q6")
    public String find() throws SQLException {

        String re=db.getAllStaffCount();

        return re;
    }
    @GetMapping("/Q7")
    public String find7() throws SQLException {

        String re=db.getContractCount();

        return re;
    }
    @GetMapping("/Q8")
    public String find8() throws SQLException {

        String re=db.getOrderCount();

        return re;
    }
    @GetMapping("/Q9")
    public String find9() throws SQLException {

        String re=db.getNeverSoldProduct();

        return re;
    }
    @GetMapping("/Q10")
    public String find10() throws SQLException {

        String re=db.getFavoriteProductModel();

        return re;
    }
    @GetMapping("/Q11")
    public String find11() throws SQLException {

        String re=db.getAvgStockByCenter();

        return re;
    }
    @GetMapping("/Q12")
    public String find12(@RequestParam String search) throws SQLException {

        String re=db.getProductByNumber(search);

        return re;
    }
    @GetMapping("/Q131")
    public String  find131(@RequestParam String search) throws SQLException {

        String re=db.getContractInfoFirst(search);

        return re;
    }
    @GetMapping("/Q132")
    public String find132(@RequestParam String search) throws SQLException {

        String re=db.getContractInfoSecond(search);

        return re;
    }
    @GetMapping("/selectStaff")
    public String selectStaff(@RequestParam String search) throws SQLException {

        String re=bs.WuSuoWei(search);

        return re;
    }
    @GetMapping("/selectCenter")
    public String selectCenter(@RequestParam String search) throws SQLException {

        String re=bs.WuSuoWei(search);

        return re;
    }
    @GetMapping("/selectModel")
    public String selectModel(@RequestParam String search) throws SQLException {

        String re=bs.WuSuoWei(search);

        return re;
    }
    @GetMapping("/selectenterprise")
    public String selectenterprise (@RequestParam String search) throws SQLException {

        String re=bs.WuSuoWei(search);

        return re;
    }
    @GetMapping("/selectOrders")
    public String selectOrders (@RequestParam String search) throws SQLException {

        String re=bs.WuSuoWei(search);

        return re;
    }
    @GetMapping("/selectBill")
    public String selectBill () throws SQLException {
        bs.loadBill();
        String re=bs.showBill();
        return re;
    }

    @GetMapping("/others")
    public Result<?> others(@RequestParam String search) throws SQLException {
        String re=bs.WuSuoWei(search);
        return Result.success(re);

    }
    @PostMapping("/boss")
    public Result<?> boss() throws SQLException {
        bs.toBoss();
        return Result.success("[{\"huanyuhai\": \"sb\"}]");

    }

    @PostMapping("/worker")
    public Result<?> worker() throws SQLException {
        bs.toStaff1();
        return Result.success("[{\"huanyuhai\": \"sb\"}]");

    }
    @PostMapping("/OrignLoad")
    public Result<?> OrignLoad() throws SQLException {
        bs.load_basic_data();
        return Result.success("[{\"huanyuhai\": \"sb\"}]");

    }
    @PostMapping("/deleteOrder")
    public Result<?> deleteOrder() throws SQLException {
        bestLoader.deleteOrder(bestLoader.fileName[8]);
        System.out.println(bestLoader.fileName[8]);
        return Result.success("[{\"huanyuhai\": \"sb\"}]");

    }
    @PostMapping("/UpdateOrder")
    public Result<?>  UpdateOrder() throws SQLException {
        bestLoader.updateOrder(bestLoader.fileName[7]);
        System.out.println(bestLoader.fileName[7]);
        return Result.success("[{\"huanyuhai\": \"sb\"}]");

    }
    @PostMapping("/placeOrder")
    public Result<?>  placeOrder() throws SQLException {
        bestLoader.placeOrder(bestLoader.fileName[6]);
        System.out.println(bestLoader.fileName[6]);
        return Result.success("[{\"huanyuhai\": \"sb\"}]");

    }
    @PostMapping("/stockin")
    public Result<?>  stockin() throws SQLException {
        bestLoader.stock_in(bestLoader.fileName[5]);
        System.out.println(bestLoader.fileName[5]);
        return Result.success("[{\"huanyuhai\": \"sb\"}]");

    }
}
