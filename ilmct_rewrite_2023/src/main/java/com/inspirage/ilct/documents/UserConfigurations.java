package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.bean.ApplicationStatus;
import com.inspirage.ilct.dto.bean.VisibilityBean;
import com.inspirage.ilct.enums.RoleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "userConfigurations")
public class UserConfigurations extends BaseDoc {

    private String userId;

    @Enumerated(value = EnumType.STRING)
    private RoleType roleType;

  private List<Monitor> monitorSettings = new ArrayList<>();

    private Integer autoRefreshTime;

    private ApplicationStatus applicationStatus;

    private VisibilityBean vehicleVisibilityMap;

    private VisibilityBean itemVisibilityMap;
    private VisibilityBean containerVisibilityMap;
    private VisibilityBean vehicleVisibilityTable;
    private VisibilityBean containerVisibilityTable;
    private VisibilityBean completedShipmentsTable;
    private VisibilityBean orderVisibilityTable;

    private VisibilityBean parcelVisibilityTable = new VisibilityBean();
}
