package com.inspirage.ilct.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "country")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CountryDoc extends BaseDoc {
    private String name;
    private String iso2Code;
    private String iso3Code;
}
