package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoadReference {
    private String loadReferenceType;
    private String content;

    public LoadReference(String loadReferenceType, String content){
        super();
            this.loadReferenceType = loadReferenceType;
            this.content = content;
    }
}
