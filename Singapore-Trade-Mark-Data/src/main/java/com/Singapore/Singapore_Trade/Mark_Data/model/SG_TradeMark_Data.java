package com.Singapore.Singapore_Trade.Mark_Data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@SuppressWarnings("All")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class SG_TradeMark_Data {

    private String mark;
    private String registrationNumber;
    private String internationalClasses;
    private String owner;
    private String ownerDetails;
    private String expiryDate;

}
