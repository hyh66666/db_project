package com.example.demo.enity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("center")
@Data

public class center {
    private Integer id;
    private String name;
}
