package com.Singapore.Singapore_Trade.Mark_Data.controller;

import com.Singapore.Singapore_Trade.Mark_Data.model.SG_TradeMark_Data;
import com.Singapore.Singapore_Trade.Mark_Data.service.SG_TradeMark_Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SuppressWarnings("All")
@RestController
public class SG_TradeMark_Controller {

    private final SG_TradeMark_Service sgTradeMarkService;

    public SG_TradeMark_Controller(SG_TradeMark_Service sgTradeMarkService){

        this.sgTradeMarkService=sgTradeMarkService;
    }

    @GetMapping("/run")
    public ResponseEntity<List<SG_TradeMark_Data>> runSG(){
        return sgTradeMarkService.runSGTradeMark();
    }
}
