package com.inspirage.ilct.dto.bean;

import lombok.Data;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 01-11-2023
 */
@Data
public class VisibilityBean {
    private Boolean enableAccessToScreen ;
    private Boolean mapView ;
    private Boolean tableView ;
    private Boolean mapAndTableView ;

    public VisibilityBean(){
        this.enableAccessToScreen = false;
        this.mapView = false;
        this.tableView = false;
        this.mapAndTableView = false;
    }
}
